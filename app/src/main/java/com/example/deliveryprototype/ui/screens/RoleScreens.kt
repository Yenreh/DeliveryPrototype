package com.example.deliveryprototype.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun RoleSelectionScreen(onRoleSelected: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Selecciona tu rol:", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { onRoleSelected("tendero") }) { Text("Tendero") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onRoleSelected("cliente") }) { Text("Cliente") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onRoleSelected("repartidor") }) { Text("Repartidor") }
    }
}

@Composable
fun TenderoScreenWrapper() {
    // Llama a la función renombrada
    TenderoBasicScreen()
}

@Composable
fun ClienteScreen() {
    // Llama a la función renombrada
    ClienteBasicScreen()
}

@Composable
fun RepartidorScreen() {
    // Llama a la función renombrada
    RepartidorBasicScreen()
}
