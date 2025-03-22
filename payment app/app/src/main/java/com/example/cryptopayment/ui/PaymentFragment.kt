package com.example.cryptopayment.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cryptopayment.R
import com.example.cryptopayment.databinding.FragmentPaymentBinding
import com.example.cryptopayment.service.BlockchainService
import com.example.cryptopayment.service.ExchangeRateService
import com.example.cryptopayment.service.WalletService
import com.example.cryptopayment.service.mock.MockEthereumService
import com.example.cryptopayment.service.mock.MockExchangeRateService
import com.example.cryptopayment.service.mock.MockNfcService
import com.example.cryptopayment.service.mock.MockWalletService
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: PaymentViewModel
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
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupServices()
        setupViewModel()
        setupUI()
        setupObservers()
        
        // For demo purposes, simulate receiving payment request from merchant
        simulatePaymentRequest()
    }
    
    private fun setupServices() {
        // Initialize mock services
        blockchainService = MockEthereumService()
        exchangeRateService = MockExchangeRateService()
        walletService = MockWalletService(blockchainService, exchangeRateService)
        nfcService = MockNfcService()
    }
    
    private fun setupViewModel() {
        viewModel = PaymentViewModel(walletService, exchangeRateService)
    }
    
    private fun setupUI() {
        // Setup crypto spinner
        val cryptos = arrayOf("ETH", "BTC", "USDC")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cryptos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.cryptoSpinner.adapter = adapter
        
        binding.cryptoSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCrypto = cryptos[position]
                viewModel.selectCryptoCurrency(selectedCrypto)
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
        
        // Setup buttons
        binding.tapToPayButton.setOnClickListener {
            navigateToNfcSimulation()
        }
        
        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupObservers() {
        // Observe merchant info
        viewModel.merchantName.observe(viewLifecycleOwner) { name ->
            binding.merchantName.text = name
        }
        
        // Observe fiat amount
        viewModel.fiatAmount.observe(viewLifecycleOwner) { amount ->
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
            binding.usdAmount.text = currencyFormat.format(amount)
        }
        
        // Observe crypto amount
        viewModel.cryptoAmount.observe(viewLifecycleOwner) { amount ->
            val crypto = viewModel.selectedCryptoCode.value ?: "ETH"
            binding.cryptoAmount.text = "$amount $crypto"
        }
        
        // Observe exchange rate
        viewModel.exchangeRate.observe(viewLifecycleOwner) { rate ->
            val crypto = viewModel.selectedCryptoCode.value ?: "ETH"
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
            binding.exchangeRate.text = "1 $crypto = ${currencyFormat.format(rate)} USD"
        }
    }
    
    private fun simulatePaymentRequest() {
        // Simulate merchant data for the demo
        viewModel.setMerchantInfo("Coffee Shop", "0x1234567890123456789012345678901234567890")
        viewModel.setFiatAmount(BigDecimal("25.75"))
    }
    
    private fun navigateToNfcSimulation() {
        findNavController().navigate(R.id.action_paymentFragment_to_nfcSimulationFragment)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}