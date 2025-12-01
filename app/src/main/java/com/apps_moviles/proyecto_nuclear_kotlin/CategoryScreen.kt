package com.apps_moviles.proyecto_nuclear_kotlin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.apps_moviles.proyecto_nuclear_kotlin.viewmodel.ItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    viewModel: ItemViewModel,
    categoryName: String,
    onBack: () -> Unit,
    onOpenMenu: () -> Unit,
    onProductClick: (Int) -> Unit
) {
    val filters = listOf("Todos", "Disponibles", "En negociación")
    var selectedFilter by remember { mutableStateOf("Todos") }

    // Leer userId desde el UserViewModel
    val userId by viewModel.loggedUserId.collectAsState(initial = null)

    // Cargar items por categoria al abrir la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadItemsByCategory(category = categoryName, userId = userId)
    }

    // Items reales desde Room
    val items = viewModel.items.collectAsState()

    // FILTRO POR CATEGORÍA
    //val categoryFiltered = allProducts.filter {
    //    it.category == categoryName || categoryName == "Todos"
    //}
//
    //// FILTRO POR ESTADO
    //val products = categoryFiltered.filter {
    //    selectedFilter == "Todos" || it.status == selectedFilter
    //}

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            categoryName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "menu", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF003D6A)
                )
            )
        },
        floatingActionButton = {
            ItemFormFab(itemViewModel = viewModel)
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            // CHIPS DE FILTRO
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    filters.forEach { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // INFORMACIÓN DE RESULTADOS
            item {
                Text(
                    "${items.value.size} artículos encontrados",
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(16.dp))
            }

            // LISTA DE ARTÍCULOS
            items(items.value) { item ->
                ProductCardHome(item, onClick = { onProductClick(item.item.id) })
                Spacer(Modifier.height(16.dp))
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
