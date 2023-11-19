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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sofiyavolkovaproyects.texthunter.data.DocumentsRepository
import com.sofiyavolkovaproyects.texthunter.data.local.database.DocumentItem
import com.sofiyavolkovaproyects.texthunter.ui.savedDocs.SavedDocsUiState.Empty
import com.sofiyavolkovaproyects.texthunter.ui.savedDocs.SavedDocsUiState.Error
import com.sofiyavolkovaproyects.texthunter.ui.savedDocs.SavedDocsUiState.Loading
import com.sofiyavolkovaproyects.texthunter.ui.savedDocs.SavedDocsUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedDocsViewModel @Inject constructor(
    private val savedDocsRepository: DocumentsRepository
) : ViewModel() {

    private var _uiState: MutableStateFlow<SavedDocsUiState> = MutableStateFlow(Loading)
    internal val uiState: StateFlow<SavedDocsUiState> get() = _uiState

    init {
        getDocumentList()
    }

    fun removeDocument(document: DocumentItem) {
        viewModelScope.launch {
            savedDocsRepository.remove(document)
        }
        getDocumentList()
    }

    private fun getDocumentList() {
        viewModelScope.launch {
            savedDocsRepository.getSavedDocuments()
                .catch { error -> _uiState.update { Error(error) } }
                .collect { docItemList ->
                    _uiState.update {
                        if (docItemList.isNotEmpty()) {
                            Success(docItemList)
                        } else {
                            Empty
                        }
                    }
                }
        }
    }
}

sealed interface SavedDocsUiState {
    data object Loading : SavedDocsUiState
    data object Empty : SavedDocsUiState
    data class Error(val throwable: Throwable) : SavedDocsUiState
    data class Success(val data: List<DocumentItem>) : SavedDocsUiState
}