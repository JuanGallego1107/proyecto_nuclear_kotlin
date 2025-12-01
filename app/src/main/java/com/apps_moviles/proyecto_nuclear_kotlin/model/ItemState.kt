package com.apps_moviles.proyecto_nuclear_kotlin.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_states")
data class ItemState(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)
