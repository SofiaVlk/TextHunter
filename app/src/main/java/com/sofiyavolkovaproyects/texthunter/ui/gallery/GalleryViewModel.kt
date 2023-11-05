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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val imagesRepository: DefaultImagesRepository
) : ViewModel() {
    var uiState: StateFlow<GalleryUiState> = MutableStateFlow(Loading)

    init {
        viewModelScope.launch {
            uiState = imagesRepository.getImages()
                .map<List<Media>, GalleryUiState> { imagesList ->
                    Success(imagesList)
                }
                .catch { emit(Error(it)) }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)
        }
    }

    fun removeImage(media: Media) {
        viewModelScope.launch {
            imagesRepository.deleteImage(media)
        }
    }

}

sealed interface GalleryUiState {
    data object Loading : GalleryUiState
    data class Success(val mediaList: List<Media>) : GalleryUiState
    data class Error(val throwable: Throwable) : GalleryUiState
}
