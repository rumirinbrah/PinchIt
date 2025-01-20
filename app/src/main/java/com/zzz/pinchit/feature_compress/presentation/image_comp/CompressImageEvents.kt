package com.zzz.pinchit.feature_compress.presentation.image_comp

interface CompressImageEvents {
    data object OnSaveSuccess : CompressImageEvents
    data object OnError : CompressImageEvents
    data object Idle : CompressImageEvents
}