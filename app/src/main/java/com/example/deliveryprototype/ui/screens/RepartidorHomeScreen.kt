package com.example.deliveryprototype.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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