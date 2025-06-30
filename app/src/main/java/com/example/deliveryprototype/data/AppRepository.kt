package com.example.deliveryprototype.data

import android.content.Context
import com.example.deliveryprototype.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppRepository(context: Context) {
    internal val db = AppDatabase.getDatabase(context)
    private val userDao = db.userDao()
    private val productoDao = db.productoDao()
    private val tiendaDao = db.tiendaDao()
    private val pedidoDao = db.pedidoDao()

    fun insertSampleData() {
        CoroutineScope(Dispatchers.IO).launch {
            // Usuarios
            userDao.insertUser(UserEntity(id = 1, name = "tendero1", email = "tendero@tienda.com", password = "tendero1", role = "tendero", address = "Calle 123 #45-67", phone = "3001234567", storeName = "Tienda Uno"))
            userDao.insertUser(UserEntity(id = 2, name = "cliente1", email = "cliente@correo.com", password = "cliente1", role = "cliente", address = "Cra 50 #10-20", phone = "3012345678"))
            userDao.insertUser(UserEntity(id = 3, name = "repartidor1", email = "repartidor@correo.com", password = "repartidor1", role = "repartidor", address = "Calle 99 #99-99", phone = "3023456789", vehicle = "Moto"))

            // Tiendas
            tiendaDao.insertTienda(TiendaEntity(id = 1, nombre = "Tienda Uno", direccion = "Calle 123 #45-67", tenderoId = 1))
            tiendaDao.insertTienda(TiendaEntity(id = 2, nombre = "Restaurante El Sabor", direccion = "Cra 10 #20-30", tenderoId = 1))
            tiendaDao.insertTienda(TiendaEntity(id = 3, nombre = "Farmacia Salud", direccion = "Av 5 #15-25", tenderoId = 1))

            // Productos
            productoDao.insertProducto(ProductoEntity(id = 1, nombre = "Pan", descripcion = "Pan fresco", precio = 1500.0, stock = 100, tenderoId = 1))
            productoDao.insertProducto(ProductoEntity(id = 2, nombre = "Leche", descripcion = "Leche entera", precio = 2500.0, stock = 50, tenderoId = 1))
            productoDao.insertProducto(ProductoEntity(id = 3, nombre = "Hamburguesa", descripcion = "Hamburguesa especial", precio = 12000.0, stock = 30, tenderoId = 2))
            productoDao.insertProducto(ProductoEntity(id = 4, nombre = "Aspirina", descripcion = "Caja de 20 tabletas", precio = 8000.0, stock = 40, tenderoId = 3))

            // Pedidos con cantidades (formato id:cantidad)
            pedidoDao.insertPedido(PedidoEntity(clienteId = 2, tenderoId = 1, repartidorId = 3, productosIds = "1:2,2:1", estado = "PENDIENTE", fecha = "2025-06-30 11:00", tarifaEnvio = 2000.0, tarifaServicio = 1000.0))
            pedidoDao.insertPedido(PedidoEntity(clienteId = 2, tenderoId = 2, repartidorId = 3, productosIds = "3:3", estado = "EN_CAMINO", fecha = "2025-06-30 11:10", tarifaEnvio = 2000.0, tarifaServicio = 1000.0))
            pedidoDao.insertPedido(PedidoEntity(clienteId = 2, tenderoId = 3, repartidorId = 3, productosIds = "4:1", estado = "ENTREGADO", fecha = "2025-06-30 10:30", tarifaEnvio = 2000.0, tarifaServicio = 1000.0))
        }
    }

    /**
     * Parsea el string productosIds (formato "id:cantidad,id:cantidad") y devuelve una lista de pares (id, cantidad)
     */
    fun parseProductosIds(productosIds: String): List<Pair<Int, Int>> {
        return productosIds.split(",")
            .mapNotNull { entry ->
                val parts = entry.split(":")
                if (parts.size == 2) {
                    val id = parts[0].toIntOrNull()
                    val cantidad = parts[1].toIntOrNull()
                    if (id != null && cantidad != null) id to cantidad else null
                } else null
            }
    }

    suspend fun getUserById(id: Int) = userDao.getUserById(id)
    suspend fun getUsersByRole(role: String) = userDao.getUsersByRole(role)
    suspend fun getUserByUsernameAndPassword(username: String, password: String) = userDao.getUserByUsernameAndPassword(username, password)
    suspend fun getAllTiendas() = tiendaDao.getAllTiendas()
    suspend fun getProductosByTienda(tenderoId: Int) = productoDao.getProductosByTienda(tenderoId)
    // ...otros métodos según necesidad
}
