package com.apps_moviles.proyecto_nuclear_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.apps_moviles.proyecto_nuclear_kotlin.navigation.NavigationWrapper
import com.apps_moviles.proyecto_nuclear_kotlin.ui.theme.Proyecto_nuclear_kotlinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Proyecto_nuclear_kotlinTheme{
                NavigationWrapper()
            }
        }
    }
}