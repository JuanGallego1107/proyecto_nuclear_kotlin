package com.apps_moviles.proyecto_nuclear_kotlin.navigation

import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
data class Home(val name: String)

@Serializable
data class CategoryRoute(val category: String)

@Serializable
data class DetailRoute(
    val name: String,
    val category: String,
    val owner: String,
    val imageUrl: String,
    val status: String
)