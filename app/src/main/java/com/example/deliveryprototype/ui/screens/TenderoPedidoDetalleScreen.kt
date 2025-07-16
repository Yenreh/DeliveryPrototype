package com.example.deliveryprototype.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Cancel
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
import com.example.deliveryprototype.model.TiendaEntity
import com.example.deliveryprototype.model.UserEntity
import com.example.deliveryprototype.ui.theme.Primary
import com.example.deliveryprototype.ui.theme.GrayText
import com.example.deliveryprototype.ui.theme.GrayBackground
import com.example.deliveryprototype.utils.FeeUtils
import com.example.deliveryprototype.utils.OrderCalculationUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenderoPedidoDetalleScreen(
    pedidoId: Int,
    onBack: () -> Unit,
    onUpdateEstado: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    var pedido by remember { mutableStateOf<PedidoEntity?>(null) }
    var productos by remember { mutableStateOf<List<ProductoEntity>>(emptyList()) }
    var cantidades by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var tienda by remember { mutableStateOf<TiendaEntity?>(null) }
    var cliente by remember { mutableStateOf<UserEntity?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(pedidoId) {
        pedido = repository.db.pedidoDao().getPedidoById(pedidoId)
        pedido?.let { pedidoEntity ->
            tienda = repository.db.tiendaDao().getTiendaById(pedidoEntity.tenderoId)
            cliente = repository.db.userDao().getUserById(pedidoEntity.clienteId)
            val cantidadesMap = OrderCalculationUtils.parseProductosIds(pedidoEntity.productosIds)
            val ids = cantidadesMap.keys.toList()
            productos = ids.mapNotNull { id -> repository.db.productoDao().getProductoById(id) }
            cantidades = cantidadesMap
        }
        isLoading = false
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (pedido == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Pedido no encontrado", color = GrayText)
        }
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().background(GrayBackground).padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
            }
            Text(
                "Detalle del Pedido",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        
        // Header del pedido
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Código-pedido: #${pedido!!.id}",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Cliente: ${cliente?.name ?: "Usuario #${pedido!!.clienteId}"}", color = Color.Black)
                Text("Dirección: ${cliente?.address ?: "Sin dirección"}", color = GrayText)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Storefront,
                        contentDescription = "Tienda",
                        modifier = Modifier.size(32.dp),
                        tint = Primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Tienda: ${tienda?.nombre ?: "Mi tienda"}",
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Estado del pedido
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "ESTADO DEL PEDIDO",
                    fontWeight = FontWeight.Bold,
                    color = GrayText,
                    fontSize = 12.sp
                )
                Text(
                    pedido!!.estado,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    fontSize = 16.sp
                )
                Text("Fecha: ${pedido!!.fecha}", color = GrayText, fontSize = 12.sp)
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Botones de acción solo si el pedido está pendiente o en camino
                if (pedido!!.estado in listOf("PENDIENTE", "EN_CAMINO")) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { 
                                scope.launch {
                                    repository.db.pedidoDao().updateEstadoPedido(pedidoId, "ENTREGADO")
                                    onUpdateEstado?.invoke("ENTREGADO")
                                    pedido = pedido!!.copy(estado = "ENTREGADO")
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Marcar entregado")
                        }
                        
                        OutlinedButton(
                            onClick = { 
                                scope.launch {
                                    repository.db.pedidoDao().updateEstadoPedido(pedidoId, "CANCELADO")
                                    onUpdateEstado?.invoke("CANCELADO")
                                    pedido = pedido!!.copy(estado = "CANCELADO")
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Valores del pedido
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Valores",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
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
                            color = Color.Black
                        )
                        Text(
                            FeeUtils.formatMoney(producto.precio * cantidad),
                            color = Color.Black
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
                    Text("Tarifa de domicilio", color = GrayText)
                    Text(FeeUtils.formatMoney(pedido!!.tarifaEnvio), color = GrayText)
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tarifa de servicio", color = GrayText)
                    Text(FeeUtils.formatMoney(pedido!!.tarifaServicio), color = GrayText)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0xFFE0E0E0))
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Total de la compra",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    val subtotal = OrderCalculationUtils.calculateSubtotal(productos, cantidades)
                    val total = OrderCalculationUtils.calculateGrandTotal(
                        subtotal, 
                        pedido!!.tarifaEnvio, 
                        pedido!!.tarifaServicio
                    )
                    Text(
                        FeeUtils.formatMoney(total),
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Productos detallados
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Productos",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                productos.forEach { producto ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Storefront,
                                contentDescription = "Producto",
                                modifier = Modifier.size(24.dp),
                                tint = GrayText
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    producto.nombre,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                                Text(
                                    FeeUtils.formatMoney(producto.precio),
                                    color = GrayText,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Text(
                            "${cantidades[producto.id] ?: 1}",
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}