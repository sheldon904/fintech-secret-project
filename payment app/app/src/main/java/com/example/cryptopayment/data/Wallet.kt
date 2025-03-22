package com.example.cryptopayment.data

import java.math.BigDecimal

data class CryptoCurrency(
    val code: String,
    val name: String,
    val balance: BigDecimal,
    val fiatValue: BigDecimal,
    val address: String
)

data class Wallet(
    val id: String,
    val currencies: List<CryptoCurrency>,
    val transactions: List<Transaction>
)