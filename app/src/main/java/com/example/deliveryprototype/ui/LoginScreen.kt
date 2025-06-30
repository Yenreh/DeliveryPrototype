package com.example.deliveryprototype.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deliveryprototype.ui.theme.GrayBackground
import com.example.deliveryprototype.ui.theme.GraySurface
import com.example.deliveryprototype.ui.theme.BlackText
import com.example.deliveryprototype.ui.theme.Primary
import com.example.deliveryprototype.ui.theme.GrayText

@Composable
fun LoginScreen(onLogin: (String, String) -> Unit, error: String?) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberUser by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF222222)), // fondo oscuro como en el mockup
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(GrayBackground, shape = MaterialTheme.shapes.medium)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Icon(Icons.Filled.Person, contentDescription = null, tint = GrayText, modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(8.dp))
            Text("Nombre Empresa", color = BlackText, fontSize = 20.sp)
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario", color = GrayText) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = GraySurface,
                    focusedLabelColor = Primary,
                    unfocusedLabelColor = GrayText,
                    cursorColor = Primary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", color = GrayText) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = GraySurface,
                    focusedLabelColor = Primary,
                    unfocusedLabelColor = GrayText,
                    cursorColor = Primary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Recordar usuario", color = GrayText)
                Switch(checked = rememberUser, onCheckedChange = { rememberUser = it }, colors = SwitchDefaults.colors(checkedThumbColor = Primary))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onLogin(username, password) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White)
            ) { Text("Ingresar") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White)
            ) {
                Icon(painterResource(id = android.R.drawable.ic_menu_search), contentDescription = null, tint = Color.Unspecified)
                Spacer(Modifier.width(8.dp))
                Text("Ingresa con Google")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Recuperar contraseña?", color = GrayText, fontSize = 12.sp)
            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(error, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
