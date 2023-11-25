package com.sofiyavolkovaproyects.texthunter.ui.gallery

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sofiyavolkovaproyects.texthunter.data.DefaultImagesRepository
import com.sofiyavolkovaproyects.texthunter.modelo.Media
import com.sofiyavolkovaproyects.texthunter.ui.gallery.GalleryUiState.CaptureText
import com.sofiyavolkovaproyects.texthunter.ui.gallery.GalleryUiState.Empty
import com.sofiyavolkovaproyects.texthunter.ui.gallery.GalleryUiState.Error
import com.sofiyavolkovaproyects.texthunter.ui.gallery.GalleryUiState.Initialize
import com.sofiyavolkovaproyects.texthunter.ui.gallery.GalleryUiState.Loading
import com.sofiyavolkovaproyects.texthunter.ui.gallery.GalleryUiState.NavigateToEdit
import com.sofiyavolkovaproyects.texthunter.ui.gallery.GalleryUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val imagesRepository: DefaultImagesRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<GalleryUiState> = MutableStateFlow(Initialize)
    val uiState: StateFlow<GalleryUiState> get() = _uiState

    private fun getGalleryImages() {
        viewModelScope.launch {
            imagesRepository.getImages()
                .catch { _uiState.update { Error } }
                .collect { imageList ->
                    _uiState.update {
                        if (imageList.isNotEmpty()) {
                            Success(imageList)
                        } else {
                            Empty
                        }
                    }
                }
        }
    }

    private fun removeImage(media: Media) {
        viewModelScope.launch {
            imagesRepository.deleteImage(media)
        }
    }

    fun handlerAction(action: GalleryUIAction) {
        when (action) {
            is GalleryUIAction.OnClickDeleteImage -> {
                removeImage(media = action.media)
                getGalleryImages()
            }
            //actualiza el estado al puslsar el boton Capturar
            is GalleryUIAction.OnClickCaptureImage ->{
                _uiState.update { CaptureText(action.uri) }

            }
            GalleryUIAction.OnInitialize ->{
                _uiState.update { Loading }
                getGalleryImages()
            }
            GalleryUIAction.OnErrorText ->{
                _uiState.update { Error }
            }
            is GalleryUIAction.OnSuccessText -> {
                _uiState.update { NavigateToEdit(action.text) }
            }
        }
    }

}

sealed interface GalleryUiState {
    data object Loading : GalleryUiState
    data object Initialize : GalleryUiState
    data class Success(val mediaList: List<Media>) : GalleryUiState
    data object Error : GalleryUiState
    data object Empty : GalleryUiState
    data class CaptureText(val uri : Uri): GalleryUiState
    data class NavigateToEdit(val text:String):GalleryUiState
}
