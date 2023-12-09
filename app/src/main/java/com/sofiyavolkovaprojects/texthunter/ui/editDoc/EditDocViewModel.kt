package com.sofiyavolkovaprojects.texthunter.ui.editDoc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sofiyavolkovaprojects.texthunter.data.DocumentsRepository
import com.sofiyavolkovaprojects.texthunter.data.local.database.DocumentItem
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
import com.sofiyavolkovaprojects.texthunter.ui.editDoc.EditDocUiState.TextUpdated
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EditTextViewModel @Inject constructor(
    private val savedDocsRepository: DocumentsRepository
) : ViewModel() {

    // Backing property to avoid state updates from other classes
    private val _uiState: MutableStateFlow<EditDocUiStateView> =
        MutableStateFlow(EditDocUiStateView())

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<EditDocUiStateView> by lazy { _uiState }

    private val _effect: Channel<EditDocSideEffect> = Channel()
    val effect = _effect.receiveAsFlow()

    private fun addDocument(documentItem: DocumentItem) {
        viewModelScope.launch {
            savedDocsRepository.add(documentItem)
        }
    }

    fun handlerAction(action: EditDocUIAction) {
        when (action) {
            OnSaveClick -> updateState { AlertDialogSaveDoc(true) }
            is OnShareClick -> setEffect(shareTextEffect)
            OnExportClick -> updateState {
                AlertDialogExportDoc(true)
            }

            OnSavedDismissClicked -> updateState {
                AlertDialogSaveDoc(false)
            }

            OnExportDismissClicked -> updateState {
                AlertDialogExportDoc(false)
            }

            OnExportDoneClick -> {
                updateState { AlertDialogExportDoc(false) }
            }

            is OnSavedDoneClick -> {
                addDocument(DocumentItem(action.title, uiState.value.documentItem.body))
                updateState {
                    AlertDialogExportDoc(false)
                }
            }

            is OnTextChanged -> {
                updateData(DocumentItem(_uiState.value.documentItem.title, action.text))
            }

            is OnExportError -> {
                updateState {
                    AlertDialogExportDoc(true, action.text)
                }
            }

            is Initialized -> {
                if (action.id != -1) {
                    viewModelScope.launch {
                        savedDocsRepository.getDocumentById(action.id)
                            .catch { updateState { Error } }
                            .collect { updateData(it) }
                    }
                } else {
                    updateData(DocumentItem(body = action.text))
                }
            }

            OnSpokenText -> setEffect(readTextEffect(uiState.value.documentItem.body) )
        }
    }

    private fun updateState(uiState: () -> EditDocUiState) {
        _uiState.update { it.copy(uiState = uiState()) }
    }

    private fun updateData(documentItem: DocumentItem) {
        _uiState.update { it.copy(documentItem = documentItem, uiState = TextUpdated) }
    }

    private fun setEffect(effect: EditDocSideEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }

}

data class EditDocUiStateView(
    val documentItem: DocumentItem = DocumentItem(),
    val uiState: EditDocUiState = Initialize
)

sealed interface EditDocSideEffect {
    data object shareTextEffect : EditDocSideEffect
    data class readTextEffect(val text: String) : EditDocSideEffect
}

sealed interface EditDocUiState {
    data object Loading : EditDocUiState
    data class AlertDialogSaveDoc(val visible: Boolean = false) : EditDocUiState
    data class AlertDialogExportDoc(val visible: Boolean = false, val message: String = "") :
        EditDocUiState

    data class OnSnackBar(val text: String) : EditDocUiState
    data object Initialize : EditDocUiState
    data object TextUpdated : EditDocUiState
    data object Error : EditDocUiState
}
