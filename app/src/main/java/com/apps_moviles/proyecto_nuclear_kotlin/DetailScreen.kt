package com.apps_moviles.proyecto_nuclear_kotlin

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.apps_moviles.proyecto_nuclear_kotlin.model.Interaction
import com.apps_moviles.proyecto_nuclear_kotlin.model.ItemInteractionRelations
import com.apps_moviles.proyecto_nuclear_kotlin.viewmodel.InteractionViewModel
import com.apps_moviles.proyecto_nuclear_kotlin.viewmodel.ItemViewModel
import java.util.Date
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: ItemViewModel,
    interactionViewModel: InteractionViewModel,
    itemId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Leer userId desde el UserViewModel
    val userId by viewModel.loggedUserId.collectAsState(initial = null)

    // Cargar el item al abrir
    LaunchedEffect(itemId) {
        viewModel.loadItemById(itemId)
    }

    // Escuchar el item cargado
    val itemWithRelations by viewModel.currentItem.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var requested by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf<Pair<Boolean, ItemInteractionRelations?>>(false to null) }


    // Loading si aún no llega el item
    if (itemWithRelations == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val item = itemWithRelations!!.item
    val user = itemWithRelations!!.user
    val state = itemWithRelations!!.state
    val interactions = itemWithRelations!!.interactions
    val ratings = itemWithRelations!!.ratings
    val publicationType = itemWithRelations!!.publicationType

    fun hasUserInteracted(userId: Int?, interactions: List<ItemInteractionRelations>): Boolean {
        // Si userId es null, retornamos false
        if (userId == null) return false

        // Recorremos las interacciones y vemos si alguna coincide con el userId
        return interactions.any { it.user.id == userId }
    }

    val userHasInteracted = hasUserInteracted(userId, interactions)

    //Log.i("USUARIO", userHasInteracted.toString());

    var showImageDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Detalle del artículo", color = Color.White)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF003D6A)
                )
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 🖼 Imagen principal
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {

                    // Product Image
                    AsyncImage(
                        model = item.photoPath,
                        contentDescription = "product image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.LightGray)
                            .clickable { showImageDialog = true },
                        contentScale = ContentScale.Crop
                    )

                    // Chips overlays
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        // Chip left (State)
                        ChipSmall(text = state.name)

                        // Chip right (Publication Type)
                        ChipSmall(text = publicationType.name)
                    }
                }
            }

            // 📌 Título y categoría
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {

                        AssistChip(
                            onClick = {},
                            label = { Text(item.category) }
                        )

                        Spacer(Modifier.height(10.dp))

                        Text(
                            item.title,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }

            // 📝 Descripción
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Descripción", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text(item.description)
                    }
                }
            }

            // 📍 Ubicación (si la agregarás después)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Sede/Ubicación 📍", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text(item.address)
                    }
                }
            }

            // 👤 Información del oferente
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        AsyncImage(
                            model = "https://randomuser.me/api/portraits/men/32.jpg",
                            contentDescription = "user",
                            modifier = Modifier
                                .size(55.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                        )

                        Spacer(Modifier.width(14.dp))

                        // 👉 Empuja el rating a la derecha
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                user.user.fullName,
                                fontWeight = FontWeight.Bold
                            )
                            Text(user.user.email)
                        }

                        //  CÁLCULO DEL PROMEDIO DE RATINGS
                        val averageRating = user.items
                            .mapNotNull { it.ratings.firstOrNull()?.rating }
                            .average()

                        val displayRating = if (averageRating.isNaN()) null else averageRating

                        //  MOSTRAR RATING SI EXISTE
                        if (displayRating != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {

                                Text(
                                    text = String.format("%.1f", displayRating),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(Modifier.width(4.dp))

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

            // Listado de interacciones activas para el oferente
            if(user.user.id == userId) {
                item {
                    Text(
                        text = "Personas interesadas en tú articulo",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Interacciones

                items(interactions) { interaction ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {

                            // ---------------------------
                            // ROW PRINCIPAL
                            // ---------------------------
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Icono placeholder usuario
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Usuario",
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = interaction.user.fullName,
                                        style = MaterialTheme.typography.titleSmall
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = interaction.user.email,
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Chip estado
                                    val stateColor = when (interaction.state.name) {
                                        "Completado" -> Color(0xFF00913f)
                                        "Sin completar" -> Color(0xFFF4B400)
                                        else -> Color.LightGray
                                    }

                                    AssistChip(
                                        onClick = {},
                                        label = { Text(interaction.state.name) },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = stateColor,
                                            labelColor = Color.White
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = interaction.interaction.comment ?: "Sin comentarios",
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 2
                                    )

                                    Text(
                                        text = "Fecha de interés: ${
                                            formatDateString(interaction.interaction.interactionDate)
                                        }",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                // Botón check si está "En espera"
                                if (interaction.state.name == "En espera") {
                                    IconButton(
                                        onClick = {
                                            showConfirmDialog = true to interaction
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Confirmar",
                                            tint = Color(0xFF003D6A)
                                        )
                                    }
                                }
                            }

                            // -------------------------------------------------------------
                            // SECCIÓN DE RATING (solo si está completado y tiene rating)
                            // -------------------------------------------------------------
                            val rating = ratings.firstOrNull()

                            if (interaction.state.name == "Completado" && rating != null) {

                                Spacer(modifier = Modifier.height(12.dp))

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                                    elevation = CardDefaults.cardElevation(2.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {

                                        // ⭐ Estrellas
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            repeat(5) { index ->
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = if (index < rating.rating) Color(0xFFFFC107) else Color.Gray,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Comentario
                                        Text(
                                            text = rating.comments?.takeIf { it.isNotBlank() } ?: "Sin comentarios",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

            }

            item { Spacer(Modifier.height(80.dp)) }
        }

        // Botón de solicitar
        if(user.user.id != userId && !userHasInteracted) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .navigationBarsPadding(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Button(
                    onClick = { showDialog = true; requested = true },
                    enabled = !requested,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF003D6A)
                    )
                ) {
                    Text("Solicitar", color = Color.White)
                }
            }
        }

        if (userHasInteracted) {

            // Obtener la interacción del usuario
            val userInteraction = interactions.firstOrNull { it.user.id == userId }
            val interactionStateName = userInteraction?.state?.name ?: "Sin información"

            val color = when (interactionStateName) {
                "Completado" -> Color(0xFF00913f)
                "Sin completar" -> Color(0xFFF4B400)
                else ->  Color.LightGray
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .navigationBarsPadding(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Ya has interactuado con este artículo",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = color,
                            tonalElevation = 2.dp
                        ) {
                            Text(
                                text = interactionStateName,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                color = Color.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // AlertDialog de confirmación para completar intercambio
        if (showConfirmDialog.first) {
            val interaction = showConfirmDialog.second
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false to null },
                title = { Text("Confirmación") },
                text = { Text("¿Estás seguro que deseas dar como completado el ${publicationType.name} de tú articulo con ${interaction?.user?.fullName}?") },
                confirmButton = {
                    Button(onClick = {
                        if(interaction?.interaction?.id != null){
                            interactionViewModel.completeInteraction(interaction.interaction.id, item.id)
                            viewModel.loadItemById(itemId)
                        }
                        showConfirmDialog = false to null
                    }) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showConfirmDialog = false to null }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // 🟩 Pop-up de confirmación
        // Modal de comentario para enviar solicitud/interes en el articulo
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Enviar solicitud") },
                text = {
                    Column {
                        Text("Escribe un comentario para el oferente:")
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = { Text("Tu comentario") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Crear la interacción en Room
                            interactionViewModel.createInteraction(
                                Interaction(
                                    itemId = item.id,
                                    userId = userId ?: 2 ,
                                    comment = commentText,
                                    interactionStateId = 1,
                                    interactionDate = Date().toString()
                                )
                            )

                            showDialog = false
                            requested = true
                            commentText = ""

                            // Mostrar Toast
                            Toast.makeText(
                                context,
                                "El oferente ha sido notificado sobre tu interés en este artículo.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    ) {
                        Text("Enviar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (showImageDialog) {
            Dialog(
                onDismissRequest = { showImageDialog = false }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        .clickable { showImageDialog = false }
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black)
                    ) {
                        AsyncImage(
                            model = item.photoPath,
                            contentDescription = "zoomed image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun ChipSmall(text: String) {

    val color = when (text) {
        "Publicado" -> Color.Blue
        "Entregado" -> Color(0xFF00913f)
        else -> Color.Black.copy(alpha = 0.6f)
    }

    Box(
        modifier = Modifier
            .background(
                color = color,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

fun formatDateString(dateString: String): String {
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



