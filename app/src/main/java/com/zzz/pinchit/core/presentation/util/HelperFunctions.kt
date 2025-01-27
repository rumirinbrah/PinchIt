package com.zzz.pinchit.core.presentation.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Parcelable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import kotlin.math.min
import kotlin.math.roundToInt

fun getUriFromIntent(intent: Intent) : Uri?{
    val uri = (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)
    return uri
}

fun isPdfSizeAllowed(uri: Uri,context: Context):Boolean{
    val length = context.contentResolver.openAssetFileDescriptor(uri,"r")
        ?.use {
            it.length
        }
    return if(length==null){
        false
    }
    else{
        length < 21000000
    }
}

//---- for OCR image cropper -----

fun isDragWithinBounds(
    point: Offset ,
    drag: Offset ,
    canvasWidth: Float ,
    canvasHeight: Float
): Boolean {
    val isHorizontalAllowed = ((point.x + drag.x) < canvasWidth) && ((point.x + drag.x) > 0f)
    val isVerticalAllowed = ((point.y + drag.y) < canvasHeight) && ((point.y + drag.y) > 0f)
    return (isVerticalAllowed == isHorizontalAllowed)
}
fun isCenterDragWithinBounds(
    top : Offset ,
    bottom : Offset ,
    drag: Offset ,
    canvasWidth: Float ,
    canvasHeight: Float
) : Boolean{
    val horizontal = (bottom.x + drag.x)<canvasWidth && top.x + drag.x > 0f
    val vertical = (bottom.y + drag.y)<canvasHeight && top.y + drag.y > 0f
    return horizontal == vertical
}
fun getCroppedBitmap(
    image: Bitmap ,
    cropRect: Rect ,
    canvasWidth: Float ,
    canvasHeight: Float ,
): Bitmap {
    val bitmapWidth = image.width.toFloat()
    val bitmapHeight = image.height.toFloat()

    val widthRatio = canvasWidth / bitmapWidth
    val heightRatio = canvasHeight / bitmapHeight

    val scaleFactor = min(widthRatio , heightRatio)
    val displayImageWidth = bitmapWidth * scaleFactor
    val displayImageHeight = bitmapHeight * scaleFactor

    val offsetX = (canvasWidth - displayImageWidth) / 2
    val offsetY = (canvasHeight - displayImageHeight) / 2

    val cropLeft =
        ((cropRect.left - offsetX) / scaleFactor).roundToInt().coerceIn(0 , bitmapWidth.toInt())
    val cropTop =
        ((cropRect.top - offsetY) / scaleFactor).roundToInt().coerceIn(0 , bitmapHeight.toInt())
    val cropRight =
        ((cropRect.right - offsetX) / scaleFactor).roundToInt().coerceIn(0 , bitmapWidth.toInt())
    val cropBottom =
        ((cropRect.bottom - offsetY) / scaleFactor).roundToInt().coerceIn(0 , bitmapHeight.toInt())

    val cropWidth = (cropRight - cropLeft).coerceAtLeast(50)
    val cropHeight = (cropBottom - cropTop).coerceAtLeast(50)

    return Bitmap.createBitmap(
        image ,
        cropLeft ,
        cropTop ,
        cropWidth ,
        cropHeight
    )

}

fun getBitmapFromUri(context: Context , uri: Uri): Bitmap? {
    val inputBytes = context.contentResolver.openInputStream(uri)
        ?.use {
            it.readBytes()
        }
    return if (inputBytes == null) {
        null
    } else {
        val bitmap = BitmapFactory.decodeByteArray(inputBytes , 0 , inputBytes.size)
        bitmap
    }
}

fun Offset.isNear(point: Offset , threshold: Float = 50f): Boolean {
    return (this - point).getDistance() <= threshold
}
//----- image crop end ----







