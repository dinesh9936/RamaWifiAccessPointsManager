package com.rama.wifiapmanager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.rama.wifiapmanager.core.WifiScanManager
import com.rama.wifiapmanager.core.WifiScanReceiver
import com.rama.wifiapmanager.model.ScanResultModel
import com.rama.wifiapmanager.model.WifiScanState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class WifiScanner(private val context: Context) {

    private val wifiScanManager = WifiScanManager(context)
    private val wifiScanReceiver = WifiScanReceiver(context, wifiScanManager)

    fun startScan(): Flow<WifiScanState> {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            return flowOf(WifiScanState.MissingPermission)
        }

        return try {
            wifiScanReceiver.listenForScanResults()
                .map { success ->
                    if (success) {
                        val results = try {
                            wifiScanManager.getResults().map {
                                ScanResultModel(
                                    ssid = it.SSID,
                                    bssid = it.BSSID,
                                    signalLevel = it.level,
                                    frequency = it.frequency
                                )
                            }
                        } catch (e: Exception) {
                            // Catch any unexpected error while getting results
                            return@map WifiScanState.Error("Failed to get results: ${e.message}")
                        }

                        if (results.isNotEmpty()) {
                            WifiScanState.Success(results)
                        } else {
                            WifiScanState.Error("Wi-Fi scan returned no results")
                        }
                    } else {
                        WifiScanState.Error("Wi-Fi scan failed")
                    }
                }
                .onStart {
                    try {
                        wifiScanManager.startScan()
                    } catch (e: Exception) {
                        emit(WifiScanState.Error("Failed to start scan: ${e.message}"))
                    }
                }
        } catch (e: Exception) {
            // Catch any unexpected error in the Flow itself
            flowOf(WifiScanState.Error("Unexpected error: ${e.message}"))
        }
    }

    fun stopScan() {
        // nothing needed now because Flow cleanup is automatic
    }
}
