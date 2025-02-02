package com.zzz.pinchit.core.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zzz.pinchit.ui.theme.snackWipeGreen
import com.zzz.pinchit.ui.theme.snackWipeRed
import com.zzz.pinchit.ui.theme.snackbarGreen
import com.zzz.pinchit.ui.theme.snackbarRed

@Composable
fun CustomSnackbar(
    snackbarData: SnackbarData
) {

    val wipeColor : Color
    val color = when(snackbarData.visuals.actionLabel){
        "S"->{
            wipeColor = snackWipeGreen
            snackbarGreen
        }
        "E"->{
            wipeColor = snackWipeRed
            snackbarRed
        }else->{
            wipeColor = snackWipeGreen
            snackbarGreen
        }
    }
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(4000)
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .drawBehind {
                drawRect(
                    color = wipeColor ,
                    size = Size(
                        width = size.width * progress.value ,
                        height = size.height
                    )
                )
            }
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = snackbarData.visuals.message,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.fillMaxWidth(0.9f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            snackbarData.visuals.actionLabel?.let {
                IconButton(
                    onClick = {
                        snackbarData.performAction()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Close ,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}