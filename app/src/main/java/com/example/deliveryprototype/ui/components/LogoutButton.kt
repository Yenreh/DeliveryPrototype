package com.example.deliveryprototype.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Componente reutilizable para botón de logout con estilo consistente
 */
@Composable
fun LogoutButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(Icons.Filled.Logout, contentDescription = "Cerrar sesión")
    }
}