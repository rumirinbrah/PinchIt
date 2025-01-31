package com.zzz.pinchit.feature_compress.presentation.image_comp

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

class ImageCompressorViewModel(
    private val context: Context
) : ViewModel() {

    private val compressImageState = MutableStateFlow(CompressImageState())

    private val _uiState = MutableStateFlow(CompImageUIState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<CompressImageEvents>()
    val events = _events.receiveAsFlow()

    init {
        Log.d("runtime" , "ImageCompVM init")
    }


    fun onAction(action: CompImageAction) {
        when (action) {
            //ui
            is CompImageAction.OnQualityChange -> {
                _uiState.update {
                    it.copy(currentQuality = action.quality)
                }
            }
            //ui
            is CompImageAction.OnImageSelect -> {
                _uiState.update {
                    it.copy(
                        phase = CompressPhase.IMAGE_SELECTED,
                        currentImage = action.uri
                    )
                }
            }
            //compress
            is CompImageAction.OnCompress -> {
                compressImage(_uiState.value.currentImage!!)
            }
            //save
            CompImageAction.OnSave -> {
                saveCompressedImage()
            }
            //cancel
            CompImageAction.OnCancel -> {
                resetStates()
            }
        }
    }

    //compress image
    private fun compressImage(
        contentUri: Uri ,
    ) {
        saveFileInfo(contentUri)
        _uiState.update {
            it.copy(loading = true)
        }
        viewModelScope.launch(Dispatchers.IO) {
            val mimeType = context.contentResolver.getType(contentUri)
            val inputBytes = context
                .contentResolver
                .openInputStream(contentUri)
                ?.use { inputStream ->
                    inputStream.readBytes()
                } ?: return@launch
            ensureActive()
            withContext(Dispatchers.Main) {
                val bitmap = BitmapFactory.decodeByteArray(inputBytes , 0 , inputBytes.size)
                ensureActive()

                val compressFormat = when (mimeType) {
                    "image/png" -> {
                        Bitmap.CompressFormat.PNG
                    }

                    "image/jpeg" -> {
                        Bitmap.CompressFormat.JPEG
                    }

                    "image/webp" -> {
                        if (Build.VERSION.SDK_INT >= 30) {
                            Bitmap.CompressFormat.WEBP_LOSSLESS
                        } else {
                            Bitmap.CompressFormat.WEBP
                        }
                    }

                    else -> {
                        Bitmap.CompressFormat.JPEG
                    }
                }
                var outputBytes: ByteArray

                ByteArrayOutputStream().use { opStream ->
                    bitmap.compress(
                        compressFormat ,
                        _uiState.value.currentQuality.quality ,
                        opStream
                    )
                    outputBytes = opStream.toByteArray()
                }

                _uiState.update {
                    it.copy(compressedImage = outputBytes , phase = CompressPhase.IMAGE_COMPRESSED)
                }
                println("Image compressed")


                _uiState.update {
                    it.copy(loading = false)
                }
            }
        }

    }

    //save IMAGE INFO
    private fun saveFileInfo(uri: Uri) {

        //dont make changes here mf
        val name = SimpleDateFormat("yyyy", Locale.getDefault()).format(System.currentTimeMillis())
        val type = context.contentResolver.getType(uri)
        compressImageState.update {
            it.copy(
                fileName = "$name${System.currentTimeMillis()}${Random.nextInt()}" ,
                fileType = type
            )
        }
    }

    //save to gallery
    private fun saveCompressedImage() {
        _uiState.update {
            it.copy(loading = true)
        }

        val imageState = compressImageState.value

        val timeStamp = System.currentTimeMillis()
        println("saving in progress")
        viewModelScope.launch(Dispatchers.IO) {

            // >android 10
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.MIME_TYPE , imageState.fileType)
                values.put(MediaStore.Images.Media.DATE_ADDED , timeStamp)
                //!!!ADD THE FILE NAME
                values.put(MediaStore.Images.Media.DISPLAY_NAME , imageState.fileName)
                values.put(MediaStore.Images.Media.DATE_TAKEN , timeStamp)
                values.put(MediaStore.Images.Media.RELATIVE_PATH , imageState.relativePath)
                values.put(MediaStore.Images.Media.IS_PENDING , true)
                val uri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI ,
                    values
                )
                uri?.let { fileUri ->
                    try {
                        context
                            .contentResolver
                            .openOutputStream(fileUri)
                            ?.use { opStream ->
                                opStream.write(_uiState.value.compressedImage)
                            }
                        values.put(MediaStore.Images.Media.IS_PENDING , false)
                        context.contentResolver.update(fileUri , values , null , null)

                        //SUCCESS
                        _events.send(CompressImageEvents.OnSaveSuccess)
                    } catch (e: Exception) {
                        Log.d(TAG , "saveBitmapToGallery: ${e.printStackTrace()}")
                        _events.send(CompressImageEvents.OnError)
                    }
                }
                //<Android 10
            } else {
                Log.d("image", "Step1 Android ver<10 ")

                val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

                val fileFolder = File(directory,"PinchIt")
                if(!fileFolder.exists()){
                    fileFolder.mkdirs()
                }
                Log.d("image", "Step2 File created ${fileFolder.path} ")
                //fileName.png
                val imageName = "${imageState.fileName}${imageState.fileExtension}"
                val file = File(fileFolder,imageName)
                //"${imageState.fileName!!}.jpg"

                try {
                    Log.d("image", "Step3 in try block ")
                    Log.d("image", "Step4 Write to OP stream successful")
                    val opUri = Uri.fromFile(file)
                    context.contentResolver.openOutputStream(opUri)
                        ?.use {opStream->
                            opStream.write(_uiState.value.compressedImage)
                        }
                    val mediaScan = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                    mediaScan.data = opUri
                    context.sendBroadcast(mediaScan)


                    Log.d("image", "Step5 Media scan intent sent!! ")
                    _events.send(CompressImageEvents.OnSaveSuccess)

                }catch (e :Exception){
                    Log.d("image", "Error In exception block ")
                    e.printStackTrace()
                    _events.send(CompressImageEvents.OnError)
                }


            }
            _uiState.update {
                it.copy(loading = false)
            }
        }
    }

    //reset
    private fun resetStates() {
        viewModelScope.launch {
            delay(300)
            _uiState.update {
                CompImageUIState()
            }
            compressImageState.update {
                CompressImageState()
            }
        }

    }


}



