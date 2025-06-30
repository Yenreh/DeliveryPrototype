package com.example.deliveryprototype.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clienteId: Int,
    val tenderoId: Int,
    val repartidorId: Int?,
    val productosIds: String, // Lista de IDs serializada (ej: "1,2,3")
    val estado: String, // "PENDIENTE", "EN_CAMINO", etc.
    val fecha: String, // Fecha en formato ISO8601 o simple (ej: "2025-06-30 11:30")
    val tarifaEnvio: Double = 0.0, // Tarifa de envío
    val tarifaServicio: Double = 0.0 // Tarifa de servicio
)
