package com.example.deliveryprototype.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val role: String, // "tendero", "cliente", "repartidor"
    val address: String? = null,
    val phone: String? = null,
    val storeName: String? = null,
    val vehicle: String? = null
)
