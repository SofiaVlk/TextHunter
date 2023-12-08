package com.sofiyavolkovaprojects.texthunter.ui.hunter

sealed class HunterUiAction {
    data object OnCapturedButtonClick: HunterUiAction()
    data class SuccessImage(val text: String): HunterUiAction()
    data object ErrorImage: HunterUiAction()
    data object OnNavigate: HunterUiAction()
}