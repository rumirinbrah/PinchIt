package com.zzz.pinchit.core.presentation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.navigation.navDeepLink
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.zzz.pinchit.core.presentation.util.DeepLink
import com.zzz.pinchit.core.presentation.util.Screen
import com.zzz.pinchit.core.presentation.util.hasExternalStoragePermission
import com.zzz.pinchit.core.presentation.util.requestExternalStoragePermission
import com.zzz.pinchit.feature_compress.presentation.image_comp.CompImageAction
import com.zzz.pinchit.feature_compress.presentation.image_comp.CompressImageEvents
import com.zzz.pinchit.feature_compress.presentation.image_comp.ImageCompPage
import com.zzz.pinchit.feature_compress.presentation.image_comp.ImageCompressorViewModel
import com.zzz.pinchit.feature_compress.presentation.pdf_comp.PDFCompPage
import com.zzz.pinchit.feature_compress.presentation.pdf_comp.PDFCompressorViewModel
import com.zzz.pinchit.feature_convert.presentation.img_to_pdf.DocScannerActions
import com.zzz.pinchit.feature_convert.presentation.img_to_pdf.DocScannerEvents
import com.zzz.pinchit.feature_convert.presentation.img_to_pdf.DocumentScannerViewModel
import com.zzz.pinchit.feature_convert.presentation.img_to_pdf.ImageToPdfPage
import com.zzz.pinchit.feature_convert.presentation.pdf_to_img.PdfToImagePage
import com.zzz.pinchit.feature_convert.presentation.pdf_to_img.PdfToImageViewModel
import com.zzz.pinchit.feature_convert.presentation.pdf_to_img.PdfToImgActions
import com.zzz.pinchit.feature_convert.presentation.pdf_to_img.PdfToImgEvents
import com.zzz.pinchit.feature_ocr.presentation.OCRPage
import com.zzz.pinchit.feature_ocr.presentation.OcrActions
import com.zzz.pinchit.feature_ocr.presentation.OcrViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun Navigation(
    startDestination : Screen,
    navController: NavHostController,
    imageCompressorViewModel: ImageCompressorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current


    //view models
    val pdfCompressorViewModel = remember { PDFCompressorViewModel(context) }
    val documentScannerViewModel = remember { DocumentScannerViewModel(context) }
    val pdfToImageViewModel = remember { PdfToImageViewModel(context) }
    val ocrViewModel = remember { OcrViewModel(context) }


    val imageUIState by imageCompressorViewModel.uiState.collectAsStateWithLifecycle()
    val imageEvents : Flow<CompressImageEvents> by lazy { imageCompressorViewModel.events }

    val docScannerUIState by documentScannerViewModel.uiState.collectAsStateWithLifecycle()
    val docEvents : Flow<DocScannerEvents> by lazy { documentScannerViewModel.events }

    val pdfToImgUIState by pdfToImageViewModel.uiState.collectAsStateWithLifecycle()
    val pdfToImgEvents : Flow<PdfToImgEvents> by lazy { pdfToImageViewModel.events }

    val ocrUIState by ocrViewModel.uiState.collectAsStateWithLifecycle()
    val ocrEvents : Flow<UIEvents> by lazy { ocrViewModel.events }

    //for doc scanner
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        if(activityResult.resultCode == Activity.RESULT_OK){
            val result = GmsDocumentScanningResult.fromActivityResultIntent(activityResult.data)
            result?.pdf?.let {pdf->
                documentScannerViewModel.onAction(DocScannerActions.OnUriReady(pdf.uri))
            }
        }
    }

    println("Permission ${hasExternalStoragePermission(context)}")
    if(!hasExternalStoragePermission(context)){
        requestExternalStoragePermission(context)
    }

    Scaffold(
        modifier = modifier,
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
                startDestination = startDestination,
                enterTransition = {
                    slideInHorizontally()
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = {
                            it
                        }
                    )
                }
            ) {
                //HOME
                composable<Screen.HomeScreen> {
                    HomePage(
                        onFeatureClick = {
                            navController.navigate(it)
                        }
                    )
                }
                //image compressor
                composable<Screen.ImageCompScreen>(
                    deepLinks = listOf(
                        navDeepLink {
                            uriPattern = DeepLink.IMAGE_COMPRESS
                        }
                    )
                ) {
                    BackHandler {
                        imageCompressorViewModel.onAction(CompImageAction.OnCancel)
                        navController.navigateUp()
                    }
                    ImageCompPage(
                        state = imageUIState,
                        events = imageEvents,
                        onAction = {action->
                            imageCompressorViewModel.onAction(action)
                        }
                    )
                }
                //pdf compressor
                composable<Screen.PDFCompScreen> {
                    BackHandler {
                        //pdfCompressorViewModel.renderer?.close()
                        navController.navigateUp()
                    }
                    PDFCompPage(pdfCompressorViewModel)
                }
                //Doc scanner
                composable<Screen.IMGToPDFScreen> {

                    ImageToPdfPage(
                        state = docScannerUIState,
                        events = docEvents,
                        onAction = {action->
                            when(action){
                                DocScannerActions.OnGet->{
                                    documentScannerViewModel.startScan(
                                        onSuccess = {
                                            launcher.launch(
                                                IntentSenderRequest.Builder(it).build()
                                            )
                                        }
                                    )
                                }
                                else->{
                                    documentScannerViewModel.onAction(action)
                                }
                            }
                        }
                    )
                }
                //PDF to img
                composable<Screen.PDFToIMGScreen> {
                    BackHandler {
                        navController.navigateUp()
                        pdfToImageViewModel.onAction(PdfToImgActions.OnClear)
                    }
                    PdfToImagePage(
                        state = pdfToImgUIState,
                        events = pdfToImgEvents,
                        onDone = {
                            navController.navigateUp()
                            pdfToImageViewModel.onAction(PdfToImgActions.OnClear)
                        },
                        onAction = {action->
                            pdfToImageViewModel.onAction(action)
                        }
                    )
                }
                //ocr
                composable<Screen.OCRScreen> {
                    BackHandler {
                        navController.navigateUp()
                        ocrViewModel.onAction(OcrActions.Reset)
                    }
                    OCRPage(
                        state = ocrUIState ,
                        events = ocrEvents,
                        onAction = {action->
                            ocrViewModel.onAction(action)
                        }

                    ) 
                }
            }
        }
    }
}