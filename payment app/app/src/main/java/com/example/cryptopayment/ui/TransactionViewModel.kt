package com.example.cryptopayment.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptopayment.data.StatusUpdate
import com.example.cryptopayment.data.StatusUpdateType
import com.example.cryptopayment.data.Transaction
import com.example.cryptopayment.service.WalletService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class TransactionViewModel(private val walletService: WalletService) : ViewModel() {
    
    private val _transaction = MutableLiveData<Transaction>()
    val transaction: LiveData<Transaction> = _transaction
    
    private val _statusUpdates = MutableLiveData<List<StatusUpdate>>()
    val statusUpdates: LiveData<List<StatusUpdate>> = _statusUpdates
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private var monitoringJob: Job? = null
    private val updates = mutableListOf<StatusUpdate>()
    
    fun loadTransaction(transactionId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val tx = walletService.getTransaction(transactionId)
                if (tx != null) {
                    _transaction.value = tx
                } else {
                    _error.value = "Transaction not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to load transaction: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun startMonitoring(transactionId: String) {
        // Cancel previous job if any
        monitoringJob?.cancel()
        
        // Add initial status update
        addStatusUpdate("Initiating payment", StatusUpdateType.INFO)
        
        monitoringJob = viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Let's add some simulated merchant-related status updates
                addStatusUpdate("Connecting to payment terminal", StatusUpdateType.INFO)
                delay(1000)
                addStatusUpdate("Payment request received", StatusUpdateType.INFO)
                delay(1000)
                addStatusUpdate("Transaction submitted to blockchain", StatusUpdateType.INFO)
                
                // Now monitor the actual blockchain transaction
                walletService.monitorTransaction(transactionId).collect { tx ->
                    _transaction.value = tx
                    
                    when (tx.status) {
                        com.example.cryptopayment.data.TransactionStatus.PENDING -> {
                            addStatusUpdate("Transaction pending - waiting for confirmation", StatusUpdateType.INFO)
                        }
                        com.example.cryptopayment.data.TransactionStatus.CONFIRMED -> {
                            addStatusUpdate("Transaction confirmed on blockchain", StatusUpdateType.SUCCESS)
                            addStatusUpdate("Converting cryptocurrency to USD", StatusUpdateType.INFO)
                            delay(1000)
                            addStatusUpdate("USD settlement initiated to merchant", StatusUpdateType.SUCCESS)
                            delay(1000)
                            addStatusUpdate("Payment completed successfully", StatusUpdateType.SUCCESS)
                        }
                        com.example.cryptopayment.data.TransactionStatus.FAILED -> {
                            addStatusUpdate("Transaction failed on blockchain", StatusUpdateType.ERROR)
                        }
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error monitoring transaction: ${e.message}"
                addStatusUpdate("Error monitoring transaction: ${e.message}", StatusUpdateType.ERROR)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun addStatusUpdate(message: String, type: StatusUpdateType) {
        val update = StatusUpdate(
            message = message,
            type = type,
            timestamp = Date()
        )
        updates.add(update)
        _statusUpdates.postValue(updates.toList())
    }
    
    private suspend fun delay(millis: Long) {
        kotlinx.coroutines.delay(millis)
    }
    
    override fun onCleared() {
        monitoringJob?.cancel()
        super.onCleared()
    }
}