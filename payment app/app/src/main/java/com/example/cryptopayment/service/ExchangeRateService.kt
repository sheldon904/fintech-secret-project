package com.example.cryptopayment.service

import java.math.BigDecimal

interface ExchangeRateService {
    /**
     * Get current exchange rate for a cryptocurrency to USD
     * @param cryptoCode Cryptocurrency code (e.g., ETH, BTC)
     * @return Exchange rate as BigDecimal (1 crypto = x USD)
     */
    suspend fun getCurrentRate(cryptoCode: String): BigDecimal
    
    /**
     * Convert cryptocurrency amount to USD
     * @param cryptoCode Cryptocurrency code (e.g., ETH, BTC)
     * @param amount Amount in cryptocurrency
     * @return Equivalent amount in USD
     */
    suspend fun convertToUsd(cryptoCode: String, amount: BigDecimal): BigDecimal
    
    /**
     * Convert USD amount to cryptocurrency
     * @param cryptoCode Cryptocurrency code (e.g., ETH, BTC)
     * @param usdAmount Amount in USD
     * @return Equivalent amount in cryptocurrency
     */
    suspend fun convertToCrypto(cryptoCode: String, usdAmount: BigDecimal): BigDecimal
}