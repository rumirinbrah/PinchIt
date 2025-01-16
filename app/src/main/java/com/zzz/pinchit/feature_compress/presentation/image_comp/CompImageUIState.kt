package com.zzz.pinchit.feature_compress.presentation.image_comp

import com.zzz.pinchit.feature_compress.presentation.util.CompressQuality

data class CompImageUIState(
    val phase : CompressPhase = CompressPhase.IMAGE_NOT_SELECTED,
    val loading : Boolean = false,
    val currentQuality: CompressQuality = CompressQuality.MEDIUM,
    val compressedImage : ByteArray? = null
)
enum class CompressPhase{
    IMAGE_NOT_SELECTED,
    IMAGE_SELECTED,
    IMAGE_COMPRESSED,
}