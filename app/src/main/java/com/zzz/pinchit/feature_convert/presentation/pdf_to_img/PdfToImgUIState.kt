package com.zzz.pinchit.feature_convert.presentation.pdf_to_img

import android.net.Uri

data class PdfToImgUIState(
    val uri: Uri? = null,
    val phase: PdfToImgPhase = PdfToImgPhase.IDLE
)
enum class PdfToImgPhase{
    IDLE,
    RENDER,
    READY
}


