package com.apps_moviles.proyecto_nuclear_kotlin.model

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithRelations(

    @Embedded
    val user: User,

    @Relation(
        parentColumn = "id",
        entityColumn = "user_id",
        entity = Item::class
    )
    val items: List<ItemRatingRelations> = emptyList()
)
