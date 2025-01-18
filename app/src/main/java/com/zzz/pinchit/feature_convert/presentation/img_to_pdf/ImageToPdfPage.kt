package com.zzz.pinchit.feature_convert.presentation.img_to_pdf

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.zzz.pinchit.core.presentation.util.ObserveAsEvents
import com.zzz.pinchit.feature_convert.presentation.img_to_pdf.components.RenameDialog
import kotlinx.coroutines.flow.Flow

@Composable
fun ImageToPdfPage(
    state: DocScannerUIState,
    events: Flow<DocScannerEvents>,
    onAction :(DocScannerActions)->Unit
) {
    var uris by remember{ mutableStateOf<List<Uri>>(emptyList()) }
    val context = LocalContext.current

    /*
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) {activityResult->
        if(activityResult.resultCode ==Activity.RESULT_OK){
            val result = GmsDocumentScanningResult.fromActivityResultIntent(activityResult.data)
            uris = result?.pages?.map {
                it.imageUri
            } ?: emptyList()
            result?.pdf?.let {pdf->
                //documentScannerViewModel.savePDFToDevice(pdf.uri)
            }
        }

    }

     */

    ObserveAsEvents(events) {event->
        when(event){
            DocScannerEvents.Success->{
                Toast.makeText(context , "PDF has been saved to the device" , Toast.LENGTH_SHORT).show()
            }
            is DocScannerEvents.Error->{
                Toast.makeText(context , event.error , Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp) ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when{
            state.showRenameDialog->{
                RenameDialog(
                    name = state.fileName,
                    onDone = {
                        onAction(DocScannerActions.OnSaveFile(it))
                    }
                )
            }
            !state.loading->{
                Button(
                    modifier = Modifier.padding(vertical = 16.dp),
                    onClick = {
                        onAction(DocScannerActions.OnGet)
                    }
                ) {
                    Text(
                        "Open Scanner",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            state.loading->{
                Box(Modifier.fillMaxWidth()){
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }

    }
}