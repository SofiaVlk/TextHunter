package com.sofiyavolkovaproyects.texthunter.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DocumentItem(
    val title: String = "",
    val body: String= ""
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}