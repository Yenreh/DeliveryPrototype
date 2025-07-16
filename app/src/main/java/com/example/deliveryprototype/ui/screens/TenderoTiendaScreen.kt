package com.example.deliveryprototype.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.deliveryprototype.data.AppRepository
import com.example.deliveryprototype.model.ProductoEntity
import com.example.deliveryprototype.model.TiendaEntity
import com.example.deliveryprototype.ui.theme.GrayText
import com.example.deliveryprototype.ui.theme.Primary
import com.example.deliveryprototype.utils.FeeUtils

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
                FeeUtils.formatMoney(producto.precio),
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