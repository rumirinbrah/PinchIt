package com.zzz.pinchit.feature_convert.presentation.pdf_to_img

import android.net.Uri

sealed class PdfToImgActions {

    data class OnPdfSelect(val pdfUri: Uri) : PdfToImgActions()
    data object OnConvert : PdfToImgActions()
    data object OnCancel : PdfToImgActions()

}