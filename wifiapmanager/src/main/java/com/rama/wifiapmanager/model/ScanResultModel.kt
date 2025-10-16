package com.rama.wifiapmanager.model

data class ScanResultModel(
    val ssid: String,
    val bssid: String,
    val signalLevel: Int,
    val frequency: Int
)