package com.example.cryptopayment

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.tech.IsoDep
import android.nfc.tech.MifareClassic
import android.nfc.tech.NfcA
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.cryptopayment.databinding.ActivityMainBinding
import com.example.cryptopayment.service.mock.MockEthereumService
import com.example.cryptopayment.service.mock.MockExchangeRateService
import com.example.cryptopayment.service.mock.MockWalletService

/**
 * Main activity that hosts all the fragments for the payment flow.
 * Also handles NFC foreground dispatch for payment terminal interaction simulation.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var intentFilters: Array<IntentFilter>? = null
    private var techLists: Array<Array<String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        setupNfc()
        setupMockServices()
    }

    private fun setupMockServices() {
        // Initialize the services on app start
        val blockchainService = MockEthereumService()
        val exchangeRateService = MockExchangeRateService()
        MockWalletService(blockchainService, exchangeRateService)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setupNfc() {
        // Check if NFC is available on device
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        
        if (nfcAdapter != null) {
            // Create a PendingIntent for NFC events
            val intent = Intent(this, javaClass).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            
            // Use FLAG_MUTABLE for Android 12+ compatibility
            pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_MUTABLE
            )
            
            // Setup intent filters for NFC
            val ndef = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
            intentFilters = arrayOf(ndef)
            
            // Setup tech lists
            techLists = arrayOf(
                arrayOf(NfcA::class.java.name),
                arrayOf(IsoDep::class.java.name),
                arrayOf(MifareClassic::class.java.name)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        // Enable NFC foreground dispatch
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFilters, techLists)
    }

    override fun onPause() {
        super.onPause()
        // Disable NFC foreground dispatch
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle NFC intent
        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            val navController = findNavController(R.id.nav_host_fragment)
            
            // If we're in the NFC simulation fragment, process the NFC tag
            if (navController.currentDestination?.id == R.id.nfcSimulationFragment) {
                // We would process the NFC tag here, but for simulation purposes,
                // the NfcSimulationFragment will handle this process on its own
            }
        }
    }
}