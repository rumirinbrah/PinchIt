package com.zzz.pinchit.feature_ocr.presentation.crop

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.zzz.pinchit.core.presentation.util.getBitmapFromUri
import com.zzz.pinchit.core.presentation.util.getCroppedBitmap
import com.zzz.pinchit.core.presentation.util.isCenterDragWithinBounds
import com.zzz.pinchit.core.presentation.util.isDragWithinBounds
import com.zzz.pinchit.core.presentation.util.isNear
import com.zzz.pinchit.feature_compress.presentation.util.VerticalSpace

@Composable
fun ImageCropper(
    imageUri: Uri,
    onCrop : (Bitmap)->Unit,
    modifier: Modifier = Modifier
)
{
    val context = LocalContext.current
    val bitmap = getBitmapFromUri(context,imageUri)

    var draggingCenter by remember{ mutableStateOf(false) }
    var draggingCorner by remember { mutableStateOf<Corner?>(null) }

    var topLeft by remember { mutableStateOf<Offset>(Offset(400f , 400f)) }
    var topRight by remember { mutableStateOf<Offset>(Offset(800f , 400f)) }
    var bottomLeft by remember { mutableStateOf<Offset>(Offset(400f , 800f)) }
    var bottomRight by remember { mutableStateOf<Offset>(Offset(800f , 800f)) }

    var canvasWidth : Float = 0f
    var canvasHeight : Float = 0f

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
        ){
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .crossfade(true)
                    .data(imageUri)
                    .build() ,
                contentDescription = "crop image" ,
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
            Canvas(
                Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                draggingCorner = when {
                                    offset.isNear(topLeft) -> Corner.TopLeft
                                    offset.isNear(topRight) -> Corner.TopRight
                                    offset.isNear(bottomLeft) -> Corner.BottomLeft
                                    offset.isNear(bottomRight) -> Corner.BottomRight
                                    else -> null
                                }
                                val rect = Rect(topLeft , bottomRight)
                                if (draggingCorner == null && rect.contains(offset)) {
                                    draggingCenter = true
                                }

                            } ,
                            onDrag = { change , dragAmount ->
                                change.consume()
                                when (draggingCorner) {
                                    Corner.TopLeft -> {

                                        if (
                                            isDragWithinBounds(
                                                topLeft ,
                                                dragAmount ,
                                                canvasWidth ,
                                                canvasHeight
                                            )
                                        ) {
                                            topLeft += dragAmount
                                            topRight = topRight.copy(y = topLeft.y)
                                            bottomLeft = bottomLeft.copy(x = topLeft.x)
                                        }
                                    }

                                    Corner.TopRight -> {
                                        if (
                                            isDragWithinBounds(
                                                topRight ,
                                                dragAmount ,
                                                canvasWidth ,
                                                canvasHeight
                                            )
                                        ) {
                                            topRight += dragAmount
                                            topLeft = topLeft.copy(y = topRight.y)
                                            bottomRight = bottomRight.copy(x = topRight.x)
                                        }
                                    }

                                    Corner.BottomLeft -> {
                                        if (
                                            isDragWithinBounds(
                                                bottomLeft ,
                                                dragAmount ,
                                                canvasWidth ,
                                                canvasHeight
                                            )
                                        ) {
                                            bottomLeft += dragAmount
                                            topLeft = topLeft.copy(x = bottomLeft.x)
                                            bottomRight = bottomRight.copy(y = bottomLeft.y)
                                        }
                                    }

                                    Corner.BottomRight -> {
                                        if (
                                            isDragWithinBounds(
                                                bottomRight ,
                                                dragAmount ,
                                                canvasWidth ,
                                                canvasHeight
                                            )
                                        ) {

                                            bottomRight += dragAmount
                                            bottomLeft = bottomLeft.copy(y = bottomRight.y)
                                            topRight = topRight.copy(x = bottomRight.x)
                                        }
                                    }

                                    null -> {
                                        if(
                                            isCenterDragWithinBounds(
                                                topLeft,
                                                bottomRight,
                                                dragAmount,
                                                canvasWidth,
                                                canvasHeight
                                            )
                                        ){
                                            topLeft += dragAmount
                                            topRight += dragAmount
                                            bottomLeft += dragAmount
                                            bottomRight += dragAmount
                                        }

                                    }
                                }
                            } ,
                            onDragEnd = {
                                draggingCenter = false
                                draggingCorner = null
                            }
                        )
                    }
            ) {
                canvasWidth = size.width
                canvasHeight = size.height

                val rectSize =
                    Size(width = topRight.x - topLeft.x , height = bottomLeft.y - topLeft.y)

                drawRect(
                    Color.DarkGray.copy(alpha=0.5f) ,
                    size = rectSize ,
                    topLeft = topLeft
                )
                drawCircle(Color.White , center = topLeft , radius = 35f)
                drawCircle(Color.White , center = topRight , radius = 35f)
                drawCircle(Color.White , center = bottomLeft , radius = 35f)
                drawCircle(Color.White , center = bottomRight , radius = 35f)


                //helper lines
                val numVerticalLines = 3
                val verticalSize = size.width / 3
                repeat(numVerticalLines) { i ->
                    val startX = verticalSize * (i + 1)

                    drawLine(
                        Color.White ,
                        start = Offset(startX , 0f) ,
                        end = Offset(startX , size.height) ,
                        strokeWidth = 3f
                    )
                    drawLine(
                        Color.White ,
                        start = Offset(0f , startX) ,
                        end = Offset(size.width , startX) ,
                        strokeWidth = 3f
                    )
                }

            }

        }
        VerticalSpace()
        Button(
            onClick = {
                bitmap?.let {
                    val cropperBitmap = getCroppedBitmap(
                        bitmap,
                        Rect(topLeft,bottomRight),
                        canvasWidth,
                        canvasHeight
                    )
                    onCrop(cropperBitmap)
                }
            }
        ) {
            Text(
                "Crop Image" ,
                color = MaterialTheme.colorScheme.onBackground ,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }


}