package com.example.deliveryprototype.data

import androidx.room.*
import com.example.deliveryprototype.model.ProductoEntity

@Dao
interface ProductoDao {
    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getProductoById(id: Int): ProductoEntity?

    @Query("SELECT * FROM productos WHERE tenderoId = :tenderoId")
    suspend fun getProductosByTendero(tenderoId: Int): List<ProductoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: ProductoEntity): Long

    @Update
    suspend fun updateProducto(producto: ProductoEntity)

    @Delete
    suspend fun deleteProducto(producto: ProductoEntity)
}
