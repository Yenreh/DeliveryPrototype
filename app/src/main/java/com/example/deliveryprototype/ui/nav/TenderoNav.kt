package com.example.deliveryprototype.ui.nav

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.deliveryprototype.ui.components.AppTopBar
import com.example.deliveryprototype.ui.components.LogoutButton
import com.example.deliveryprototype.ui.screens.*

sealed class TenderoNavItem(val label: String, val icon: ImageVector) {
    object Tienda : TenderoNavItem("Tu tienda", Icons.Filled.Shop)
    object Home : TenderoNavItem("Inicio", Icons.Filled.Home)
    object Pedidos : TenderoNavItem("Pedidos", Icons.Filled.List)
}

// Sealed class para las diferentes pantallas del tendero
sealed class TenderoScreen {
    object Home : TenderoScreen()
    object Tienda : TenderoScreen()
    object Pedidos : TenderoScreen()
    data class ProductForm(val productoId: Int?) : TenderoScreen()
    data class PedidoDetalle(val pedidoId: Int) : TenderoScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenderoNavScaffold(onLogout: () -> Unit) {
    var selectedIndex by remember { mutableStateOf(1) }
    val currentScreen = remember { mutableStateOf<TenderoScreen>(TenderoScreen.Home) }
    
    val items = listOf(
        TenderoNavItem.Tienda,
        TenderoNavItem.Home,
        TenderoNavItem.Pedidos
    )
    
    // Función para navegar entre pantallas
    val navigateToScreen = { screen: TenderoScreen ->
        currentScreen.value = screen
    }
    
    // Función para regresar al home
    val navigateBack = {
        currentScreen.value = TenderoScreen.Home
        selectedIndex = 1
    }
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Panel Tendero",
                actions = {
                    LogoutButton(onClick = onLogout)
                }
            )
        },
        bottomBar = {
            // Solo mostrar bottom bar en las pantallas principales
            if (currentScreen.value in listOf(TenderoScreen.Home, TenderoScreen.Tienda, TenderoScreen.Pedidos)) {
                NavigationBar {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedIndex == index,
                            onClick = { 
                            selectedIndex = index
                            currentScreen.value = when (index) {
                                0 -> TenderoScreen.Tienda
                                1 -> TenderoScreen.Home
                                2 -> TenderoScreen.Pedidos
                                else -> TenderoScreen.Home
                            }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val screen = currentScreen.value) {
                is TenderoScreen.Home -> TenderoHomeScreen(
                    onPedidoDetalle = { pedidoId ->
                        currentScreen.value = TenderoScreen.PedidoDetalle(pedidoId)
                    }
                )
                is TenderoScreen.Tienda -> TenderoTiendaScreen(
                    onProductoAdd = {
                        currentScreen.value = TenderoScreen.ProductForm(null)
                    },
                    onProductoEdit = { productoId ->
                        currentScreen.value = TenderoScreen.ProductForm(productoId)
                    }
                )
                is TenderoScreen.Pedidos -> TenderoPedidosScreen(
                    onPedidoDetalle = { pedidoId ->
                        currentScreen.value = TenderoScreen.PedidoDetalle(pedidoId)
                    }
                )
                is TenderoScreen.ProductForm -> {
                    // Import the ProductFormScreen from the existing file
                    com.example.deliveryprototype.ui.screens.ProductFormScreen(
                        productoId = screen.productoId,
                        onBack = navigateBack,
                        onSave = navigateBack
                    )
                }
                is TenderoScreen.PedidoDetalle -> {
                    // Import the TenderoPedidoDetalleScreen from the existing file
                    com.example.deliveryprototype.ui.screens.TenderoPedidoDetalleScreen(
                        pedidoId = screen.pedidoId,
                        onBack = navigateBack,
                        onUpdateEstado = { /* Estado actualizado */ }
                    )
                }
            }
        }
    }
}