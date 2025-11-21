package com.apps_moviles.proyecto_nuclear_kotlin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

// ------------------------------------------------------------
// FAB
// ------------------------------------------------------------
@Composable
fun ItemFormFab() {
    var showForm by remember { mutableStateOf(false) }

    FloatingActionButton(
        onClick = { showForm = true },
        containerColor = Color(0xFF003D6A),
        contentColor = Color.White
    ) {
        Icon(Icons.Default.Add, contentDescription = "add article")
    }

    if (showForm) {
        FullScreenPublishModal { showForm = false }
    }
}

// ------------------------------------------------------------
// MODAL COMPLETO
// ------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenPublishModal(onClose: () -> Unit) {
    // Estados globales del formulario
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    var showSuccess by remember { mutableStateOf(false) }
    var showErrors by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
                            Icon(Icons.Default.ArrowBack, "close", tint = Color.White)
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {

                // --------------------------
                // PICKER DE FOTOS FUNCIONAL
                // --------------------------
                ImagePickerSection(
                    imageUris = imageUris,
                    onImagesSelected = { imageUris = it }
                )

                Spacer(Modifier.height(20.dp))

                // --------------------------
                // TÍTULO
                //---------------------------
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título del artículo*") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // --------------------------
                // DESCRIPCIÓN
                // --------------------------
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción*") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )

                Spacer(Modifier.height(12.dp))

                // --------------------------
                // SELECTOR DE CATEGORÍA
                // --------------------------
                CategorySelector(
                    selected = category,
                    onSelected = { category = it }
                )

                Spacer(Modifier.height(25.dp))

                // --------------------------
                // BOTÓN GUARDAR
                // --------------------------
                Button(
                    onClick = {
                        val errors = mutableListOf<String>()

                        if (title.isBlank()) errors.add("El título es obligatorio.")
                        if (description.isBlank()) errors.add("La descripción es obligatoria.")
                        if (category.isBlank()) errors.add("Debes seleccionar una categoría.")
                        if (imageUris.isEmpty()) errors.add("Debes agregar al menos una foto.")

                        if (errors.isEmpty()) {
                            showSuccess = true
                        } else {
                            errorText = errors.joinToString("\n")
                            showErrors = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003D6A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Guardar", color = Color.White)
                }

                Spacer(Modifier.height(20.dp))
            }

            // --------------------------
            // POPUP DE ERROR
            // --------------------------
            if (showErrors) {
                AlertDialog(
                    onDismissRequest = { showErrors = false },
                    confirmButton = {
                        TextButton(onClick = { showErrors = false }) {
                            Text("OK")
                        }
                    },
                    title = { Text("Errores en el formulario") },
                    text = { Text(errorText) }
                )
            }

            // --------------------------
            // POPUP DE ÉXITO
            // --------------------------
            if (showSuccess) {
                AlertDialog(
                    onDismissRequest = { showSuccess = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showSuccess = false
                            onClose()
                        }) {
                            Text("OK")
                        }
                    },
                    title = { Text("Publicado") },
                    text = { Text("El artículo se publicó correctamente.") }
                )
            }
        }
    }
}

// ------------------------------------------------------------
// COMPONENTE PICKER DE IMÁGENES
// ------------------------------------------------------------
@Composable
fun ImagePickerSection(
    imageUris: List<Uri>,
    onImagesSelected: (List<Uri>) -> Unit,
    maxSelection: Int = 4
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        val combined = (imageUris + uris).distinct().take(maxSelection)
        onImagesSelected(combined)
    }

    Column {
        Text("Fotos del artículo", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .clickable(enabled = imageUris.size < maxSelection) {
                        launcher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = "add photo")
            }

            imageUris.forEach { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }
    }
}

// ------------------------------------------------------------
// SELECTOR DE CATEGORÍAS
// ------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelector(
    selected: String,
    onSelected: (String) -> Unit
) {
    val categories = listOf("Libros", "Uniformes", "Tecnología", "Ropa")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        OutlinedTextField(
            value = selected,
            onValueChange = {},
            label = { Text("Categoría*") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
