package com.sofiyavolkovaprojects.texthunter.ui.hunter

import androidx.lifecycle.ViewModel
import com.sofiyavolkovaprojects.texthunter.ui.hunter.HunterUiAction.ErrorImage
import com.sofiyavolkovaprojects.texthunter.ui.hunter.HunterUiAction.OnCapturedButtonClick
import com.sofiyavolkovaprojects.texthunter.ui.hunter.HunterUiAction.OnNavigate
import com.sofiyavolkovaprojects.texthunter.ui.hunter.HunterUiAction.SuccessImage
import com.sofiyavolkovaprojects.texthunter.ui.hunter.HunterUiState.ErrorScreen
import com.sofiyavolkovaprojects.texthunter.ui.hunter.HunterUiState.Initial
import com.sofiyavolkovaprojects.texthunter.ui.hunter.HunterUiState.Loading
import com.sofiyavolkovaprojects.texthunter.ui.hunter.HunterUiState.NavigateToEdit
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class HunterViewModel @Inject constructor(): ViewModel() {

    private val _uiState = MutableStateFlow<HunterUiState>(Initial)
    val uiState: StateFlow<HunterUiState> get() = _uiState

    fun handlerAction(action: HunterUiAction) {
        when (action) {
            is SuccessImage ->
                _uiState.update { NavigateToEdit(action.text) }

            ErrorImage ->
                _uiState.update { ErrorScreen }

            OnCapturedButtonClick ->
                _uiState.update { Loading }

            OnNavigate -> _uiState.update { Initial }
        }
    }
}

//Estados
sealed interface HunterUiState {
    data object Initial : HunterUiState
    data object Loading : HunterUiState
    data object ErrorScreen : HunterUiState
    data class NavigateToEdit(val text: String) : HunterUiState
}
