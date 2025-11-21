package com.apps_moviles.proyecto_nuclear_kotlin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.apps_moviles.proyecto_nuclear_kotlin.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Login
    ) {

        // LOGIN
        composable<Login> {
            LoginScreen { name ->
                navController.navigate(Home(name = name))
            }
        }

        // HOME
        composable<Home> { backEntry ->
            val name = backEntry.arguments?.getString("name") ?: "Usuario"

            HomeScreen(
                username = name,
                onLogout = {
                    navController.navigate(Login)
                },
                onCategoryClick = { selectedCategory ->
                    navController.navigate(CategoryRoute(category = selectedCategory))
                },
                onProductClick = { product ->
                    navController.navigate(
                        DetailRoute(
                            name = product.name,
                            category = product.category,
                            owner = product.owner,
                            imageUrl = product.imageUrl,
                            status = product.status
                        )
                    )
                }
            )
        }

        // CATEGORY
        composable<CategoryRoute> { entry ->
            val categoryName = entry.arguments?.getString("category") ?: "Todos"

            CategoryScreen(
                categoryName = categoryName,
                onBack = { navController.popBackStack() },
                onOpenMenu = { },
                onProductClick = { product ->
                    navController.navigate(
                        DetailRoute(
                            name = product.name,
                            category = product.category,
                            owner = product.owner,
                            imageUrl = product.imageUrl,
                            status = product.status
                        )
                    )
                }
            )
        }

        // DETAIL SCREEN
        composable<DetailRoute> { entry ->
            val args = entry.arguments!!

            val product = Product(
                name = args.getString("name")!!,
                category = args.getString("category")!!,
                owner = args.getString("owner")!!,
                imageUrl = args.getString("imageUrl")!!,
                status = args.getString("status")!!
            )

            DetailScreen(
                product = product,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
