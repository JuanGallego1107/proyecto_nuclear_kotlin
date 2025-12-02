package com.apps_moviles.proyecto_nuclear_kotlin

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.apps_moviles.proyecto_nuclear_kotlin.model.InsertItemEnum
import com.apps_moviles.proyecto_nuclear_kotlin.model.Item
import com.apps_moviles.proyecto_nuclear_kotlin.viewmodel.ItemViewModel
import java.io.File
import java.io.FileOutputStream
import java.util.Date

// ------------------------------------------------------------
// FAB
// ------------------------------------------------------------
@Composable
fun ItemFormFab(
    itemViewModel: ItemViewModel,
    source: InsertItemEnum
) {
    var showForm by remember { mutableStateOf(false) }

    FloatingActionButton(
        onClick = { showForm = true },
        containerColor = Color(0xFF003D6A),
        contentColor = Color.White
    ) {
        Icon(Icons.Default.Add, contentDescription = "add article")
    }

    if (showForm) {
        FullScreenPublishModal(
            itemViewModel = itemViewModel,
            onClose = { showForm = false },
            source = source
        )
    }
}


// ------------------------------------------------------------
// MODAL COMPLETO
// ------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenPublishModal(itemViewModel: ItemViewModel, onClose: () -> Unit, source: InsertItemEnum) {

    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var publicationType by remember { mutableStateOf<Int?>(null) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var savedImagePath by remember { mutableStateOf<String?>(null) }

    var showSuccess by remember { mutableStateOf(false) }
    var showErrors by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    // Leer userId desde el UserViewModel
    val userId by itemViewModel.loggedUserId.collectAsState(initial = null)

    fun saveImageToInternalStorage(context: Context, bitmap: Bitmap, fileName: String): String {
        val file = File(context.filesDir, "$fileName.jpg")

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }

        return file.absolutePath
    }


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
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                // --------------------------
                // PICKER SINGLE IMAGE
                // --------------------------
                ImagePickerSection(
                    imageUri = imageUri,
                    onImageSelected = { uri ->
                        imageUri = uri

                        // Convertimos URI → Bitmap → Internal Storage
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            context.contentResolver,
                            uri
                        )

                        savedImagePath = saveImageToInternalStorage(
                            context,
                            bitmap,
                            "item_${System.currentTimeMillis()}"
                        )
                    }
                )

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título del artículo*") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción*") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Sede/Ubicación*") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                CategorySelector(
                    selected = category,
                    onSelected = { category = it }
                )

                Spacer(Modifier.height(25.dp))

                PublicationTypeSelector(
                    selectedId = publicationType,
                    onSelected = { id -> publicationType = id }
                )

                Spacer(Modifier.height(25.dp))

                Button(
                    onClick = {
                        val errors = mutableListOf<String>()

                        if (title.isBlank()) errors.add("El título es obligatorio.")
                        if (description.isBlank()) errors.add("La descripción es obligatoria.")
                        if (category.isBlank()) errors.add("Debes seleccionar una categoría.")
                        if (address.isBlank()) errors.add("La sede/ubicación es obligatoria.")
                        if (publicationType == null) errors.add("Debes seleccionar un tipo de publicación.")
                        if (savedImagePath.isNullOrBlank()) errors.add("Debes agregar una foto.")

                        if (errors.isEmpty()) {

                            itemViewModel.insert(
                                Item(
                                    title = title,
                                    description = description,
                                    category = category,
                                    photoPath = savedImagePath,
                                    userId = userId ?: 2,
                                    stateId = 1,
                                    publicationTypeId = publicationType ?: 1 ,
                                    publicationDate = Date().toString(),
                                    address = address
                                ),
                                source = source
                            )

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

            if (showErrors) {
                AlertDialog(
                    onDismissRequest = { showErrors = false },
                    confirmButton = { TextButton(onClick = { showErrors = false }) { Text("OK") } },
                    title = { Text("Errores en el formulario") },
                    text = { Text(errorText) }
                )
            }

            if (showSuccess) {
                AlertDialog(
                    onDismissRequest = { showSuccess = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showSuccess = false
                            onClose()
                        }) { Text("OK") }
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
    imageUri: Uri?,
    onImageSelected: (Uri) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onImageSelected(it) }
    }

    Column {
        Text("Foto del artículo", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = "add photo")
            }

            imageUri?.let { uri ->
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

data class PublicationTypeOption(
    val id: Int,
    val name: String
)


// ------------------------------------------------------------
// SELECTOR DE TIPO DE PUBLICACION
// ------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationTypeSelector(
    selectedId: Int?,
    onSelected: (Int) -> Unit
) {
    val types = listOf(
        PublicationTypeOption(1, "Intercambio"),
        PublicationTypeOption(2, "Donación"),
        PublicationTypeOption(3, "Trueque")
    )

    var expanded by remember { mutableStateOf(false) }

    // Texto visible según el ID seleccionado
    val selectedName = types.find { it.id == selectedId }?.name ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            label = { Text("Tipo de publicación:*") },
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
            types.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name) },
                    onClick = {
                        onSelected(option.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
