package com.apps_moviles.proyecto_nuclear_kotlin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.apps_moviles.proyecto_nuclear_kotlin.data.ItemWithRelations
import com.apps_moviles.proyecto_nuclear_kotlin.model.InsertItemEnum
import com.apps_moviles.proyecto_nuclear_kotlin.viewmodel.ItemViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ItemViewModel,
    username: String,
    onLogout: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onItemClick: (Int) -> Unit,
    onOpenInterest: () -> Unit,
    onOpenPublished: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Leer userId desde el UserViewModel
    val userId by viewModel.loggedUserId.collectAsState(initial = null)

    // Cargar items al abrir la pantalla
    LaunchedEffect(userId) {
        viewModel.loadItems(userId)
    }

    // Items reales desde Room
    val items = viewModel.items.collectAsState()

    // Estado local para el texto del buscador
    var searchText by remember { mutableStateOf("") }

    // Filtrar en la vista
    val filteredItems = items.value.filter { item ->
        item.item.title.contains(searchText, ignoreCase = true)
    }

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFFF7F9FC)
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                // Encabezado del usuario
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "user",
                        tint = Color(0xFF003D6A),
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Hola",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                        Text(
                            text = username,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Divider()

                // ⭐ Artículos de interés
                DrawerItem(
                    text = "Artículos de interés",
                    icon = Icons.Default.Favorite,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onOpenInterest()
                    }
                )

                // 📦 Mis publicaciones
                DrawerItem(
                    text = "Mis publicaciones",
                    icon = Icons.Default.List,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onOpenPublished()
                    }
                )

                Divider(Modifier.padding(vertical = 12.dp))

                // ❌ Cerrar sesión
                DrawerItem(
                    text = "Cerrar sesión",
                    icon = Icons.Default.ExitToApp,
                    textColor = Color(0xFFD32F2F),
                    iconTint = Color(0xFFD32F2F),
                    onClick = onLogout
                )
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
                ItemFormFab(itemViewModel = viewModel, source = InsertItemEnum.ALL_ITEMS)
            },
            floatingActionButtonPosition = FabPosition.End,
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus()
                }
            }
        ) { paddingValues ->

            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {

                // BUSCADOR
                item {

                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = {
                            Text(
                                "Buscar artículos...",
                                color = Color.Gray
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = Color.Gray
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .focusRequester(focusRequester)
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

                items(filteredItems) { item ->
                    ProductCardHome(item, onClick = { onItemClick(item.item.id) })
                    Spacer(Modifier.height(16.dp))
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun DrawerItem(
    text: String,
    icon: ImageVector,
    textColor: Color = Color.Black,
    iconTint: Color = Color(0xFF003D6A),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(Modifier.width(16.dp))

        Text(
            text,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor
        )
    }
}


@Composable
fun ProductCardHome(
    item: ItemWithRelations,
    onClick: () -> Unit,
    loggedUserId: Int? = null
) {
    val showRating = item.user.user.id == loggedUserId
    val rating = item.ratings.firstOrNull()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // -------------------------------
            // 📌 Imagen
            // -------------------------------
            AsyncImage(
                model = item.item.photoPath,
                contentDescription = "product image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
            )

            Spacer(Modifier.width(12.dp))

            // -------------------------------
            // 📌 Información principal
            // -------------------------------
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = item.item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = item.item.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = item.user.user.fullName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // -------------------------------
            // 📌 Estado + Rating (si aplica)
            // -------------------------------
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                StatusChip(item.state.name)

                if (showRating && rating != null && item.state.name == "Entregado") {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Text(
                            text = rating.rating.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.width(2.dp))

                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "rating",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val color = when (status) {
        "Publicado" -> Color.Blue
        "Entregado" -> Color(0xFF00913f)
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
