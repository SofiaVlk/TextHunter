package com.sofiyavolkovaprojects.texthunter.ui.hunter

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
import com.sofiyavolkovaprojects.texthunter.R.drawable
import com.sofiyavolkovaprojects.texthunter.R.string
import com.sofiyavolkovaprojects.texthunter.ui.common.components.CustomCircularProgressBar
import com.sofiyavolkovaprojects.texthunter.ui.common.components.InfoMessage
import com.sofiyavolkovaprojects.texthunter.ui.common.components.RequiresSimplePermission
import com.sofiyavolkovaprojects.texthunter.ui.hunter.HunterUiAction.ErrorImage
import com.sofiyavolkovaprojects.texthunter.ui.hunter.HunterUiAction.OnCapturedButtonClick
import com.sofiyavolkovaprojects.texthunter.ui.hunter.HunterUiAction.OnNavigate
import com.sofiyavolkovaprojects.texthunter.ui.hunter.HunterUiAction.SuccessImage
import com.sofiyavolkovaprojects.texthunter.ui.hunter.HunterUiState.ErrorScreen
import com.sofiyavolkovaprojects.texthunter.ui.hunter.HunterUiState.Initial
import com.sofiyavolkovaprojects.texthunter.ui.hunter.HunterUiState.Loading
import com.sofiyavolkovaprojects.texthunter.ui.hunter.HunterUiState.NavigateToEdit
import com.sofiyavolkovaprojects.texthunter.ui.hunter.view.CameraView
import com.sofiyavolkovaprojects.texthunter.ui.navigation.NavigationParams.EditText

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
        Loading -> CustomCircularProgressBar()
        is NavigateToEdit -> {
            LaunchedEffect(true) {
                onActionEvent(OnNavigate)
                navigateTo(EditText.createNavTextRoute(uiState.text))
            }
        }
        Initial -> CameraView(
            onImageCaptured = { uri, _ ->
                Log.d(TAG, imageUri + uri.path)
                onActionEvent(OnCapturedButtonClick)
                try {
                    context.textRecognizerProcess(uri)
                        .addOnSuccessListener {
                            onActionEvent(SuccessImage(it.text))
                        }
                        .addOnFailureListener {
                            onActionEvent(ErrorImage)
                        }
                } catch (e: Exception) {
                    onActionEvent(ErrorImage)
                }
            }, onError = { imageCaptureException ->
                Log.d(TAG, imageCaptureError + imageCaptureException.message)
                onActionEvent(ErrorImage)
            }
        )

        ErrorScreen -> InfoMessage(
            imagePainter = painterResource(drawable.error_message_01),
            title = stringResource(string.th_error_title),
            bodyText = stringResource(string.th_error_body_message)
        )
    }

}
