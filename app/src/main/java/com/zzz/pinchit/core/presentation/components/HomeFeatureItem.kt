package com.zzz.pinchit.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zzz.pinchit.R
import com.zzz.pinchit.core.presentation.util.Screen
import com.zzz.pinchit.ui.theme.PinchItTheme

@Composable
fun HomeFeatureItem(
    featureItem : FeatureItem,
    onClick : ()->Unit,
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.padding(8.dp) ,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row (
            Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column (
                Modifier.fillMaxHeight()
                    .weight(0.7f),
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    featureItem.title ,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    featureItem.body ,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Image(
                painter = painterResource(featureItem.icon) ,
                contentDescription = null,
                //tint = featureItem.iconTint
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun FeatureItemPrev() {
    PinchItTheme {
        HomeFeatureItem(
            featureItem = FeatureItem(
                route = Screen.HomeScreen ,
                title = "Compress Image" ,
                body = "Without wasting any quality" ,
                icon = R.drawable.jpg_icon,
                iconTint = Color.White
            )
        ) { }
    }
}