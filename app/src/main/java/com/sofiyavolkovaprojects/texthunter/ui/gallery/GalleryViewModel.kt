package com.sofiyavolkovaprojects.texthunter.ui.gallery

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sofiyavolkovaprojects.texthunter.data.DefaultImagesRepository
import com.sofiyavolkovaprojects.texthunter.model.Media
import com.sofiyavolkovaprojects.texthunter.ui.gallery.GallerySideEffect.CaptureText
import com.sofiyavolkovaprojects.texthunter.ui.gallery.GallerySideEffect.NavigateToEdit
import com.sofiyavolkovaprojects.texthunter.ui.gallery.GalleryUIAction.OnClickCaptureImage
import com.sofiyavolkovaprojects.texthunter.ui.gallery.GalleryUIAction.OnClickDeleteImage
import com.sofiyavolkovaprojects.texthunter.ui.gallery.GalleryUIAction.OnErrorText
import com.sofiyavolkovaprojects.texthunter.ui.gallery.GalleryUIAction.OnSuccessText
import com.sofiyavolkovaprojects.texthunter.ui.gallery.GalleryUiState.Empty
import com.sofiyavolkovaprojects.texthunter.ui.gallery.GalleryUiState.Error
import com.sofiyavolkovaprojects.texthunter.ui.gallery.GalleryUiState.Loading
import com.sofiyavolkovaprojects.texthunter.ui.gallery.GalleryUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val imagesRepository: DefaultImagesRepository
) : ViewModel() {
    //privado para que el estado solo pueda ser modificado desde el viewmodel
    private val _uiState: MutableStateFlow<GalleryUiState> = MutableStateFlow(Loading)
    val uiState: StateFlow<GalleryUiState> get() = _uiState
    //privado para que el efecto solo pueda ser modificado desde el viewmodel
    private val _effect: Channel<GallerySideEffect> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        _uiState.update { Loading }
        getGalleryImages()
    }
//recupera del ropositorio una lista de imágenes
    private fun getGalleryImages() {
    //lanza una corrutina  dentro del ciclo de vida del viewModel cancelandose automaticamente si este finaliza su ciclo
        viewModelScope.launch {
            imagesRepository.getImages()
                .catch { _uiState.update { Error } }
                .collect { imageList ->
                    _uiState.update {
                        /*si se han recuperado imágenes y la lista está vacía, establecemos el estado a vacío,
                        estado se pone a success*/
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
            is OnClickDeleteImage -> {
                removeImage(media = action.media)
                getGalleryImages()
            }
            //actualiza el estado al puslsar el boton Capturar
            is OnClickCaptureImage -> {
                setEffect(CaptureText(action.uri))
            }

            OnErrorText -> {
                _uiState.update { Error }
            }

            is OnSuccessText -> {
                setEffect(NavigateToEdit(action.text))
            }
        }
    }

    private fun setEffect(effect: GallerySideEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
//Selección de efectos para galleryscreen
sealed interface GallerySideEffect {
    data class NavigateToEdit(val text: String) : GallerySideEffect
    data class CaptureText(val uri: Uri) : GallerySideEffect
}
//Estados para galleryscreen
sealed interface GalleryUiState {
    data object Loading : GalleryUiState
    data class Success(val mediaList: List<Media>) : GalleryUiState
    data object Error : GalleryUiState
    data object Empty : GalleryUiState
}
