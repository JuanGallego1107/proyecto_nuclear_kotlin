package com.apps_moviles.proyecto_nuclear_kotlin.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PublicationType::class,
            parentColumns = ["id"],
            childColumns = ["publication_type_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ItemState::class,
            parentColumns = ["id"],
            childColumns = ["state_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("user_id"),
        Index("publication_type_id"),
        Index("state_id")
    ]
)
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "publication_type_id")
    val publicationTypeId: Int,

    @ColumnInfo(name = "state_id")
    val stateId: Int,

    val description: String,
    val category: String,

    @ColumnInfo(name = "publication_date")
    val publicationDate: String,

    val title: String,

    val address: String,

    @ColumnInfo(name = "photo_path")
    val photoPath: String?
)

