package com.example.deliveryprototype.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deliveryprototype.data.AppRepository
import com.example.deliveryprototype.model.PedidoEntity
import com.example.deliveryprototype.model.ProductoEntity
import com.example.deliveryprototype.model.UserEntity
import com.example.deliveryprototype.ui.components.BackButton
import com.example.deliveryprototype.ui.theme.BlackText
import com.example.deliveryprototype.ui.theme.GrayBackground
import com.example.deliveryprototype.ui.theme.GrayText
import com.example.deliveryprototype.ui.theme.Primary
import com.example.deliveryprototype.utils.FeeUtils
import com.example.deliveryprototype.utils.OrderCalculationUtils
import kotlinx.coroutines.launch

@Composable
fun RepartidorPedidoDetalleScreen(pedidoId: Int, onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    var pedido by remember { mutableStateOf<PedidoEntity?>(null) }
    var cliente by remember { mutableStateOf<UserEntity?>(null) }
    var productos by remember { mutableStateOf<List<ProductoEntity>>(emptyList()) }
    var cantidades by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(pedidoId) {
        pedido = repository.db.pedidoDao().getPedidoById(pedidoId)
        pedido?.let {
            cliente = repository.db.userDao().getUserById(it.clienteId)
            val cantidadesMap = OrderCalculationUtils.parseProductosIds(it.productosIds)
            val ids = cantidadesMap.keys.toList()
            productos = ids.mapNotNull { id -> repository.db.productoDao().getProductoById(id) }
            cantidades = cantidadesMap
        }
    }

    if (pedido == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Primary)
        }
        return
    }

    val subtotal = OrderCalculationUtils.calculateSubtotal(productos, cantidades)
    val total = subtotal + pedido!!.tarifaEnvio + pedido!!.tarifaServicio

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top bar con botón de regreso
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(onClick = onBack)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Detalle del Pedido",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Código del pedido
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Código-pedido",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BlackText
                        )
                    }
                }
            }

            // Información del cliente
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "Cliente",
                                modifier = Modifier.size(24.dp),
                                tint = Primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "Pedido para ${cliente?.name ?: "Cliente"}",
                                    fontWeight = FontWeight.Bold,
                                    color = BlackText
                                )
                                Text(
                                    "Dirección de entrega",
                                    color = GrayText,
                                    fontSize = 14.sp
                                )
                                Text(
                                    cliente?.address ?: "Dirección no disponible",
                                    color = BlackText,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            // Foto de perfil placeholder
                            Card(
                                modifier = Modifier.size(48.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Foto de ${cliente?.name?.take(1) ?: "?"}",
                                        fontSize = 10.sp,
                                        color = GrayText
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Estado actual
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Estado - ${pedido!!.estado}",
                                color = Primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Valores
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Valores",
                            fontWeight = FontWeight.Bold,
                            color = BlackText,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        productos.forEach { producto ->
                            val cantidad = cantidades[producto.id] ?: 1
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "${producto.nombre} x $cantidad",
                                    color = BlackText
                                )
                                Text(
                                    FeeUtils.formatMoney(producto.precio * cantidad),
                                    color = BlackText
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = Color(0xFFE0E0E0))
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tus ganancias", color = BlackText, fontWeight = FontWeight.Bold)
                            Text(
                                FeeUtils.formatMoney(pedido!!.tarifaEnvio),
                                color = BlackText,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total de la compra", color = GrayText)
                            Text(FeeUtils.formatMoney(total), color = GrayText)
                        }
                    }
                }
            }

            // Productos
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Productos",
                            fontWeight = FontWeight.Bold,
                            color = BlackText,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        productos.forEach { producto ->
                            val cantidad = cantidades[producto.id] ?: 1
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Placeholder para imagen del producto
                                Card(
                                    modifier = Modifier.size(48.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "IMG",
                                            fontSize = 10.sp,
                                            color = GrayText
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "${producto.nombre}",
                                        fontWeight = FontWeight.Medium,
                                        color = BlackText
                                    )
                                    Text(
                                        "xxxx$",
                                        color = GrayText,
                                        fontSize = 12.sp
                                    )
                                }
                                
                                Text(
                                    cantidad.toString(),
                                    fontWeight = FontWeight.Bold,
                                    color = BlackText,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            // Botones de acción para cambiar estado
            item {
                if (pedido!!.estado == "PENDIENTE" || pedido!!.estado == "EN_CAMINO") {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (pedido!!.estado == "PENDIENTE") {
                            Button(
                                onClick = {
                                    scope.launch {
                                        isLoading = true
                                        repository.db.pedidoDao().updateEstadoPedido(pedidoId, "EN_CAMINO")
                                        pedido = pedido!!.copy(estado = "EN_CAMINO")
                                        isLoading = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                } else {
                                    Text("Aceptar Pedido")
                                }
                            }
                        }
                        
                        if (pedido!!.estado == "EN_CAMINO") {
                            Button(
                                onClick = {
                                    scope.launch {
                                        isLoading = true
                                        repository.db.pedidoDao().updateEstadoPedido(pedidoId, "ENTREGADO")
                                        pedido = pedido!!.copy(estado = "ENTREGADO")
                                        isLoading = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                } else {
                                    Text("Marcar como Entregado")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}