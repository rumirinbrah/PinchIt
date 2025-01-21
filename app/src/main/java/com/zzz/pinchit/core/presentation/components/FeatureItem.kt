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
    FeatureItem(
        route = Screen.IMGToPDFScreen ,
        title = "Convert IMG to PDF" ,
        body = "Either use a text scanner to scan text files or upload from gallery" ,
        icon = R.drawable.jpg_to_pdf
    ),
    FeatureItem(
        route = Screen.PDFToIMGScreen ,
        title = "Convert PDF to IMG" ,
        body = "Easily convert PDF files back to images" ,
        icon = R.drawable.pdf_to_img
    )
)