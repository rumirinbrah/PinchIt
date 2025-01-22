package com.zzz.pinchit.feature_convert.presentation.pdf_to_img

interface PdfToImgEvents {

    data object OnSuccess : PdfToImgEvents
    data class OnError(val error :String) : PdfToImgEvents

}