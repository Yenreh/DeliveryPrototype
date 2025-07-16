package com.example.deliveryprototype.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.example.deliveryprototype.model.TiendaEntity
import com.example.deliveryprototype.ui.theme.BlackText
import com.example.deliveryprototype.ui.theme.GraySurface
import com.example.deliveryprototype.ui.theme.GrayText
import com.example.deliveryprototype.ui.theme.Primary

@Composable
fun ClienteTiendasScreen(onTiendaClick: (TiendaEntity) -> Unit) {
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