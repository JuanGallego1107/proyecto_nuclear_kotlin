package com.apps_moviles.proyecto_nuclear_kotlin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    product: Product,
    onBack: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var requested by remember { mutableStateOf(false) }

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

            // 🔶 Imagen grande
            item {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = "product image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray)
                )
            }

            // 🔶 Card de categoría + título
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {

                        AssistChip(
                            onClick = {},
                            label = { Text(product.category) }
                        )

                        Spacer(Modifier.height(10.dp))

                        Text(
                            product.name,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }

            // 🔶 Descripción
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Descripción", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Este artículo está en excelente estado. Ideal para estudiantes que buscan reutilizar recursos dentro del campus. " +
                                    "Puedes solicitarlo y coordinar con el oferente para intercambio, venta o donación."
                        )
                    }
                }
            }

            // 🔶 Ubicación
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Ubicación", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text("Bogotá D.C. - Universidad del Humboldt")
                    }
                }
            }

            // 🔶 Información del oferente
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

                        Column {
                            Text(product.owner, fontWeight = FontWeight.Bold)
                            Text("Calificación: ⭐⭐⭐⭐☆")
                            Text("Intercambios realizados: 12")
                        }
                    }
                }
            }

            // Espacio al final
            item { Spacer(Modifier.height(80.dp)) }
        }

        // 🔶 Botón flotante abajo
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

        // 🔶 Pop-up de confirmación
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Solicitud enviada") },
                text = { Text("El oferente ha sido notificado sobre tu interés en este artículo.") },
                confirmButton = {
                    Button(
                        onClick = { showDialog = false }
                    ) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}
