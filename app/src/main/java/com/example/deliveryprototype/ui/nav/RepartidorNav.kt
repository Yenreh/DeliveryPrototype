package com.example.deliveryprototype.ui.nav

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.deliveryprototype.ui.components.LogoutButton
import com.example.deliveryprototype.ui.screens.RepartidorHomeScreen

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
                   LogoutButton(onClick = onLogout)
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