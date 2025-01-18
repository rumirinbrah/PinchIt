package com.zzz.pinchit.feature_convert.presentation.img_to_pdf

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ImageToPdfPage(

) {

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp) ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {

            }
        ) {
            Text(
                "Get",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}