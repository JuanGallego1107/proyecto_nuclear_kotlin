package com.apps_moviles.proyecto_nuclear_kotlin.model

import androidx.room.*

@Entity(
    tableName = "interactions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["item_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = InteractionState::class,
            parentColumns = ["id"],
            childColumns = ["interaction_state_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("user_id"),
        Index("item_id"),
        Index("interaction_state_id")
    ]
)
data class Interaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "interaction_state_id")
    val interactionStateId: Int,

    @ColumnInfo(name = "item_id")
    val itemId: Int,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "interaction_date")
    val interactionDate: String,

    val comment: String?
)
