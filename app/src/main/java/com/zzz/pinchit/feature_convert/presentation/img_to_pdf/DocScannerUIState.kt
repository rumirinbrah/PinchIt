package com.zzz.pinchit.feature_convert.presentation.img_to_pdf

import android.content.IntentSender

data class DocScannerUIState(
    val loading : Boolean = false,
    val showRenameDialog : Boolean = false,
    val fileName : String = "unknown${System.currentTimeMillis()}"
)
