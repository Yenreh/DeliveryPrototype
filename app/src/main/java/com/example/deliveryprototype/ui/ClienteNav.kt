package com.example.deliveryprototype.ui

import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import com.example.deliveryprototype.data.AppRepository
import com.example.deliveryprototype.model.TiendaEntity
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

sealed class ClienteNavItem(val label: String, val icon: ImageVector) {
    object Tiendas : ClienteNavItem("Tiendas", Icons.Filled.Storefront)
    object Home : ClienteNavItem("Inicio", Icons.Filled.Home)
    object Pedidos : ClienteNavItem("Tus Pedidos", Icons.Filled.Assignment)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteNavScaffold(onLogout: () -> Unit, loggedInUser: com.example.deliveryprototype.model.UserEntity) {
    var selectedIndex by remember { mutableStateOf(1) }
    val items = listOf(
        ClienteNavItem.Tiendas,
        ClienteNavItem.Home,
        ClienteNavItem.Pedidos
    )
    var tiendaSeleccionada by remember { mutableStateOf<com.example.deliveryprototype.model.TiendaEntity?>(null) }
    var mostrarProductos by remember { mutableStateOf(false) }
    var productosSeleccionados by remember { mutableStateOf<List<Pair<com.example.deliveryprototype.model.ProductoEntity, Int>>>(emptyList()) }
    var pedidoDetalleId by remember { mutableStateOf<Int?>(null) }
    var productoDetalleId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Cliente") },
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
                        onClick = {
                            selectedIndex = index
                            // Only reset product view state, preserve other navigation state
                            if (mostrarProductos) mostrarProductos = false
                            // Reset detail views when switching main tabs
                            if (pedidoDetalleId != null) pedidoDetalleId = null
                            if (productoDetalleId != null) productoDetalleId = null
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // Manejo de back para detalles de producto y pedido
            if (productoDetalleId != null) {
                BackHandler { productoDetalleId = null }
                ClienteProductoDetalleScreen(productoId = productoDetalleId!!, onBack = { productoDetalleId = null })
            } else if (pedidoDetalleId != null) {
                BackHandler { pedidoDetalleId = null }
                ClientePedidoDetalleScreen(pedidoId = pedidoDetalleId!!, onBack = { pedidoDetalleId = null })
            } else when {
                mostrarProductos && tiendaSeleccionada != null ->
                    ClienteProductosScreen(
                        tienda = tiendaSeleccionada!!,
                        onComprar = { seleccionados ->
                            productosSeleccionados = seleccionados
                            mostrarProductos = false
                        },
                        onProductoDetalle = { productoId -> productoDetalleId = productoId },
                        onBack = { mostrarProductos = false }
                    )
                selectedIndex == 0 -> ClienteTiendasScreenNav(onTiendaClick = { tienda ->
                    tiendaSeleccionada = tienda
                    mostrarProductos = true
                })
                selectedIndex == 1 -> ClienteHomeScreenNav(loggedInUser = loggedInUser)
                selectedIndex == 2 -> ClientePedidosScreen(
                    loggedInUser = loggedInUser,
                    onPedidoDetalle = { pedidoId -> pedidoDetalleId = pedidoId }
                )
            }
        }
    }
}
