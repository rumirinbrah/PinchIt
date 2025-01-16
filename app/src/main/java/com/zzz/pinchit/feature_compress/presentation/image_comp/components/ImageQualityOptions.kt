package com.zzz.pinchit.feature_compress.presentation.image_comp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zzz.pinchit.feature_compress.presentation.util.CompressQuality

@Composable
fun ImageQualityOptions(
    currentQuality: CompressQuality,
    onQualityChange : (CompressQuality)->Unit,
    modifier: Modifier = Modifier,
) {
    //val currentQuality = remember { mutableStateOf(CompressQuality.HIGH) }
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 8.dp),
            text = "Quality Options",
            fontWeight = FontWeight.Bold
        )
        QualityRadioButton(
            title = "High Quality",
            quality = CompressQuality.HIGH,
            selected = currentQuality == CompressQuality.HIGH,
            onClick = {
                onQualityChange(it)
            }
        )
        QualityRadioButton(
            title = "Medium Quality",
            quality = CompressQuality.MEDIUM,
            selected = currentQuality == CompressQuality.MEDIUM,
            onClick = {
                onQualityChange(it)
            }
        )
        QualityRadioButton(
            title = "Low Quality",
            quality = CompressQuality.LOW,
            selected = currentQuality == CompressQuality.LOW,
            onClick = {
                onQualityChange(it)
            }
        )
    }
}

@Composable
private fun QualityRadioButton(
    title : String,
    quality: CompressQuality,
    selected : Boolean,
    onClick:(CompressQuality)->Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RadioButton(
            onClick = {
                onClick(quality)
            },
            selected = selected
        )
        Text(title)
    }
}
@Preview(showBackground = true)
@Composable
private fun QualityOptPrev() {
    //ImageQualityOptions()
}