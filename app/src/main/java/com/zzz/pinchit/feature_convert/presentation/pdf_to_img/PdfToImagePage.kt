package com.zzz.pinchit.feature_convert.presentation.pdf_to_img

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.zzz.pinchit.core.presentation.components.CustomProgressBar
import com.zzz.pinchit.core.presentation.util.ObserveAsEvents
import com.zzz.pinchit.core.presentation.util.isPdfSizeAllowed
import com.zzz.pinchit.feature_compress.presentation.util.VerticalSpace
import kotlinx.coroutines.flow.Flow

@Composable
fun PdfToImagePage(
    state : PdfToImgUIState,
    events: Flow<PdfToImgEvents>,
    onAction: (PdfToImgActions)->Unit,
    onDone :()->Unit,
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

    ObserveAsEvents(events = events) {event->
        when(event){
            is PdfToImgEvents.OnError->{
                Toast.makeText(context , event.error , Toast.LENGTH_SHORT).show()
            }
            PdfToImgEvents.OnSuccess->{
                Toast.makeText(context , "Images saved to gallery!!" , Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp) ,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        VerticalSpace()
        when(state.phase){
            PdfToImgPhase.IDLE -> {
                Button(
                    onClick = {
                        pdfPicker.launch(arrayOf("application/pdf"))
                    },
                ) {
                    Text(
                        "Choose PDF",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                VerticalSpace()
                Text("(Max file size is 20MB)",style = MaterialTheme.typography.bodySmall)
            }
            PdfToImgPhase.RENDER -> {
                LinearProgressIndicator(
                    strokeCap = StrokeCap.Butt
                )
                VerticalSpace(10.dp)
                Text("Rendering PDF please wait...")
            }
            PdfToImgPhase.READY -> {
                Button(
                    onClick = {
                        onAction(PdfToImgActions.OnConvert)
                    }
                ) {
                    Text(
                        "Convert",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                VerticalSpace(10.dp)
                Text("PDF is ready for conversion")
            }

            PdfToImgPhase.SAVING -> {
                CustomProgressBar(progress = state.saveProgress)
                VerticalSpace(10.dp)
                Text("Saving images to gallery please wait")
            }
            PdfToImgPhase.SAVED -> {
                Button(
                    onClick = onDone
                ) {
                    Text(
                        "Done",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            PdfToImgPhase.ERROR -> {
                Button(
                    onClick = onDone,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        "Close",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                VerticalSpace(10.dp)
                Text("An unknown error occurred")
            }
        }

    }


}