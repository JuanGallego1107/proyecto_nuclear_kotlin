package com.apps_moviles.proyecto_nuclear_kotlin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apps_moviles.proyecto_nuclear_kotlin.viewmodel.ItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishedItemsScreen(
    viewModel: ItemViewModel,
    onBack: () -> Unit,
    onItemClick: (Int) -> Unit
) {
    val userId by viewModel.loggedUserId.collectAsState(initial = null)

    // Cargar los items del usuario
    LaunchedEffect(userId) {
        userId?.let { viewModel.loadItemsByUser(it) }
    }

    val items = viewModel.items.collectAsState()

    // ESTADOS DISPONIBLES PARA FILTRAR
    val estados = listOf("Todos", "Publicado", "Entregado")
    var selectedEstado by remember { mutableStateOf("Todos") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Publicaciones") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            ItemFormFab(itemViewModel = viewModel)
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            // -------------------------
            // FILTRO POR ESTADO
            // -------------------------
            Text(text = "Filtrar por estado", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(6.dp))

            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {

                OutlinedTextField(
                    value = selectedEstado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .clickable { expanded = true },
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    estados.forEach { estado ->
                        DropdownMenuItem(
                            text = { Text(estado) },
                            onClick = {
                                selectedEstado = estado
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // -------------------------
            // LISTA FILTRADA
            // -------------------------
            val filteredItems = items.value.filter { item ->
                selectedEstado == "Todos" || item.state.name == selectedEstado
            }

            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay publicaciones en este estado")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredItems) { item ->
                        ProductCardHome(item) {
                            onItemClick(item.item.id)
                        }
                    }
                }
            }
        }
    }
}
