package com.apps_moviles.proyecto_nuclear_kotlin.data

import com.apps_moviles.proyecto_nuclear_kotlin.model.Rating

class RatingRepository(private val dao: RatingDao) {

    suspend fun insert(rating: Rating) = dao.insertRating(rating)

    suspend fun getAll() = dao.getAll()

    suspend fun getById(id: Int) = dao.getById(id)

    suspend fun getByUserId(userId: Int) = dao.getByUserId(userId)
}
