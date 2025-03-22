package com.example.cryptopayment.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptopayment.R
import com.example.cryptopayment.databinding.FragmentWalletBinding
import com.example.cryptopayment.service.BlockchainService
import com.example.cryptopayment.service.ExchangeRateService
import com.example.cryptopayment.service.WalletService
import com.example.cryptopayment.service.mock.MockEthereumService
import com.example.cryptopayment.service.mock.MockExchangeRateService
import com.example.cryptopayment.service.mock.MockWalletService
import com.example.cryptopayment.ui.adapters.TransactionAdapter
import java.text.NumberFormat
import java.util.*

class WalletFragment : Fragment() {

    private var _binding: FragmentWalletBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: WalletViewModel
    private lateinit var transactionAdapter: TransactionAdapter
    
    // Mock services
    private lateinit var blockchainService: BlockchainService
    private lateinit var exchangeRateService: ExchangeRateService
    private lateinit var walletService: WalletService
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupServices()
        setupViewModel()
        setupUI()
        setupObservers()
    }
    
    private fun setupServices() {
        // Initialize mock services
        blockchainService = MockEthereumService()
        exchangeRateService = MockExchangeRateService()
        walletService = MockWalletService(blockchainService, exchangeRateService)
        
        // Add a mock transaction for demo purposes
        (walletService as MockWalletService).addMockTransaction()
    }
    
    private fun setupViewModel() {
        viewModel = WalletViewModel(walletService)
    }
    
    private fun setupUI() {
        // Setup transaction list
        transactionAdapter = TransactionAdapter(requireContext())
        binding.transactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }
        
        // Setup make payment button
        binding.makePaymentButton.setOnClickListener {
            findNavController().navigate(R.id.action_walletFragment_to_paymentFragment)
        }
    }
    
    private fun setupObservers() {
        // Observe wallet data
        viewModel.wallet.observe(viewLifecycleOwner) { wallet ->
            if (wallet != null && wallet.currencies.isNotEmpty()) {
                val eth = wallet.currencies.firstOrNull { it.code == "ETH" }
                if (eth != null) {
                    binding.balanceValue.text = "${eth.balance} ETH"
                    
                    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
                    binding.usdEquivalentValue.text = currencyFormat.format(eth.fiatValue)
                }
            }
        }
        
        // Observe transaction list
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.setTransactions(transactions)
        }
        
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Show/hide loading indicator if needed
        }
        
        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            // Show error message if needed
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}