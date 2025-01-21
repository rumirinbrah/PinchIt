package com.zzz.pinchit.feature_convert.presentation.pdf_to_img

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PdfToImageViewModel(
    private val context: Context
) : ViewModel() {

    private var renderer : PdfRenderer? = null

    private val _uiState = MutableStateFlow(PdfToImgUIState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action : PdfToImgActions){
        when(action){
            //cancel
            PdfToImgActions.OnCancel -> {
                resetStates()
            }
            //convert
            PdfToImgActions.OnConvert -> {

            }
            //select
            is PdfToImgActions.OnPdfSelect -> {
                _uiState.update {
                    it.copy(uri = action.pdfUri, phase = PdfToImgPhase.RENDER)
                }
            }
        }
    }

    private fun renderPdf(){
        renderer?.close()
        val images : List<Bitmap> = emptyList()
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                context.contentResolver
                    .openFileDescriptor(_uiState.value.uri!!,"r")
                    ?.use { descriptor->
                        with(PdfRenderer(descriptor)){
                            renderer = this
                            (0 until pageCount).map { index->
                                async {
                                    openPage(index).use { page ->

                                    }
                                }.await()
                            }
                        }
                    }
                //here
            }
        }
    }

    private fun resetStates(){
        _uiState.update {
            PdfToImgUIState()
        }
    }
    private fun saveImagesToGallery(images : List<Bitmap>){

    }

}