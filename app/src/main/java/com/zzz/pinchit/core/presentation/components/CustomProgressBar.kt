package com.zzz.pinchit.core.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * @param progress - Progress value for the progress bar. Range : 0f to 1f
 */
@Composable
fun CustomProgressBar(progress:Float) {
    val brush = Brush.linearGradient(listOf(Color(0xFF26B8FA) , Color(0xFFBCDFEF)))
    val float by animateFloatAsState(
        targetValue = progress,
    )

    Canvas(
        modifier = Modifier.size(70.dp)
    ) {
        drawArc(
            brush = brush ,
            startAngle = 0f ,
            sweepAngle = 360f * float,
            size = Size(size.width,size.height) ,
            useCenter = true
        )
    }
}