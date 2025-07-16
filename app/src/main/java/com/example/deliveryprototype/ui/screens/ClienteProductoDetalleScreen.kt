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
import com.example.deliveryprototype.model.ProductoEntity
import com.example.deliveryprototype.ui.components.BackButton
import com.example.deliveryprototype.ui.theme.BlackText
import com.example.deliveryprototype.ui.theme.GrayBackground
import com.example.deliveryprototype.ui.theme.GrayText
import com.example.deliveryprototype.ui.theme.Primary

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