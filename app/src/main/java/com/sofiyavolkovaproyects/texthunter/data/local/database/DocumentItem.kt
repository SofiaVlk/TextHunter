package com.sofiyavolkovaproyects.texthunter.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
//Estructura de datos o entidad de la tabla
@Entity
data class DocumentItem(
    val title: String = "",
    val body: String= ""
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}