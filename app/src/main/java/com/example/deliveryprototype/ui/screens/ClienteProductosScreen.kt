package com.example.deliveryprototype.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.example.deliveryprototype.ui.components.OrderConfirmationDialog
import com.example.deliveryprototype.ui.theme.BlackText
import com.example.deliveryprototype.ui.theme.GrayBackground
import com.example.deliveryprototype.ui.theme.GraySurface
import com.example.deliveryprototype.ui.theme.GrayText
import com.example.deliveryprototype.ui.theme.Primary
import com.example.deliveryprototype.utils.FeeUtils
import com.example.deliveryprototype.utils.OrderCalculationUtils
import kotlinx.coroutines.launch

@Composable
fun ClienteProductosScreen(
    tienda: TiendaEntity,
    onComprar: (List<Pair<ProductoEntity, Int>>) -> Unit,
    onProductoDetalle: (Int) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    var productos by remember { mutableStateOf<List<ProductoEntity>>(emptyList()) }
    val cantidades = remember { mutableStateMapOf<Int, Int>() }
    var isLoaded by remember { mutableStateOf(false) }
    var total by remember { mutableStateOf(0.0) }
    var searchText by remember { mutableStateOf("") }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(tienda.id) {
        if (!isLoaded) {
            productos = repository.getProductosByTienda(tienda.id)
            isLoaded = true
        }
    }

    LaunchedEffect(cantidades.values.sum()) {
        total = OrderCalculationUtils.calculateSubtotal(productos, cantidades)
    }

    BackHandler(onBack = onBack)

    Column(Modifier.fillMaxSize().background(GrayBackground).padding(16.dp)) {
        BackButton(onClick = onBack, modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(12.dp))
        // Header
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = GrayBackground)) {
            Column(Modifier.padding(8.dp)) {
                Text(tienda.nombre, fontWeight = FontWeight.Bold, color = BlackText)
                Text(tienda.direccion, fontSize = 14.sp, color = GrayText)
            }
        }
        Spacer(Modifier.height(12.dp))
        // Buscador mejorado
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            ) {
                Icon(Icons.Filled.Search, contentDescription = "Buscar", modifier = Modifier.size(24.dp), tint = Primary)
                Spacer(Modifier.width(12.dp))
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Buscar productos...", color = GrayText) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Primary,
                        unfocusedIndicatorColor = GraySurface,
                        cursorColor = Primary,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("Productos", fontWeight = FontWeight.Medium, color = BlackText)
        Spacer(Modifier.height(8.dp))
        val filteredProductos = productos.filter { it.nombre.contains(searchText, ignoreCase = true) }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(filteredProductos) { producto ->
                Card(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Row(
                        Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Storefront, contentDescription = "Producto", modifier = Modifier.size(40.dp))
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text(producto.nombre, fontWeight = FontWeight.Bold, color = BlackText)
                            Text("${producto.precio} $", fontSize = 14.sp, color = GrayText)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                val current = cantidades[producto.id] ?: 0
                                if (current > 0) cantidades[producto.id] = current - 1
                            }) {
                                Icon(Icons.Filled.Remove, contentDescription = "Quitar")
                            }
                            Text((cantidades[producto.id] ?: 0).toString(), Modifier.width(24.dp), fontSize = 16.sp)
                            IconButton(onClick = {
                                val current = cantidades[producto.id] ?: 0
                                cantidades[producto.id] = current + 1
                            }) {
                                Icon(Icons.Filled.Add, contentDescription = "Agregar")
                            }
                        }
                        IconButton(onClick = { onProductoDetalle(producto.id) }) {
                            Icon(Icons.Filled.Info, contentDescription = "Detalles producto")
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        
        // Resumen de compra con tarifas
        val subtotal = total
        val deliveryFee = FeeUtils.calculateDeliveryFee()
        val serviceFee = FeeUtils.calculateServiceFee()
        val grandTotal = OrderCalculationUtils.calculateGrandTotal(subtotal, deliveryFee, serviceFee)
        
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp)) {
                Text("Resumen de compra", fontWeight = FontWeight.Bold, color = BlackText, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal", color = BlackText)
                    Text(FeeUtils.formatMoney(subtotal), color = BlackText)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Envío", color = GrayText)
                    Text(FeeUtils.formatMoney(deliveryFee), color = GrayText)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Servicio", color = GrayText)
                    Text(FeeUtils.formatMoney(serviceFee), color = GrayText)
                }
                Spacer(Modifier.height(8.dp))
                Divider(color = GraySurface)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", fontWeight = FontWeight.Bold, color = BlackText)
                    Text(FeeUtils.formatMoney(grandTotal), fontWeight = FontWeight.Bold, color = Primary)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        val hasSelectedProducts = cantidades.values.any { it > 0 }
        Button(
            onClick = {
                val productosSeleccionados = productos.map { it to (cantidades[it.id] ?: 0) }.filter { it.second > 0 }
                if (OrderCalculationUtils.hasValidProductsForOrder(productosSeleccionados)) {
                    showConfirmationDialog = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = hasSelectedProducts
        ) {
            Icon(Icons.Filled.ShoppingCart, contentDescription = "Comprar")
            Spacer(Modifier.width(8.dp))
            Text("Comprar")
        }
        
        // Confirmation Dialog
        val productosSeleccionados = productos.map { it to (cantidades[it.id] ?: 0) }.filter { it.second > 0 }
        OrderConfirmationDialog(
            isVisible = showConfirmationDialog,
            productosSeleccionados = productosSeleccionados,
            onConfirm = {
                showConfirmationDialog = false
                if (OrderCalculationUtils.hasValidProductsForOrder(productosSeleccionados)) {
                    // Crear pedido en un hilo de corrutina
                    scope.launch {
                        val deliveryFee = FeeUtils.calculateDeliveryFee()
                        val serviceFee = FeeUtils.calculateServiceFee()
                        val pedido = PedidoEntity(
                            clienteId = 2, // TODO: usar el id real del usuario logueado
                            tenderoId = tienda.id,
                            repartidorId = 3,
                            productosIds = OrderCalculationUtils.formatProductosIds(productosSeleccionados),
                            estado = "PENDIENTE",
                            fecha = java.time.LocalDateTime.now().toString(),
                            tarifaEnvio = deliveryFee,
                            tarifaServicio = serviceFee
                        )
                        repository.db.pedidoDao().insertPedido(pedido)
                    }
                    onComprar(productosSeleccionados)
                }
            },
            onDismiss = {
                showConfirmationDialog = false
            }
        )
    }
}