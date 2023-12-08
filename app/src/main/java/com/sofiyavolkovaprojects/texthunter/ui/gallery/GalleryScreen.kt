package com.sofiyavolkovaprojects.texthunter.ui.gallery


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sofiyavolkovaprojects.texthunter.R.drawable
import com.sofiyavolkovaprojects.texthunter.R.string
import com.sofiyavolkovaprojects.texthunter.model.Media
import com.sofiyavolkovaprojects.texthunter.ui.components.ButtonBasic
import com.sofiyavolkovaprojects.texthunter.ui.components.CustomCircularProgressBar
import com.sofiyavolkovaprojects.texthunter.ui.components.InfoMessage
import com.sofiyavolkovaprojects.texthunter.ui.components.RequiresMediaImagesPermission
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
import com.sofiyavolkovaprojects.texthunter.ui.hunter.textRecognizerProcess
import com.sofiyavolkovaprojects.texthunter.ui.navigation.NavigationParams.EditText
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun GalleryScreen(
  //Acción de navegación
 navigateTo: (String) -> Unit = {},
 //se inyecta el viewmodel
 viewModel: GalleryViewModel = hiltViewModel()
) {
    val galleryState by viewModel.uiState.collectAsStateWithLifecycle()
    val effectFlow = viewModel.effect
    val context = LocalContext.current

    //Recuerda el estado. Por defecto tiene el valor de una lista vacía de Media.
    var mediaList: List<Media> by remember {
        mutableStateOf(emptyList())
    }

    // Escucha efectos secundarios del viewmodel
    //Se lanza una vez
    LaunchedEffect(true) {
        //Dependiendo del efecto que se ha ejecutado, realiza una acción
        effectFlow.onEach { effect ->
            when (effect) {
                //Navega a la pantalla de EditText pasandole por parametro el texto extraido
                is NavigateToEdit -> navigateTo(EditText.createNavTextRoute(effect.text))
                //Extrae el texto de la imagen seleccionada en la galeria
                is CaptureText -> {
                    context.textRecognizerProcess(effect.uri)
                        .addOnSuccessListener { result ->
                            //Si ha ido bien: se comunica con el vm através de una acción indicando
                            // que la extracción del texto ha sido correcta
                            //y le pasa por construcción ese texto
                            viewModel.handlerAction(OnSuccessText(result.text))
                        }.addOnFailureListener {
                            //En el caso de errro, avisa al vm por medio de la acción de que
                            // ha surgido un error al extraer el texto
                            viewModel.handlerAction(OnErrorText)
                        }
                }
            }
        }.collect()
    }

    /*
    Comprueba si hay permiso para accedr a la galería, de lo contrario, muestra un pop-up
    solicitando aprobación para estos permisos. Si los permisos están concedidos pinta el contenido
     */
    RequiresMediaImagesPermission {

        //Evalua el estado de la pantalla
        when (galleryState) {
            //Si está en el estado Loading, se muestra la animación de barra circular
            Loading -> CustomCircularProgressBar()
           //Si ha recuperado las imagenes con exito, vuelca la respuesta en una variable de estado que servirá para pintar la galería de imágenes
            is Success -> mediaList = (galleryState as Success).mediaList

            //Muestra un mensaje de error
            Error -> InfoMessage(
                imagePainter = painterResource(drawable.error_message_01),
                title = stringResource(string.th_error_title),
                bodyText = stringResource(string.th_error_body_message)
            )
            //Muestra un mensaje de que no se han encontrado imágenes guardadas.
            Empty -> {
                mediaList = emptyList()
                InfoMessage(
                    title = stringResource(string.th_gallery_screen_empty_message_title),
                    bodyText = stringResource(string.th_gallery_screen_empty_message_body)
                )
            }
        }
    //Muestra una galería de imágenes
        PhotoGrid(
            photos = mediaList,
            onClickItem = {action ->
                viewModel.handlerAction(action)}
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoGrid(
    photos: List<Media>,
    onClickItem: (GalleryUIAction) -> Unit = {}
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    var itemSelected by remember { mutableStateOf("") }
    val verticalGridState = rememberLazyGridState()
    LazyVerticalGrid(
        state = verticalGridState,
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(7.dp),
    ) {

        items(photos) { media ->
            val isItemSelected = itemSelected == media.name
            Box(modifier = Modifier.animateItemPlacement()) {
               //Componenete de la librería Coil para mostrar imágenes de forma asíncrona
                AsyncImage(
                    modifier = Modifier
                        .padding(5.dp)
                        .clip(RoundedCornerShape(5))
                        .border(
                            border = BorderStroke(
                                width = 5.dp,
                                color = if (isItemSelected) primaryColor else Color.Transparent
                            ),
                            shape = RoundedCornerShape(5)
                        )
                        .height(250.dp)
                        .clickable {
                            itemSelected = media.name
                        },
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(media.uri)
                        .crossfade(true)
                        .build(),
                    contentDescription = media.name,
                    contentScale = ContentScale.Crop
                )
                //Muestra una animación cuando isItemSelected es verdadero
                AnimatedVisibility(
                    visible = isItemSelected,
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { it }
                    ),
                    exit = fadeOut() + slideOutVertically(
                        targetOffsetY = { it }
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .height(250.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly

                    ) {
                        ButtonBasic(
                            text = "Capturar",
                            icon = Icons.Default.CameraAlt,
                            onClick = {
                                onClickItem(OnClickCaptureImage(media.uri))
                            }
                        )
                        ButtonBasic(
                            text = "Eliminar",
                            icon = Icons.Default.DeleteForever,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            onClick = { onClickItem(OnClickDeleteImage(media)) }
                        )
                    }

                }
            }


        }
    }
}
