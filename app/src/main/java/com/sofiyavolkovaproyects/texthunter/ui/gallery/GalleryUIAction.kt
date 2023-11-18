package com.sofiyavolkovaproyects.texthunter.ui.gallery

import com.sofiyavolkovaproyects.texthunter.modelo.Media

sealed interface GalleryUIAction {
    data class OnClickDeleteImage(val media: Media): GalleryUIAction

}