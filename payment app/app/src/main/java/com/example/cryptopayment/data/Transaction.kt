package com.example.cryptopayment.data

import java.math.BigDecimal
import java.util.*

enum class TransactionType {
    SEND,
    RECEIVE
}

enum class TransactionStatus {
    PENDING,
    CONFIRMED,
    FAILED
}

data class Transaction(
    val id: String,
    val type: TransactionType,
    val amount: BigDecimal,
    val cryptoCurrency: String,
    val fiatAmount: BigDecimal,
    val fiatCurrency: String = "USD",
    val exchangeRate: BigDecimal,
    val timestamp: Date,
    val status: TransactionStatus,
    val recipient: String? = null,
    val sender: String? = null,
    val blockchainTxId: String? = null,
    val merchantName: String? = null
)