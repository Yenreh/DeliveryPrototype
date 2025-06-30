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
    val estado: String // "PENDIENTE", "EN_CAMINO", etc.
)
