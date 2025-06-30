package com.example.deliveryprototype.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

sealed class RepartidorNavItem(val label: String, val icon: ImageVector) {
    object Home : RepartidorNavItem("Inicio", Icons.Filled.Home)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepartidorNavScaffold(onLogout: () -> Unit) {
    var selectedIndex by remember { mutableStateOf(0) }
    val items = listOf(RepartidorNavItem.Home)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Repartidor") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Filled.Logout, contentDescription = "Cerrar sesión")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedIndex) {
                0 -> RepartidorHomeScreen()
            }
        }
    }
}

@Composable
fun RepartidorHomeScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hola *Nombre del repartidor*", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Tus datos del día")
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Card(modifier = Modifier.weight(1f).padding(4.dp)) { Box(Modifier.padding(8.dp)) { Text("XXX\nPedidos entregados") } }
            Card(modifier = Modifier.weight(1f).padding(4.dp)) { Box(Modifier.padding(8.dp)) { Text("XXX\nPedidos pendientes") } }
            Card(modifier = Modifier.weight(1f).padding(4.dp)) { Box(Modifier.padding(8.dp)) { Text("XXX.XXX,xx\nTu plata del día") } }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Pedidos")
        // Aquí iría la lista de pedidos con botón "Aceptar"
        Text("[Lista de pedidos]")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Pedidos pendientes")
        // Aquí iría la lista de pedidos pendientes
        Text("[Pedidos pendientes]")
    }
}
