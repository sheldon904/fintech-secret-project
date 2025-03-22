package com.example.cryptopayment.service.mock

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.suspendCancellableCoroutine
import java.math.BigDecimal
import kotlin.coroutines.resume

class MockNfcService {
    /**
     * Simulates an NFC payment terminal connection
     * @param onConnected Callback when connection is established
     * @param onError Callback when connection fails
     */
    fun connectToTerminal(
        onConnected: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Simulate connection delay
        Handler(Looper.getMainLooper()).postDelayed({
            onConnected()
        }, 2000)
    }
    
    /**
     * Simulates receiving a payment request from a terminal
     * @return PaymentRequest object with merchant info and payment amount
     */
    suspend fun receivePaymentRequest(): PaymentRequest = suspendCancellableCoroutine { continuation ->
        // Simulate delay for receiving payment request
        Handler(Looper.getMainLooper()).postDelayed({
            val request = PaymentRequest(
                merchantName = "Coffee Shop",
                merchantAddress = "0x1234567890123456789012345678901234567890",
                amountUsd = BigDecimal("25.75"),
                merchantId = "MERCHANT-001"
            )
            continuation.resume(request)
        }, 1500)
    }
    
    /**
     * Simulates sending payment data to the terminal
     * @param paymentData PaymentData object with transaction details
     * @return true if payment was accepted, false otherwise
     */
    suspend fun sendPaymentData(paymentData: PaymentData): Boolean = suspendCancellableCoroutine { continuation ->
        // Simulate processing delay
        Handler(Looper.getMainLooper()).postDelayed({
            // Always return true for simulation
            continuation.resume(true)
        }, 2000)
    }
    
    /**
     * Simulates disconnecting from the terminal
     */
    fun disconnect() {
        // Nothing to do here in the mock
    }
    
    /**
     * Data class representing a payment request from a terminal
     */
    data class PaymentRequest(
        val merchantName: String,
        val merchantAddress: String,
        val amountUsd: BigDecimal,
        val merchantId: String
    )
    
    /**
     * Data class representing payment data sent to the terminal
     */
    data class PaymentData(
        val transactionId: String,
        val cryptoAmount: BigDecimal,
        val cryptoCurrency: String,
        val usdAmount: BigDecimal,
        val senderAddress: String
    )
}