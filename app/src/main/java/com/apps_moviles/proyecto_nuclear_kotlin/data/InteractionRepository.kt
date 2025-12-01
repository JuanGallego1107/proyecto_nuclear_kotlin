package com.apps_moviles.proyecto_nuclear_kotlin.data

import com.apps_moviles.proyecto_nuclear_kotlin.model.Interaction
import com.apps_moviles.proyecto_nuclear_kotlin.model.InteractionWithRelations

class InteractionRepository(private val dao: InteractionDao) {

    suspend fun insert(interaction: Interaction): Long =
        dao.insert(interaction)

    suspend fun update(interaction: Interaction) =
        dao.update(interaction)

    suspend fun delete(interaction: Interaction) =
        dao.delete(interaction)

    suspend fun getAll(): List<InteractionWithRelations> =
        dao.getAll()

    suspend fun getById(id: Int): InteractionWithRelations? =
        dao.getById(id)

    suspend fun getByUserId(userId: Int): List<InteractionWithRelations> =
        dao.getByUserId(userId)
}
