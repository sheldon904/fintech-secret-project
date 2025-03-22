package com.example.cryptopayment.service.mock

import com.example.cryptopayment.data.CryptoCurrency
import com.example.cryptopayment.data.Transaction
import com.example.cryptopayment.data.TransactionStatus
import com.example.cryptopayment.data.TransactionType
import com.example.cryptopayment.data.Wallet
import com.example.cryptopayment.service.BlockchainService
import com.example.cryptopayment.service.ExchangeRateService
import com.example.cryptopayment.service.WalletService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*

class MockWalletService(
    private val blockchainService: BlockchainService,
    private val exchangeRateService: ExchangeRateService
) : WalletService {
    
    // Mock wallet data
    private val walletId = UUID.randomUUID().toString()
    private val ethereumAddress = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e"
    private val privateKey = "0xYourPrivateKeyWouldGoHereButThisIsJustAMock" // Not real
    
    private val transactions = mutableListOf<Transaction>()
    
    // Initialize wallet with mock data
    private val wallet: Wallet
        get() {
            val currencies = mutableListOf<CryptoCurrency>()
            
            GlobalScope.launch {
                // This is just for mock purposes to have a non-empty wallet
                val ethBalance = blockchainService.getBalance(ethereumAddress)
                val ethRate = exchangeRateService.getCurrentRate("ETH")
                
                currencies.add(
                    CryptoCurrency(
                        code = "ETH",
                        name = "Ethereum",
                        balance = ethBalance,
                        fiatValue = ethBalance.multiply(ethRate),
                        address = ethereumAddress
                    )
                )
            }
            
            return Wallet(
                id = walletId,
                currencies = currencies,
                transactions = transactions
            )
        }
    
    override suspend fun getWallet(): Wallet {
        return wallet
    }
    
    override suspend fun getCryptoCurrency(code: String): CryptoCurrency? {
        return wallet.currencies.find { it.code == code }
    }
    
    override suspend fun getBalance(code: String): BigDecimal {
        return getCryptoCurrency(code)?.balance ?: BigDecimal.ZERO
    }
    
    override suspend fun getTransactionHistory(): List<Transaction> {
        val blockchainTxs = blockchainService.getTransactionHistory(ethereumAddress)
        transactions.addAll(blockchainTxs.filter { tx -> transactions.none { it.id == tx.id } })
        return transactions.sortedByDescending { it.timestamp }
    }
    
    override suspend fun getTransaction(transactionId: String): Transaction? {
        return transactions.find { it.id == transactionId }
    }
    
    override suspend fun makePayment(
        recipientAddress: String,
        amount: BigDecimal,
        cryptoCode: String,
        merchantName: String?
    ): Transaction {
        // Get current exchange rate
        val exchangeRate = exchangeRateService.getCurrentRate(cryptoCode)
        val usdAmount = exchangeRateService.convertToUsd(cryptoCode, amount)
        
        // Create transaction
        val transaction = Transaction(
            id = UUID.randomUUID().toString(),
            type = TransactionType.SEND,
            amount = amount,
            cryptoCurrency = cryptoCode,
            fiatAmount = usdAmount,
            exchangeRate = exchangeRate,
            timestamp = Date(),
            status = TransactionStatus.PENDING,
            recipient = recipientAddress,
            sender = ethereumAddress,
            merchantName = merchantName
        )
        
        // Add to local transactions list
        transactions.add(transaction)
        
        // Send to blockchain
        val txId = blockchainService.sendTransaction(
            fromAddress = ethereumAddress,
            toAddress = recipientAddress,
            amount = amount,
            privateKey = privateKey
        )
        
        // Update transaction with blockchain tx ID
        val updatedTransaction = transaction.copy(blockchainTxId = txId)
        val index = transactions.indexOfFirst { it.id == transaction.id }
        if (index != -1) {
            transactions[index] = updatedTransaction
        }
        
        return updatedTransaction
    }
    
    override fun monitorTransaction(transactionId: String): Flow<Transaction> {
        val transaction = transactions.find { it.id == transactionId } ?: throw IllegalArgumentException("Transaction not found")
        
        // Monitor blockchain transaction
        return blockchainService.monitorTransaction(transaction.blockchainTxId ?: "")
            .map { blockchainTx ->
                // Update local transaction with blockchain status
                val updatedTransaction = transaction.copy(status = blockchainTx.status)
                
                // Update in transaction list
                val index = transactions.indexOfFirst { it.id == transaction.id }
                if (index != -1) {
                    transactions[index] = updatedTransaction
                }
                
                updatedTransaction
            }
    }
    
    // Add a mock transaction for testing
    fun addMockTransaction() {
        val amount = BigDecimal("0.05")
        val exchangeRate = BigDecimal("1718.45")
        val fiatAmount = amount.multiply(exchangeRate)
        
        val transaction = Transaction(
            id = UUID.randomUUID().toString(),
            type = TransactionType.SEND,
            amount = amount,
            cryptoCurrency = "ETH",
            fiatAmount = fiatAmount,
            exchangeRate = exchangeRate,
            timestamp = Date(System.currentTimeMillis() - 86400000), // 1 day ago
            status = TransactionStatus.CONFIRMED,
            recipient = "0x1234567890123456789012345678901234567890",
            sender = ethereumAddress,
            blockchainTxId = "0x${UUID.randomUUID().toString().replace("-", "")}",
            merchantName = "Coffee Shop"
        )
        
        transactions.add(transaction)
    }
}