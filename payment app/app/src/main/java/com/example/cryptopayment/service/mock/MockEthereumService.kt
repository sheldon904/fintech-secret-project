package com.example.cryptopayment.service.mock

import com.example.cryptopayment.data.Transaction
import com.example.cryptopayment.data.TransactionStatus
import com.example.cryptopayment.data.TransactionType
import com.example.cryptopayment.service.BlockchainService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal
import java.util.*
import kotlin.random.Random

class MockEthereumService : BlockchainService {
    private val walletBalances = mutableMapOf<String, BigDecimal>()
    private val transactions = mutableListOf<Transaction>()
    private val pendingTransactions = mutableMapOf<String, Transaction>()
    
    init {
        // Initialize with some test data
        val testWalletAddress = "0x742d35Cc6634C0532925a3b844Bc454e4438f44e"
        walletBalances[testWalletAddress] = BigDecimal("1.5")
    }
    
    override suspend fun connect(): Boolean {
        // Simulate connection delay
        delay(1000)
        return true
    }
    
    override suspend fun getBalance(address: String): BigDecimal {
        // Simulate network delay
        delay(500)
        return walletBalances[address] ?: BigDecimal.ZERO
    }
    
    override suspend fun getTransactionHistory(address: String): List<Transaction> {
        // Simulate network delay
        delay(800)
        return transactions.filter { it.sender == address || it.recipient == address }
    }
    
    override suspend fun sendTransaction(
        fromAddress: String,
        toAddress: String,
        amount: BigDecimal,
        privateKey: String
    ): String? {
        // Check if sender has enough balance
        val senderBalance = walletBalances[fromAddress] ?: BigDecimal.ZERO
        if (senderBalance < amount) return null
        
        // Create transaction ID (simulate Ethereum tx hash)
        val txId = "0x${UUID.randomUUID().toString().replace("-", "")}"
        
        // Create transaction object
        val transaction = Transaction(
            id = UUID.randomUUID().toString(),
            type = TransactionType.SEND,
            amount = amount,
            cryptoCurrency = "ETH",
            fiatAmount = BigDecimal.ZERO, // Will be filled by wallet service
            exchangeRate = BigDecimal.ZERO, // Will be filled by wallet service
            timestamp = Date(),
            status = TransactionStatus.PENDING,
            recipient = toAddress,
            sender = fromAddress,
            blockchainTxId = txId
        )
        
        // Store pending transaction
        pendingTransactions[txId] = transaction
        
        // Simulate blockchain confirmation delay in a separate coroutine
        // In a real implementation, this would be handled by blockchain events
        // This is just a mock to simulate the flow
        
        return txId
    }
    
    override fun monitorTransaction(transactionId: String): Flow<Transaction> = flow {
        val transaction = pendingTransactions[transactionId] ?: return@flow
        
        // Initial state
        emit(transaction)
        
        // Simulate transaction confirmation process
        delay(2000) // First block confirmation
        var updatedTx = transaction.copy(status = TransactionStatus.PENDING)
        emit(updatedTx)
        
        delay(3000) // More confirmations
        
        // 95% chance of success, 5% chance of failure
        val success = Random.nextDouble() < 0.95
        
        if (success) {
            // Update balances
            val senderBalance = walletBalances[transaction.sender] ?: BigDecimal.ZERO
            val recipientBalance = walletBalances[transaction.recipient] ?: BigDecimal.ZERO
            
            walletBalances[transaction.sender!!] = senderBalance.subtract(transaction.amount)
            walletBalances[transaction.recipient!!] = recipientBalance.add(transaction.amount)
            
            updatedTx = transaction.copy(status = TransactionStatus.CONFIRMED)
            transactions.add(updatedTx)
            pendingTransactions.remove(transactionId)
        } else {
            updatedTx = transaction.copy(status = TransactionStatus.FAILED)
            transactions.add(updatedTx)
            pendingTransactions.remove(transactionId)
        }
        
        emit(updatedTx)
    }
    
    override suspend fun disconnect() {
        // Simulate disconnect delay
        delay(300)
        // Nothing to do here in the mock
    }
}