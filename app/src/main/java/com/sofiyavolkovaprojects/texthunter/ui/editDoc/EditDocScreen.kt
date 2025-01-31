package com.sofiyavolkovaprojects.texthunter.ui.editDoc

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.TwoTone
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.twotone.SpeakerPhone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sofiyavolkovaprojects.texthunter.R
import com.sofiyavolkovaprojects.texthunter.R.string
import com.sofiyavolkovaprojects.texthunter.data.local.database.DocumentItem
import com.sofiyavolkovaprojects.texthunter.ui.common.components.CustomCircularProgressBar
import com.sofiyavolkovaprojects.texthunter.ui.common.theme.ExportIcon
import com.sofiyavolkovaprojects.texthunter.ui.common.theme.SaveIcon
import com.sofiyavolkovaprojects.texthunter.ui.common.theme.ShareIcon
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocSideEffect.readTextEffect
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocSideEffect.shareTextEffect
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocUIAction.Initialized
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocUIAction.OnExportClick
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocUIAction.OnExportDismissClicked
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocUIAction.OnExportDoneClick
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocUIAction.OnExportError
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocUIAction.OnSaveClick
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocUIAction.OnSavedDismissClicked
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocUIAction.OnSavedDoneClick
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocUIAction.OnShareClick
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocUIAction.OnSpokenText
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocUIAction.OnTextChanged
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocUiState.AlertDialogExportDoc
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocUiState.AlertDialogSaveDoc
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocUiState.Error
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocUiState.Initialize
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocUiState.Loading
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocUiState.OnSnackBar
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocUiState.TextUpdated
import com.sofiyavolkovaprojects.texthunter.ui.navigation.NavigationParams.Storage
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

//pantalla de edición
@Composable
internal fun EditDocScreen(
    viewModel: EditTextViewModel = hiltViewModel(),
    text: String = "",
    id: Int = -1,
    textToSpeech: TextToSpeech,
    navigateTo: (String) -> Unit
) {
    val uiStateView by viewModel.uiState.collectAsStateWithLifecycle()
    val effectFlow = viewModel.effect

    val context = LocalContext.current
    var fileName = ""
    val dialogText = stringResource(string.th_edit_doc_screen_dialog_text)
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val successMessageEditDoc = stringResource(R.string.th_edit_doc_screen_success_message)
    val errorMessageEditDoc = stringResource(string.th_edit_doc_screen_error_message)
    val errorValidDocName = stringResource(string.th_edit_doc_screen_error_valid_doc_name)

    LaunchedEffect(true) {
        effectFlow.onEach { effect ->
            when (effect) {
                //efecto de leer
                is readTextEffect -> {
                    speechText(context, effect.text, textToSpeech)
                }
                //efecto de compartir
                is shareTextEffect -> {
                    shareText(context = context, textState = uiStateView.documentItem.body)
                }
            }
        }.collect()
    }

    when (uiStateView.uiState) {
        //exportar
        is AlertDialogExportDoc -> {
            val alertDialogExportDoc = uiStateView.uiState as AlertDialogExportDoc
            if (alertDialogExportDoc.visible) {
                AddTitleAlertDialog(
                    onDismissRequest = { viewModel.handlerAction(OnExportDismissClicked) },
                    onConfirmation = {
                        viewModel.handlerAction(OnExportDoneClick)
                        onDialogExportConfirmationClicked(
                            fileName = fileName,
                            context = context,
                            textState = uiStateView.documentItem.body,
                            onSuccess = { isSuccess ->
                                snackBarLauncher(
                                    text = if (isSuccess) {
                                        successMessageEditDoc
                                    } else {
                                        errorMessageEditDoc
                                    },
                                    scope = scope,
                                    snackBarHostState = snackBarHostState
                                )
                            },
                            onError = {
                                viewModel.handlerAction(OnExportError(errorValidDocName))
                            }
                        )
                    },
                    dialogTitle = stringResource(string.th_edit_doc_screen_doc_title),
                    dialogText = alertDialogExportDoc.message.ifEmpty { dialogText },
                    placeholder = stringResource(string.th_edit_doc_screen_placeholder_export_doc_name),
                    onValueChange = { name -> fileName = name }
                )
            }
        }
        //titulo para guardar doc
        is AlertDialogSaveDoc ->
            if ((uiStateView.uiState as AlertDialogSaveDoc).visible) {
                AddTitleAlertDialog(
                    onDismissRequest = { viewModel.handlerAction(OnSavedDismissClicked) },
                    onConfirmation = {
                        viewModel.handlerAction(OnSavedDoneClick(fileName))
                        navigateTo(Storage.route)
                    },
                    dialogTitle = stringResource(string.th_edit_doc_screen_save_dialog_title),
                    dialogText = stringResource(string.th_edit_doc_screen_dialog_save_text_title),
                    placeholder = stringResource(string.th_edit_doc_screen_save_placeholder_doc_name),
                    onValueChange = { name -> fileName = name }
                )
            }

        is Loading -> CustomCircularProgressBar()

        is OnSnackBar -> {
            snackBarLauncher(
                text = (uiStateView.uiState as OnSnackBar).text,
                scope = scope,
                snackBarHostState = snackBarHostState
            )
        }

        Initialize -> {
            if (text.isNotEmpty()) {
                viewModel.handlerAction(Initialized(id, text))
            }
            CustomCircularProgressBar()
        }

        TextUpdated -> Unit
        Error -> Unit

    }

    EditDocument(
        documentItem = uiStateView.documentItem,
        updateState = { txtState ->
            viewModel.handlerAction(OnTextChanged(txtState))
        },
        onClick = { action -> viewModel.handlerAction(action) },
    )

    SnackbarHost(hostState = snackBarHostState)
}

private fun speechText(
    context: Context,
    text: String,
    textToSpeech: TextToSpeech
) {
    // Comprueba si el usuario no ha introducido ningún texto
    if (text.isNotEmpty()) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts1")
    } else {
        Toast.makeText(
            context,
            context.getString(string.th_edit_doc_screen_toast_not_empty), Toast.LENGTH_LONG
        )
            .show()
    }
}
//compartir texto
private fun shareText(textState: String, context: Context) {
    val sendIntent: Intent = Intent().apply {
        this.action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, textState)
        type = context.getString(string.th_edit_doc_screen_share_text_type)
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}
//editar texo
@Composable
private fun EditDocument(
    documentItem: DocumentItem,
    updateState: (String) -> Unit = {},
    onClick: (EditDocUIAction) -> Unit = {}
) {
    Box(
        modifier = Modifier.padding(12.dp),
    ) {

        Column(modifier = Modifier.clip(RoundedCornerShape(4.dp))) {
            Spacer(modifier = Modifier.size(48.dp))
            BasicTextField(
                value = documentItem.body,
                onValueChange = { textChanged -> updateState(textChanged) },
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(18.dp)
            )
        }

        Header(title = documentItem.title, onClick = onClick)
    }
}

//cabecera
@Composable
private fun Header(
    title: String,
    onClick: (EditDocUIAction) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title.ifEmpty { stringResource(string.th_edit_doc_screen_doc_header) },
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        AccordionVerticalButtonBar(onClick = onClick)
    }
}

//menú vertical desplegable
@Composable
private fun AccordionVerticalButtonBar(
    modifier: Modifier = Modifier,
    onClick: (EditDocUIAction) -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
    ) {
        Column(
            modifier = modifier
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                modifier = modifier
                    .size(38.dp)
                    .clickable { visible = !visible },
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(string.th_edit_doc_screen_settings),
                tint = MaterialTheme.colorScheme.primary

            )
            AnimatedVisibility(visible = visible) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        modifier = modifier
                            .size(38.dp)
                            .clickable { onClick(OnSaveClick) },
                        imageVector = Icons.Default.Save,
                        contentDescription = stringResource(string.th_edit_doc_screen_settings),
                        tint = SaveIcon

                    )
                    Icon(
                        modifier = modifier
                            .size(38.dp)
                            .clickable { onClick(OnShareClick) },
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(string.th_edit_doc_screen_settings),
                        tint = ShareIcon
                    )
                    Icon(
                        modifier = modifier
                            .size(38.dp)
                            .clickable { onClick(OnExportClick) },
                        imageVector = Icons.Default.ImportExport,
                        contentDescription = stringResource(string.th_edit_doc_screen_settings),
                        tint = ExportIcon
                    )
                    Icon(
                        modifier = modifier
                            .size(38.dp)
                            .clickable { onClick(OnSpokenText) },
                        imageVector = TwoTone.SpeakerPhone,
                        contentDescription = stringResource(string.th_edit_doc_screen_speek),
                        tint = ExportIcon
                    )
                }
            }
        }
    }
}

private fun snackBarLauncher(
    text: String,
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
) {
    scope.launch {
        snackBarHostState.showSnackbar(text)
    }
}
//confirmar alertdialog exportar
private fun onDialogExportConfirmationClicked(
    fileName: String,
    context: Context,
    textState: String,
    onSuccess: (Boolean) -> Unit,
    onError: () -> Unit
) {
    if (fileName.isNotEmpty() && !fileName.contentEquals(" ") && fileName.lowercase(
            Locale.getDefault()
        ).endsWith(".txt")
    ) {
        val result = writeFileOnInternalStorage(
            context,
            sTitle = fileName,
            sBody = textState
        )
        if (result) {
            onSuccess(copyFile(context, fileName))
        }
    } else {
        onError.invoke()
    }
}

//guardar en el alm. int.
fun writeFileOnInternalStorage(context: Context, sTitle: String, sBody: String): Boolean {
    val file = File(context.filesDir, "documents")
    return try {
        if (!file.exists()) {
            file.mkdir()
        }
        val fileToWrite = File(file, sTitle)
        val writer = FileWriter(fileToWrite)
        writer.append(sBody)
        writer.flush()
        writer.close()
        true
    } catch (e: Exception) {
        Log.e("Write document:", e.message.toString())
        false
    }
}

@Composable
private fun AddTitleAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    placeholder: String = "",
    onValueChange: (String) -> Unit = { }
) {

    var valueText by remember {
        mutableStateOf("")
    }

    AlertDialog(
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = dialogText)
                TextField(
                    value = valueText,
                    onValueChange = { text ->
                        onValueChange(text)
                        valueText = text
                    },
                    placeholder = {
                        Text(text = placeholder)
                    },
                )
            }
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
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Cancelar")
            }
        }
    )

}


private fun copyFile(context: Context, fileName: String): Boolean {
    val file = File(context.filesDir, "documents")
    val doc = File(file, fileName)

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        }
        val dstUri =
            context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (dstUri != null) {
            val src = FileInputStream(doc)
            val dst = context.contentResolver.openOutputStream(dstUri)
            requireNotNull(dst)
            src.copyTo(dst)
            src.close()
            dst.close()
            true
        } else {
            false
        }
    } else {
        val downloadDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (downloadDir.canWrite()) {
            val src = FileInputStream(doc)
            val dst = FileOutputStream(
                File(downloadDir, fileName)
            )
            src.copyTo(dst)
            src.close()
            dst.close()
            true
        } else {
            false
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    EditDocument(
        documentItem = DocumentItem(
            title = LoremIpsum(3).values.first().toString(),
            body = LoremIpsum(200).values.first().toString()
        )
    )
}