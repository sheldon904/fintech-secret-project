package com.example.cryptopayment.service

import com.example.cryptopayment.data.CryptoCurrency
import com.example.cryptopayment.data.Transaction
import com.example.cryptopayment.data.Wallet
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface WalletService {
    /**
     * Get the current wallet
     * @return The wallet object
     */
    suspend fun getWallet(): Wallet
    
    /**
     * Get a cryptocurrency by its code
     * @param code The cryptocurrency code (e.g., ETH, BTC)
     * @return The cryptocurrency if found, null otherwise
     */
    suspend fun getCryptoCurrency(code: String): CryptoCurrency?
    
    /**
     * Get balance for a cryptocurrency
     * @param code The cryptocurrency code (e.g., ETH, BTC)
     * @return The balance in the specified cryptocurrency
     */
    suspend fun getBalance(code: String): BigDecimal
    
    /**
     * Get transaction history
     * @return List of transactions
     */
    suspend fun getTransactionHistory(): List<Transaction>
    
    /**
     * Get a specific transaction by its ID
     * @param transactionId Transaction ID
     * @return The transaction if found, null otherwise
     */
    suspend fun getTransaction(transactionId: String): Transaction?
    
    /**
     * Make a payment
     * @param recipientAddress Recipient's blockchain address
     * @param amount Amount in cryptocurrency to send
     * @param cryptoCode Cryptocurrency code (e.g., ETH, BTC)
     * @param merchantName Name of the merchant (optional)
     * @return Transaction object representing the payment
     */
    suspend fun makePayment(
        recipientAddress: String,
        amount: BigDecimal,
        cryptoCode: String,
        merchantName: String? = null
    ): Transaction
    
    /**
     * Monitor transaction status updates
     * @param transactionId The transaction ID to monitor
     * @return Flow of transaction status updates
     */
    fun monitorTransaction(transactionId: String): Flow<Transaction>
}