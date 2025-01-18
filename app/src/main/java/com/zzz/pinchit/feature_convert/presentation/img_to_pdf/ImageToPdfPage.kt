package com.zzz.pinchit.feature_convert.presentation.img_to_pdf

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult

@Composable
fun ImageToPdfPage(
    documentScannerViewModel: DocumentScannerViewModel,
    onAction :(DocScannerActions)->Unit
) {
    var uris by remember{ mutableStateOf<List<Uri>>(emptyList()) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) {activityResult->
        if(activityResult.resultCode ==Activity.RESULT_OK){
            val result = GmsDocumentScanningResult.fromActivityResultIntent(activityResult.data)
            uris = result?.pages?.map {
                it.imageUri
            } ?: emptyList()
            result?.pdf?.let {pdf->
                documentScannerViewModel.savePDFToDevice(pdf.uri)
            }
        }

    }


    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp) ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
//                documentScannerViewModel.startScan(
//                    onSuccess = {
//                        println("success")
//                        launcher.launch(
//                            IntentSenderRequest.Builder(it)
//                                .build()
//                        )
//                    },
//                    onError = {
//                        println("error ${it.printStackTrace()}")
//                    }
//                )

            }
        ) {
            Text(
                "Get",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        uris.onEachIndexed { index,uri->
            println("Uri no $index")
        }
    }
}