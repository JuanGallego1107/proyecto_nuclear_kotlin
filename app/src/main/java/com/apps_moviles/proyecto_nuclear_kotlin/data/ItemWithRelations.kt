package com.apps_moviles.proyecto_nuclear_kotlin.data

import androidx.room.Embedded
import androidx.room.Relation
import com.apps_moviles.proyecto_nuclear_kotlin.model.Interaction
import com.apps_moviles.proyecto_nuclear_kotlin.model.InteractionWithRelations
import com.apps_moviles.proyecto_nuclear_kotlin.model.Item
import com.apps_moviles.proyecto_nuclear_kotlin.model.ItemInteractionRelations
import com.apps_moviles.proyecto_nuclear_kotlin.model.ItemState
import com.apps_moviles.proyecto_nuclear_kotlin.model.PublicationType
import com.apps_moviles.proyecto_nuclear_kotlin.model.Rating
import com.apps_moviles.proyecto_nuclear_kotlin.model.User
import com.apps_moviles.proyecto_nuclear_kotlin.model.UserWithRelations

data class ItemWithRelations(

    @Embedded
    val item: Item,

    @Relation(
        parentColumn = "user_id",
        entityColumn = "id",
        entity = User::class
    )
    val user: UserWithRelations,

    @Relation(
        parentColumn = "publication_type_id",
        entityColumn = "id"
    )
    val publicationType: PublicationType,

    @Relation(
        parentColumn = "state_id",
        entityColumn = "id"
    )
    val state: ItemState,

    @Relation(
        parentColumn = "id",
        entityColumn = "item_id",
        entity = Interaction::class
    )
    val interactions: List<ItemInteractionRelations> = emptyList(),

    @Relation(
        parentColumn = "id",
        entityColumn = "item_id",
        entity = Rating::class
    )
    val ratings: List<Rating> = emptyList()
)
