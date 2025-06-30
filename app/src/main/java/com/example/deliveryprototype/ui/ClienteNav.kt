package com.example.deliveryprototype.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shop
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
    object Tiendas : ClienteNavItem("Tiendas", Icons.Filled.Shop)
    object Home : ClienteNavItem("Inicio", Icons.Filled.Home)
    object Pedidos : ClienteNavItem("Tus Pedidos", Icons.Filled.List)
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
                            mostrarProductos = false
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when {
                productoDetalleId != null -> ClienteProductoDetalleScreen(productoId = productoDetalleId!!, onBack = { productoDetalleId = null })
                pedidoDetalleId != null -> ClientePedidoDetalleScreen(pedidoId = pedidoDetalleId!!, onBack = { pedidoDetalleId = null })
                mostrarProductos && tiendaSeleccionada != null ->
                    ClienteProductosScreen(
                        tienda = tiendaSeleccionada!!,
                        onComprar = { seleccionados ->
                            productosSeleccionados = seleccionados
                            mostrarProductos = false
                        },
                        onProductoDetalle = { productoId -> productoDetalleId = productoId }
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
