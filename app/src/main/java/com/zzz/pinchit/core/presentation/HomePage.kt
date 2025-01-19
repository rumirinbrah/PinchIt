package com.zzz.pinchit.core.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zzz.pinchit.core.presentation.components.HomeFeatureItem
import com.zzz.pinchit.core.presentation.util.Screen

@Composable
fun HomePage(
    onFeatureClick : (Screen) ->Unit
) {

    Column(
        Modifier.fillMaxSize()
    ) {
        LazyColumn(
            Modifier.fillMaxWidth()
        ) {
            items(com.zzz.pinchit.core.presentation.components.items) { item->
                HomeFeatureItem(
                    featureItem = item ,
                    onClick = {
                        onFeatureClick(item.route)
                    }
                )
            }
        }

    }
}