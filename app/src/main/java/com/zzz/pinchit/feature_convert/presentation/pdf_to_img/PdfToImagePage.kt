package com.zzz.pinchit.feature_convert.presentation.pdf_to_img

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.zzz.pinchit.core.presentation.util.isPdfSizeAllowed
import com.zzz.pinchit.feature_compress.presentation.util.VerticalSpace

@Composable
fun PdfToImagePage(
    state : PdfToImgUIState,
    onAction: (PdfToImgActions)->Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri->

        uri?.let {
            if(isPdfSizeAllowed(uri,context)){
                onAction(PdfToImgActions.OnPdfSelect(uri))
            }else{
                Toast.makeText(context , "PDF size must be less than 20MB" , Toast.LENGTH_SHORT).show()
            }

        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp) ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VerticalSpace()
        when(state.phase){
            PdfToImgPhase.IDLE -> {
                Button(
                    onClick = {
                        pdfPicker.launch(arrayOf("application/pdf"))
                    }
                ) {
                    Text(
                        "Choose PDF",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            PdfToImgPhase.RENDER -> {
                LinearProgressIndicator(
                    strokeCap = StrokeCap.Butt
                )
                VerticalSpace(10.dp)
                Text("Rendering PDF please wait...")
            }
            PdfToImgPhase.READY -> {

            }
        }

    }


}