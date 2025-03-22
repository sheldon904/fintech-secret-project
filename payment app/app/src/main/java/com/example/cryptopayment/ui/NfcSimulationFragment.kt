package com.example.cryptopayment.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cryptopayment.R
import com.example.cryptopayment.databinding.FragmentNfcSimulationBinding
import com.example.cryptopayment.service.BlockchainService
import com.example.cryptopayment.service.ExchangeRateService
import com.example.cryptopayment.service.WalletService
import com.example.cryptopayment.service.mock.MockEthereumService
import com.example.cryptopayment.service.mock.MockExchangeRateService
import com.example.cryptopayment.service.mock.MockNfcService
import com.example.cryptopayment.service.mock.MockWalletService
import java.math.BigDecimal
import java.util.*

class NfcSimulationFragment : Fragment() {

    private var _binding: FragmentNfcSimulationBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var paymentViewModel: PaymentViewModel
    private lateinit var nfcService: MockNfcService
    
    // Mock services
    private lateinit var blockchainService: BlockchainService
    private lateinit var exchangeRateService: ExchangeRateService
    private lateinit var walletService: WalletService
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNfcSimulationBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupServices()
        setupViewModel()
        setupUI()
        
        // Start the NFC simulation process
        simulateNfcPayment()
    }
    
    private fun setupServices() {
        // Initialize mock services
        blockchainService = MockEthereumService()
        exchangeRateService = MockExchangeRateService()
        walletService = MockWalletService(blockchainService, exchangeRateService)
        nfcService = MockNfcService()
    }
    
    private fun setupViewModel() {
        paymentViewModel = PaymentViewModel(walletService, exchangeRateService)
        
        // For demo, we'd normally get this from the previous fragment
        // but we'll simulate it here
        paymentViewModel.setMerchantInfo("Coffee Shop", "0x1234567890123456789012345678901234567890")
        paymentViewModel.setFiatAmount(BigDecimal("25.75"))
    }
    
    private fun setupUI() {
        // Set NFC animation image
        binding.nfcAnimationImage.setImageResource(R.drawable.ic_nfc)
        
        // Setup Cancel button
        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun simulateNfcPayment() {
        // Update UI to show connecting state
        binding.nfcStatusText.text = "Connecting to terminal..."
        binding.nfcInstructionText.text = "Hold your phone near the payment terminal"
        
        // Simulate NFC connection
        nfcService.connectToTerminal(
            onConnected = {
                // Update UI on connection
                Handler(Looper.getMainLooper()).post {
                    binding.nfcStatusText.text = "Connected to terminal"
                    binding.nfcInstructionText.text = "Processing payment..."
                    
                    // Simulate payment processing with a delay
                    Handler(Looper.getMainLooper()).postDelayed({
                        processPayment()
                    }, 2000)
                }
            },
            onError = { error ->
                // Update UI on error
                Handler(Looper.getMainLooper()).post {
                    binding.nfcStatusText.text = "Error: $error"
                    binding.nfcInstructionText.text = "Please try again"
                }
            }
        )
    }
    
    private fun processPayment() {
        // Make the payment via ViewModel
        paymentViewModel.makePayment().observe(viewLifecycleOwner) { transaction ->
            if (transaction != null) {
                // Update UI to show payment sent
                binding.nfcStatusText.text = "Payment sent to blockchain"
                binding.nfcInstructionText.text = "Transaction confirmed"
                
                // Navigate to transaction status screen
                Handler(Looper.getMainLooper()).postDelayed({
                    navigateToTransactionStatus(transaction.id)
                }, 1500)
            } else {
                // Handle payment failure
                binding.nfcStatusText.text = "Payment failed"
                binding.nfcInstructionText.text = "Please try again"
            }
        }
    }
    
    private fun navigateToTransactionStatus(transactionId: String) {
        val bundle = bundleOf("transactionId" to transactionId)
        findNavController().navigate(R.id.action_nfcSimulationFragment_to_transactionStatusFragment, bundle)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}