package com.apps_moviles.proyecto_nuclear_kotlin.model

import androidx.room.Embedded
import androidx.room.Relation

data class ItemRatingRelations(

    @Embedded
    val item: Item,

    @Relation(
        parentColumn = "id",
        entityColumn = "item_id",
        entity = Rating::class
    )
    val ratings: List<Rating> = emptyList()
)
