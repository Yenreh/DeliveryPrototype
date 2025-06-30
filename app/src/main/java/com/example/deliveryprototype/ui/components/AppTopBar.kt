package com.example.deliveryprototype.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Componente reutilizable para barra superior con estilo consistente
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = { Text(title) },
        actions = { actions() },
        modifier = modifier
    )
}