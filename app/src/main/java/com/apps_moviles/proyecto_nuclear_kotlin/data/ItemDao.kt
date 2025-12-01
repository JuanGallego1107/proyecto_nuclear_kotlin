package com.apps_moviles.proyecto_nuclear_kotlin.data

import androidx.room.*
import com.apps_moviles.proyecto_nuclear_kotlin.model.Item

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item): Long

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Transaction
    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItemsById(id: Int): ItemWithRelations?

    @Transaction
    @Query("SELECT * FROM items WHERE user_id != :userId AND state_id = 1")
    suspend fun getAllItems(userId: Int): List<ItemWithRelations>

    @Transaction
    @Query("SELECT * FROM items WHERE user_id = :userId")
    suspend fun getItemsByUser(userId: Int): List<ItemWithRelations>

    @Transaction
    @Query("SELECT * FROM items WHERE category = :category AND user_id != :userId AND state_id = 1")
    suspend fun getItemsByCategory(category: String, userId: Int): List<ItemWithRelations>
}
