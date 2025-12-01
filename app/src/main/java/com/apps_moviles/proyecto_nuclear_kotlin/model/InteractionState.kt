package com.apps_moviles.proyecto_nuclear_kotlin.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interaction_states")
data class InteractionState(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)