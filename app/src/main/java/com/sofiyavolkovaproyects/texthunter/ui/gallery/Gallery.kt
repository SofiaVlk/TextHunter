package com.sofiyavolkovaproyects.texthunter.ui.gallery

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.Images
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sofiyavolkovaproyects.texthunter.ui.components.RequiresMediaImagesPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GalleryScreen(icon: Int, description: String) {
    RequiresMediaImagesPermission {
        val galleryState = remember { mutableStateListOf<Media>() }

        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(true) {
            coroutineScope.launch {
                galleryState.addAll(
                    getImages(context.contentResolver)
                        .filter { it.mimeType == "image/jpeg" })
            }
        }

        if (galleryState.isNotEmpty()) {
            PhotoGrid(galleryState)
        }
    }
}

data class Media(
    val uri: Uri,
    val name: String,
    val size: Long,
    val mimeType: String,
)

// Run the querying logic in a coroutine outside of the main thread to keep the app responsive.
// Keep in mind that this code snippet is querying only images of the shared storage.
suspend fun getImages(contentResolver: ContentResolver): List<Media> = withContext(Dispatchers.IO) {
    val projection = arrayOf(
        Images.Media._ID,
        Images.Media.DISPLAY_NAME,
        Images.Media.SIZE,
        Images.Media.MIME_TYPE,
    )

    val collectionUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Query all the device storage volumes instead of the primary only
        Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        Images.Media.EXTERNAL_CONTENT_URI
    }

    val selection = Images.Media.MIME_TYPE + "=? AND " + Images.Media.SIZE + ">?"

    val selectionArgs = arrayOf("image/jpeg","800000")

    val images = mutableListOf<Media>()

    contentResolver.query(
        /* uri = */ collectionUri,
        /* projection = */ projection,
        /* selection = */ selection,
        /* selectionArgs = */ selectionArgs,
        /* sortOrder = */ "${Images.Media.DATE_ADDED} DESC"
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(Images.Media._ID)
        val displayNameColumn = cursor.getColumnIndexOrThrow(Images.Media.DISPLAY_NAME)
        val sizeColumn = cursor.getColumnIndexOrThrow(Images.Media.SIZE)
        val mimeTypeColumn = cursor.getColumnIndexOrThrow(Images.Media.MIME_TYPE)

        while (cursor.moveToNext()) {
            val uri = ContentUris.withAppendedId(collectionUri, cursor.getLong(idColumn))
            val name = cursor.getString(displayNameColumn)
            val size = cursor.getLong(sizeColumn)
            val mimeType = cursor.getString(mimeTypeColumn)

            val image = Media(uri, name, size, mimeType)
            images.add(image)
        }
    }

    return@withContext images
}

@Composable
fun PhotoGrid(photos: List<Media>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(photos) { media ->
                AsyncImage(
                    modifier = Modifier
                        .background(Color.DarkGray)
                        .fillMaxSize().padding(3.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(media.uri)
                        .crossfade(true)
                        .build(),
                    contentDescription = media.name,
                    contentScale = ContentScale.Fit
                )
            }
    }
}
