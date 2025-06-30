package com.example.deliveryprototype.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deliveryprototype.model.ProductoEntity
import com.example.deliveryprototype.ui.theme.Primary
import com.example.deliveryprototype.ui.theme.BlackText
import com.example.deliveryprototype.ui.theme.GrayText
import com.example.deliveryprototype.utils.FeeUtils
import com.example.deliveryprototype.utils.OrderCalculationUtils

@Composable
fun OrderConfirmationDialog(
    isVisible: Boolean,
    productosSeleccionados: List<Pair<ProductoEntity, Int>>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        val subtotal = OrderCalculationUtils.calculateSubtotal(productosSeleccionados)
        val deliveryFee = FeeUtils.calculateDeliveryFee()
        val serviceFee = FeeUtils.calculateServiceFee()
        val grandTotal = OrderCalculationUtils.calculateGrandTotal(subtotal, deliveryFee, serviceFee)
        
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Filled.ShoppingCart,
                        contentDescription = "Confirmar compra",
                        tint = Primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Confirmar compra",
                        fontWeight = FontWeight.Bold,
                        color = BlackText
                    )
                }
            },
            text = {
                Column {
                    Text(
                        "¿Estás seguro de que deseas realizar esta compra?",
                        color = BlackText,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    
                    // Productos seleccionados
                    Text(
                        "Productos:",
                        fontWeight = FontWeight.Medium,
                        color = BlackText,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    productosSeleccionados.forEach { (producto, cantidad) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "${producto.nombre} x$cantidad",
                                color = BlackText,
                                fontSize = 14.sp
                            )
                            Text(
                                FeeUtils.formatMoney(producto.precio * cantidad),
                                color = BlackText,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    Divider()
                    Spacer(Modifier.height(8.dp))
                    
                    // Resumen de costos
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal:", color = BlackText, fontSize = 14.sp)
                        Text(FeeUtils.formatMoney(subtotal), color = BlackText, fontSize = 14.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Envío:", color = GrayText, fontSize = 14.sp)
                        Text(FeeUtils.formatMoney(deliveryFee), color = GrayText, fontSize = 14.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Servicio:", color = GrayText, fontSize = 14.sp)
                        Text(FeeUtils.formatMoney(serviceFee), color = GrayText, fontSize = 14.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    Divider()
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Total:",
                            fontWeight = FontWeight.Bold,
                            color = BlackText,
                            fontSize = 16.sp
                        )
                        Text(
                            FeeUtils.formatMoney(grandTotal),
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                            fontSize = 16.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        Icons.Filled.ShoppingCart,
                        contentDescription = "Confirmar",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = GrayText
                    )
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}