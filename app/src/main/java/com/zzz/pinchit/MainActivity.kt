package com.zzz.pinchit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.zzz.pinchit.core.presentation.Navigation
import com.zzz.pinchit.core.presentation.util.Screen
import com.zzz.pinchit.core.presentation.util.getUriFromIntent
import com.zzz.pinchit.feature_compress.presentation.image_comp.CompImageAction
import com.zzz.pinchit.feature_compress.presentation.image_comp.ImageCompressorViewModel
import com.zzz.pinchit.ui.theme.PinchItTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val imageCompressorViewModel = remember{ ImageCompressorViewModel(this) }
            var actionFlag : Boolean = false


            //check for image intents
            if(intent?.action == Intent.ACTION_SEND && intent?.type?.startsWith("image/")==true){
                Log.d("action", "onCreate: Image received ${intent.type}")
                getUriFromIntent(intent)?.let {uri->
                    Log.d("action", "onCreate: Image is not null")
                    actionFlag = true
                    imageCompressorViewModel.onAction(CompImageAction.OnImageSelect(uri))
                }
            }


            PinchItTheme {
                val navController = rememberNavController()
                Navigation(
                    startDestination = if(actionFlag) Screen.ImageCompScreen else Screen.HomeScreen,
                    navController = navController,
                    imageCompressorViewModel = imageCompressorViewModel,
                )

            }
        }
    }
}
