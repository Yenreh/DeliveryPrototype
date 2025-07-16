package com.example.deliveryprototype.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.deliveryprototype.data.AppRepository
import com.example.deliveryprototype.model.ProductoEntity
import com.example.deliveryprototype.ui.theme.Primary
import com.example.deliveryprototype.ui.theme.GrayText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    productoId: Int? = null, // null para nuevo producto, id para editar
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    val scope = rememberCoroutineScope()
    
    // Estados del formulario
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val isEditing = productoId != null
    
    // Cargar datos del producto si estamos editando
    LaunchedEffect(productoId) {
        if (productoId != null) {
            val producto = repository.db.productoDao().getProductoById(productoId)
            producto?.let {
                nombre = it.nombre
                precio = it.precio.toString()
                stock = it.stock.toString()
                descripcion = it.descripcion
            }
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        // Top Bar con botón de regreso
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
            }
            Text(
                if (isEditing) "Editar Producto" else "Agregar Producto",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Formulario
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Placeholder para imagen
                Card(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Filled.CameraAlt,
                            contentDescription = "Cargar imagen",
                            modifier = Modifier.size(32.dp),
                            tint = GrayText
                        )
                        Text(
                            "Cargar imagen",
                            color = GrayText,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Campo nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del producto") },
                    placeholder = { Text("Ingrese el nombre del producto") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Campo precio
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio del producto") },
                    placeholder = { Text("Ingrese el precio del producto") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Text("$") }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Campo stock
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock del producto") },
                    placeholder = { Text("Ingrese el Stock del producto") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Campo descripción
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción del producto") },
                    placeholder = { Text("Ingrese la descripción del producto") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 4
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Mostrar error si existe
        errorMessage?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Text(
                    error,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Red
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Botones de acción
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isEditing) Arrangement.SpaceBetween else Arrangement.End
        ) {
            // Botón eliminar (solo en modo edición)
            if (isEditing) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            try {
                                isLoading = true
                                productoId?.let {
                                    repository.db.productoDao().deleteProducto(it)
                                }
                                onSave()
                            } catch (e: Exception) {
                                errorMessage = "Error al eliminar producto: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    )
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Eliminar",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Eliminar")
                }
            }
            
            // Botones Cancelar y Guardar
            Row {
                OutlinedButton(
                    onClick = onBack,
                    enabled = !isLoading
                ) {
                    Text("Cancelar")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                // Validar campos
                                if (nombre.isBlank()) {
                                    errorMessage = "El nombre es requerido"
                                    return@launch
                                }
                                if (precio.isBlank() || precio.toDoubleOrNull() == null) {
                                    errorMessage = "El precio debe ser un número válido"
                                    return@launch
                                }
                                if (stock.isBlank() || stock.toIntOrNull() == null) {
                                    errorMessage = "El stock debe ser un número válido"
                                    return@launch
                                }
                                if (descripcion.isBlank()) {
                                    errorMessage = "La descripción es requerida"
                                    return@launch
                                }
                                
                                isLoading = true
                                errorMessage = null
                                
                                val producto = ProductoEntity(
                                    id = productoId ?: 0,
                                    nombre = nombre,
                                    descripcion = descripcion,
                                    precio = precio.toDouble(),
                                    stock = stock.toInt(),
                                    tenderoId = 1 // Para el prototipo
                                )
                                
                                if (isEditing && productoId != null) {
                                    repository.db.productoDao().updateProducto(producto)
                                } else {
                                    repository.db.productoDao().insertProducto(producto)
                                }
                                
                                onSave()
                            } catch (e: Exception) {
                                errorMessage = "Error al guardar producto: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Icon(
                            Icons.Filled.Save,
                            contentDescription = "Guardar",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Guardar")
                }
            }
        }
    }
}