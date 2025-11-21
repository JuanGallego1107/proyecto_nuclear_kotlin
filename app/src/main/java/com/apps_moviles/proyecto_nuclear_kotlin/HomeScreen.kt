package com.apps_moviles.proyecto_nuclear_kotlin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

// OBJETO PRODUCTO
data class Product(
    val name: String,
    val category: String,
    val owner: String,
    val imageUrl: String,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    username: String,
    onLogout: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onProductClick: (Product) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // LISTA DE PRODUCTOS
    val sampleProducts = listOf(
        Product("Cálculo I - Stewart", "Libros", "Juan P.",
            "https://images.unsplash.com/photo-1524995997946-a1c2e315a42f", "Disponible"),

        Product("Física Universitaria", "Libros", "Felipe A.",
            "https://images.unsplash.com/photo-1519682337058-a94d519337bc", "En negociación"),

        Product("Uniforme Deportivo", "Uniformes", "Laura M.",
            "https://images.unsplash.com/photo-1517849845537-4d257902454a", "Disponible"),

        Product("Mouse Gamer Logitech", "Tecnología", "Daniel R.",
            "https://images.unsplash.com/photo-1587202372775-e229f172b9d8", "Disponible"),

        Product("Audífonos Sony WH1000XM3", "Tecnología", "Sara L.",
            "https://images.unsplash.com/photo-1518444021407-2d306d9f0f9b", "En negociación"),

        Product("Pulsera Negra Hombre", "Accesorios", "Valentina T.",
            "https://images.unsplash.com/photo-1523275335684-37898b6baf30", "Disponible"),

        Product("Chaqueta Azul Talla M", "Ropa", "Carlos F.",
            "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab", "Disponible")
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Hola, $username",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )

                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text("Cerrar sesión", color = Color.White)
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(
                            Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "HumboldtCircular",
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
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
        ) { paddingValues ->

            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {

                // BUSCADOR
                item {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Buscar artículos...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "search")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Spacer(Modifier.height(20.dp))
                }

                // CATEGORÍAS
                item {
                    Text("Categorías populares", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))

                    val categories = listOf(
                        "Libros", "Uniformes", "Accesorios", "Tecnología",
                        "Ropa", "Hogar", "Deportes"
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(categories) { category ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFE8F0FE))
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                                    .clickable { onCategoryClick(category) }
                            ) {
                                Text(category, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                }

                // PRODUCTOS
                item {
                    Text("Artículos recientes", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                }

                items(sampleProducts) { product ->
                    ProductCardHome(product, onClick = { onProductClick(product) })
                    Spacer(Modifier.height(16.dp))
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun ProductCardHome(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {

            AsyncImage(
                model = product.imageUrl,
                contentDescription = "product image",
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.LightGray)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold)
                Text(product.category, color = Color.Gray)
                Text(product.owner, color = Color.Gray)
            }

            StatusChip(product.status)
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val color = when (status) {
        "Disponible" -> Color(0xFF2ECC71)
        "En negociación" -> Color(0xFFF4B400)
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(status, color = Color.White)
    }
}
