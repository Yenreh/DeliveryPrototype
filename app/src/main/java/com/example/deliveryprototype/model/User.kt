package com.example.deliveryprototype.model

sealed class User(
    open val id: Int,
    open val name: String,
    open val email: String,
    open val password: String
)

class Tendero(
    override val id: Int,
    override val name: String,
    override val email: String,
    override val password: String,
    val storeName: String
) : User(id, name, email, password)

class Cliente(
    override val id: Int,
    override val name: String,
    override val email: String,
    override val password: String
) : User(id, name, email, password)

class Repartidor(
    override val id: Int,
    override val name: String,
    override val email: String,
    override val password: String,
    val vehicle: String
) : User(id, name, email, password)

object UserFactory {
    fun createUser(
        role: String,
        id: Int,
        name: String,
        email: String,
        password: String,
        extra: String = ""
    ): User = when (role) {
        "tendero" -> Tendero(id, name, email, password, extra)
        "cliente" -> Cliente(id, name, email, password)
        "repartidor" -> Repartidor(id, name, email, password, extra)
        else -> throw IllegalArgumentException("Unknown role")
    }
}
