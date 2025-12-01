package com.apps_moviles.proyecto_nuclear_kotlin.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "publication_types")
data class PublicationType(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)