package com.zzz.pinchit.feature_compress.presentation.image_comp

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.request.ImageRequest
import com.zzz.pinchit.core.presentation.util.ObserveAsEvents
import com.zzz.pinchit.feature_compress.presentation.image_comp.components.ImageQualityOptions
import com.zzz.pinchit.feature_compress.presentation.image_comp.components.PreviewImageWithTitle
import com.zzz.pinchit.feature_compress.presentation.util.VerticalSpace
import kotlinx.coroutines.flow.Flow
import kotlin.math.roundToInt

@Composable
fun ImageCompPage(
    state: CompImageUIState ,
    events : Flow<CompressImageEvents> ,
    onAction: (CompImageAction) -> Unit ,
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            onAction(CompImageAction.OnImageSelect(uri))
        }
    }
    ObserveAsEvents(events = events) { event->
        when(event){
            CompressImageEvents.OnSaveSuccess->{
                Toast.makeText(context , "Saved to gallery!!" , Toast.LENGTH_SHORT).show()
            }
            CompressImageEvents.OnError->{
                Toast.makeText(context , "Failed to save" , Toast.LENGTH_SHORT).show()
            }
        }
    }


    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VerticalSpace()
        when (state.phase) {


            CompressPhase.IMAGE_NOT_SELECTED -> {
                Button(
                    onClick = {
                        launcher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Text(
                        "Pick Image" ,
                        color = MaterialTheme.colorScheme.onBackground ,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                VerticalSpace()
                Text(
                    "(Note that PNGs will not be compressed)" ,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            CompressPhase.IMAGE_SELECTED -> {

                PreviewImageWithTitle(
                    model = ImageRequest.Builder(context)
                        .data(state.currentImage)
                        .crossfade(true)
                        .build() ,
                    title = "Selected Image"
                )
                VerticalSpace()
                ImageQualityOptions(
                    currentQuality = state.currentQuality ,
                    onQualityChange = {
                        onAction(CompImageAction.OnQualityChange(it))
                    }
                )
                VerticalSpace()
                Button(
                    enabled = !state.loading ,
                    onClick = {
                        onAction(CompImageAction.OnCompress)
                    }
                ) {
                    Text(
                        "Compress Image" ,
                        color = MaterialTheme.colorScheme.onBackground ,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            }

            CompressPhase.IMAGE_COMPRESSED -> {
                PreviewImageWithTitle(
                    model = ImageRequest.Builder(context)
                        .data(state.compressedImage)
                        .crossfade(true)
                        .build() ,
                    title = "Compressed Image"
                )
                val size = getFormattedImageSize(state.compressedImage?.size)
                Text("Size $size", style = MaterialTheme.typography.bodySmall)
                VerticalSpace()
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        enabled = !state.loading ,
                        onClick = {
                            onAction(CompImageAction.OnCancel)
                        } ,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(
                            "Cancel" ,
                            color = MaterialTheme.colorScheme.onBackground ,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Button(
                        enabled = !state.loading ,
                        onClick = {
                            onAction(CompImageAction.OnSave)
                        }
                    ) {
                        Text(
                            "Save Image" ,
                            color = MaterialTheme.colorScheme.onBackground ,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }


    }

}
private fun getFormattedImageSize(bytes : Int?):String{
    if(bytes==null){
        return "Unknown"
    }
    val megaBytes = bytes/1024000f
    when{
        megaBytes<=1->{
            val kiloBytes = (megaBytes*1000).roundToInt()
            return "$kiloBytes KB"
        }
        megaBytes>1->{
            val roundOff = (megaBytes*100).roundToInt()/100.0
            return "$roundOff MB"
        }
        else->{
            return ""
        }
    }
}



