package com.example.cryptopayment.service

import com.example.cryptopayment.data.Transaction
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface BlockchainService {
    /**
     * Connect to the blockchain network
     * @return true if connection was successful
     */
    suspend fun connect(): Boolean
    
    /**
     * Get balance for a specific wallet address
     * @param address Blockchain wallet address
     * @return Balance amount as BigDecimal
     */
    suspend fun getBalance(address: String): BigDecimal
    
    /**
     * Get transaction history for a specific wallet address
     * @param address Blockchain wallet address
     * @return List of transactions
     */
    suspend fun getTransactionHistory(address: String): List<Transaction>
    
    /**
     * Send cryptocurrency to another address
     * @param fromAddress Sender's address
     * @param toAddress Recipient's address
     * @param amount Amount to send
     * @param privateKey Private key for signing the transaction
     * @return Transaction ID if successful, null otherwise
     */
    suspend fun sendTransaction(
        fromAddress: String,
        toAddress: String,
        amount: BigDecimal,
        privateKey: String
    ): String?
    
    /**
     * Monitor transaction status updates
     * @param transactionId The transaction ID to monitor
     * @return Flow of transaction status updates
     */
    fun monitorTransaction(transactionId: String): Flow<Transaction>
    
    /**
     * Disconnect from the blockchain network
     */
    suspend fun disconnect()
}