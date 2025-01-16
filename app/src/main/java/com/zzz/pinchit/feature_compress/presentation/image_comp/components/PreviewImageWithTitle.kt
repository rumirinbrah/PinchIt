package com.zzz.pinchit.feature_compress.presentation.image_comp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun PreviewImageWithTitle(
    model : ImageRequest,
    title : String,
) {

    Column(
        Modifier.fillMaxWidth()
            .aspectRatio(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 5.dp)
            )
        AsyncImage(
            modifier = Modifier.fillMaxSize()
                .clip(Shapes().small)
                .background(MaterialTheme.colorScheme.surface),
            model = model,
            contentDescription = title,
            contentScale = ContentScale.Fit
        )
    }

}
