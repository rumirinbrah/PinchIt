package com.zzz.pinchit.feature_convert.presentation.pdf_to_img

import android.graphics.Bitmap

data class PdfToImgState(
    val images : List<Bitmap> = emptyList(),
    val relativePath : String = "Pictures/PinchIt",
    val mimeType : String = "image/jpeg"
)
