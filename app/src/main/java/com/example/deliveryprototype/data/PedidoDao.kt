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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPedido(pedido: PedidoEntity): Long

    @Update
    suspend fun updatePedido(pedido: PedidoEntity)

    @Delete
    suspend fun deletePedido(pedido: PedidoEntity)
}
