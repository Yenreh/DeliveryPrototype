package com.example.deliveryprototype.model

data class Pedido(
    val id: Int,
    val clienteId: Int,
    val tenderoId: Int,
    val repartidorId: Int?,
    val productos: List<Producto>,
    val estado: EstadoPedido
)

enum class EstadoPedido {
    PENDIENTE, EN_CAMINO, ENTREGADO, CANCELADO
}
