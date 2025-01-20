package com.zzz.pinchit.feature_compress.presentation.image_comp

import com.zzz.pinchit.feature_compress.presentation.util.CompressQuality

data class CompressImageState(
    val fileName : String? = "unknown${System.currentTimeMillis()}",
    val fileType : String? = "image/png",
    val relativePath : String = "Pictures/PinchIt",
    val quality : CompressQuality = CompressQuality.MEDIUM
)
