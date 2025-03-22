package com.example.cryptopayment.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptopayment.data.Transaction
import com.example.cryptopayment.service.ExchangeRateService
import com.example.cryptopayment.service.WalletService
import kotlinx.coroutines.launch
import java.math.BigDecimal

class PaymentViewModel(
    private val walletService: WalletService,
    private val exchangeRateService: ExchangeRateService
) : ViewModel() {
    
    private val _selectedCryptoCode = MutableLiveData<String>("ETH")
    val selectedCryptoCode: LiveData<String> = _selectedCryptoCode
    
    private val _cryptoAmount = MutableLiveData<BigDecimal>()
    val cryptoAmount: LiveData<BigDecimal> = _cryptoAmount
    
    private val _fiatAmount = MutableLiveData<BigDecimal>()
    val fiatAmount: LiveData<BigDecimal> = _fiatAmount
    
    private val _exchangeRate = MutableLiveData<BigDecimal>()
    val exchangeRate: LiveData<BigDecimal> = _exchangeRate
    
    private val _merchantName = MutableLiveData<String>()
    val merchantName: LiveData<String> = _merchantName
    
    private val _merchantAddress = MutableLiveData<String>()
    val merchantAddress: LiveData<String> = _merchantAddress
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    fun setMerchantInfo(name: String, address: String) {
        _merchantName.value = name
        _merchantAddress.value = address
    }
    
    fun setFiatAmount(amount: BigDecimal) {
        _fiatAmount.value = amount
        updateCryptoAmount()
    }
    
    fun selectCryptoCurrency(code: String) {
        if (_selectedCryptoCode.value != code) {
            _selectedCryptoCode.value = code
            updateExchangeRate()
            updateCryptoAmount()
        }
    }
    
    private fun updateExchangeRate() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val rate = exchangeRateService.getCurrentRate(_selectedCryptoCode.value ?: "ETH")
                _exchangeRate.value = rate
            } catch (e: Exception) {
                _error.value = "Failed to get exchange rate: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun updateCryptoAmount() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val fiatAmount = _fiatAmount.value ?: BigDecimal.ZERO
                val cryptoCode = _selectedCryptoCode.value ?: "ETH"
                val cryptoAmount = exchangeRateService.convertToCrypto(cryptoCode, fiatAmount)
                _cryptoAmount.value = cryptoAmount
            } catch (e: Exception) {
                _error.value = "Failed to convert amount: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun makePayment(): LiveData<Transaction> {
        val result = MutableLiveData<Transaction>()
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val recipientAddress = _merchantAddress.value ?: return@launch
                val amount = _cryptoAmount.value ?: return@launch
                val cryptoCode = _selectedCryptoCode.value ?: "ETH"
                val merchantName = _merchantName.value
                
                val transaction = walletService.makePayment(
                    recipientAddress = recipientAddress,
                    amount = amount,
                    cryptoCode = cryptoCode,
                    merchantName = merchantName
                )
                
                result.value = transaction
            } catch (e: Exception) {
                _error.value = "Payment failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
        
        return result
    }
    
    init {
        updateExchangeRate()
    }
}