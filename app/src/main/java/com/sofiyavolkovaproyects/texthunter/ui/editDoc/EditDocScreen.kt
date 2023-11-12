package com.sofiyavolkovaproyects.texthunter.ui.editDoc

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
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
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sofiyavolkovaproyects.texthunter.ui.components.CustomCircularProgressBar
import com.sofiyavolkovaproyects.texthunter.ui.components.RequiresSimplePermission
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavigationParams.Storage
import com.sofiyavolkovaproyects.texthunter.ui.theme.ExportIcon
import com.sofiyavolkovaproyects.texthunter.ui.theme.SaveIcon
import com.sofiyavolkovaproyects.texthunter.ui.theme.ShareIcon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.util.*


@Composable
internal fun EditDocScreen(
    modifier: Modifier = Modifier,
    viewModel: EditTextViewModel = hiltViewModel(),
    text: String,
    navigateTo: (String) -> Unit
) {
    val uiStateView by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var fileName = ""
    val dialogText = "Introduce el nombre del documento de texto. \n ejemplo: NombreDoc.txt"
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    when (uiStateView.uiState) {
        is EditDocUiState.AlertDialogExportDoc -> {
            val alertDialogExportDoc = uiStateView.uiState as EditDocUiState.AlertDialogExportDoc
            if (alertDialogExportDoc.visible) {
                RequiresSimplePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    AddTitleAlertDialog(
                        onDismissRequest = { viewModel.handlerAction(EditDocUIAction.OnExportDismissClicked) },
                        onConfirmation = {
                            viewModel.handlerAction(EditDocUIAction.OnExportDoneClick)
                            onDialogExportConfirmationClicked(
                                fileName = fileName,
                                context = context,
                                textState = uiStateView.text,
                                onSuccess = { isSuccess ->
                                    snackBarLauncher(
                                        text = if (isSuccess) {
                                            "Fichero guardado con exito en Download"
                                        } else {
                                            "Algo fue mal al crear el documento"
                                        },
                                        scope = scope,
                                        snackBarHostState = snackbarHostState
                                    )
                                },
                                onError = {
                                    viewModel.handlerAction(EditDocUIAction.OnExportError("Introduce un nombre válido para el documento."))
                                }
                            )
                        },
                        dialogTitle = "Nombre del documento",
                        dialogText = alertDialogExportDoc.message.ifEmpty { dialogText },
                        placeholder = "NombreDocumento.txt",
                        onValueChange = { name -> fileName = name }
                    )
                }
            }
        }

        is EditDocUiState.AlertDialogSaveDoc ->
            if ((uiStateView.uiState as EditDocUiState.AlertDialogSaveDoc).visible) {
                AddTitleAlertDialog(
                    onDismissRequest = { viewModel.handlerAction(EditDocUIAction.OnSavedDismissClicked) },
                    onConfirmation = {
                        viewModel.handlerAction(EditDocUIAction.OnSavedDoneClick(fileName))
                        navigateTo(Storage.route)
                        if (text.isNotEmpty()) {
                            viewModel.handlerAction(EditDocUIAction.OnTextChanged(text))
                        }
                    },
                    dialogTitle = "Titulo.",
                    dialogText = "Añade un titulo para el texto.",
                    placeholder = "NombreDocumento",
                    onValueChange = { name -> fileName = name }
                )
            }

        is EditDocUiState.Loading -> CustomCircularProgressBar()

        is EditDocUiState.OnSharedClick -> {
            shareText(context = context, textState = uiStateView.text)
        }

        is EditDocUiState.OnSnackBar -> {
            snackBarLauncher(
                text = (uiStateView.uiState as EditDocUiState.OnSnackBar).text,
                scope = scope,
                snackBarHostState = snackbarHostState
            )
        }

        EditDocUiState.Initialize -> {
            if (text.isNotEmpty()) {
                viewModel.handlerAction(EditDocUIAction.OnTextChanged(text))
            }
            CustomCircularProgressBar()
        }

        EditDocUiState.TextUpdated -> Unit

    }

    EditDocument(
        text = uiStateView.text,
        updateState = { txtState ->
                viewModel.handlerAction(EditDocUIAction.OnTextChanged(txtState))
        },
        onClick = { action -> viewModel.handlerAction(action) },
    )

    SnackbarHost(hostState = snackbarHostState)
}

private fun shareText(textState: String, context: Context) {
    val sendIntent: Intent = Intent().apply {
        this.action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, textState)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

@Composable
private fun EditDocument(
    text: String,
    updateState: (String) -> Unit = {},
    onClick: (EditDocUIAction) -> Unit = {}
) {
    Box(
        modifier = Modifier.padding(12.dp),
    ) {

        Column(modifier = Modifier.clip(RoundedCornerShape(4.dp))) {
            Spacer(modifier = Modifier.size(48.dp))
            BasicTextField(
                value = text,
                onValueChange = { textChanged -> updateState(textChanged) },
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(18.dp)
            )
        }

        Header(text = text, onClick = onClick)
    }
}

@Composable
private fun Header(
    text: String,
    onClick: (EditDocUIAction) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "Titulo",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        AccordionVerticalButtonBar(text = text, onClick = onClick)
    }
}

@Composable
private fun AccordionVerticalButtonBar(
    modifier: Modifier = Modifier,
    text: String,
    onClick: (EditDocUIAction) -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.background)
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
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.primary

            )
            AnimatedVisibility(visible = visible) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        modifier = modifier
                            .size(38.dp)
                            .clickable { onClick(EditDocUIAction.OnSaveClick) },
                        imageVector = Icons.Default.Save, contentDescription = "Settings",
                        tint = SaveIcon

                    )
                    Icon(
                        modifier = modifier
                            .size(38.dp)
                            .clickable { onClick(EditDocUIAction.OnShareClick(text)) },
                        imageVector = Icons.Default.Share,
                        contentDescription = "Settings",
                        tint = ShareIcon
                    )
                    Icon(
                        modifier = modifier
                            .size(38.dp)
                            .clickable { onClick(EditDocUIAction.OnExportClick) },
                        imageVector = Icons.Default.ImportExport,
                        contentDescription = "Settings",
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

//escribir
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
    EditDocument(text = LoremIpsum(200).values.first().toString())
}