package com.apps_moviles.proyecto_nuclear_kotlin.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.apps_moviles.proyecto_nuclear_kotlin.model.ItemState
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemStateDao {

    @Query("SELECT * FROM item_states")
    fun getItemStates(): Flow<List<ItemState>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(itemState: ItemState)

    @Delete
    suspend fun delete(itemState: ItemState)

}
