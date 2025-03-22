package com.example.cryptopayment.di

import com.example.cryptopayment.service.BlockchainService
import com.example.cryptopayment.service.ExchangeRateService
import com.example.cryptopayment.service.WalletService
import com.example.cryptopayment.service.mock.MockEthereumService
import com.example.cryptopayment.service.mock.MockExchangeRateService
import com.example.cryptopayment.service.mock.MockNfcService
import com.example.cryptopayment.service.mock.MockWalletService
import com.example.cryptopayment.ui.PaymentViewModel
import com.example.cryptopayment.ui.TransactionViewModel
import com.example.cryptopayment.ui.WalletViewModel

/**
 * Simple dependency injection class for creating services and view models.
 * In a real app, you would use Dagger/Hilt or Koin for proper DI.
 */
object ServiceLocator {
    
    // Services
    private val blockchainService: BlockchainService by lazy { MockEthereumService() }
    private val exchangeRateService: ExchangeRateService by lazy { MockExchangeRateService() }
    private val walletService: WalletService by lazy { 
        val service = MockWalletService(blockchainService, exchangeRateService)
        service.addMockTransaction() // Add mock transaction for demo
        service
    }
    private val nfcService: MockNfcService by lazy { MockNfcService() }
    
    // ViewModels
    fun provideWalletViewModel(): WalletViewModel {
        return WalletViewModel(walletService)
    }
    
    fun providePaymentViewModel(): PaymentViewModel {
        return PaymentViewModel(walletService, exchangeRateService)
    }
    
    fun provideTransactionViewModel(): TransactionViewModel {
        return TransactionViewModel(walletService)
    }
    
    // Services
    fun provideBlockchainService(): BlockchainService = blockchainService
    
    fun provideExchangeRateService(): ExchangeRateService = exchangeRateService
    
    fun provideWalletService(): WalletService = walletService
    
    fun provideNfcService(): MockNfcService = nfcService
}