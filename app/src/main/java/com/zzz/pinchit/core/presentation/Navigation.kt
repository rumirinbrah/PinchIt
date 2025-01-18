package com.zzz.pinchit.core.presentation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.zzz.pinchit.core.presentation.util.ObserveAsEvents
import com.zzz.pinchit.core.presentation.util.Screen
import com.zzz.pinchit.feature_compress.CompressImageEvents
import com.zzz.pinchit.feature_compress.presentation.CompImageAction
import com.zzz.pinchit.feature_compress.presentation.image_comp.ImageCompressorViewModel
import com.zzz.pinchit.feature_compress.presentation.image_comp.ImageCompPage
import com.zzz.pinchit.feature_compress.presentation.pdf_comp.PDFCompPage
import com.zzz.pinchit.feature_compress.presentation.pdf_comp.PDFCompressorViewModel
import com.zzz.pinchit.feature_convert.presentation.img_to_pdf.DocumentScannerViewModel
import com.zzz.pinchit.feature_convert.presentation.img_to_pdf.ImageToPdfPage

@Composable
fun Navigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        if(activityResult.resultCode == Activity.RESULT_OK){
            val result = GmsDocumentScanningResult.fromActivityResultIntent(activityResult.data)
            result?.pdf?.let {

            }
        }
    }

    //view models
    val imageCompressorViewModel = remember{ ImageCompressorViewModel(context) }
    val pdfCompressorViewModel = remember { PDFCompressorViewModel(context) }
    val documentScannerViewModel = remember { DocumentScannerViewModel(context) }


    val imageUIState by imageCompressorViewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(events = imageCompressorViewModel.events) { event->
        when(event){
            CompressImageEvents.OnSaveSuccess->{
                Toast.makeText(context , "Saved!!" , Toast.LENGTH_SHORT).show()
            }
            CompressImageEvents.OnError->{
                Toast.makeText(context , "Failed to save" , Toast.LENGTH_SHORT).show()
            }
        }
    }
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ){
                Text(
                    "Pinch It" ,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.HomeScreen
            ) {
                composable<Screen.HomeScreen> {
                    HomePage(
                        onFeatureClick = {
                            navController.navigate(it)
                        }
                    )
                }
                composable<Screen.ImageCompScreen> {
                    BackHandler {
                        imageCompressorViewModel.onAction(CompImageAction.OnCancel)
                        navController.navigateUp()
                    }
                    ImageCompPage(
                        state = imageUIState,
                        onAction = {action->
                            imageCompressorViewModel.onAction(action)
                        }
                    )
                }
                composable<Screen.PDFCompScreen> {
                    BackHandler {
                        //pdfCompressorViewModel.renderer?.close()
                        navController.navigateUp()
                    }
                    PDFCompPage(pdfCompressorViewModel)
                }
                composable<Screen.IMGToPDFScreen> {
                    ImageToPdfPage(
                        documentScannerViewModel
                    )
                }
            }
        }
    }
}