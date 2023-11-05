package com.sofiyavolkovaproyects.texthunter.ui.edittext

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sofiyavolkovaproyects.texthunter.R.drawable
import com.sofiyavolkovaproyects.texthunter.ui.components.ButtonBasic
import com.sofiyavolkovaproyects.texthunter.ui.components.RequiresSimplePermission
import com.sofiyavolkovaproyects.texthunter.ui.navigation.NavigationParams.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.util.*


@Composable
internal fun EditTextScreen(
    modifier: Modifier = Modifier,
    viewModel: EditTextViewModel = hiltViewModel(),
    text: String,
    navigateTo: (String) -> Unit
) {
    // val items by viewModel.uiState.collectAsStateWithLifecycle()

    // val keyboardController = LocalSoftwareKeyboardController.current

    val context = LocalContext.current
    var fileName = ""
    var openAlertDialogExportDoc by remember { mutableStateOf(false) }
    var openAlertDialogSaveDoc by remember { mutableStateOf(false) }
    var textState by remember { mutableStateOf(text) }
    var dialogText by remember { mutableStateOf("Introduce el nombre del documento de texto. \n ejemplo: NombreDoc.txt") }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                painter = painterResource(id = drawable.card_background_01),
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )
            Text(
                modifier = Companion.fillMaxWidth(),
                text = "Titulo del Documento",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.background,
                textAlign = TextAlign.Center
            )
        }
        TextField(
            value = textState,
            onValueChange = { textChanged -> textState = textChanged },
            modifier = Modifier
                .fillMaxWidth()
                .weight(3f)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ButtonBasic(
                modifier = Modifier.fillMaxWidth(),
                text = "Guardar",
                onClick = {
                    openAlertDialogSaveDoc = true
                }
            )

            ButtonBasic(
                modifier = Modifier.fillMaxWidth(),
                text = "Exportar",
                onClick = {
                    openAlertDialogExportDoc = true;
                }
            )
        }

    }

    when {
        openAlertDialogExportDoc -> {
            RequiresSimplePermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                AddTitleAlertDialog(
                    onDismissRequest = { openAlertDialogExportDoc = false },
                    onConfirmation = {
                        openAlertDialogExportDoc = false
                        onDialogExportConfirmationClicked(
                            fileName = fileName,
                            context = context,
                            textState = textState,
                            onSuccess = { isSuccess ->
                                LaunchSnackBar(
                                    text = if (isSuccess) {
                                        "Fichero guardado con exito en Download"
                                    } else {
                                        "Algo fue mal al crear el documento"
                                    },
                                    scope = scope,
                                    snackBarHostState = snackbarHostState
                                )
                                openAlertDialogExportDoc = false
                            },
                            onError = {
                                dialogText = "Introduce un nombre válido para el documento."
                                openAlertDialogExportDoc = true
                            }
                        )
                    },
                    dialogTitle = "Nombre del documento",
                    dialogText = dialogText,
                    onValueChange = { name -> fileName = name }
                )
            }
        }

        openAlertDialogSaveDoc ->
            AddTitleAlertDialog(
                onDismissRequest = { openAlertDialogSaveDoc = false },
                onConfirmation = {
                    openAlertDialogSaveDoc = false
                    viewModel.addDocument(fileName, text)
                    navigateTo(Storage.route)
                },
                dialogTitle = "Titulo.",
                dialogText = "Añade un titulo para el texto.",
                onValueChange = { name -> fileName = name }
            )
    }

    SnackbarHost(hostState = snackbarHostState)

}

private fun LaunchSnackBar(
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
        val fileToWrite = File(file, "$sTitle")
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

//leer
fun readFileOnInternalStorage(context: Context, fileName: String): String {
    val file = File(context?.filesDir, "documents")
    var ret = ""
    try {
        if (!file.exists()) {
            return ret
        }
        val fileToRead = File(file, fileName)
        val reader = FileReader(fileToRead)
        ret = reader.readText()
        reader.close()
    } catch (e: Exception) {
        Log.e("Read document:", e.message.toString())
    }
    return ret
}


@Composable
private fun AddTitleAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    onValueChange: (String) -> Unit = { text -> }
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
                        Text(text = "NombreDocumento.txt")
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
    val doc = File(file, "$fileName")

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
    EditTextScreen(text = LoremIpsum(200).values.first().toString(), navigateTo = {})
}