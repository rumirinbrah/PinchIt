package com.zzz.pinchit.feature_convert.presentation.img_to_pdf

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zzz.pinchit.core.presentation.components.CustomSnackbar
import com.zzz.pinchit.core.presentation.util.ObserveAsEvents
import com.zzz.pinchit.feature_convert.presentation.img_to_pdf.components.RenameDialog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun ImageToPdfPage(
    state: DocScannerUIState,
    events: Flow<DocScannerEvents>,
    onAction :(DocScannerActions)->Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }

    ObserveAsEvents(events) {event->
        when(event){
            DocScannerEvents.Success->{
                //Toast.makeText(context , "PDF has been saved to the device" , Toast.LENGTH_SHORT).show()
                scope.launch {
                    snackbarState.showSnackbar(
                        message = "PDF has been saved to the device",
                        actionLabel = "S",
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            is DocScannerEvents.Error->{
                //Toast.makeText(context , event.error , Toast.LENGTH_SHORT).show()
                scope.launch {
                    snackbarState.showSnackbar(
                        message = event.error,
                        actionLabel = "E",
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Box(){
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp) ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when{
                state.showRenameDialog->{
                    RenameDialog(
                        name = state.fileName,
                        onDone = {
                            onAction(DocScannerActions.OnSaveFile(it))
                        }
                    )
                }
                !state.loading->{
                    Button(
                        modifier = Modifier.padding(vertical = 16.dp),
                        onClick = {
                            onAction(DocScannerActions.OnGet)
                        }
                    ) {
                        Text(
                            "Open Scanner",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                state.loading->{
                    Box(Modifier.fillMaxWidth()){
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
            }

        }
        SnackbarHost(
            hostState = snackbarState,
            modifier = Modifier.align(Alignment.BottomCenter),
            snackbar = { CustomSnackbar(it) }
        )

    }

}