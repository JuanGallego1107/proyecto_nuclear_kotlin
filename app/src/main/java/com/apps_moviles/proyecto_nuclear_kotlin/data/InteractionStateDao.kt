package com.apps_moviles.proyecto_nuclear_kotlin.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.apps_moviles.proyecto_nuclear_kotlin.model.InteractionState
import kotlinx.coroutines.flow.Flow

@Dao
interface InteractionStateDao {

    @Query("SELECT * FROM interaction_states")
    fun getInteractionStates(): Flow<List<InteractionState>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(interactionState: InteractionState)

    @Delete
    suspend fun delete(interactionState: InteractionState)

}
