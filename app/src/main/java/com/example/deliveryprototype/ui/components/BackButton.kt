package com.example.deliveryprototype.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Componente reutilizable para botón "Volver" con estilo consistente
 */
@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier.wrapContentSize()
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
        Spacer(Modifier.width(4.dp))
        Text("Volver")
    }
}