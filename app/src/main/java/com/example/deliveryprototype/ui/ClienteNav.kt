package com.example.deliveryprototype.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import com.example.deliveryprototype.data.AppRepository
import com.example.deliveryprototype.model.TiendaEntity
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable

sealed class ClienteNavItem(val label: String, val icon: ImageVector) {
    object Tiendas : ClienteNavItem("Tiendas", Icons.Filled.Shop)
    object Home : ClienteNavItem("Inicio", Icons.Filled.Home)
    object Pedidos : ClienteNavItem("Tus Pedidos", Icons.Filled.List)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteNavScaffold(onLogout: () -> Unit) {
    var selectedIndex by remember { mutableStateOf(1) }
    val items = listOf(
        ClienteNavItem.Tiendas,
        ClienteNavItem.Home,
        ClienteNavItem.Pedidos
    )
    var tiendaSeleccionadaId by remember { mutableStateOf<Int?>(null) }
    var mostrarProductos by remember { mutableStateOf(false) }
    var productosSeleccionados by remember { mutableStateOf<List<Pair<com.example.deliveryprototype.model.ProductoEntity, Int>>>(emptyList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Cliente") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Filled.Logout, contentDescription = "Cerrar sesión")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            mostrarProductos = false
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when {
                mostrarProductos && tiendaSeleccionadaId != null ->
                    ClienteProductosScreen(tiendaId = tiendaSeleccionadaId!!) { seleccionados ->
                        productosSeleccionados = seleccionados
                        // Aquí puedes manejar la compra
                        mostrarProductos = false
                    }
                selectedIndex == 0 -> ClienteTiendasScreen(onTiendaClick = { id ->
                    tiendaSeleccionadaId = id
                    mostrarProductos = true
                })
                selectedIndex == 1 -> ClienteHomeScreen()
                selectedIndex == 2 -> ClientePedidosScreen()
            }
        }
    }
}

@Composable
fun ClienteHomeScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hola *Nombre del cliente*", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Tus pedidos a Call XX # XX-XX")
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("¿Qué necesitas hoy?") }, leadingIcon = { Icon(Icons.Filled.Search, null) })
        Spacer(modifier = Modifier.height(16.dp))
        // Aquí iría el carrusel de banners o promociones
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("¿Qué buscas?") })
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { }) { Text("Restaurantes") }
            Button(onClick = { }) { Text("Compras") }
            Button(onClick = { }) { Text("Farmacias") }
        }
    }
}

@Composable
fun ClienteTiendasScreen(onTiendaClick: (Int) -> Unit) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    val tiendas = remember { mutableStateListOf<TiendaEntity>() }
    val scope = rememberCoroutineScope()
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!isLoaded) {
            val list = repository.db.tiendaDao().getTiendasByTendero(1) // Para el prototipo, tenderoId 1
            tiendas.clear()
            tiendas.addAll(list)
            isLoaded = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Tiendas", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Texto de búsqueda") }, leadingIcon = { Icon(Icons.Filled.Search, null) })
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(tiendas) { tienda ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onTiendaClick(tienda.id) },
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(tienda.nombre, style = MaterialTheme.typography.titleMedium)
                        Text(tienda.direccion)
                        Text("ID tendero: ${tienda.tenderoId}")
                    }
                }
            }
        }
    }
}

@Composable
fun ClientePedidosScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Tus Pedidos", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        // Aquí iría la lista de pedidos
        Text("[Lista de pedidos]")
    }
}
