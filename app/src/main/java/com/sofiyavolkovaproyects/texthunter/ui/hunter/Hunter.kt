package com.sofiyavolkovaproyects.texthunter.ui.hunter

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import com.sofiyavolkovaproyects.texthunter.ui.hunter.view.CameraView

@Composable
fun HunterScreen(icon: Int, description: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = description,
            tint = Color(Color.Blue.toArgb())
        )
        Text(text = "Home", color = Color.Black)

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