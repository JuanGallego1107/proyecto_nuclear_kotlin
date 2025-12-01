package com.apps_moviles.proyecto_nuclear_kotlin

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.apps_moviles.proyecto_nuclear_kotlin.viewmodel.LoginState
import com.apps_moviles.proyecto_nuclear_kotlin.viewmodel.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(userViewModel: UserViewModel, onLoginSuccess: (String) -> Unit) {

    val userName = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val showPassword = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val loginState by userViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                val user = (loginState as LoginState.Success).user
                Toast.makeText(context, "Inicio exitoso", Toast.LENGTH_SHORT).show()
                onLoginSuccess(user.fullName)
            }
            is LoginState.Error -> {
                Toast.makeText(context, (loginState as LoginState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    // Fondo degradado suave
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFE9F2FF), Color(0xFFF7FAFF))
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Icono ecológico dentro del círculo
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0D3B66)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = "https://cdn-icons-png.freepik.com/512/17642/17642349.png",
                    contentDescription = "eco-icon",
                    modifier = Modifier.size(45.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "HumboldtCircular",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D3B66)
            )

            Text(
                text = "¡ Juntos por una economía circular !",
                fontSize = 14.sp,
                color = Color(0xFF4A4A4A),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Campo correo
            OutlinedTextField(
                value = userName.value,
                onValueChange = { userName.value = it },
                label = { Text("Correo institucional") },
                placeholder = { Text("usuario@humboldt.edu.co") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Campo contraseña
            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Text(
                        text = if (showPassword.value) "Ocultar" else "Ver",
                        fontSize = 12.sp,
                        color = Color(0xFF0D3B66),
                        modifier = Modifier.clickable {
                            showPassword.value = !showPassword.value
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Botón ingresar
            Button(
                onClick = {
                    if (userName.value.isBlank() || password.value.isBlank()) {
                        Toast.makeText(context, "Por favor ingrese sus credenciales", Toast.LENGTH_SHORT).show()
                    } else {
                        userViewModel.login(
                            email = userName.value,
                            password = password.value
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0D3B66),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Ingresar", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "¿Olvidaste tu contraseña?",
                color = Color(0xFF0D3B66),
                fontSize = 14.sp,
                modifier = Modifier.clickable { }
            )

            Spacer(modifier = Modifier.height(25.dp))

            Divider(modifier = Modifier.fillMaxWidth(0.9f))

            Spacer(modifier = Modifier.height(20.dp))

            Row {
                Text(text = "¿No tienes cuenta?", fontSize = 14.sp)
                Text(
                    text = "Crear cuenta",
                    color = Color(0xFF0D3B66),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .clickable { }
                )
            }
        }
    }
}
