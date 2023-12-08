package com.sofiyavolkovaprojects.texthunter.ui.editDoc

sealed class EditDocUIAction {
    data object OnSaveClick : EditDocUIAction()
    data object OnShareClick : EditDocUIAction()
    data object OnExportClick : EditDocUIAction()
    data object OnSavedDismissClicked : EditDocUIAction()
    data object OnExportDismissClicked : EditDocUIAction()
    data object OnExportDoneClick : EditDocUIAction()
    data class OnSavedDoneClick(val title: String) : EditDocUIAction()
    data class OnTextChanged(val text: String) : EditDocUIAction()
    data class OnExportError(val text: String) : EditDocUIAction()
    data class Initialized(val id: Int, val text: String): EditDocUIAction()
    data object OnSpokenText: EditDocUIAction()
}
