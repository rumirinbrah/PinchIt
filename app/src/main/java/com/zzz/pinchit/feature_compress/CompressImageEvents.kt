package com.zzz.pinchit.feature_compress

interface CompressImageEvents {
    data object OnSaveSuccess : CompressImageEvents
    data object OnError : CompressImageEvents
    data object Idle : CompressImageEvents
}