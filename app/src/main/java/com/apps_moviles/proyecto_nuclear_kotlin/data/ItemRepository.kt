package com.apps_moviles.proyecto_nuclear_kotlin.data

import com.apps_moviles.proyecto_nuclear_kotlin.model.Item

class ItemRepository(private val itemDao: ItemDao) {

    suspend fun insert(item: Item): Long {
        return itemDao.insert(item)
    }

    suspend fun update(item: Item) {
        itemDao.update(item)
    }

    suspend fun delete(item: Item) {
        itemDao.delete(item)
    }

    suspend fun getItemById(id: Int): ItemWithRelations? {
        return itemDao.getItemsById(id)
    }

    suspend fun getAllItems(userId: Int): List<ItemWithRelations> {
        return itemDao.getAllItems(userId)
    }

    suspend fun getItemsByUser(userId: Int): List<ItemWithRelations> {
        return itemDao.getItemsByUser(userId)
    }

    suspend fun getItemsByCategory(category: String, userId: Int): List<ItemWithRelations> {
        return itemDao.getItemsByCategory(category, userId)
    }
}
