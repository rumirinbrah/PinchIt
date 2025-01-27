package com.zzz.pinchit.feature_ocr.presentation

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.request.ImageRequest
import com.zzz.pinchit.core.presentation.UIEvents
import com.zzz.pinchit.core.presentation.util.ObserveAsEvents
import com.zzz.pinchit.feature_compress.presentation.image_comp.components.PreviewImageWithTitle
import com.zzz.pinchit.feature_compress.presentation.util.VerticalSpace
import com.zzz.pinchit.feature_ocr.presentation.crop.ImageCropper
import kotlinx.coroutines.flow.Flow

@Composable
fun OCRPage(
    state: OcrUIState ,
    events: Flow<UIEvents> ,
    onAction: (OcrActions) -> Unit
) {

    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            onAction(OcrActions.OnUriSelect(uri))
        }
    }

    ObserveAsEvents(events) {event->
        when(event){
            UIEvents.Success->{
                Toast.makeText(context , "Text extracted successfully" , Toast.LENGTH_SHORT).show()
            }
            is UIEvents.Error->{
                Toast.makeText(context , event.error , Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp) ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VerticalSpace()
        when (state.phase) {
            OcrPhase.IDLE -> {
                Button(
                    onClick = {
                        launcher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Text(
                        "Upload Image" ,
                        color = MaterialTheme.colorScheme.onBackground ,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            OcrPhase.CROPPING->{
                ImageCropper(
                    imageUri = state.currentImage!!,
                    onCrop = {
                        onAction(OcrActions.OnCrop(it))
                    }
                )
            }
            OcrPhase.PROCESSING -> {
                LinearProgressIndicator()
                VerticalSpace(10.dp)
                Text("Processing image please wait...")
            }

            OcrPhase.DONE -> {
                PreviewImageWithTitle(
                    model = ImageRequest.Builder(context)
                        .data(state.currentBitmap)
                        .crossfade(true)
                        .build() ,
                    title = "Selected Image"
                )
                VerticalSpace()
                Text(
                    text = state.text ?: "" ,
                    textAlign = TextAlign.Center ,
                    style = MaterialTheme.typography.bodySmall ,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surface ,
                            Shapes().small
                        )
                        .padding(vertical = 8.dp)
                )
                VerticalSpace()
                Button(
                    onClick = {
                        clipboard.setText(AnnotatedString(state.text ?: ""))
                        Toast.makeText(context , "Copied!" , Toast.LENGTH_SHORT).show()
                        println(state.text)
                    }
                ) {
                    Text(
                        "Copy to clipboard" ,
                        color = MaterialTheme.colorScheme.onBackground ,
                        style = MaterialTheme.typography.bodyMedium ,
                    )
                }
            }
        }

    }
}