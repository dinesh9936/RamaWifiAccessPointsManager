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

        return wifiScanReceiver.listenForScanResults()
            .map { success ->
                if (success) {
                    val results = wifiScanManager.getResults().map {
                        ScanResultModel(
                            ssid = it.SSID,
                            bssid = it.BSSID,
                            signalLevel = it.level,
                            frequency = it.frequency
                        )
                    }
                    WifiScanState.Success(results)
                } else {
                    WifiScanState.Error("Wi-Fi scan failed or returned no results.")
                }
            }
            .onStart { wifiScanManager.startScan() }
    }

    fun stopScan() {
        // nothing needed now because Flow cleanup is automatic
    }
}