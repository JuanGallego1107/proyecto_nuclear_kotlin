package com.apps_moviles.proyecto_nuclear_kotlin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    categoryName: String,
    onBack: () -> Unit,
    onOpenMenu: () -> Unit,
    onProductClick: (Product) -> Unit
) {
    val filters = listOf("Todos", "Disponibles", "En negociación")
    var selectedFilter by remember { mutableStateOf("Todos") }

    // 🔥 DATOS OPTIMIZADOS CON IMÁGENES REALES
    val allProducts = listOf(
        Product("Cálculo I - Stewart", "Libros", "Juan P.",
            "https://images.unsplash.com/photo-1524995997946-a1c2e315a42f", "Disponible"),

        Product("Física Universitaria", "Libros", "Felipe A.",
            "https://images.unsplash.com/photo-1519682337058-a94d519337bc", "Disponible"),

        Product("Uniforme Deportivo", "Uniformes", "Laura M.",
            "https://images.unsplash.com/photo-1517849845537-4d257902454a", "Disponible"),

        Product("Mochila Universitaria", "Accesorios", "Martín G.",
            "https://images.unsplash.com/photo-1509114397022-ed747cca3f65", "En negociación"),

        Product("Mouse Gamer Logitech", "Tecnología", "Daniel R.",
            "https://images.unsplash.com/photo-1587202372775-e229f172b9d8", "Disponible"),

        Product("Chaqueta Azul Talla M", "Ropa", "Carlos F.",
            "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab", "En negociación")
    )

    // 📌 FILTRO POR CATEGORÍA
    val categoryFiltered = allProducts.filter {
        it.category == categoryName || categoryName == "Todos"
    }

    // 📌 FILTRO POR ESTADO
    val products = categoryFiltered.filter {
        selectedFilter == "Todos" || it.status == selectedFilter
    }

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
            ItemFormFab()
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            // 🔶 CHIPS DE FILTRO
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

            // 🔶 INFORMACIÓN DE RESULTADOS
            item {
                Text(
                    "${products.size} artículos encontrados",
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(16.dp))
            }

            // 🔶 LISTA DE ARTÍCULOS
            items(products) { item ->
                ProductCardHome(item, onClick = { onProductClick(item) })
                Spacer(Modifier.height(16.dp))
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
