package com.example.deliveryprototype.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deliveryprototype.data.AppRepository
import com.example.deliveryprototype.model.PedidoEntity
import com.example.deliveryprototype.model.UserEntity
import com.example.deliveryprototype.ui.theme.BlackText
import com.example.deliveryprototype.ui.theme.GrayBackground
import com.example.deliveryprototype.ui.theme.GrayText
import com.example.deliveryprototype.ui.theme.Primary
import com.example.deliveryprototype.utils.FeeUtils
import kotlinx.coroutines.launch

@Composable
fun RepartidorHomeScreen(
    loggedInUser: UserEntity? = null,
    onPedidoDetalle: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    var pedidosAsignados by remember { mutableStateOf<List<PedidoEntity>>(emptyList()) }
    var pedidosDisponibles by remember { mutableStateOf<List<PedidoEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()

    // Para el prototipo, usar repartidorId = 3
    val repartidorId = loggedInUser?.id ?: 3

    LaunchedEffect(Unit) {
        // Cargar pedidos asignados al repartidor
        pedidosAsignados = repository.db.pedidoDao().getPedidosByRepartidor(repartidorId)
        
        // Cargar pedidos disponibles (sin repartidor asignado)
        pedidosDisponibles = repository.db.pedidoDao().getPedidosDisponibles().take(3) // Mostrar máximo 3 pedidos nuevos
    }

    // Calcular estadísticas del día
    val pedidosEntregados = pedidosAsignados.count { it.estado == "ENTREGADO" }
    val pedidosPendientes = pedidosAsignados.count { it.estado == "EN_CAMINO" }
    val gananciasDelDia = pedidosAsignados.filter { it.estado == "ENTREGADO" }
        .sumOf { it.tarifaEnvio }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
            .padding(16.dp)
    ) {
        // Saludo
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Repartidor",
                    modifier = Modifier.size(32.dp),
                    tint = Primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Hola ${loggedInUser?.name ?: "*Nombre del repartidor"}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BlackText
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tus datos del día
        Text(
            "Tus datos del día",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = BlackText,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                RepartidorStatsCard(
                    value = pedidosEntregados.toString(),
                    label = "Pedidos\nentregados",
                    modifier = Modifier
                        .width(120.dp)
                        .height(100.dp)
                )
            }
            item {
                RepartidorStatsCard(
                    value = pedidosPendientes.toString(),
                    label = "Pedidos\npendientes",
                    modifier = Modifier
                        .width(120.dp)
                        .height(100.dp)
                )
            }
            item {
                RepartidorStatsCard(
                    value = FeeUtils.formatMoney(gananciasDelDia),
                    label = "Tu plata del día",
                    modifier = Modifier
                        .width(200.dp)
                        .height(100.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pedidos nuevos
        if (pedidosDisponibles.isNotEmpty()) {
            Text(
                "Pedidos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = BlackText,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            pedidosDisponibles.forEach { pedido ->
                RepartidorOrderCard(
                    pedido = pedido,
                    onAccept = {
                        scope.launch {
                            // Asignar pedido al repartidor
                            val pedidoActualizado = pedido.copy(repartidorId = repartidorId)
                            repository.db.pedidoDao().updatePedido(pedidoActualizado)
                            
                            // Recargar listas
                            pedidosAsignados = repository.db.pedidoDao().getPedidosByRepartidor(repartidorId)
                            pedidosDisponibles = repository.db.pedidoDao().getPedidosDisponibles().take(3)
                        }
                    },
                    onDetalle = onPedidoDetalle
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pedidos pendientes asignados
        Text(
            "Pedidos pendientes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = BlackText,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        val pedidosPendientesAsignados = pedidosAsignados.filter { 
            it.estado == "EN_CAMINO" || it.estado == "PENDIENTE" 
        }

        if (pedidosPendientesAsignados.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Box(
                    modifier = Modifier.padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes pedidos pendientes", color = GrayText)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(pedidosPendientesAsignados) { pedido ->
                    RepartidorPendingOrderCard(
                        pedido = pedido,
                        onDetalle = onPedidoDetalle
                    )
                }
            }
        }
    }
}

@Composable
fun RepartidorStatsCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = GrayText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun RepartidorOrderCard(
    pedido: PedidoEntity,
    onAccept: () -> Unit,
    onDetalle: (Int) -> Unit
) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    var cliente by remember { mutableStateOf<UserEntity?>(null) }

    LaunchedEffect(pedido.clienteId) {
        cliente = repository.db.userDao().getUserById(pedido.clienteId)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Cliente",
                    modifier = Modifier.size(24.dp),
                    tint = Primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Ey, ${cliente?.name ?: "*nombre"} quiere algo",
                        fontWeight = FontWeight.Medium,
                        color = BlackText
                    )
                    Text(
                        cliente?.address ?: "Dirección Calle XX # XX - XX",
                        color = GrayText,
                        fontSize = 12.sp
                    )
                    Text(
                        "Orden recibida a las ${pedido.fecha.takeLast(5)}",
                        color = GrayText,
                        fontSize = 12.sp
                    )
                }
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Aceptar")
                }
            }
        }
    }
}

@Composable
fun RepartidorPendingOrderCard(
    pedido: PedidoEntity,
    onDetalle: (Int) -> Unit
) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    var cliente by remember { mutableStateOf<UserEntity?>(null) }

    LaunchedEffect(pedido.clienteId) {
        cliente = repository.db.userDao().getUserById(pedido.clienteId)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetalle(pedido.id) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar placeholder
            Card(
                modifier = Modifier.size(48.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        cliente?.name?.take(1)?.uppercase() ?: "U",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrayText
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Pedido de ${cliente?.name ?: "*nombre"}",
                    fontWeight = FontWeight.Bold,
                    color = BlackText
                )
                Text(
                    cliente?.address ?: "Dirección Calle XX # XX - XX",
                    color = GrayText,
                    fontSize = 12.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.DeliveryDining,
                        contentDescription = "Tiempo",
                        modifier = Modifier.size(16.dp),
                        tint = GrayText
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        FeeUtils.formatMoney(pedido.tarifaEnvio),
                        color = GrayText,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "30 min",
                        color = GrayText,
                        fontSize = 12.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "Código-pedido #${pedido.id}",
                    fontWeight = FontWeight.Bold,
                    color = BlackText,
                    fontSize = 14.sp
                )
                Text(
                    FeeUtils.formatMoney(pedido.tarifaEnvio + pedido.tarifaServicio),
                    fontWeight = FontWeight.Bold,
                    color = BlackText,
                    fontSize = 16.sp
                )
            }
        }
    }
}