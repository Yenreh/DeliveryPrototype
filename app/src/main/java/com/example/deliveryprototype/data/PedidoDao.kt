package com.example.deliveryprototype.data

import androidx.room.*
import com.example.deliveryprototype.model.PedidoEntity

@Dao
interface PedidoDao {
    @Query("SELECT * FROM pedidos WHERE id = :id")
    suspend fun getPedidoById(id: Int): PedidoEntity?

    @Query("SELECT * FROM pedidos WHERE clienteId = :clienteId")
    suspend fun getPedidosByCliente(clienteId: Int): List<PedidoEntity>

    @Query("SELECT * FROM pedidos WHERE tenderoId = :tenderoId")
    suspend fun getPedidosByTendero(tenderoId: Int): List<PedidoEntity>

    @Query("SELECT * FROM pedidos WHERE repartidorId = :repartidorId")
    suspend fun getPedidosByRepartidor(repartidorId: Int): List<PedidoEntity>

    @Query("SELECT * FROM pedidos WHERE repartidorId IS NULL AND estado = 'PENDIENTE'")
    suspend fun getPedidosDisponibles(): List<PedidoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPedido(pedido: PedidoEntity): Long

    @Query("UPDATE pedidos SET estado = :estado WHERE id = :id")
    suspend fun updateEstadoPedido(id: Int, estado: String)

    @Update
    suspend fun updatePedido(pedido: PedidoEntity)

    @Delete
    suspend fun deletePedido(pedido: PedidoEntity)
}
