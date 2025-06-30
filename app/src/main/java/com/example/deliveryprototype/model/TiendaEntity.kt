package com.example.deliveryprototype.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tiendas")
data class TiendaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val direccion: String,
    val tenderoId: Int
)
