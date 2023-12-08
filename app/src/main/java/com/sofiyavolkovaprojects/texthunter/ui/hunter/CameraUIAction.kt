package com.sofiyavolkovaprojects.texthunter.ui.hunter

sealed class CameraUIAction {
    data object OnCameraClick : CameraUIAction()
    data object OnGalleryViewClick : CameraUIAction()
    data object OnSwitchCameraClick : CameraUIAction()
}
