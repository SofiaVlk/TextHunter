package com.sofiyavolkovaproyects.texthunter.ui.gallery


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.sofiyavolkovaproyects.texthunter.ui.components.CustomCircularProgressBar
import com.sofiyavolkovaproyects.texthunter.ui.components.RequiresMediaImagesPermission

@Composable
fun GalleryScreen(modifier: Modifier = Modifier, viewModel: GalleryViewModel = hiltViewModel()) {
    val galleryState by viewModel.uiState.collectAsStateWithLifecycle()
    RequiresMediaImagesPermission {

        when (galleryState) {
            GalleryUiState.Loading -> CustomCircularProgressBar()
            is GalleryUiState.Success -> PhotoGrid(
                modifier = modifier,
                photos = (galleryState as GalleryUiState.Success).mediaList,
                onClickItem = {})
            is GalleryUiState.Error -> Unit
        }
    }
}

@Composable
fun PhotoGrid(
    modifier: Modifier = Modifier,
    photos: List<Media>,
    borderColor: Color = MaterialTheme.colorScheme.background,
    onClickItem: (Media) -> Unit = {}
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    var itemSelected by remember { mutableStateOf("") }
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(12.dp),
    ) {

        items(photos) { media ->
            AsyncImage(
                modifier = Modifier
                    .padding(5.dp)
                    .clip(RoundedCornerShape(5))
                    .border(
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (itemSelected == media.name) primaryColor else borderColor
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
        }
    }
}
