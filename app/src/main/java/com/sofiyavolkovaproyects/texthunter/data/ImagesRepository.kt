package com.sofiyavolkovaproyects.texthunter.data

import com.sofiyavolkovaproyects.texthunter.data.local.source.THLocalImageSource
import com.sofiyavolkovaproyects.texthunter.modelo.Media
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ImagesRepository{
    suspend fun getImages(): Flow<List<Media>>
    suspend fun deleteImage(media: Media)

}

class DefaultImagesRepository @Inject constructor(
    private val localImagesSource: THLocalImageSource
) : ImagesRepository{
    override suspend fun getImages(): Flow<List<Media>> = localImagesSource.getImageList()

    override suspend fun deleteImage(media: Media) {
        localImagesSource.removeImage(media)
    }

}