package com.zzz.pinchit.core.presentation.util

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable data object HomeScreen : Screen()
    @Serializable data object ImageCompScreen : Screen()
    @Serializable data object PDFCompScreen : Screen()
}