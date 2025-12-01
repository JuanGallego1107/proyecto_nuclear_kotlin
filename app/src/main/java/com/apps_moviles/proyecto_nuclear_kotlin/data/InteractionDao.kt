package com.apps_moviles.proyecto_nuclear_kotlin.data

import androidx.room.*
import com.apps_moviles.proyecto_nuclear_kotlin.model.Interaction
import com.apps_moviles.proyecto_nuclear_kotlin.model.InteractionWithRelations

@Dao
interface InteractionDao {

    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(interaction: Interaction): Long

    // Update
    @Update
    suspend fun update(interaction: Interaction)

    // Delete
    @Delete
    suspend fun delete(interaction: Interaction)

    // Obtener todas con relaciones
    @Transaction
    @Query("SELECT * FROM interactions")
    suspend fun getAll(): List<InteractionWithRelations>

    // Obtener por ID con relaciones
    @Transaction
    @Query("SELECT * FROM interactions WHERE id = :id")
    suspend fun getById(id: Int): InteractionWithRelations?

    // Obtener por usuario con relaciones
    @Transaction
    @Query("SELECT * FROM interactions WHERE user_id = :userId")
    suspend fun getByUserId(userId: Int): List<InteractionWithRelations>
}
