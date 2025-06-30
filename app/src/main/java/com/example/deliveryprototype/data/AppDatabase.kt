package com.example.deliveryprototype.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.deliveryprototype.model.*

@Database(
    entities = [UserEntity::class, ProductoEntity::class, TiendaEntity::class, PedidoEntity::class],
    version = 2 // Incrementado por cambio de schema (se agregó 'fecha' a PedidoEntity)
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productoDao(): ProductoDao
    abstract fun tiendaDao(): TiendaDao
    abstract fun pedidoDao(): PedidoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
