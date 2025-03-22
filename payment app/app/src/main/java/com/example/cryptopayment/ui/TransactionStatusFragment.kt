package com.example.cryptopayment.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptopayment.R
import com.example.cryptopayment.databinding.FragmentTransactionStatusBinding
import com.example.cryptopayment.service.BlockchainService
import com.example.cryptopayment.service.ExchangeRateService
import com.example.cryptopayment.service.WalletService
import com.example.cryptopayment.service.mock.MockEthereumService
import com.example.cryptopayment.service.mock.MockExchangeRateService
import com.example.cryptopayment.service.mock.MockWalletService
import com.example.cryptopayment.ui.adapters.StatusUpdateAdapter
import java.text.NumberFormat
import java.util.*

class TransactionStatusFragment : Fragment() {

    private var _binding: FragmentTransactionStatusBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: TransactionViewModel
    private lateinit var statusUpdateAdapter: StatusUpdateAdapter
    
    // Mock services
    private lateinit var blockchainService: BlockchainService
    private lateinit var exchangeRateService: ExchangeRateService
    private lateinit var walletService: WalletService
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionStatusBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupServices()
        setupViewModel()
        setupUI()
        setupObservers()
        
        // Get transaction ID from arguments
        val transactionId = arguments?.getString("transactionId")
        if (transactionId != null) {
            viewModel.loadTransaction(transactionId)
            viewModel.startMonitoring(transactionId)
        } else {
            // Handle missing transaction ID
            findNavController().navigateUp()
        }
    }
    
    private fun setupServices() {
        // Initialize mock services
        blockchainService = MockEthereumService()
        exchangeRateService = MockExchangeRateService()
        walletService = MockWalletService(blockchainService, exchangeRateService)
    }
    
    private fun setupViewModel() {
        viewModel = TransactionViewModel(walletService)
    }
    
    private fun setupUI() {
        // Setup status updates list
        statusUpdateAdapter = StatusUpdateAdapter()
        binding.statusUpdatesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = statusUpdateAdapter
        }
        
        // Setup Done button
        binding.doneButton.setOnClickListener {
            // Go back to wallet screen
            findNavController().navigate(R.id.action_transactionStatusFragment_to_walletFragment)
        }
    }
    
    private fun setupObservers() {
        // Observe transaction data
        viewModel.transaction.observe(viewLifecycleOwner) { transaction ->
            if (transaction != null) {
                // Update transaction details in the UI
                binding.transactionId.text = transaction.blockchainTxId
                binding.merchantInfo.text = transaction.merchantName ?: "Unknown Merchant"
                
                binding.cryptoAmount.text = "${transaction.amount} ${transaction.cryptoCurrency}"
                
                val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
                binding.usdAmount.text = currencyFormat.format(transaction.fiatAmount)
            }
        }
        
        // Observe status updates
        viewModel.statusUpdates.observe(viewLifecycleOwner) { updates ->
            statusUpdateAdapter.setStatusUpdates(updates)
            // Scroll to the bottom of the list to show the latest update
            if (updates.isNotEmpty()) {
                binding.statusUpdatesRecyclerView.scrollToPosition(updates.size - 1)
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}