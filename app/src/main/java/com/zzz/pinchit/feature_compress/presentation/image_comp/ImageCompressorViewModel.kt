package com.zzz.pinchit.feature_compress.presentation.image_comp

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
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
import java.io.FileOutputStream
import java.text.SimpleDateFormat
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

        val name = SimpleDateFormat("yyyy").format(System.currentTimeMillis())
        val type = context.contentResolver.getType(uri)
        compressImageState.update {
            it.copy(
                fileName = "$name${System.currentTimeMillis()}${Random.nextInt()}" ,
                fileType = type
            )
        }
    }

    private fun saveCompressedImage() {
        _uiState.update {
            it.copy(loading = true)
        }

        val imageState = compressImageState.value

        val timeStamp = System.currentTimeMillis()
        println("saving in progress")
        viewModelScope.launch(Dispatchers.IO) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.MIME_TYPE , imageState.fileType)
            values.put(MediaStore.Images.Media.DATE_ADDED , timeStamp)
            //!!!ADD THE FILE NAME
            values.put(MediaStore.Images.Media.DISPLAY_NAME , imageState.fileName)


            // >android 10
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
                                //bitmap.compress(Bitmap.CompressFormat.JPEG,50,opStream)
                            }
                        values.put(MediaStore.Images.Media.IS_PENDING , false)
                        context.contentResolver.update(fileUri , values , null , null)

                        //SUCCESS
                        //resetStates()
                        _events.send(CompressImageEvents.OnSaveSuccess)
                        //Toast.makeText(context , "Saved!" , Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.d(TAG , "saveBitmapToGallery: ${e.printStackTrace()}")
                        _events.send(CompressImageEvents.OnError)
                    }
                }
            } else {
                Log.d("image", "Step1 In else block ")

                val fileDir = "${Environment.DIRECTORY_PICTURES}/PinchIt"
                val fileName = imageState.fileName
                val imageFile = File(fileDir,fileName?:"unknown")

                Log.d("image", "Step2 File path is ${imageFile.absolutePath} ")
                Log.d("image", "Step2 File name is ${imageFile.name} ")

                values.put(MediaStore.Images.Media.RELATIVE_PATH , imageState.relativePath)

                val uri = MediaStore.Images.Media.getContentUri("external")//context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
                Log.d("image", "Step3 Inserting into content resolver")
                uri?.let {fileUri->
                    try {
                        Log.d("image", "Step4 In try block attempting to open OutputStream ")
                        context.contentResolver
                            .openOutputStream(fileUri)
                            ?.use {opStream->
                                opStream.write(_uiState.value.compressedImage)
                            }
                        Log.d("image", "Step5 Updating content resolver ")
                        context.contentResolver.update(fileUri,values,null,null)
                        Log.d("image", "Step6 Putting data in media store ")
                        values.put(MediaStore.Images.Media.DATA , imageFile.absolutePath)
                        _events.send(CompressImageEvents.OnSaveSuccess)

                    } catch (e: Exception) {
                        Log.d(TAG , "saveBitmapToGallery: ${e.printStackTrace()}")
                        _events.send(CompressImageEvents.OnError)
                    }
                }

            }
            _uiState.update {
                it.copy(loading = false)
            }
        }
    }
/*
val bitmap = BitmapFactory.decodeByteArray(_uiState.value.compressedImage,0,_uiState.value.compressedImage!!.size)
 MediaStore.Images.Media.insertImage(context.contentResolver,bitmap,fileName,"")
 */
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



