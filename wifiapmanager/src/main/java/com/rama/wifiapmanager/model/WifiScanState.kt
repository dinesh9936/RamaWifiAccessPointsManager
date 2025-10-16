package com.rama.wifiapmanager.model

sealed class WifiScanState {
    object MissingPermission : WifiScanState()
    data class Success(val results: List<ScanResultModel>) : WifiScanState()
    data class Error(val message: String) : WifiScanState()
}