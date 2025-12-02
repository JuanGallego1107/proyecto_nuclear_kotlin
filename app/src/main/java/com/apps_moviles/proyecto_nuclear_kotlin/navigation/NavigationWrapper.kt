package com.apps_moviles.proyecto_nuclear_kotlin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.apps_moviles.proyecto_nuclear_kotlin.*
import androidx.compose.runtime.*
import com.apps_moviles.proyecto_nuclear_kotlin.viewmodel.InteractionViewModel
import com.apps_moviles.proyecto_nuclear_kotlin.viewmodel.ItemViewModel
import com.apps_moviles.proyecto_nuclear_kotlin.viewmodel.RatingViewModel
import com.apps_moviles.proyecto_nuclear_kotlin.viewmodel.UserViewModel

@Composable
fun NavigationWrapper(
    userViewModel: UserViewModel,
    itemViewModel: ItemViewModel,
    interactionViewModel: InteractionViewModel,
    ratingViewModel: RatingViewModel
) {
    val navController = rememberNavController()

    // Leer userId desde el UserViewModel
    val userId by userViewModel.loggedUserId.collectAsState(initial = null)

    // Leer userFullName del UserViewModel
    val fullname by userViewModel.loggedUserName.collectAsState(initial = "")

    // Elegir pantalla de inicio según si hay usuario logueado o no
    val startDestination = if (userId != null) Home(name = fullname ?: "Usuario") else Login

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // LOGIN
        composable<Login> {
            LoginScreen(
                onLoginSuccess = { name ->
                    navController.navigate(Home(name = name)) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                userViewModel = userViewModel
            )
        }

        // HOME
        composable<Home> { backEntry ->
            val name = backEntry.arguments?.getString("name") ?: "Usuario"

            HomeScreen(
                viewModel = itemViewModel,
                username = name,
                onLogout = {
                    userViewModel.clearLoggedUser()
                    navController.navigate(Login) {
                        popUpTo(Home(name = name)) { inclusive = true }
                    }
                },
                onCategoryClick = { selectedCategory ->
                    navController.navigate(CategoryRoute(category = selectedCategory))
                },
                onItemClick = { itemId ->
                    navController.navigate(DetailRoute(itemId = itemId))
                },
                onOpenInterest = {
                    navController.navigate(InterestItems)
                },
                onOpenPublished = {
                    navController.navigate(PublishedItems)
                }
            )
        }

        // CATEGORY
        composable<CategoryRoute> { entry ->
            val categoryName = entry.arguments?.getString("category") ?: "Todos"

            CategoryScreen(
                viewModel = itemViewModel,
                categoryName = categoryName,
                onBack = { navController.popBackStack() },
                onOpenMenu = { },
                onProductClick = { itemId ->
                    navController.navigate(DetailRoute(itemId = itemId))
                }
            )
        }

        // DETAIL SCREEN
        composable<DetailRoute> { entry ->
            val itemId = entry.arguments?.getInt("itemId") ?: 1

            DetailScreen(
                viewModel = itemViewModel,
                interactionViewModel= interactionViewModel,
                itemId = itemId,
                onBack = { navController.popBackStack() }
            )
        }

        // INTEREST ITEMS
        composable<InterestItems> {
            InterestItemsScreen(
                interactionViewModel = interactionViewModel,
                ratingViewModel = ratingViewModel,
                onBack = { navController.popBackStack() },
                onInteractionClick = { },
            )
        }

        // PUBLISHED ITEMS
        composable<PublishedItems> {
            PublishedItemsScreen(
                viewModel = itemViewModel,
                onItemClick = { itemId ->
                    navController.navigate(DetailRoute(itemId = itemId))
                },
                onBack = { navController.popBackStack() },)
        }

    }
}

