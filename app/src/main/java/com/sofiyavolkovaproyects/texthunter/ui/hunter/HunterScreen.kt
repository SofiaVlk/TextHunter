package com.sofiyavolkovaproyects.texthunter.ui.hunter

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.sofiyavolkovaproyects.texthunter.ui.components.RequiresSimplePermission
import com.sofiyavolkovaproyects.texthunter.ui.hunter.view.CameraView
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavigationParams.EditText
import java.io.IOException

@Composable
fun HunterScreen(modifier: Modifier = Modifier, viewModel: HunterViewModel = hiltViewModel(), navController: NavController) {
    //val items by viewModel.uiState.collectAsStateWithLifecycle()
    RequiresSimplePermission {
        HunterScreenView(modifier = modifier, navController = navController)
    }
}

@Composable
fun HunterScreenView(modifier: Modifier = Modifier, navController: NavController) {
    val context = LocalContext.current
    // When using Latin script library
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

   // var openAlertDialog by remember { mutableStateOf(false) }
    var textCaptured by remember {
        mutableStateOf("")
    }

    /*
    when {
        // ...
        openAlertDialog -> {
            AlertDialogTextCaptured(
                onDismissRequest = { openAlertDialog = false },
                onConfirmation = {
                    openAlertDialog = false
                    println("Confirmation registered") // Add logic here to handle confirmation.
                },
                dialogTitle = "Texto Capturado",
                dialogText = textCaptured,
            )
        }
    }

     */


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        CameraView(
            onImageCaptured = { uri, fromGallery ->
                Log.d(TAG, "Image Uri Captured from Camera View in: " + uri.path)
                val image: InputImage

                try {
                    image = InputImage.fromFilePath(context, uri)

                    val result = recognizer.process(image)
                        .addOnSuccessListener { visionText ->
                            // Task completed successfully
                            // ...

                            textCaptured = visionText.text
                            navController.navigate(EditText.createNavTextRoute(visionText.text))
                            //openAlertDialog = true
                        }
                        .addOnFailureListener { e ->
                            // Task failed with an exception
                            // ...
                        }

                } catch (e: IOException) {
                    e.printStackTrace()
                }


//Todo : use the uri as needed

            }, onError = { imageCaptureException ->
                Log.d(TAG, "ERROR CAPTURE CAMERA: " + imageCaptureException.message)
            }
        )
    }
}

@Composable
private fun AlertDialogTextCaptured(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
) {
    AlertDialog(
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}