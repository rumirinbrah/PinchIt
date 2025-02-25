package com.zzz.pinchit.feature_convert.presentation.img_to_pdf.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun RenameDialog(
    name: String ,
    onDone: (String) -> Unit ,
    modifier: Modifier = Modifier
) {
    val brush = Brush.linearGradient(
        colors = listOf(Color(0xFF0591DC) , Color(0xFF5CBFEE))
    )

    val fileName = remember { mutableStateOf(TextFieldValue(name, selection = TextRange(0,name.length))) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()

    }


    Dialog(
        onDismissRequest = {}
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .clip(Shapes().large)
                .background(MaterialTheme.colorScheme.background) ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                Modifier
                    .fillMaxWidth()
                    .background(brush)
                    .padding(vertical = 26.dp) ,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Rename PDF" ,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            TextField(
                modifier = Modifier
                    .focusRequester(focusRequester),
                value = fileName.value ,
                onValueChange = { fileName.value = it } ,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background ,
                    focusedContainerColor = MaterialTheme.colorScheme.surface ,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                ),
            )
            Button(
                onClick = { onDone(fileName.value.text) } ,
                modifier = Modifier
                    .padding(vertical = 8.dp) ,
                shape = Shapes().small
            ) {
                Text(
                    "Done" ,
                    style = MaterialTheme.typography.bodyMedium ,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RenameDialogPrev() {
    RenameDialog(name = "unknonwnigga" , onDone = {})
}



