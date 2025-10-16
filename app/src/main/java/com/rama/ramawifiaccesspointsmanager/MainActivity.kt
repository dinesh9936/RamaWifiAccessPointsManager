package com.rama.ramawifiaccesspointsmanager

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.rama.wifiapmanager.WifiScanner
import com.rama.wifiapmanager.model.WifiScanState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var scanner: WifiScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scanner = WifiScanner(this)

        setContent {
            WifiScanScreen()
        }
    }

    @Composable
    fun WifiScanScreen() {
        var scanResults by remember { mutableStateOf(listOf<String>()) }

        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Button(onClick = { startScanAndCollect { scanResults = it } }) {
                    Text("Start Scan")
                }

                // Show results
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    scanResults.forEach { ssid ->
                        Text(text = ssid)
                    }
                }
            }
        }
    }

    private fun startScanAndCollect(onResults: (List<String>) -> Unit) {
        // Make sure permission is granted
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lifecycleScope.launch {
                scanner.startScan().collect { state ->
                    when (state) {
                        is WifiScanState.Success -> {
                            val ssids = state.results.map { it.ssid }
                            Log.d(TAG, "Scan results: $ssids")
                            onResults(ssids)
                        }
                        is WifiScanState.Error -> {
                            Log.d(TAG, "Scan error: ${state.message}")
                        }
                        is WifiScanState.MissingPermission -> {
                            Log.d(TAG, "Scan missing permission")
                        }
                    }
                }
            }
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }
    }
}
