package com.zzz.pinchit.feature_ocr.presentation

import android.graphics.Bitmap
import android.net.Uri

data class OcrUIState(
    val currentImage : Uri? = null,
    val currentBitmap: Bitmap? = null,
    val phase: OcrPhase = OcrPhase.IDLE,
    val text : String? = ""
)
enum class OcrPhase{
    IDLE,
    CROPPING,
    PROCESSING,
    DONE
}


