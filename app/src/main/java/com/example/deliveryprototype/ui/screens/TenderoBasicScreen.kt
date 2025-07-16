package com.example.deliveryprototype.ui

import android.content.Context
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
// Esta función fue renombrada para evitar conflicto con RoleScreens.kt
fun TenderoScreenContent() {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    var productos by remember { mutableStateOf<List<ProductoEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        productos = repository.db.productoDao().getProductosByTendero(1)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mis Productos", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        if (productos.isEmpty()) {
            Text("No tienes productos.", color = MaterialTheme.colorScheme.onSurface)
        } else {
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
}
