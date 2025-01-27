package com.zzz.pinchit.feature_ocr.presentation

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.zzz.pinchit.core.presentation.UIEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OcrViewModel(
    private val context: Context
) :ViewModel(){

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val _uiState = MutableStateFlow(OcrUIState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<UIEvents>()
    val events = _events.receiveAsFlow()

    fun onAction(action: OcrActions){
        when(action){
            is OcrActions.OnUriSelect -> {
                _uiState.update {
                    it.copy(currentImage = action.uri, phase = OcrPhase.CROPPING)
                }
                //processImage()
            }
            is OcrActions.OnCrop->{
                _uiState.update {
                    it.copy(currentBitmap = action.croppedImage, phase = OcrPhase.PROCESSING)
                }
                processImage(action.croppedImage)
            }
            OcrActions.Reset -> {
                resetStates()
            }
        }
    }

    private fun processImage(bitmap: Bitmap){
        viewModelScope.launch(Dispatchers.Default) {
            val inputImage : InputImage
            try {
                //inputImage = InputImage.fromFilePath(context,_uiState.value.currentImage!!)

                inputImage = InputImage.fromBitmap(bitmap,0)
                recognizer.process(inputImage)
                    .addOnCompleteListener {result->
                        _uiState.update {
                            it.copy(
                                text = result.result.text,
                                phase = OcrPhase.DONE
                            )
                        }
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                    }
                _events.send(UIEvents.Success)
            }catch (e : Exception){
                e.printStackTrace()
                _events.send(UIEvents.Error("Error parsing image"))
            }
        }
    }
    private fun resetStates(){
        viewModelScope.launch {
            delay(300)
            _uiState.update {
                OcrUIState()
            }
        }
    }



}