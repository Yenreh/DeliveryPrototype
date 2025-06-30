package com.example.deliveryprototype.data

import androidx.room.*
import com.example.deliveryprototype.model.TiendaEntity

@Dao
interface TiendaDao {
    @Query("SELECT * FROM tiendas WHERE id = :id")
    suspend fun getTiendaById(id: Int): TiendaEntity?

    @Query("SELECT * FROM tiendas WHERE tenderoId = :tenderoId")
    suspend fun getTiendasByTendero(tenderoId: Int): List<TiendaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTienda(tienda: TiendaEntity): Long

    @Update
    suspend fun updateTienda(tienda: TiendaEntity)

    @Delete
    suspend fun deleteTienda(tienda: TiendaEntity)
}
