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
            userDao.insertUser(UserEntity(id = 1, name = "tendero1", email = "tendero@tienda.com", password = "tendero1", role = "tendero", storeName = "Tienda Uno"))
            userDao.insertUser(UserEntity(id = 2, name = "cliente1", email = "cliente@correo.com", password = "cliente1", role = "cliente"))
            userDao.insertUser(UserEntity(id = 3, name = "repartidor1", email = "repartidor@correo.com", password = "repartidor1", role = "repartidor", vehicle = "Moto"))

            // Tienda
            tiendaDao.insertTienda(TiendaEntity(nombre = "Tienda Uno", direccion = "Calle 123", tenderoId = 1))

            // Productos
            productoDao.insertProducto(ProductoEntity(nombre = "Pan", descripcion = "Pan fresco", precio = 1.5, stock = 100, tenderoId = 1))
            productoDao.insertProducto(ProductoEntity(nombre = "Leche", descripcion = "Leche entera", precio = 2.0, stock = 50, tenderoId = 1))

            // Pedido
            pedidoDao.insertPedido(PedidoEntity(clienteId = 2, tenderoId = 1, repartidorId = 3, productosIds = "1,2", estado = "PENDIENTE"))
        }
    }

    suspend fun getUserById(id: Int) = userDao.getUserById(id)
    suspend fun getUsersByRole(role: String) = userDao.getUsersByRole(role)
    suspend fun getUserByUsernameAndPassword(username: String, password: String) = userDao.getUserByUsernameAndPassword(username, password)
    // ...otros métodos según necesidad
}
