package com.zzz.pinchit.feature_compress.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

class CompressedFileManager(
    private val context: Context
) {

    suspend fun saveCompressedImage(
        contentUri : Uri,
        fileName : String
    ){
        withContext(Dispatchers.IO){
            context
                .contentResolver
                .openInputStream(contentUri)
                ?.use {ipStream->
                    println("from file manager $fileName")
                    context.openFileOutput(fileName,Context.MODE_PRIVATE)
                        .use {
                            ipStream.copyTo(it)
                        }
                }
        }

    }
    suspend fun saveCompressedImage(
        byteArray: ByteArray,
        fileName: String
    ){
        println("Compressed Image saved $fileName")
        withContext(Dispatchers.IO){
            context
                .openFileOutput(fileName,Context.MODE_PRIVATE)
                .use {opStream->
                    opStream.write(byteArray)
                }
        }
    }


    private fun getFileNameWithExtension(name : String,uri : Uri) : String{
        val type=context.contentResolver.getType(uri)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(type)
        return "$name.$extension"
    }

}