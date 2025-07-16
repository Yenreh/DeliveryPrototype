package com.example.deliveryprototype.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storefront
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
import com.example.deliveryprototype.ui.components.BackButton
import com.example.deliveryprototype.ui.theme.BlackText
import com.example.deliveryprototype.ui.theme.GrayBackground
import com.example.deliveryprototype.ui.theme.GraySurface
import com.example.deliveryprototype.ui.theme.GrayText
import com.example.deliveryprototype.ui.theme.Primary
import com.example.deliveryprototype.utils.FeeUtils
import com.example.deliveryprototype.utils.OrderCalculationUtils

@Composable
fun ClientePedidoDetalleScreen(pedidoId: Int, onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    var pedido by remember { mutableStateOf<PedidoEntity?>(null) }
    var tienda by remember { mutableStateOf<TiendaEntity?>(null) }
    var productos by remember { mutableStateOf<List<ProductoEntity>>(emptyList()) }
    var cantidades by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(pedidoId) {
        pedido = repository.db.pedidoDao().getPedidoById(pedidoId)
        pedido?.let {
            tienda = repository.db.tiendaDao().getTiendaById(it.tenderoId)
            val cantidadesMap = OrderCalculationUtils.parseProductosIds(it.productosIds)
            val ids = cantidadesMap.keys.toList()
            productos = ids.mapNotNull { id -> repository.db.productoDao().getProductoById(id) }
            cantidades = cantidadesMap
        }
    }

    if (pedido == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(Modifier.fillMaxSize().background(GrayBackground).padding(16.dp)) {
        BackButton(onClick = onBack, modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(8.dp))
        // Header
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp)) {
                Text("Código-pedido: #${pedido!!.id}", fontWeight = FontWeight.Bold, color = BlackText, fontSize = 18.sp)
                Spacer(Modifier.height(4.dp))
                Text("Pedido para usuario #${pedido!!.clienteId}", color = BlackText)
                Text("Dirección de entrega: ${tienda?.direccion ?: "-"}", color = GrayText)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Storefront, contentDescription = "Tienda", modifier = Modifier.size(32.dp), tint = Primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Pedido de ${tienda?.nombre ?: "-"}", color = BlackText, fontWeight = FontWeight.Medium)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        
        // Estado del pedido con estilo destacado
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp)) {
                Text("ESTADO DEL PEDIDO", fontWeight = FontWeight.Bold, color = GrayText, fontSize = 12.sp)
                Text(pedido!!.estado, fontWeight = FontWeight.Bold, color = Primary, fontSize = 16.sp)
                Text("Fecha: ${pedido!!.fecha}", color = GrayText, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(16.dp))
        
        // Valores
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp)) {
                Text("Resumen del pedido", fontWeight = FontWeight.Bold, color = BlackText, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                productos.forEach { producto ->
                    val cantidad = cantidades[producto.id] ?: 1
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${producto.nombre} x $cantidad", color = BlackText)
                        Text(FeeUtils.formatMoney(producto.precio * cantidad), color = BlackText)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Divider(color = GraySurface)
                Spacer(Modifier.height(8.dp))
                // Tarifas desde la base de datos
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tarifa de domicilio", color = GrayText)
                    Text(FeeUtils.formatMoney(pedido!!.tarifaEnvio), color = GrayText)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tarifa de servicio", color = GrayText)
                    Text(FeeUtils.formatMoney(pedido!!.tarifaServicio), color = GrayText)
                }
                Spacer(Modifier.height(8.dp))
                Divider(color = GraySurface)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total de la compra", fontWeight = FontWeight.Bold, color = BlackText)
                    val subtotal = OrderCalculationUtils.calculateSubtotal(productos, cantidades)
                    val total = OrderCalculationUtils.calculateGrandTotal(subtotal, pedido!!.tarifaEnvio, pedido!!.tarifaServicio)
                    Text(FeeUtils.formatMoney(total), fontWeight = FontWeight.Bold, color = Primary)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        
        // Productos detallados
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp)) {
                Text("Productos", fontWeight = FontWeight.Bold, color = BlackText, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                productos.forEach { producto ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 4.dp), 
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Storefront, contentDescription = "Producto", modifier = Modifier.size(24.dp), tint = GrayText)
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(producto.nombre, fontWeight = FontWeight.Medium, color = BlackText)
                                Text("$ ${producto.precio}", color = GrayText, fontSize = 12.sp)
                            }
                        }
                        Text("x${cantidades[producto.id] ?: 1}", fontWeight = FontWeight.Bold, color = Primary)
                    }
                }
            }
        }
    }
}