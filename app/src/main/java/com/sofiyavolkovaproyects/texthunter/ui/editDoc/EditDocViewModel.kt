package com.sofiyavolkovaproyects.texthunter.ui.editDoc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sofiyavolkovaproyects.texthunter.data.DocumentsRepository
import com.sofiyavolkovaproyects.texthunter.data.local.database.DocumentItem
import com.sofiyavolkovaproyects.texthunter.ui.editDoc.EditDocUiState.TextUpdated
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTextViewModel @Inject constructor(
    private val savedDocsRepository: DocumentsRepository
) : ViewModel() {

    // Backing property to avoid state updates from other classes
    private val _uiState: MutableStateFlow<EditDocUiStateView> =
        MutableStateFlow(EditDocUiStateView())

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<EditDocUiStateView> by lazy { _uiState }

    private fun addDocument(documentItem: DocumentItem) {
        viewModelScope.launch {
            savedDocsRepository.add(documentItem)
        }
    }

    fun handlerAction(action: EditDocUIAction) {
        when (action) {
            EditDocUIAction.OnSaveClick -> updateState { EditDocUiState.AlertDialogSaveDoc(true) }
            is EditDocUIAction.OnShareClick -> updateState { EditDocUiState.OnSharedClick }
            EditDocUIAction.OnExportClick -> updateState {
                EditDocUiState.AlertDialogExportDoc(true)
            }

            EditDocUIAction.OnSavedDismissClicked -> updateState {
                EditDocUiState.AlertDialogSaveDoc(false)
            }

            EditDocUIAction.OnExportDismissClicked -> updateState {
                EditDocUiState.AlertDialogExportDoc(false)
            }

            EditDocUIAction.OnExportDoneClick -> {
                updateState { EditDocUiState.AlertDialogExportDoc(false) }
            }

            is EditDocUIAction.OnSavedDoneClick -> {
                addDocument(DocumentItem(action.title, uiState.value.documentItem.body))
                updateState {
                    EditDocUiState.AlertDialogExportDoc(false)
                }
            }

            is EditDocUIAction.OnTextChanged -> {
                updateData(DocumentItem(_uiState.value.documentItem.title, action.text))
            }

            is EditDocUIAction.OnExportError -> {
                updateState {
                    EditDocUiState.AlertDialogExportDoc(true, action.text)
                }
            }

            is EditDocUIAction.Initialized -> {
                if (action.id != -1) {
                    viewModelScope.launch {
                            savedDocsRepository.getDocumentById(action.id)
                                .catch { _uiState.value.copy(uiState = EditDocUiState.Error) }
                                .collect { updateData(it) }
                    }
                } else {
                    updateData(DocumentItem(body = action.text))
                }
            }
        }
    }

    private fun updateState(uiState: () -> EditDocUiState) {
        _uiState.update { it.copy(uiState = uiState()) }
    }

    private fun updateData(documentItem: DocumentItem) {
        _uiState.update { it.copy(documentItem = documentItem, uiState = TextUpdated) }
    }

}

data class EditDocUiStateView(
    val documentItem: DocumentItem = DocumentItem(),
    val uiState: EditDocUiState = EditDocUiState.Initialize
)

sealed interface EditDocUiState {
    data object Loading : EditDocUiState
    data class AlertDialogSaveDoc(val visible: Boolean = false) : EditDocUiState
    data class AlertDialogExportDoc(val visible: Boolean = false, val message: String = "") :
        EditDocUiState

    data object OnSharedClick : EditDocUiState
    data class OnSnackBar(val text: String) : EditDocUiState
    data object Initialize : EditDocUiState
    data object TextUpdated : EditDocUiState
    data object Error : EditDocUiState
}