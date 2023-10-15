package com.sofiyavolkovaproyects.texthunter.ui.hunter

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.sofiyavolkovaproyects.texthunter.ui.hunter.view.CameraView


@Composable
fun HunterScreen(modifier: Modifier = Modifier, viewModel: HunterViewModel = hiltViewModel()) {
    //val items by viewModel.uiState.collectAsStateWithLifecycle()
    HunterScreenView()
}

@Composable
fun HunterScreenView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        CameraView(
            onImageCaptured = { uri, fromGallery ->
                Log.d(TAG, "Image Uri Captured from Camera View in: " + uri.path)
//Todo : use the uri as needed

            }, onError = { imageCaptureException ->
                Log.d(TAG, "ERROR CAPTURE CAMERA: " + imageCaptureException.message)
            }
        )
    }
}