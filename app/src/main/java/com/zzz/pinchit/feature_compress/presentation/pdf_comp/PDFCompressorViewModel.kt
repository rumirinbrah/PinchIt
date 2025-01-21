package com.zzz.pinchit.feature_compress.presentation.pdf_comp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream

class PDFCompressorViewModel(
    private val context: Context
) : ViewModel() {

    private var renderer: PdfRenderer? = null

    init {
        Log.d("runtime", "PDFCompVM init")
    }

    suspend fun pdfToBitmap(pdfUri: Uri): List<Bitmap> {
        renderer?.close()
        return withContext(Dispatchers.IO) {
            context.contentResolver.openFileDescriptor(
                pdfUri ,
                "r"
            )?.use { descriptor ->
                with(PdfRenderer(descriptor)) {
                    renderer = this
                    return@withContext (0 until pageCount).map { index ->
                        async {
                            openPage(index).use { page ->
                                val bitmap = Bitmap.createBitmap(
                                    page.width ,
                                    page.height ,
                                    Bitmap.Config.ARGB_8888
                                )
                                val canvas = Canvas(bitmap).apply {
                                    drawColor(Color.WHITE)
                                    drawBitmap(bitmap , 0f , 0f , null)
                                }
                                page.render(
                                    bitmap ,
                                    null ,
                                    null ,
                                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                                )

                                bitmap
                            }
                        }.await()
                        //wait till everyone's done
                    }//.awaitAll()
                }
            }
            return@withContext emptyList()
        }
    }

    fun getBitmap(uri: Uri) {
        renderer?.close()
        val document = PdfDocument()
        viewModelScope.launch (Dispatchers.IO){
            context.contentResolver.openFileDescriptor(uri , "r")
                ?.use { descriptor ->
                    with(PdfRenderer(descriptor)) {
                        renderer = this
                        for (index in 0..<pageCount){
                            openPage(index).use { page->
                                val docPageInfo = PdfDocument.PageInfo.Builder(page.width,page.height,index).create()
                                val docPage = document.startPage(docPageInfo)
                                val bitmap = Bitmap.createBitmap(
                                    page.width ,
                                    page.height ,
                                    Bitmap.Config.ARGB_8888
                                )
//                                val canvas = Canvas(bitmap).apply {
//                                    drawColor(Color.WHITE)
//                                    drawBitmap(bitmap , 0f , 0f , null)
//                                }
                                page.render(
                                    bitmap ,
                                    null ,
                                    null ,
                                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                                )


                                docPage.canvas.setBitmap(bitmap)
                                document.finishPage(docPage)
                            }
                        }
                    }
                }
            try {
                document.writeTo(FileOutputStream("Documents/test.pdf"))
            }catch (e :Exception){
                e.printStackTrace()
            }
        }

    }

    private fun saveBitmapToDevice(pdfBitmap: List<Bitmap>) {


    }


}