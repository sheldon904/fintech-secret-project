package com.example.cryptopayment.service.mock

import com.example.cryptopayment.service.ExchangeRateService
import kotlinx.coroutines.delay
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

class MockExchangeRateService : ExchangeRateService {
    private val rates = mutableMapOf(
        "ETH" to BigDecimal("1718.45"),
        "BTC" to BigDecimal("52304.18"),
        "USDC" to BigDecimal("1.00")
    )
    
    override suspend fun getCurrentRate(cryptoCode: String): BigDecimal {
        // Simulate network delay
        delay(500)
        
        return rates[cryptoCode.uppercase()] ?: BigDecimal.ZERO
    }
    
    override suspend fun convertToUsd(cryptoCode: String, amount: BigDecimal): BigDecimal {
        val rate = getCurrentRate(cryptoCode)
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP)
    }
    
    override suspend fun convertToCrypto(cryptoCode: String, usdAmount: BigDecimal): BigDecimal {
        val rate = getCurrentRate(cryptoCode)
        if (rate == BigDecimal.ZERO) return BigDecimal.ZERO
        
        return usdAmount.divide(rate, 8, RoundingMode.HALF_UP)
    }
    
    // Method for refreshing rates with slight variations to simulate market changes
    suspend fun refreshRates() {
        rates.forEach { (crypto, rate) ->
            // Add small random variation (+/- 1%)
            val variation = Random.nextDouble(-0.01, 0.01)
            val newRate = rate.multiply(BigDecimal(1 + variation))
                .setScale(2, RoundingMode.HALF_UP)
            rates[crypto] = newRate
        }
    }
}