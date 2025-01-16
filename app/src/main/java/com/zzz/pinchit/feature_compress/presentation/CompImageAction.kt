package com.zzz.pinchit.feature_compress.presentation

import android.net.Uri
import com.zzz.pinchit.feature_compress.presentation.util.CompressQuality

sealed class CompImageAction {
    data class OnCompress(val uri: Uri ) : CompImageAction()
    data object OnSave : CompImageAction()
    data class OnQualityChange(val quality: CompressQuality) : CompImageAction()
    data object OnImageSelect : CompImageAction()
    data object OnCancel : CompImageAction()
}