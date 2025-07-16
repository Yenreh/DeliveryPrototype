package com.example.deliveryprototype.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.deliveryprototype.data.AppRepository
import com.example.deliveryprototype.model.PedidoEntity
import com.example.deliveryprototype.model.UserEntity
import com.example.deliveryprototype.ui.components.OrderCard
import com.example.deliveryprototype.ui.theme.BlackText
import com.example.deliveryprototype.ui.theme.GrayBackground
import com.example.deliveryprototype.ui.theme.GrayText

@Composable
fun ClientePedidosScreen(loggedInUser: UserEntity, onPedidoDetalle: (Int) -> Unit) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    var pedidos by remember { mutableStateOf<List<PedidoEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(loggedInUser.id) {
        pedidos = repository.db.pedidoDao().getPedidosByCliente(loggedInUser.id)
    }

    Column(Modifier.fillMaxSize().background(GrayBackground).padding(16.dp)) {
        Text("Tus Pedidos", style = MaterialTheme.typography.titleLarge, color = BlackText)
        Spacer(Modifier.height(8.dp))
        if (pedidos.isEmpty()) {
            Text("No tienes pedidos.", color = GrayText)
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(pedidos) { pedido ->
                    OrderCard(
                        pedido = pedido,
                        onCardClick = onPedidoDetalle,
                        showDetailButton = true,
                        showApproxTotal = false
                    )
                }
            }
        }
    }
}