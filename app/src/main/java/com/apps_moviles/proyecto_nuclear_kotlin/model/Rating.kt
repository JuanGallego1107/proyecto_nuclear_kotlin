package com.apps_moviles.proyecto_nuclear_kotlin.model

import androidx.room.*
import java.util.Date

@Entity(
    tableName = "ratings",
    foreignKeys = [
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["item_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("item_id"),
        Index("user_id")
    ]
)
data class Rating(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "item_id") val itemId: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
    val rating: Int, // 1–5
    val comments: String?,
    @ColumnInfo(name = "rating_date") val ratingDate: String
)
