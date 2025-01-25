package com.zzz.pinchit.feature_convert.presentation.img_to_pdf

import android.net.Uri

sealed class DocScannerActions {
    data object OnGet : DocScannerActions()
    data class OnUriReady(val uri: Uri) : DocScannerActions()
    data class OnSaveFile(val name : String) : DocScannerActions()
    data object OnCancel : DocScannerActions()
}