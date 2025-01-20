package com.zzz.pinchit.feature_compress.presentation.image_comp

import android.net.Uri
import com.zzz.pinchit.feature_compress.presentation.util.CompressQuality

sealed class CompImageAction {
    data object OnCompress : CompImageAction()
    data object OnSave : CompImageAction()
    data class OnQualityChange(val quality: CompressQuality) : CompImageAction()
    data class OnImageSelect(val uri: Uri) : CompImageAction()
    data object OnCancel : CompImageAction()
}