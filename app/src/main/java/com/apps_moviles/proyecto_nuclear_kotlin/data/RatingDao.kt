package com.apps_moviles.proyecto_nuclear_kotlin.data

import androidx.room.*
import com.apps_moviles.proyecto_nuclear_kotlin.model.Rating

@Dao
interface RatingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRating(rating: Rating)

    @Transaction
    @Query("SELECT * FROM ratings")
    suspend fun getAll(): List<Rating>

    @Transaction
    @Query("SELECT * FROM ratings WHERE id = :id")
    suspend fun getById(id: Int): Rating?

    @Transaction
    @Query("SELECT * FROM ratings WHERE user_id = :userId")
    suspend fun getByUserId(userId: Int): List<Rating>
}