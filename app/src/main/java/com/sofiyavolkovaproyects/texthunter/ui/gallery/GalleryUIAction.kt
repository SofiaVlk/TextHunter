package com.sofiyavolkovaproyects.texthunter.ui.gallery

import android.net.Uri
import com.sofiyavolkovaproyects.texthunter.modelo.Media

sealed interface GalleryUIAction {
    data class OnClickDeleteImage(val media: Media) : GalleryUIAction
    data class OnClickCaptureImage(val uri: Uri) : GalleryUIAction
    data object OnErrorText : GalleryUIAction
    data class OnSuccessText(val text : String): GalleryUIAction
    data object OnInitialize : GalleryUIAction
}