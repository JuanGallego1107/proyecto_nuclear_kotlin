package com.apps_moviles.proyecto_nuclear_kotlin.navigation

import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object PublishedItems

@Serializable
object InterestItems

@Serializable
data class Home(val name: String)

@Serializable
data class CategoryRoute(val category: String)

@Serializable
data class DetailRoute(
    val itemId: Int
)