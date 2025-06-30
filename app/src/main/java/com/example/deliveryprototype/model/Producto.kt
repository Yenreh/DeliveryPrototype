package com.example.deliveryprototype.model

data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val tenderoId: Int
)
