package com.zzz.pinchit.feature_compress.presentation.util

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun VerticalSpace(
    height : Dp = 20.dp
)
{
    Spacer(Modifier.height(height))
}