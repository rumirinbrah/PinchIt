package com.zzz.pinchit.feature_convert.presentation.img_to_pdf

interface DocScannerEvents {

    data object Success : DocScannerEvents
    data class Error(val error : String) : DocScannerEvents
    data object Idle : DocScannerEvents

}