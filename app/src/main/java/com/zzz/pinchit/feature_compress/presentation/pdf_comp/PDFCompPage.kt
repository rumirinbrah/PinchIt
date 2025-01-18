package com.zzz.pinchit.feature_compress.presentation.pdf_comp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.zzz.pinchit.feature_compress.presentation.pdf_comp.components.PDFPage
import java.io.File

@Composable
fun PDFCompPage(
    pdfCompressorViewModel: PDFCompressorViewModel,
    modifier: Modifier = Modifier
) {
    var currentPdf by remember { mutableStateOf<Uri?>(null) }
    var pdfBitmap by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {
        currentPdf = it
    }

    val infiniteAnimation = rememberInfiniteTransition()
    val progress = infiniteAnimation.animateValue(
        initialValue = 0,
        targetValue = 4,
        animationSpec = infiniteRepeatable(
            tween(2000),
            repeatMode = RepeatMode.Restart
        ),
        typeConverter = Int.VectorConverter,
        label = "dots"
    )

//    LaunchedEffect(currentPdf) {
//        if(currentPdf!=null){
//            pdfBitmap = pdfCompressorViewModel.pdfToBitmap(currentPdf!!)
//            //pdfCompressorViewModel.getBitmap(currentPdf!!)
//        }
//    }


    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("Work In Progress"+".".repeat(progress.value))


    }
}
/*
when{
            currentPdf==null->{
                Button(
                    onClick = {
                        launcher.launch(arrayOf("application/pdf"))
                    }
                ) {
                    Text(
                        "Choose PDF" ,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            currentPdf!=null->{
                LazyColumn(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(pdfBitmap){page->
                        PDFPage(
                            page
                        )
                    }
                }
            }
        }
 */