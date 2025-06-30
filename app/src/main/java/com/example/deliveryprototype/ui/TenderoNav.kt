package com.example.deliveryprototype.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

sealed class TenderoNavItem(val label: String, val icon: ImageVector) {
    object Tienda : TenderoNavItem("Tu tienda", Icons.Filled.Shop)
    object Home : TenderoNavItem("Inicio", Icons.Filled.Home)
    object Pedidos : TenderoNavItem("Pedidos", Icons.Filled.List)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenderoNavScaffold(onLogout: () -> Unit) {
    var selectedIndex by remember { mutableStateOf(1) }
    val items = listOf(
        TenderoNavItem.Tienda,
        TenderoNavItem.Home,
        TenderoNavItem.Pedidos
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Tendero") },
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
                0 -> TenderoTiendaScreen()
                1 -> TenderoHomeScreen()
                2 -> TenderoPedidosScreen()
            }
        }
    }
}

@Composable
fun TenderoHomeScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("*Nombre del comercio*", style = MaterialTheme.typography.titleLarge)
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
        Text("Alertas")
        Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) { Box(Modifier.padding(8.dp)) { Text("Quedan pocos productos de ***") } }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Pedidos")
        // Aquí iría la lista de pedidos con botón "Ver detalles"
        Text("[Lista de pedidos]")
    }
}

@Composable
fun TenderoTiendaScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("*Nombre del comercio*", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Productos")
        // Aquí iría la lista de productos con botón "Agregar" y "Ver detalles"
        Text("[Lista de productos]")
    }
}

@Composable
fun TenderoPedidosScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Pedidos", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        // Aquí iría la lista de pedidos
        Text("[Lista de pedidos]")
    }
}
