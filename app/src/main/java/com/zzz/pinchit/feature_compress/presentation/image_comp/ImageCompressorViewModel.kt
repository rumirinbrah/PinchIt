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
import com.zzz.pinchit.feature_compress.CompressImageEvents
import com.zzz.pinchit.feature_compress.presentation.CompImageAction
import com.zzz.pinchit.feature_compress.presentation.CompressImageState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
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
import kotlin.random.Random

class ImageCompressorViewModel(
    private val context: Context
) : ViewModel() {

    private val compressImageState = MutableStateFlow(CompressImageState())

    private val _uiState = MutableStateFlow(CompImageUIState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<CompressImageEvents>()
    val events = _events.receiveAsFlow()

    fun onAction(action: CompImageAction) {
        when (action) {
            //ui
            is CompImageAction.OnQualityChange -> {
                _uiState.update {
                    it.copy(currentQuality = action.quality)
                }
            }
            //ui
            CompImageAction.OnImageSelect ->{
                _uiState.update {
                    it.copy(phase = CompressPhase.IMAGE_SELECTED)
                }
            }
            //compress
            is CompImageAction.OnCompress -> {
                compressImage(action.uri)
            }
            //save
            CompImageAction.OnSave -> {
                saveCompressedImage()
            }
            //cancel
            CompImageAction.OnCancel ->{
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
                    it.copy(compressedImage = outputBytes, phase = CompressPhase.IMAGE_COMPRESSED)
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
                val fileFolder = File("${Environment.getExternalStorageDirectory()}/PinchIt")
                if (!fileFolder.exists()) {
                    fileFolder.mkdirs()
                }

                val imageFile = File(fileFolder , imageState.fileName!!)
                try {
                    context.openFileOutput(imageFile.absolutePath , Context.MODE_PRIVATE)
                        ?.use { opStream ->
                            opStream.write(_uiState.value.compressedImage)
                        }

                    values.put(MediaStore.Images.Media.DATA , imageFile.absolutePath)
                    context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI ,
                        values
                    )

                    //SUCCESS
                    _events.send(CompressImageEvents.OnSaveSuccess)
                    //resetStates()
                    //Toast.makeText(context , "Saved!" , Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e(TAG , "saveBitmapToGallery: ${e.printStackTrace()}")
                    _events.send(CompressImageEvents.OnError)
                }
            }
            _uiState.update {
                it.copy(loading = false)
            }
        }
    }
    private fun resetStates(){
        _uiState.update {
            CompImageUIState()
        }
        compressImageState.update {
            CompressImageState()
        }
    }


}



