package com.example.deliveryprototype.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.example.deliveryprototype.ui.components.LogoutButton
import com.example.deliveryprototype.ui.components.AppTopBar
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.min
import com.example.deliveryprototype.data.AppRepository
import com.example.deliveryprototype.model.PedidoEntity
import com.example.deliveryprototype.model.ProductoEntity
import com.example.deliveryprototype.model.TiendaEntity
import com.example.deliveryprototype.ui.components.OrderCard
import com.example.deliveryprototype.ui.theme.GrayText
import com.example.deliveryprototype.ui.theme.Primary

sealed class TenderoNavItem(val label: String, val icon: ImageVector) {
    object Tienda : TenderoNavItem("Tu tienda", Icons.Filled.Shop)
    object Home : TenderoNavItem("Inicio", Icons.Filled.Home)
    object Pedidos : TenderoNavItem("Pedidos", Icons.Filled.List)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenderoNavScaffold(onLogout: () -> Unit) {
var selectedIndex by remember { mutableStateOf(1) }
val currentScreen = remember { mutableStateOf<TenderoScreen>(TenderoScreen.Home) }
    
    val items = listOf(
        TenderoNavItem.Tienda,
        TenderoNavItem.Home,
        TenderoNavItem.Pedidos
    )
    
    // Función para navegar entre pantallas
    val navigateToScreen = { screen: TenderoScreen ->
        currentScreen.value = screen
    }
    
    // Función para regresar al home
    val navigateBack = {
        currentScreen.value = TenderoScreen.Home
        selectedIndex = 1
    }
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Panel Tendero",
                actions = {
                    LogoutButton(onClick = onLogout)
                }
            )
        },
        bottomBar = {
            // Solo mostrar bottom bar en las pantallas principales
            if (currentScreen.value in listOf(TenderoScreen.Home, TenderoScreen.Tienda, TenderoScreen.Pedidos)) {
                NavigationBar {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedIndex == index,
                            onClick = { 
                            selectedIndex = index
                            currentScreen.value = when (index) {
                                0 -> TenderoScreen.Tienda
                                1 -> TenderoScreen.Home
                                2 -> TenderoScreen.Pedidos
                                else -> TenderoScreen.Home
                            }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val screen = currentScreen.value) {
                is TenderoScreen.Home -> TenderoHomeScreen(
                    onPedidoDetalle = { pedidoId ->
                        currentScreen.value = TenderoScreen.PedidoDetalle(pedidoId)
                    }
                )
                is TenderoScreen.Tienda -> TenderoTiendaScreen(
                    onProductoAdd = {
                        currentScreen.value = TenderoScreen.ProductForm(null)
                    },
                    onProductoEdit = { productoId ->
                        currentScreen.value = TenderoScreen.ProductForm(productoId)
                    }
                )
                is TenderoScreen.Pedidos -> TenderoPedidosScreen(
                    onPedidoDetalle = { pedidoId ->
                        currentScreen.value = TenderoScreen.PedidoDetalle(pedidoId)
                    }
                )
                is TenderoScreen.ProductForm -> ProductFormScreen(
                    productoId = screen.productoId,
                    onBack = navigateBack,
                    onSave = navigateBack
                )
                is TenderoScreen.PedidoDetalle -> TenderoPedidoDetalleScreen(
                    pedidoId = screen.pedidoId,
                    onBack = navigateBack,
                    onUpdateEstado = { /* Estado actualizado */ }
                )
            }
        }
    }
}

// Sealed class para las diferentes pantallas del tendero
sealed class TenderoScreen {
    object Home : TenderoScreen()
    object Tienda : TenderoScreen()
    object Pedidos : TenderoScreen()
    data class ProductForm(val productoId: Int?) : TenderoScreen()
    data class PedidoDetalle(val pedidoId: Int) : TenderoScreen()
}

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
            val cantidadesMap = com.example.deliveryprototype.utils.OrderCalculationUtils.parseProductosIds(pedido.productosIds)
            val productosDelPedido = cantidadesMap.keys.mapNotNull { id -> 
                productos.find { it.id == id }
            }
            com.example.deliveryprototype.utils.OrderCalculationUtils.calculateSubtotal(productosDelPedido, cantidadesMap)
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
                    value = com.example.deliveryprototype.utils.FeeUtils.formatMoney(gananciasDelDia),
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


@Composable
fun TenderoTiendaScreen(onProductoAdd: () -> Unit = {}, onProductoEdit: (Int) -> Unit = {}) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    var productos by remember { mutableStateOf<List<ProductoEntity>>(emptyList()) }
    var tienda by remember { mutableStateOf<TiendaEntity?>(null) }
    LaunchedEffect(Unit) {
        productos = repository.db.productoDao().getProductosByTendero(1)
        tienda = repository.db.tiendaDao().getTiendaById(1)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        // Header del comercio
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

        // Productos header con botón agregar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Productos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Button(
                onClick = onProductoAdd,
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Agregar producto",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Agregar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Lista de productos
        if (productos.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Box(
                    modifier = Modifier.padding(32.dp).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No tienes productos. ¡Agrega tu primer producto!",
                        color = GrayText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(productos) { producto ->
                    ProductCard(
                        producto = producto,
                        onEditClick = { onProductoEdit(producto.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    producto: ProductoEntity,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del producto (placeholder)
            Card(
                modifier = Modifier.size(60.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Storefront,
                        contentDescription = "Producto",
                        modifier = Modifier.size(32.dp),
                        tint = GrayText
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Información del producto
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Stock: ${producto.stock}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (producto.stock <= 5) Color.Red else GrayText
                )
            }
            
            // Precio
            Text(
                com.example.deliveryprototype.utils.FeeUtils.formatMoney(producto.precio),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Botón ver detalles
            TextButton(onClick = onEditClick) {
                Text("Ver detalles")
            }
        }
    }
}

@Composable
fun TenderoPedidosScreen(onPedidoDetalle: (Int) -> Unit = {}) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    var pedidos by remember { mutableStateOf<List<PedidoEntity>>(emptyList()) }
    
    // Para el prototipo, usar tenderoId = 1
    LaunchedEffect(Unit) {
        pedidos = repository.db.pedidoDao().getPedidosByTendero(1)
    }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Pedidos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (pedidos.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Box(
                    modifier = Modifier.padding(32.dp).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No tienes pedidos.",
                        color = GrayText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
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
