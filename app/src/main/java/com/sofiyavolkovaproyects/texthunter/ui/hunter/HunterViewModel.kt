package com.sofiyavolkovaproyects.texthunter.ui.hunter

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HunterViewModel @Inject constructor(): ViewModel() {

    private val _uiState = MutableStateFlow<HunterUiState>(HunterUiState.Initial)
    val uiState: StateFlow<HunterUiState> get() = _uiState

    fun handlerAction(action: HunterUiAction) {
        when (action) {
            is HunterUiAction.SuccessImage ->
                _uiState.update { HunterUiState.NavigateToEdit(action.text) }

            HunterUiAction.ErrorImage ->
                _uiState.update { HunterUiState.ErrorScreen }

            HunterUiAction.OnCapturedButtonClick ->
                _uiState.update { HunterUiState.Loading }

            HunterUiAction.OnNavigate -> _uiState.update { HunterUiState.Initial }
        }
    }
}

sealed interface HunterUiState {
    data object Initial : HunterUiState
    data object Loading : HunterUiState
    data object ErrorScreen : HunterUiState
    data class NavigateToEdit(val text: String) : HunterUiState
}
