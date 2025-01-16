package com.zzz.pinchit.feature_compress.presentation

import com.zzz.pinchit.feature_compress.presentation.util.CompressQuality

data class CompressImageState(
    val fileName : String? = "unknown${System.currentTimeMillis()}",
    val fileType : String? = ".png",
    val relativePath : String = "Pictures/PinchIt",
    val quality : CompressQuality = CompressQuality.MEDIUM
)
