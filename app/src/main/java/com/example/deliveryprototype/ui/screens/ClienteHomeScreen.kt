package com.example.deliveryprototype.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
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
import com.example.deliveryprototype.model.UserEntity
import com.example.deliveryprototype.ui.theme.GrayBackground
import com.example.deliveryprototype.ui.theme.GrayBar
import com.example.deliveryprototype.ui.theme.BlackText
import com.example.deliveryprototype.ui.theme.Primary
import com.example.deliveryprototype.ui.theme.GrayText
import com.example.deliveryprototype.ui.components.OrderCard

@Composable
fun ClienteHomeScreen(loggedInUser: UserEntity, onPedidoDetalle: (Int) -> Unit = {}) {
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween // <-- Esto pega el contenido al fondo
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
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
                modifier = Modifier.fillMaxWidth().height(170.dp)
            ) {
                items(listOf("Promo 1", "Promo 2", "Promo 3")) { promo ->
                    Card(
                        modifier = Modifier.size(180.dp, 160.dp).padding(end = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(promo)
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
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
                Text("No hay pedidos recientes.", color = GrayText)
            } else {
                val ultimosPedidos = pedidos.takeLast(4).reversed()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                ) {
                    items(ultimosPedidos) { pedido ->
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
        Spacer(modifier = Modifier.height(0.dp)) // Elimina espacio extra abajo
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