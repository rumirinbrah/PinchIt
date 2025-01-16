package com.zzz.pinchit.core.presentation

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.zzz.pinchit.core.presentation.util.ObserveAsEvents
import com.zzz.pinchit.core.presentation.util.Screen
import com.zzz.pinchit.feature_compress.CompressImageEvents
import com.zzz.pinchit.feature_compress.presentation.CompImageAction
import com.zzz.pinchit.feature_compress.presentation.CompressorViewModel
import com.zzz.pinchit.feature_compress.presentation.image_comp.ImageCompPage

@Composable
fun Navigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val compressorViewModel = remember{CompressorViewModel(context)}
    val imageUIState by compressorViewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(events = compressorViewModel.events) {event->
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
                            navController.navigate(Screen.ImageCompScreen)
                        }
                    )
                }
                composable<Screen.ImageCompScreen> {
                    BackHandler {
                        compressorViewModel.onAction(CompImageAction.OnCancel)
                        navController.navigateUp()
                    }
                    ImageCompPage(
                        state = imageUIState,
                        onAction = {action->
                            compressorViewModel.onAction(action)
                        }
                    )
                }
                composable<Screen.PDFCompScreen> {

                }
            }
        }
    }
}