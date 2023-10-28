/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sofiyavolkovaproyects.texthunter.ui.savedDocs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sofiyavolkovaproyects.texthunter.data.local.database.DocumentItem
import com.sofiyavolkovaproyects.texthunter.ui.components.ButtonBasic
import com.sofiyavolkovaproyects.texthunter.ui.theme.MyApplicationTheme

@Composable
fun SavedDocsScreen(
    modifier: Modifier = Modifier,
    viewModel: SavedDocsViewModel = hiltViewModel()
) {
    val items by viewModel.uiState.collectAsStateWithLifecycle()
    if (items is SavedDocsUiState.Success) {
        SavedDocsScreen(
            docList = (items as SavedDocsUiState.Success).data,
            onSave = viewModel::addDocument,
            modifier = modifier
        )
    }
}

@Composable
internal fun SavedDocsScreen(
    docList: List<DocumentItem>,
    onSave: (title: String, body: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)

    ) {
        var titleState by remember { mutableStateOf("Compose") }
        var bodyState by remember { mutableStateOf("Compose") }

            TextField(
                value = titleState,
                onValueChange = { titleState = it }
            )

            TextField(
                value = bodyState,
                onValueChange = { bodyState = it }
            )

        ButtonBasic(
            onClick = { onSave(titleState, bodyState) },
            text = "Save"
        )


        LazyColumn {

            items(docList) { document ->

                ElevatedCard(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)

                ) {
                    Text(
                        text = document.title,
                        modifier = Modifier
                            .fillMaxSize(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = document.body,
                        modifier = Modifier.fillMaxSize()
                            .padding(12.dp)
                    )
                }

            }
        }

    }
}

// Previews

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        SavedDocsScreen(
            listOf(DocumentItem("Documento 1", LoremIpsum(50).values.first())),
            onSave = { _, _ -> })
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun PortraitPreview() {
    MyApplicationTheme {
        SavedDocsScreen(
            listOf(DocumentItem("Documento 1", LoremIpsum(50).values.first())),
            onSave = { _, _ -> })
    }
}
