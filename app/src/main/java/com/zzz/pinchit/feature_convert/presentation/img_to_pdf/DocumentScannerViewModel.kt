package com.zzz.pinchit.feature_convert.presentation.img_to_pdf

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import kotlin.random.Random

class DocumentScannerViewModel(
    private val context: Context
) : ViewModel() {

    private val docState = MutableStateFlow(DocScannerState())

    private val _uiState = MutableStateFlow(DocScannerUIState())
    val uiState = _uiState.asStateFlow()


    private val scannerOptions = GmsDocumentScannerOptions.Builder()
        .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
        .setGalleryImportAllowed(true)
        .setPageLimit(20)
        .setResultFormats(
            GmsDocumentScannerOptions.RESULT_FORMAT_JPEG ,
            GmsDocumentScannerOptions.RESULT_FORMAT_PDF
        )
        .build()
    private val documentScanner: GmsDocumentScanner by lazy {
        GmsDocumentScanning.getClient(scannerOptions)
    }

    fun onAction(action: DocScannerActions) {
        when (action) {
            DocScannerActions.OnGet -> {
                Unit
            }

            is DocScannerActions.OnUriReady -> {
                viewModelScope.launch {
                    docState.update {
                        it.copy(
                            pdfUri = action.uri ,
                        )
                    }
                    delay(2000)
                    showDialog()
                }

            }

            is DocScannerActions.OnSaveFile -> {
                hideDialog()
                savePDFToDevice(action.name)
            }
        }
    }

    private fun hideDialog() {
        _uiState.update {
            it.copy(
                showRenameDialog = false
            )
        }
    }

    private fun showDialog() {
        _uiState.update {
            it.copy(
                showRenameDialog = true
            )
        }
    }


    fun startScan(
        onSuccess: (IntentSender) -> Unit ,
        onError: (Exception) -> Unit ,
    ) {
        documentScanner.getStartScanIntent(context as Activity)
            .addOnSuccessListener {
                onSuccess(it)
            }
            .addOnFailureListener {
                onError(it)
            }
    }

    private fun saveFileInfo() {
        val date = SimpleDateFormat("yyyy").format(System.currentTimeMillis())
        val name = "$date${System.currentTimeMillis()}${Random.nextInt()}"

        docState.update {
            it.copy(
                fileName = name ,
            )
        }
        _uiState.update {
            it.copy(fileName = name)
        }
    }

    fun savePDFToDevice(fileName: String) {
        Log.d("doc" , "Saving to device")

        //val idk = MediaStore.Files.FileColumns.DOCUMENT_ID
        saveFileInfo()
        val timeStamp = System.currentTimeMillis()
        val values = ContentValues()
        val docState = docState.value
        var byteArray: ByteArray? = null

        viewModelScope.launch(Dispatchers.IO) {
            context.contentResolver
                .openInputStream(docState.pdfUri!!)
                ?.use { ipStream ->
                    byteArray = ipStream.readBytes()
                }
            Log.d("doc" , "Doc state ${docState.fileType} path ${docState.relativePath} ")

            values.put(MediaStore.Files.FileColumns.MIME_TYPE , docState.fileType)
            values.put(MediaStore.Files.FileColumns.DATE_ADDED , timeStamp)
            values.put(MediaStore.Files.FileColumns.DISPLAY_NAME , fileName)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                values.put(MediaStore.Files.FileColumns.DATE_TAKEN , timeStamp)
                values.put(MediaStore.Files.FileColumns.RELATIVE_PATH , docState.relativePath)
                values.put(MediaStore.Files.FileColumns.IS_PENDING , true)

                val uri = context.contentResolver.insert(
                    MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL) ,
                    values
                )
                uri?.let {
                    Log.d("doc" , "uri is not null")
                    try {
                        context.contentResolver
                            .openOutputStream(uri)
                            ?.use { opStream ->
                                opStream.write(byteArray)
                            }
                        values.put(MediaStore.Files.FileColumns.IS_PENDING , false)
                        context.contentResolver.update(uri , values , null , null)
                        Log.d("doc" , "Successful!!!")
                    } catch (e: Exception) {
                        Log.e("doc" , "savePDFToDevice: Failed" , e)
                    }
                }

            }

        }
    }


}
