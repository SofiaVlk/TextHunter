package com.sofiyavolkovaproyects.texthunter.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sofiyavolkovaproyects.texthunter.data.DefaultImagesRepository
import com.sofiyavolkovaproyects.texthunter.modelo.Media
import com.sofiyavolkovaproyects.texthunter.ui.gallery.GalleryUiState.Error
import com.sofiyavolkovaproyects.texthunter.ui.gallery.GalleryUiState.Loading
import com.sofiyavolkovaproyects.texthunter.ui.gallery.GalleryUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val imagesRepository: DefaultImagesRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<GalleryUiState> = MutableStateFlow(Loading)
    val uiState: StateFlow<GalleryUiState> get() = _uiState

    init {
        getGalleryImages()
    }

    private fun getGalleryImages() {
        viewModelScope.launch {
            imagesRepository.getImages()
                .catch { _uiState.update { Error } }
                .collect { imageList -> _uiState.update { Success(imageList) } }
        }
    }

    private fun removeImage(media: Media) {
        viewModelScope.launch {
            imagesRepository.deleteImage(media)
        }
    }
    fun handlerAction(action: GalleryUIAction) {
        when (action){
           is GalleryUIAction.OnClickDeleteImage -> {
               removeImage(media = action.media)
               getGalleryImages()
           }

        }
    }

    }

sealed interface GalleryUiState {
    data object Loading : GalleryUiState
    data class Success(val mediaList: List<Media>) : GalleryUiState
    data object Error : GalleryUiState
}
