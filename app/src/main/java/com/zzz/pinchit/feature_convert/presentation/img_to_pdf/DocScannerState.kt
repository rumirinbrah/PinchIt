package com.zzz.pinchit.feature_convert.presentation.img_to_pdf

import android.os.Environment

data class DocScannerState(
    val fileName : String ="unknown${System.currentTimeMillis()}",
    val fileType : String? ="application/pdf",
    val relativePath : String = Environment.DIRECTORY_DOCUMENTS
)
