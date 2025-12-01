package com.apps_moviles.proyecto_nuclear_kotlin.data


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.apps_moviles.proyecto_nuclear_kotlin.model.PublicationType
import kotlinx.coroutines.flow.Flow

@Dao
interface PublicationTypeDao {

    @Query("SELECT * FROM publication_types")
    fun getPublicationTypes(): Flow<List<PublicationType>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(publicationType: PublicationType)

    @Delete
    suspend fun delete(publicationType: PublicationType)

}