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
import com.sofiyavolkovaproyects.texthunter.ui.savedDocs.SavedDocsUiState.Error
import com.sofiyavolkovaproyects.texthunter.ui.savedDocs.SavedDocsUiState.Loading
import com.sofiyavolkovaproyects.texthunter.ui.savedDocs.SavedDocsUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SavedDocsViewModel @Inject constructor(
    private val savedDocsRepository: DocumentsRepository
) : ViewModel() {

    internal val uiState: StateFlow<SavedDocsUiState> = savedDocsRepository
        .savedDocuments
        .map<List<String>, SavedDocsUiState> { list -> Success(list) }
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    fun addDocument(name: String) {
        viewModelScope.launch {
            savedDocsRepository.add(name)
        }
    }
}

sealed interface SavedDocsUiState {
    data object Loading : SavedDocsUiState
    data class Error(val throwable: Throwable) : SavedDocsUiState
    data class Success(val data: List<String>) : SavedDocsUiState
}
