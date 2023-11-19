package com.sofiyavolkovaproyects.texthunter.ui.gallery


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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sofiyavolkovaproyects.texthunter.modelo.Media
import com.sofiyavolkovaproyects.texthunter.ui.components.ButtonBasic
import com.sofiyavolkovaproyects.texthunter.ui.components.CustomCircularProgressBar
import com.sofiyavolkovaproyects.texthunter.ui.components.MessageEmptyState
import com.sofiyavolkovaproyects.texthunter.ui.components.RequiresMediaImagesPermission
import com.sofiyavolkovaproyects.texthunter.ui.gallery.GalleryUiState.Empty
import com.sofiyavolkovaproyects.texthunter.ui.gallery.GalleryUiState.Error
import com.sofiyavolkovaproyects.texthunter.ui.gallery.GalleryUiState.Loading
import com.sofiyavolkovaproyects.texthunter.ui.gallery.GalleryUiState.Success

@Composable
fun GalleryScreen(modifier: Modifier = Modifier, viewModel: GalleryViewModel = hiltViewModel()) {
    val galleryState by viewModel.uiState.collectAsStateWithLifecycle()
    RequiresMediaImagesPermission {

        when (galleryState) {
            Loading -> CustomCircularProgressBar()
            is Success -> PhotoGrid(
                modifier = modifier,
                photos = (galleryState as Success).mediaList,
                onClickItem = {},
                onDeleteClick = { action ->
                    viewModel.handlerAction(action)
                }
            )

            is Error -> Unit
            Empty -> MessageEmptyState(title = "No se encuentran fotografia guardadas.", bodyText = "Según vayas haciendop fotografias para capturar textos se irán mostrando en esta sección, podra volver a utilizar la imagen para extraer su texto o eliminarla si no la necesita mas.")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoGrid(
    photos: List<Media>,
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.background,
    onClickItem: (Media) -> Unit = {},
    onDeleteClick: (GalleryUIAction) -> Unit = {}
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
                            onClickItem(media)
                            itemSelected = media.name
                        },
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(media.uri)
                        .crossfade(true)
                        .build(),
                    contentDescription = media.name,
                    contentScale = ContentScale.Crop
                )

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
                            onClick = {}
                        )
                        ButtonBasic(
                            text = "Eliminar",
                            icon = Icons.Default.DeleteForever,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            onClick = { onDeleteClick(GalleryUIAction.OnClickDeleteImage(media)) }
                        )
                    }

                }
            }


        }
    }
}
