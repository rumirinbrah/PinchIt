package com.zzz.pinchit.feature_ocr.presentation

import android.graphics.Bitmap
import android.net.Uri

sealed class OcrActions {

    data class OnUriSelect(val uri: Uri) : OcrActions()
    data class OnCrop(val croppedImage : Bitmap) : OcrActions()


    data object Reset : OcrActions()

}