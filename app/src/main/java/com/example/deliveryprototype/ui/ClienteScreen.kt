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
import androidx.compose.material.icons.filled.Shop
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

    LaunchedEffect(Unit) {
        if (!isLoaded) {
            tiendas = repository.getAllTiendas()
            isLoaded = true
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Tiendas", style = MaterialTheme.typography.titleLarge, color = BlackText)
        Spacer(Modifier.height(8.dp))
        // Buscador
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().background(GrayBar, shape = MaterialTheme.shapes.medium).padding(8.dp)
        ) {
            Icon(Icons.Filled.Search, contentDescription = "Buscar", modifier = Modifier.size(32.dp), tint = Primary)
            Spacer(Modifier.width(8.dp))
            Text("Texto de búsqueda", fontWeight = FontWeight.Medium, color = BlackText)
        }
        Spacer(Modifier.height(8.dp))
        Text("Tiendas", fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))
        LazyColumn {
            items(tiendas) { tienda ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onTiendaClick(tienda) },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Shop, contentDescription = "Tienda", modifier = Modifier.size(40.dp))
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
fun ClienteProductosScreen(tienda: com.example.deliveryprototype.model.TiendaEntity, onComprar: (List<Pair<ProductoEntity, Int>>) -> Unit) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    var productos by remember { mutableStateOf<List<ProductoEntity>>(emptyList()) }
    val cantidades = remember { mutableStateMapOf<Int, Int>() }
    var isLoaded by remember { mutableStateOf(false) }
    var total by remember { mutableStateOf(0.0) }

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
        // Buscador
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().background(GrayBar, shape = MaterialTheme.shapes.medium).padding(8.dp)
        ) {
            Icon(Icons.Filled.Search, contentDescription = "Buscar", modifier = Modifier.size(32.dp), tint = Primary)
            Spacer(Modifier.width(8.dp))
            Text("Texto de búsqueda", fontWeight = FontWeight.Medium, color = BlackText)
        }
        Spacer(Modifier.height(12.dp))
        Text("Productos", fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))
        Column(Modifier.fillMaxWidth()) {
            productos.forEach { producto ->
                Card(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Row(
                        Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Shop, contentDescription = "Producto", modifier = Modifier.size(40.dp))
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text(producto.nombre, fontWeight = FontWeight.Bold)
                            Text("${producto.precio} $", fontSize = 14.sp)
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
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("Total de la compra    $ ${"%.2f".format(total)}", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { onComprar(productos.map { it to (cantidades[it.id] ?: 0) }.filter { it.second > 0 }) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Shop, contentDescription = "Comprar")
            Spacer(Modifier.width(8.dp))
            Text("Comprar")
        }
    }
}

@Composable
fun ClientePedidoDetalleScreen(pedidoId: Int) {
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
            // Para el prototipo, cantidades dummy (1 por producto)
            cantidades = ids.groupingBy { it }.eachCount()
        }
    }

    if (pedido == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        // Header
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = GrayBackground)) {
            Column(Modifier.padding(8.dp)) {
                Text("Código-pedido: #${pedido!!.id}", fontWeight = FontWeight.Bold, color = BlackText)
                Text("Pedido para ${pedido!!.clienteId}", color = BlackText)
                Text("Dirección de entrega: ${tienda?.direccion ?: "-"}", color = GrayText)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Shop, contentDescription = "Tienda", modifier = Modifier.size(32.dp), tint = Primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Pedido de ${tienda?.nombre ?: "-"}", color = BlackText)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("ESTADO DEL PEDIDO: ${pedido!!.estado}", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        // Valores
        Text("Valores", fontWeight = FontWeight.Medium)
        productos.forEach { producto ->
            val cantidad = cantidades[producto.id] ?: 1
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${producto.nombre} x $cantidad")
                Text("$ ${"%.2f".format(producto.precio * cantidad)}")
            }
        }
        // Tarifas dummy
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("tarifa de domicilio")
            Text("$ 3.000,00")
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("tarifa de servicio")
            Text("$ 1.000,00")
        }
        Divider(Modifier.padding(vertical = 8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total de la compra", fontWeight = FontWeight.Bold)
            val total = productos.sumOf { (cantidades[it.id] ?: 1) * it.precio } + 3000 + 1000
            Text("$ ${"%.2f".format(total)}", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        // Productos
        Text("Productos", fontWeight = FontWeight.Medium)
        productos.forEach { producto ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Shop, contentDescription = "Producto", modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(producto.nombre)
                }
                Text("${cantidades[producto.id] ?: 1}")
            }
        }
    }
}

@Composable
fun ClientePedidosScreen(loggedInUser: UserEntity) {
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
                        colors = CardDefaults.cardColors(containerColor = GrayBackground)
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            Text("Pedido #${pedido.id}", fontWeight = FontWeight.Bold, color = BlackText)
                            Text("Estado: ${pedido.estado}", color = BlackText)
                            Text("Fecha: ${pedido.fecha}", color = GrayText)
                        }
                    }
                }
            }
        }
    }
}
