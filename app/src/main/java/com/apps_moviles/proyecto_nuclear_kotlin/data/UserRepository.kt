package com.apps_moviles.proyecto_nuclear_kotlin.data

import com.apps_moviles.proyecto_nuclear_kotlin.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun delete(user: User) {
        userDao.delete(user)
    }

    suspend fun login(email: String, password: String): User? {
        val user = userDao.getUserByEmail(email)
        return if (user != null && user.password == password) user else null
    }

    suspend fun findUser(email: String): User? {
        return userDao.getUserByEmail(email)
    }
}
