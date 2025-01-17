package com.zzz.pinchit.core.presentation.components

import androidx.annotation.DrawableRes
import com.zzz.pinchit.R
import com.zzz.pinchit.core.presentation.util.Screen

data class FeatureItem(
    val route : Screen,
    val title : String,
    val body : String,
    @DrawableRes val icon : Int
)
val items = listOf(
    FeatureItem(
        route = Screen.ImageCompScreen ,
        title = "Compress Images" ,
        body = "Easily compress images by about 80%" ,
        icon = R.drawable.jpg_icon
    ),
    FeatureItem(
        route = Screen.PDFCompScreen ,
        title = "Compress PDFs" ,
        body = "Reduce PDF size without compromising much quality" ,
        icon = R.drawable.pdf_icon
    ),
)