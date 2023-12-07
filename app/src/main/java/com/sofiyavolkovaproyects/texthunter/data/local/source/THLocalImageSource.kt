package com.sofiyavolkovaproyects.texthunter.data.local.source

import android.content.Context
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.sofiyavolkovaproyects.texthunter.data.local.di.IoDispatcher
import com.sofiyavolkovaproyects.texthunter.model.Media
import com.sofiyavolkovaproyects.texthunter.ui.hunter.getOutputDirectory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface THLocalImageSource {
    suspend fun getImageList(): Flow<List<Media>>
    suspend fun removeImage(media: Media)
}

class DefaultTHLocalImageSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : THLocalImageSource {
    override suspend fun getImageList(): Flow<List<Media>> = getImages(context)

    override suspend fun removeImage(media: Media) {
        try {
            media.uri.toFile().delete()
        } catch (e: Exception) {
            Log.d(DefaultTHLocalImageSource::class.simpleName, "removeImage: ${e.message}")
        }
    }

    private suspend fun getImages(context: Context): Flow<List<Media>> = flow {
        val collectionUri = context.getOutputDirectory().listFiles()

        collectionUri?.let { uriList ->
            emit(uriList.mapNotNull {
                Media(
                    uri = it.toUri(),
                    name = it.nameWithoutExtension,
                    size = it.totalSpace,
                    mimeType = "image/jpeg"
                )
            })
        }

    }.flowOn(ioDispatcher)

}