package com.example.deliveryprototype.ui.nav

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.deliveryprototype.model.UserEntity
import com.example.deliveryprototype.ui.components.AppTopBar
import com.example.deliveryprototype.ui.components.LogoutButton
import com.example.deliveryprototype.ui.screens.RepartidorHomeScreen
import com.example.deliveryprototype.ui.screens.RepartidorPedidoDetalleScreen

sealed class RepartidorNavItem(val label: String, val icon: ImageVector) {
    object Home : RepartidorNavItem("Inicio", Icons.Filled.Home)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepartidorNavScaffold(onLogout: () -> Unit, loggedInUser: UserEntity) {
    var selectedIndex by remember { mutableStateOf(0) }
    val items = listOf(RepartidorNavItem.Home)
    var pedidoDetalleId by remember { mutableStateOf<Int?>(null) }
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Panel Repartidor",
                actions = {
                   LogoutButton(onClick = onLogout)
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
                            // Reset detail view when switching tabs
                            if (pedidoDetalleId != null) pedidoDetalleId = null
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // Handle back for order detail
            if (pedidoDetalleId != null) {
                BackHandler { pedidoDetalleId = null }
                RepartidorPedidoDetalleScreen(
                    pedidoId = pedidoDetalleId!!, 
                    onBack = { pedidoDetalleId = null }
                )
            } else {
                when (selectedIndex) {
                    0 -> RepartidorHomeScreen(
                        loggedInUser = loggedInUser,
                        onPedidoDetalle = { pedidoId -> pedidoDetalleId = pedidoId }
                    )
                }
            }
        }
    }
}