package com.example.deliveryprototype.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deliveryprototype.data.AppRepository
import com.example.deliveryprototype.model.PedidoEntity
import com.example.deliveryprototype.model.ProductoEntity
import com.example.deliveryprototype.model.TiendaEntity
import com.example.deliveryprototype.model.UserEntity
import com.example.deliveryprototype.ui.theme.GrayBackground
import com.example.deliveryprototype.ui.theme.GraySurface
import com.example.deliveryprototype.ui.theme.GrayBar
import com.example.deliveryprototype.ui.theme.BlackText
import com.example.deliveryprototype.ui.theme.Primary
import com.example.deliveryprototype.ui.theme.GrayText
import com.example.deliveryprototype.ui.components.BackButton
import com.example.deliveryprototype.utils.FeeUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Esta función fue renombrada para evitar conflicto con RoleScreens.kt
@Composable
fun ClienteScreenContent() {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    val productos = remember { mutableStateListOf<ProductoEntity>() }
    val scope = rememberCoroutineScope()
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!isLoaded) {
            val list = repository.db.productoDao().getProductosByTendero(1) // Para el prototipo, productos del tendero 1
            productos.clear()
            productos.addAll(list)
            isLoaded = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Productos Disponibles", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(productos) { producto ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                        Text(producto.descripcion)
                        Text("Precio: $${producto.precio}")
                        Text("Stock: ${producto.stock}")
                    }
                }
            }
        }
    }
}

@Composable
fun ClienteHomeScreenNav(loggedInUser: UserEntity) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    var pedidos by remember { mutableStateOf<List<PedidoEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(loggedInUser.id) {
        pedidos = repository.db.pedidoDao().getPedidosByCliente(loggedInUser.id)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Saludo y dirección
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GrayBackground)
        ) {
            Column(Modifier.padding(8.dp)) {
                Text("Hola ${loggedInUser.name}", fontWeight = FontWeight.Bold, color = BlackText)
                Text("Tus pedidos a\n${loggedInUser.address ?: "Sin dirección"}", fontSize = 14.sp, color = GrayText)
            }
        }
        Spacer(Modifier.height(12.dp))
        // Buscador
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(GrayBar, shape = MaterialTheme.shapes.medium)
                .padding(8.dp)
        ) {
            Icon(Icons.Filled.Search, contentDescription = "Buscar", modifier = Modifier.size(32.dp), tint = Primary)
            Spacer(Modifier.width(8.dp))
            Text("¿Qué necesitas hoy?", fontWeight = FontWeight.Medium, color = BlackText)
        }
        Spacer(Modifier.height(12.dp))
        // Carrusel (simulado)
        LazyRow(
            modifier = Modifier.fillMaxWidth().height(100.dp)
        ) {
            items(listOf("Promo 1", "Promo 2", "Promo 3")) { promo ->
                Card(
                    modifier = Modifier.size(180.dp, 100.dp).padding(end = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(promo)
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        // Categorías
        Text("¿Qué buscas?", fontWeight = FontWeight.Medium, modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CategoryButton(icon = Icons.Filled.Restaurant, label = "Restaurantes")
            CategoryButton(icon = Icons.Filled.LocalGroceryStore, label = "Compras")
            CategoryButton(icon = Icons.Filled.LocalPharmacy, label = "Farmacias")
        }
        Spacer(Modifier.height(16.dp))
        // Pedidos recientes
        Text("Tus pedidos recientes", fontWeight = FontWeight.Medium, modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(8.dp))
        if (pedidos.isEmpty()) {
            Text("No tienes pedidos recientes.")
        } else {
            pedidos.take(2).forEach { pedido ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(Modifier.padding(8.dp)) {
                        Text("Pedido #${pedido.id}")
                        Text("Estado: ${pedido.estado}")
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { /* Navegar a categoría */ }.padding(8.dp)
    ) {
        Card(
            modifier = Modifier.size(56.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, modifier = Modifier.size(32.dp))
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 12.sp)
    }
}

@Composable
fun ClienteTiendasScreenNav(onTiendaClick: (TiendaEntity) -> Unit) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    var tiendas by remember { mutableStateOf<List<TiendaEntity>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var isLoaded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (!isLoaded) {
            tiendas = repository.getAllTiendas()
            isLoaded = true
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Tiendas", style = MaterialTheme.typography.titleLarge, color = BlackText)
        Spacer(Modifier.height(8.dp))
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
                    placeholder = { Text("Buscar tiendas...", color = GrayText) },
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
        Spacer(Modifier.height(8.dp))
        Text("Tiendas", fontWeight = FontWeight.Medium, color = BlackText)
        Spacer(Modifier.height(8.dp))
        val filteredTiendas = tiendas.filter { it.nombre.contains(searchText, ignoreCase = true) }
        LazyColumn {
            items(filteredTiendas) { tienda ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onTiendaClick(tienda) },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Storefront, contentDescription = "Tienda", modifier = Modifier.size(40.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(tienda.nombre, fontWeight = FontWeight.Bold)
                            Text(tienda.direccion, fontSize = 12.sp)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("$ 5.000", fontSize = 12.sp) // Placeholder precio envío
                        Spacer(Modifier.width(8.dp))
                        Text("30 min", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ClienteProductosScreen(
    tienda: com.example.deliveryprototype.model.TiendaEntity,
    onComprar: (List<Pair<ProductoEntity, Int>>) -> Unit,
    onProductoDetalle: (Int) -> Unit
) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    var productos by remember { mutableStateOf<List<ProductoEntity>>(emptyList()) }
    val cantidades = remember { mutableStateMapOf<Int, Int>() }
    var isLoaded by remember { mutableStateOf(false) }
    var total by remember { mutableStateOf(0.0) }
    var searchText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(tienda.id) {
        if (!isLoaded) {
            productos = repository.getProductosByTienda(tienda.id)
            isLoaded = true
        }
    }

    LaunchedEffect(productos, cantidades) {
        total = productos.sumOf { (cantidades[it.id] ?: 0) * it.precio }
    }

    Column(Modifier.fillMaxSize().background(GrayBackground).padding(16.dp)) {
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
        Column(Modifier.fillMaxWidth()) {
            filteredProductos.forEach { producto ->
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
        val grandTotal = subtotal + deliveryFee + serviceFee
        
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
        Button(
            onClick = {
                val productosSeleccionados = productos.map { it to (cantidades[it.id] ?: 0) }.filter { it.second > 0 }
                if (productosSeleccionados.isNotEmpty()) {
                    // Crear pedido en un hilo de corrutina
                    scope.launch {
                        val deliveryFee = FeeUtils.calculateDeliveryFee()
                        val serviceFee = FeeUtils.calculateServiceFee()
                        val pedido = PedidoEntity(
                            clienteId = 2, // TODO: usar el id real del usuario logueado
                            tenderoId = tienda.id,
                            repartidorId = 3,
                            productosIds = productosSeleccionados.joinToString(",") { it.first.id.toString() },
                            estado = "PENDIENTE",
                            fecha = java.time.LocalDateTime.now().toString(),
                            tarifaEnvio = deliveryFee,
                            tarifaServicio = serviceFee
                        )
                        repository.db.pedidoDao().insertPedido(pedido)
                    }
                }
                onComprar(productosSeleccionados)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = total > 0
        ) {
            Icon(Icons.Filled.ShoppingCart, contentDescription = "Comprar")
            Spacer(Modifier.width(8.dp))
            Text("Comprar")
        }
    }
}

@Composable
fun ClientePedidoDetalleScreen(pedidoId: Int, onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    var pedido by remember { mutableStateOf<PedidoEntity?>(null) }
    var tienda by remember { mutableStateOf<com.example.deliveryprototype.model.TiendaEntity?>(null) }
    var productos by remember { mutableStateOf<List<com.example.deliveryprototype.model.ProductoEntity>>(emptyList()) }
    var cantidades by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(pedidoId) {
        pedido = repository.db.pedidoDao().getPedidoById(pedidoId)
        pedido?.let {
            tienda = repository.db.tiendaDao().getTiendaById(it.tenderoId)
            val ids = it.productosIds.split(",").mapNotNull { s -> s.toIntOrNull() }
            productos = ids.mapNotNull { id -> repository.db.productoDao().getProductoById(id) }
            cantidades = ids.groupingBy { it }.eachCount()
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
                        Text("$ ${"%.2f".format(producto.precio * cantidad)}", color = BlackText)
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
                    val subtotal = productos.sumOf { (cantidades[it.id] ?: 1) * it.precio }
                    val total = subtotal + pedido!!.tarifaEnvio + pedido!!.tarifaServicio
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
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Pedido #${pedido.id}", fontWeight = FontWeight.Bold, color = BlackText, fontSize = 16.sp)
                                    Text("Estado: ${pedido.estado}", color = Primary, fontWeight = FontWeight.Medium)
                                    Text("Fecha: ${pedido.fecha}", color = GrayText, fontSize = 12.sp)
                                }
                                Icon(Icons.Filled.Assignment, contentDescription = "Pedido", modifier = Modifier.size(32.dp), tint = GrayText)
                            }
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = { onPedidoDetalle(pedido.id) },
                                modifier = Modifier.align(Alignment.End),
                                colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Filled.Info, contentDescription = "Ver detalles", modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Ver detalles")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClienteProductoDetalleScreen(productoId: Int, onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    var producto by remember { mutableStateOf<ProductoEntity?>(null) }
    LaunchedEffect(productoId) {
        producto = repository.db.productoDao().getProductoById(productoId)
    }
    if (producto == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    Column(Modifier.fillMaxSize().background(GrayBackground).padding(16.dp)) {
        BackButton(onClick = onBack, modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Storefront, contentDescription = "Producto", modifier = Modifier.size(80.dp), tint = Primary)
                Spacer(Modifier.height(16.dp))
                Text(producto!!.nombre, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = BlackText)
                Spacer(Modifier.height(8.dp))
                Text(producto!!.descripcion, color = GrayText, fontSize = 16.sp)
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Precio:", color = GrayText, fontSize = 14.sp)
                        Text("$${producto!!.precio}", color = BlackText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Stock disponible:", color = GrayText, fontSize = 14.sp)
                        Text("${producto!!.stock} unidades", color = BlackText, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
