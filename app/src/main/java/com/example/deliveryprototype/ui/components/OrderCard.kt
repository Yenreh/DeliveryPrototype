package com.example.deliveryprototype.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deliveryprototype.model.PedidoEntity
import com.example.deliveryprototype.ui.theme.Primary
import com.example.deliveryprototype.ui.theme.BlackText
import com.example.deliveryprototype.ui.theme.GrayText
import com.example.deliveryprototype.utils.FeeUtils

/**
 * Componente reutilizable para mostrar una tarjeta de pedido
 */
@Composable
fun OrderCard(
    pedido: PedidoEntity,
    onCardClick: ((Int) -> Unit)? = null,
    showDetailButton: Boolean = true,
    showApproxTotal: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (showDetailButton) Color.White else Color(0xFFF5F5F5)
        ),
        elevation = if (showDetailButton) CardDefaults.cardElevation(2.dp) else CardDefaults.cardElevation(0.dp)
    ) {
        Column(Modifier.padding(if (showDetailButton) 16.dp else 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Pedido #${pedido.id}",
                        fontWeight = FontWeight.Bold,
                        color = BlackText,
                        fontSize = if (showDetailButton) 16.sp else 14.sp
                    )
                    Text(
                        "Estado: ${pedido.estado}",
                        color = if (showDetailButton) Primary else GrayText,
                        fontSize = 14.sp,
                        fontWeight = if (showDetailButton) FontWeight.Medium else FontWeight.Normal
                    )
                    Text(
                        "Fecha: ${pedido.fecha}",
                        color = GrayText,
                        fontSize = 12.sp
                    )
                    
                    // Show approximate total only for home screen
                    if (showApproxTotal && (pedido.tarifaEnvio > 0 || pedido.tarifaServicio > 0)) {
                        val total = pedido.tarifaEnvio + pedido.tarifaServicio
                        Text(
                            "Total aprox: ${FeeUtils.formatMoney(total)}",
                            color = Primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                if (showDetailButton) {
                    Icon(
                        Icons.Filled.Assignment,
                        contentDescription = "Pedido",
                        modifier = Modifier.size(32.dp),
                        tint = GrayText
                    )
                }
            }
            
            if (showDetailButton && onCardClick != null) {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { onCardClick(pedido.id) },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = "Ver detalles",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Ver detalles")
                }
            }
        }
    }
}