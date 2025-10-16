package com.rama.wifiapmanager.core

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat

internal class WifiScanManager(private val context: Context) {

    private val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    fun startScan(): Boolean = wifiManager.startScan()

    fun getResults(): List<ScanResult> {
        return if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            wifiManager.scanResults
        } else {
            emptyList() // or throw custom exception if you want
        }
    }
}