package com.apps_moviles.proyecto_nuclear_kotlin.model

import androidx.room.Embedded
import androidx.room.Relation

data class InteractionWithRelations(
    @Embedded val interaction: Interaction,

    @Relation(
        parentColumn = "user_id",
        entityColumn = "id"
    )
    val user: User,

    @Relation(
        parentColumn = "item_id",
        entityColumn = "id",
        entity = Item::class
    )
    val item: ItemRatingRelations,

    @Relation(
        parentColumn = "interaction_state_id",
        entityColumn = "id"
    )
    val state: InteractionState
)
