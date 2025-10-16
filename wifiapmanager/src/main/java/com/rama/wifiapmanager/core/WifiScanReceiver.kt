package com.rama.wifiapmanager.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal class WifiScanReceiver(
    private val context: Context,
    private val wifiScanManager: WifiScanManager
) {
    fun listenForScanResults(): Flow<Boolean> = callbackFlow {
        val filter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val success = intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false) ?: false
                trySend(success)
            }
        }

        context.registerReceiver(receiver, filter)
        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }
}