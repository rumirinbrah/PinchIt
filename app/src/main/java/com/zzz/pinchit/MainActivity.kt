package com.zzz.pinchit

import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.zzz.pinchit.core.presentation.Navigation
import com.zzz.pinchit.ui.theme.PinchItTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PinchItTheme {
                val navController = rememberNavController()
                Navigation(navController)

            }
        }
    }
}
