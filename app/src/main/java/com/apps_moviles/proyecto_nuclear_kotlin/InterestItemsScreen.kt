package com.apps_moviles.proyecto_nuclear_kotlin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.apps_moviles.proyecto_nuclear_kotlin.model.InteractionWithRelations
import com.apps_moviles.proyecto_nuclear_kotlin.viewmodel.InteractionViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestItemsScreen(
    interactionViewModel: InteractionViewModel,
    onBack: () -> Unit,
    onInteractionClick: (Int) -> Unit
) {

    val userId by interactionViewModel.loggedUserId.collectAsState(initial = null)

    // Cargar interacciones cuando haya userId
    LaunchedEffect(userId) {
        userId?.let { interactionViewModel.loadByUserId(it) }
    }

    val interactions = interactionViewModel.interactions.collectAsState()

    // ESTADOS DISPONIBLES PARA FILTRAR
    val estados = listOf("Todos") + interactions.value
        .map { it.state.name }
        .distinct()

    var selectedEstado by remember { mutableStateOf("Todos") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Interacciones") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
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
            val filtered = interactions.value.filter { inter ->
                selectedEstado == "Todos" || inter.state.name == selectedEstado
            }

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay interacciones en este estado")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered) { interactionWithRelations ->

                        InteractionCard(
                            data = interactionWithRelations,
                            onClick = { onInteractionClick(interactionWithRelations.interaction.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InteractionCard(
    data: InteractionWithRelations,
    onClick: () -> Unit,
    onRateClick: () -> Unit = {}
) {
    val color = when (data.state.name) {
        "Completado" -> Color(0xFF00913f)
        "Sin completar" -> Color(0xFFF4B400)
        else -> Color.LightGray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Imagen a la izquierda
                AsyncImage(
                    model = data.item.photoPath,
                    contentDescription = "product image",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.LightGray)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Información a la derecha
                Column(modifier = Modifier.weight(1f)) {

                    // TÍTULO DEL ITEM
                    Text(
                        text = data.item.title,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // CHIP DE ESTADO
                    AssistChip(
                        onClick = {},
                        label = { Text(data.state.name) }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // COMENTARIO
                    Text(
                        text = data.interaction.comment ?: "Sin comentarios",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // FECHA
                    Text(
                        text = "Fecha de interés: ${formatDate(data.interaction.interactionDate)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // ------------------------------------------------------
            //  BOTÓN CALIFICAR SOLO SI EL ESTADO ES "Completado"
            // ------------------------------------------------------
            if (data.state.name == "Completado") {

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onRateClick() },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF003D6A)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = "Calificar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Calificar")
                }

            }
        }
    }
}


fun formatDate(dateString: String): String {
    return try {
        // Formato del String original de Date().toString()
        val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)

        // Formato deseado
        val outputFormat = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
        date?.let { outputFormat.format(it) } ?: ""
    } catch (e: Exception) {
        dateString // si falla, devolver el String original
    }
}

