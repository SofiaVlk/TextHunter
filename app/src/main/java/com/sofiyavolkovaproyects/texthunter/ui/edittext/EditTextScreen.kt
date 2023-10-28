package com.sofiyavolkovaproyects.texthunter.ui.edittext

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sofiyavolkovaproyects.texthunter.ui.components.ButtonBasic


@Composable
internal fun EditTextScreen(
    modifier: Modifier = Modifier,
    viewModel: EditTextViewModel = hiltViewModel(),
    text: String
) {
    // val items by viewModel.uiState.collectAsStateWithLifecycle()

    // val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier) {
        var textState by remember { mutableStateOf(text) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TextField(
                value = textState,
                onValueChange = { textChanged -> textState = textChanged },
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
                    .height(400.dp)
            )
        }
        Row(modifier = Modifier.padding(12.dp))
        {
            ButtonBasic(
                text = "Guardar",
                onClick = { }
            )

            Spacer(modifier = Modifier.size(12.dp))

            ButtonBasic(
                text = "Descartar",
                onClick = { }
            )
        }

    }
}


@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    EditTextScreen(text = LoremIpsum(200).values.first().toString())
}