package com.example.cryptopayment.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptopayment.data.CryptoCurrency
import com.example.cryptopayment.data.Transaction
import com.example.cryptopayment.data.Wallet
import com.example.cryptopayment.service.WalletService
import kotlinx.coroutines.launch
import java.math.BigDecimal

class WalletViewModel(private val walletService: WalletService) : ViewModel() {
    
    private val _wallet = MutableLiveData<Wallet>()
    val wallet: LiveData<Wallet> = _wallet
    
    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    init {
        loadWallet()
        loadTransactions()
    }
    
    fun loadWallet() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _wallet.value = walletService.getWallet()
            } catch (e: Exception) {
                _error.value = "Failed to load wallet: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadTransactions() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _transactions.value = walletService.getTransactionHistory()
            } catch (e: Exception) {
                _error.value = "Failed to load transactions: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun getCryptoCurrency(code: String): CryptoCurrency? {
        return _wallet.value?.currencies?.find { it.code == code }
    }
    
    fun getBalance(code: String): BigDecimal {
        return getCryptoCurrency(code)?.balance ?: BigDecimal.ZERO
    }
    
    fun getTotalBalance(): BigDecimal {
        return _wallet.value?.currencies?.sumOf { it.fiatValue } ?: BigDecimal.ZERO
    }
}