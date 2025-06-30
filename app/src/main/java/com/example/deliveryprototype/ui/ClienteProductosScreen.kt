package com.example.deliveryprototype.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.deliveryprototype.data.AppRepository
import com.example.deliveryprototype.model.ProductoEntity
import kotlinx.coroutines.launch

@Composable
fun ClienteProductosScreen(tiendaId: Int, onComprar: (List<Pair<ProductoEntity, Int>>) -> Unit) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    val productos = remember { mutableStateListOf<ProductoEntity>() }
    val cantidades = remember { mutableStateMapOf<Int, Int>() }
    val scope = rememberCoroutineScope()
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(tiendaId) {
        if (!isLoaded) {
            val list = repository.db.productoDao().getProductosByTendero(tiendaId)
            productos.clear()
            productos.addAll(list)
            list.forEach { cantidades[it.id] = 0 }
            isLoaded = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Productos", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(productos) { producto ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(producto.descripcion)
                            Text("Precio: $${producto.precio}")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { cantidades[producto.id] = (cantidades[producto.id] ?: 0) - 1 }) {
                                Text("-")
                            }
                            Text("${cantidades[producto.id] ?: 0}", modifier = Modifier.width(24.dp))
                            IconButton(onClick = { cantidades[producto.id] = (cantidades[producto.id] ?: 0) + 1 }) {
                                Text("+")
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        val hasSelectedProducts = cantidades.values.any { it > 0 }
        Button(
            onClick = {
                val seleccionados = productos.mapNotNull { p ->
                    val cantidad = cantidades[p.id] ?: 0
                    if (cantidad > 0) p to cantidad else null
                }
                onComprar(seleccionados)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = hasSelectedProducts
        ) {
            Text("Comprar")
        }
    }
}
