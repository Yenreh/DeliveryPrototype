package com.example.deliveryprototype.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.deliveryprototype.data.AppRepository
import com.example.deliveryprototype.model.PedidoEntity
import com.example.deliveryprototype.model.ProductoEntity
import com.example.deliveryprototype.model.TiendaEntity
import com.example.deliveryprototype.ui.components.OrderCard
import com.example.deliveryprototype.ui.theme.GrayText
import com.example.deliveryprototype.ui.theme.Primary
import com.example.deliveryprototype.utils.FeeUtils
import com.example.deliveryprototype.utils.OrderCalculationUtils

@Composable
fun TenderoHomeScreen(onPedidoDetalle: (Int) -> Unit = {}) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    var pedidos by remember { mutableStateOf<List<PedidoEntity>>(emptyList()) }
    var productos by remember { mutableStateOf<List<ProductoEntity>>(emptyList()) }
    var tienda by remember { mutableStateOf<TiendaEntity?>(null) }
    
    // Para el prototipo, usar tenderoId = 1
    LaunchedEffect(Unit) {
        pedidos = repository.db.pedidoDao().getPedidosByTendero(1)
        productos = repository.db.productoDao().getProductosByTendero(1)
        tienda = repository.db.tiendaDao().getTiendaById(1)
    }
    
    // Calcular estadísticas del día
    val pedidosEntregados = pedidos.count { it.estado == "ENTREGADO" }
    val pedidosPendientes = pedidos.count { it.estado == "PENDIENTE" || it.estado == "EN_CAMINO" }
    val gananciasDelDia = pedidos.filter { it.estado == "ENTREGADO" }
        .sumOf { pedido ->
            val cantidadesMap = OrderCalculationUtils.parseProductosIds(pedido.productosIds)
            val productosDelPedido = cantidadesMap.keys.mapNotNull { id -> 
                productos.find { it.id == id }
            }
            OrderCalculationUtils.calculateSubtotal(productosDelPedido, cantidadesMap)
        }
    
    // Detectar productos con bajo stock
    val productosPocoStock = productos.filter { it.stock <= 5 }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Nombre del comercio
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Storefront, 
                    contentDescription = "Tienda",
                    modifier = Modifier.size(32.dp),
                    tint = Primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    tienda?.nombre ?: "Mi Comercio", 
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tus datos del día
        Text(
            "Tus datos del día", 
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                StatsCard(
                    value = pedidosEntregados.toString(),
                    label = "Pedidos\nentregados",
                    modifier = Modifier
                        .width(120.dp)
                        .height(100.dp)
                        .padding(vertical = 4.dp)
                )
            }
            item {
                StatsCard(
                    value = pedidosPendientes.toString(),
                    label = "Pedidos\npendientes",
                    modifier = Modifier
                        .width(120.dp)
                        .height(100.dp)
                        .padding(vertical = 4.dp)
                )
            }
            item {
                StatsCard(
                    value = FeeUtils.formatMoney(gananciasDelDia),
                    label = "Tu plata del día",
                    modifier = Modifier
                        .width(200.dp)
                        .height(100.dp)
                        .padding(vertical = 4.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Alertas
        Text(
            "Alertas", 
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        if (productosPocoStock.isNotEmpty()) {
            productosPocoStock.take(2).forEach { producto ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = "Alerta",
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Quedan pocos productos de ${producto.nombre} (${producto.stock} disponibles)")
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = { /* TODO: Navegar a inventario */ }) {
                            Text("Ver detalles")
                        }
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
            ) {
                Box(modifier = Modifier.padding(12.dp)) {
                    Text("No hay alertas por el momento", color = Color(0xFF4CAF50))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Pedidos recientes
        Text(
            "Pedidos", 
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium, 
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        if (pedidos.isEmpty()) {
            Text("No hay pedidos recientes.", color = GrayText)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp)
            ) {
                items(pedidos.takeLast(5).reversed()) { pedido ->
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

@Composable
fun StatsCard(
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
                modifier = Modifier.fillMaxWidth() // clave aquí también
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