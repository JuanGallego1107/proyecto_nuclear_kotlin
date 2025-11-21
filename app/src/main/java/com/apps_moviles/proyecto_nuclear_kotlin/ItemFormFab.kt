package com.apps_moviles.proyecto_nuclear_kotlin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemFormFab() {
    var showForm by remember { mutableStateOf(false) }

    // 🔵 FAB Reutilizable
    FloatingActionButton(
        onClick = { showForm = true },
        containerColor = Color(0xFF003D6A),
        contentColor = Color.White
    ) {
        Icon(Icons.Default.Add, contentDescription = "add article")
    }

    // 🟢 Modal de pantalla completa
    if (showForm) {
        FullScreenPublishModal(
            onClose = { showForm = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenPublishModal(onClose: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Publicar artículo", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF003D6A)
                    ),
                    navigationIcon = {
                        IconButton(onClick = onClose) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                tint = Color.White,
                                contentDescription = "close",
                                modifier = Modifier.rotate(45f)
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("Formulario de publicación (próximamente)", style = MaterialTheme.typography.bodyLarge)
                Text("Aquí irá: nombre, categoría, imagen, descripción, ubicación…")
            }
        }
    }
}
