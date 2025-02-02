package com.zzz.pinchit.feature_convert.presentation.pdf_to_img

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

class PdfToImageViewModel(
    private val context: Context
) : ViewModel() {

    private var renderer: PdfRenderer? = null

    private val _uiState = MutableStateFlow(PdfToImgUIState())
    val uiState = _uiState.asStateFlow()

    private val pdfToImgState = MutableStateFlow(PdfToImgState())

    private val _events = Channel<PdfToImgEvents>()
    val events = _events.receiveAsFlow()


    fun onAction(action: PdfToImgActions) {
        when (action) {
            //cancel
            PdfToImgActions.OnCancel -> {
                resetStates()
            }
            //convert
            PdfToImgActions.OnConvert -> {
                //saveImagesToGalleryUsingMediaStore(pdfToImgState.value.images)
                saveImageCallResolver()
            }

            PdfToImgActions.OnClear -> {
                resetStates()
            }
            //select
            is PdfToImgActions.OnPdfSelect -> {
                _uiState.update {
                    it.copy(uri = action.pdfUri , phase = PdfToImgPhase.RENDER)
                }
                renderPdf()
            }
        }
    }

    //render
    private fun renderPdf() {

        renderer?.close()
        //val images : List<Bitmap> = emptyList()
        viewModelScope.launch {
            //do we need this explicitly?
            withContext(Dispatchers.IO) {
                context.contentResolver
                    .openFileDescriptor(_uiState.value.uri!! , "r")
                    ?.use { descriptor ->
                        try {
                            with(PdfRenderer(descriptor)) {
                                renderer = this
                                val images = (0 until pageCount).map { index ->
                                    async {
                                        openPage(index).use { page ->

                                            //custom scaling for pages
                                            val scaleFactor = 2.5f
                                            val scaledWidth = (scaleFactor * page.width).toInt()
                                            val scaledHeight = (scaleFactor * page.height).toInt()

                                            val bitmap = Bitmap.createBitmap(
                                                scaledWidth ,
                                                scaledHeight ,
                                                Bitmap.Config.ARGB_8888
                                            )
                                            val matrix = Matrix()
                                            matrix.setScale(scaleFactor , scaleFactor)

                                            val canvas = Canvas(bitmap).apply {
                                                drawColor(Color.WHITE)
                                                drawBitmap(bitmap , 0f , 0f , null)
                                            }

                                            page.render(
                                                bitmap ,
                                                null ,
                                                matrix ,
                                                PdfRenderer.Page.RENDER_MODE_FOR_PRINT
                                            )
                                            bitmap
                                        }
                                    }.await()
                                }
                                pdfToImgState.update { it.copy(images = images) }
                                Log.d("render" , "renderPdf: Num of img ${images.size} ")
                                _uiState.update { it.copy(phase = PdfToImgPhase.READY) }
                            }
                        } catch (e: Exception) {
                            _events.send(PdfToImgEvents.OnError("PDF format not supported"))
                            e.printStackTrace()
                        }

                    }
                //here
            }
        }
    }

    private fun saveImageCallResolver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImagesToGalleryUsingMediaStore(pdfToImgState.value.images)
        } else {
            saveImagesToGalleryUsingFiles(pdfToImgState.value.images)
        }
    }

    //save to gallery Android<10
    private fun saveImagesToGalleryUsingFiles(images: List<Bitmap>) {
        val size = images.size * 1f
        var saveSuccessful = true
        _uiState.update { it.copy(phase = PdfToImgPhase.SAVING) }
        viewModelScope.launch(Dispatchers.IO) {
            val directory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val fileFolder = File(directory , "PinchIt")
            if (!fileFolder.exists()) {
                fileFolder.mkdirs()
            }
            images.onEachIndexed { index , bitmap ->
                _uiState.update {
                    it.copy(saveProgress = index / size)
                }

                val name = getFileName()
                val file = File(fileFolder , "$name.jpg")

                try {

                    val fileUri = Uri.fromFile(file)
                    context.contentResolver.openOutputStream(fileUri)
                        ?.use { opStream ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG , 100 , opStream)
                        }
                    //notify about new media
                    val mediaScannerIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                    mediaScannerIntent.data = fileUri
                    context.sendBroadcast(mediaScannerIntent)
                } catch (e: Exception) {
                    _events.send(PdfToImgEvents.OnError("Failed to save images to gallery"))
                    saveSuccessful = false
                    e.printStackTrace()
                    return@launch
                }
            }

            Log.d("render" , "saveImagesToGallery: $saveSuccessful ")
            if (saveSuccessful) {
                _events.send(PdfToImgEvents.OnSuccess)
                _uiState.update {
                    it.copy(phase = PdfToImgPhase.SAVED , uri = null)
                }
            } else {
                _uiState.update {
                    it.copy(phase = PdfToImgPhase.ERROR , uri = null)
                }
            }

        }

    }

    //save to gallery Android 10 +
    private fun saveImagesToGalleryUsingMediaStore(images: List<Bitmap>) {
        val size = images.size * 1f
        var saveSuccessful = true
        _uiState.update { it.copy(phase = PdfToImgPhase.SAVING) }
        viewModelScope.launch(Dispatchers.IO) {
            images.onEachIndexed { index , bitmap ->
                _uiState.update {
                    it.copy(saveProgress = index / size)
                }
                val timeStamp = System.currentTimeMillis()
                val imageState = pdfToImgState.value
                val name = getFileName()
                val values = ContentValues()
                values.put(MediaStore.Images.Media.MIME_TYPE , imageState.mimeType)
                values.put(MediaStore.Images.Media.DATE_ADDED , timeStamp)
                values.put(MediaStore.Images.Media.DISPLAY_NAME , name)

                values.put(MediaStore.Images.Media.RELATIVE_PATH , imageState.relativePath)
                values.put(MediaStore.Images.Media.IS_PENDING , true)

                val uri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI ,
                    values
                )
                uri?.let { fileUri ->
                    try {
                        context.contentResolver
                            .openOutputStream(fileUri)
                            ?.use { opStream ->
                                //changes here
                                bitmap.compress(Bitmap.CompressFormat.JPEG , 100 , opStream)
                            }
                        values.clear()
                        values.put(MediaStore.Images.Media.IS_PENDING , false)
                        context.contentResolver.update(fileUri , values , null , null)
                    } catch (e: Exception) {
                        _events.send(PdfToImgEvents.OnError("Failed to save images to gallery"))
                        saveSuccessful = false
                        e.printStackTrace()
                        return@launch
                    }
                }
            }
            Log.d("render" , "saveImagesToGallery: $saveSuccessful ")
            if (saveSuccessful) {
                _events.send(PdfToImgEvents.OnSuccess)
                _uiState.update {
                    it.copy(phase = PdfToImgPhase.SAVED , uri = null)
                }
            } else {
                _uiState.update {
                    it.copy(phase = PdfToImgPhase.ERROR , uri = null)
                }
            }

        }


    }

    private fun getFileName(): String {
        val timeStamp = System.currentTimeMillis()
        val date = SimpleDateFormat("yyyy" , Locale.getDefault()).format(timeStamp)
        val fileName = "$date$timeStamp${Random.nextInt()}"
        return fileName
    }


    private fun resetStates() {
        viewModelScope.launch {
            delay(300)
            _uiState.update {
                PdfToImgUIState()
            }
            pdfToImgState.update {
                it.copy(images = emptyList())
            }
        }

    }


}