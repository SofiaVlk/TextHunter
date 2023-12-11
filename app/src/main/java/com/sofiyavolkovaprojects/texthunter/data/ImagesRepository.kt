package com.sofiyavolkovaprojects.texthunter.data

import com.sofiyavolkovaprojects.texthunter.data.local.source.THLocalImageSource
import com.sofiyavolkovaprojects.texthunter.model.Media
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface ImagesRepository{
    suspend fun getImages(): Flow<List<Media>>
    suspend fun deleteImage(media: Media)

}
// recupera una lista de imágenes y elimina una imagen seleccionada
class DefaultImagesRepository @Inject constructor(
    private val localImagesSource: THLocalImageSource
) : ImagesRepository {
    override suspend fun getImages(): Flow<List<Media>> = localImagesSource.getImageList()

    override suspend fun deleteImage(media: Media) {
        localImagesSource.removeImage(media)
    }

}