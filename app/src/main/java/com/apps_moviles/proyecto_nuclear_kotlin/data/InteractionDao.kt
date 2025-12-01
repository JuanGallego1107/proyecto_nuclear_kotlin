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

    @Transaction
    suspend fun updateStates(id: Int, itemId: Int) {

        // 1. Actualizar todos los registros de la interacción con itemId
        updateStateByItemId(itemId, 3)

        // 2. Actualizar el registro de la interacción por su id
        updateStateById(id, 2)

        // 3. Actualizar el estado del item relacionado
        updateItemState(itemId, 2)
    }

    @Query("UPDATE interactions SET interaction_state_id = :stateId WHERE id = :id")
    suspend fun updateStateById(id: Int, stateId: Int)

    @Query("UPDATE interactions SET interaction_state_id = :stateId WHERE item_id = :itemId")
    suspend fun updateStateByItemId(itemId: Int, stateId: Int)

    @Query("UPDATE items SET state_id = :stateId WHERE id = :itemId")
    suspend fun updateItemState(itemId: Int, stateId: Int)
}
