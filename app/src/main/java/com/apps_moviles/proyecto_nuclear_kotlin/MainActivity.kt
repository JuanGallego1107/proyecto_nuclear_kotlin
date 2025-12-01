package com.apps_moviles.proyecto_nuclear_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.apps_moviles.proyecto_nuclear_kotlin.data.AppDatabase
import com.apps_moviles.proyecto_nuclear_kotlin.data.InteractionRepository
import com.apps_moviles.proyecto_nuclear_kotlin.data.ItemRepository
import com.apps_moviles.proyecto_nuclear_kotlin.data.UserPreferences
import com.apps_moviles.proyecto_nuclear_kotlin.data.UserRepository
import com.apps_moviles.proyecto_nuclear_kotlin.navigation.NavigationWrapper
import com.apps_moviles.proyecto_nuclear_kotlin.ui.theme.Proyecto_nuclear_kotlinTheme
import com.apps_moviles.proyecto_nuclear_kotlin.viewmodel.InteractionViewModel
import com.apps_moviles.proyecto_nuclear_kotlin.viewmodel.InteractionViewModel.InteractionViewModelFactory
import com.apps_moviles.proyecto_nuclear_kotlin.viewmodel.ItemViewModel
import com.apps_moviles.proyecto_nuclear_kotlin.viewmodel.ItemViewModelFactory
import com.apps_moviles.proyecto_nuclear_kotlin.viewmodel.UserViewModel
import com.apps_moviles.proyecto_nuclear_kotlin.viewmodel.UserViewModelFactory

class MainActivity : ComponentActivity() {

    lateinit var userPrefs: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userPrefs = UserPreferences(this)

        // Initializa base de datos local
        val database = AppDatabase.getInstance(this)

        // Inicializa view model de usuarios
        val userRepository = UserRepository(database.userDao())
        val userFactory = UserViewModelFactory(userRepository, userPrefs)
        val userViewModel = ViewModelProvider(this, userFactory)[UserViewModel::class.java]

        // Inicializa view model de items
        val itemRepository = ItemRepository(database.itemDao())
        val itemFactory = ItemViewModelFactory(itemRepository, userPrefs)
        val itemViewModel = ViewModelProvider(this, itemFactory)[ItemViewModel::class.java]

        // Inicializa view model de interacciones
        val interactionRepository = InteractionRepository(database.interactionDao())
        val interactionFactory = InteractionViewModelFactory(interactionRepository, userPrefs)
        val interactionViewModel = ViewModelProvider(this, interactionFactory)[InteractionViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            Proyecto_nuclear_kotlinTheme {
                NavigationWrapper(userViewModel, itemViewModel, interactionViewModel)
            }
        }
    }
}
