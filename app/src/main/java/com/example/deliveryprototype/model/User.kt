package com.example.deliveryprototype.model

sealed class User(
    open val id: Int,
    open val name: String,
    open val email: String,
    open val password: String,
    open val address: String?,
    open val phone: String?
)

class Tendero(
    override val id: Int,
    override val name: String,
    override val email: String,
    override val password: String,
    override val address: String?,
    override val phone: String?,
    val storeName: String
) : User(id, name, email, password, address, phone)

class Cliente(
    override val id: Int,
    override val name: String,
    override val email: String,
    override val password: String,
    override val address: String?,
    override val phone: String?
) : User(id, name, email, password, address, phone)

class Repartidor(
    override val id: Int,
    override val name: String,
    override val email: String,
    override val password: String,
    override val address: String?,
    override val phone: String?,
    val vehicle: String
) : User(id, name, email, password, address, phone)

object UserFactory {
    fun createUser(
        role: String,
        id: Int,
        name: String,
        email: String,
        password: String,
        address: String? = null,
        phone: String? = null,
        extra: String = ""
    ): User = when (role) {
        "tendero" -> Tendero(id, name, email, password, address, phone, extra)
        "cliente" -> Cliente(id, name, email, password, address, phone)
        "repartidor" -> Repartidor(id, name, email, password, address, phone, extra)
        else -> throw IllegalArgumentException("Unknown role")
    }
}
