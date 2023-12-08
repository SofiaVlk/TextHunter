package com.sofiyavolkovaprojects.texthunter.model

import android.net.Uri

//Información referente a las imágenes en el dispositivo
data class Media(
    val uri: Uri,
    val name: String,
    val size: Long,
    val mimeType: String
)