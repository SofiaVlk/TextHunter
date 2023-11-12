package com.sofiyavolkovaproyects.texthunter.ui.editDoc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sofiyavolkovaproyects.texthunter.data.DocumentsRepository
import com.sofiyavolkovaproyects.texthunter.ui.editDoc.EditDocUiState.TextUpdated
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTextViewModel @Inject constructor(
    private val savedDocsRepository: DocumentsRepository
) : ViewModel() {

    // Backing property to avoid state updates from other classes
    private var _uiState: MutableStateFlow<EditDocUiStateView> =
        MutableStateFlow(EditDocUiStateView())

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<EditDocUiStateView> = _uiState

    private fun addDocument(title: String, body: String) {
        viewModelScope.launch {
            savedDocsRepository.add(title, body)
        }
    }

    fun handlerAction(action: EditDocUIAction) {
        when (action) {
            EditDocUIAction.OnSaveClick -> updateState { EditDocUiState.AlertDialogSaveDoc(true) }
            is EditDocUIAction.OnShareClick -> updateState { EditDocUiState.OnSharedClick(action.text) }
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
                addDocument(action.title, uiState.value.text)
                updateState {
                    EditDocUiState.AlertDialogExportDoc(false)
                }
            }

            is EditDocUIAction.OnTextChanged -> {
                updateData(action.text)
            }
            is EditDocUIAction.OnExportError -> {
                updateState {
                    EditDocUiState.AlertDialogExportDoc(true, action.text)
                }

            }
        }
    }

    private fun updateState(uiState: () -> EditDocUiState) { _uiState.update { it.copy(uiState = uiState()) } }
    private fun updateData(text: String) { _uiState.update { it.copy(text = text, uiState = TextUpdated) } }

}

data class EditDocUiStateView(
    val text: String = "",
    val uiState: EditDocUiState = EditDocUiState.Initialize
)
sealed interface EditDocUiState {
    data object Loading : EditDocUiState
    data class AlertDialogSaveDoc(val visible: Boolean = false) : EditDocUiState
    data class AlertDialogExportDoc(val visible: Boolean = false, val message:String = "") : EditDocUiState
    data class OnSharedClick(val text: String) : EditDocUiState
    data class OnSnackBar(val text: String) : EditDocUiState
    data object Initialize: EditDocUiState
    data object TextUpdated: EditDocUiState
}
