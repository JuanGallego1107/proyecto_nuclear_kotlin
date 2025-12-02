package com.apps_moviles.proyecto_nuclear_kotlin

import android.util.Log
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
import com.apps_moviles.proyecto_nuclear_kotlin.model.Rating
import com.apps_moviles.proyecto_nuclear_kotlin.viewmodel.InteractionViewModel
import com.apps_moviles.proyecto_nuclear_kotlin.viewmodel.RatingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestItemsScreen(
    interactionViewModel: InteractionViewModel,
    ratingViewModel: RatingViewModel,
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

    var showRatingDialog by remember { mutableStateOf(false) }
    var selectedItemId by remember { mutableStateOf<Int?>(null) }

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
                            onClick = { onInteractionClick(interactionWithRelations.interaction.id) },
                            onRateClick = {
                                selectedItemId = interactionWithRelations.item.item.id
                                showRatingDialog = true
                            }
                        )
                    }
                }
            }

            if (showRatingDialog && selectedItemId != null) {
                RatingDialog(
                    onDismiss = { showRatingDialog = false },
                    onSubmit = { rating, comment ->

                        ratingViewModel.insert(
                            Rating(
                                itemId = selectedItemId!!,   // id del item
                                userId = userId ?: return@RatingDialog,
                                rating = rating,
                                comments = comment,
                                ratingDate = Date().toString()
                            )
                        )
                        // Recarga la lista de interacciones
                        userId?.let { interactionViewModel.loadByUserId(it) }

                        showRatingDialog = false
                    }
                )
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

    val rating = data.item.ratings.firstOrNull()

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
                    model = data.item.item.photoPath,
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
                        text = data.item.item.title,
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
            if (data.state.name == "Completado" && rating == null) {

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

            // ------------------------------------------------------------------
            // MOSTRAR CALIFICACIÓN SI YA EXISTE
            // ------------------------------------------------------------------
            if (data.state.name == "Completado"  && rating != null) {

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        // ⭐⭐ ESTRELLAS DE CALIFICACIÓN ⭐⭐
                        Row {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (index < rating.rating) Color(0xFFFFC107) else Color.Gray,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Comentario
                        Text(
                            text = rating.comments?.takeIf { it.isNotBlank() } ?: "Sin comentarios",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Calificado el: ${formatDate(rating.ratingDate)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var rating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = { onSubmit(rating, comment) },
                enabled = rating > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar")
            }
        },
        title = { Text("Calificar artículo") },
        text = {
            Column {

                // -----------------------
                // ⭐ Seleccionar estrellas
                // -----------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    (1..5).forEach { star ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star $star",
                            tint = if (star <= rating) Color(0xFFFFC107) else Color.Gray,
                            modifier = Modifier
                                .size(40.dp)
                                .padding(4.dp)
                                .clickable { rating = star }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // -----------------------
                // 📝 Comentario
                // -----------------------
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comentario (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
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

