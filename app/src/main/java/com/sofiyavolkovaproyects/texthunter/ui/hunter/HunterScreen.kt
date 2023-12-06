package com.sofiyavolkovaproyects.texthunter.ui.hunter

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sofiyavolkovaproyects.texthunter.R.drawable
import com.sofiyavolkovaproyects.texthunter.R.string
import com.sofiyavolkovaproyects.texthunter.ui.components.CustomCircularProgressBar
import com.sofiyavolkovaproyects.texthunter.ui.components.InfoMessage
import com.sofiyavolkovaproyects.texthunter.ui.components.RequiresSimplePermission
import com.sofiyavolkovaproyects.texthunter.ui.hunter.HunterUiAction.OnNavigate
import com.sofiyavolkovaproyects.texthunter.ui.hunter.view.CameraView
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavigationParams.EditText

@Composable
fun HunterScreen(
    modifier: Modifier = Modifier,
    viewModel: HunterViewModel = hiltViewModel(),
    navigateTo: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RequiresSimplePermission {
        HunterScreenView(
            modifier = modifier,
            uiState = uiState,
            navigateTo = navigateTo,
            onActionEvent = { action ->
                viewModel.handlerAction(action)
            }
        )
    }
}

@Composable
fun HunterScreenView(
    modifier: Modifier = Modifier,
    uiState: HunterUiState,
    onActionEvent: (HunterUiAction) -> Unit,
    navigateTo: (String) -> Unit
) {
    val context = LocalContext.current
    val imageCaptureError = stringResource(string.th_hunter_screen_image_capture_error)
    val imageUri = stringResource(string.th_hunter_screen_image_uri)
    when (uiState) {
        HunterUiState.Loading -> CustomCircularProgressBar()
        is HunterUiState.NavigateToEdit -> {
            LaunchedEffect(true) {
                onActionEvent(OnNavigate)
                navigateTo(EditText.createNavTextRoute(uiState.text))
            }
        }
        HunterUiState.Initial -> CameraView(
            onImageCaptured = { uri, _ ->
                Log.d(TAG, imageUri + uri.path)
                onActionEvent(HunterUiAction.OnCapturedButtonClick)
                try {
                    context.textRecognizerProcess(uri)
                        .addOnSuccessListener {
                            onActionEvent(HunterUiAction.SuccessImage(it.text))
                        }
                        .addOnFailureListener {
                            onActionEvent(HunterUiAction.ErrorImage)
                        }
                } catch (e: Exception) {
                    onActionEvent(HunterUiAction.ErrorImage)
                }
            }, onError = { imageCaptureException ->
                Log.d(TAG, imageCaptureError + imageCaptureException.message)
                onActionEvent(HunterUiAction.ErrorImage)
            }
        )

        HunterUiState.ErrorScreen -> InfoMessage(
            imagePainter = painterResource(drawable.error_message_01),
            title = stringResource(string.th_error_title),
            bodyText = stringResource(string.th_error_body_message)
        )
    }

}
