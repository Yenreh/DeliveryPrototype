package com.example.deliveryprototype.utils

import com.example.deliveryprototype.model.ProductoEntity

/**
 * Utilidades para cálculos de pedidos
 */
object OrderCalculationUtils {
    
    /**
     * Calcula el subtotal de una lista de productos con sus cantidades
     */
    fun calculateSubtotal(productosConCantidades: List<Pair<ProductoEntity, Int>>): Double {
        return productosConCantidades.sumOf { (producto, cantidad) -> 
            producto.precio * cantidad 
        }
    }
    
    /**
     * Calcula el subtotal desde un mapa de cantidades y lista de productos
     */
    fun calculateSubtotal(productos: List<ProductoEntity>, cantidades: Map<Int, Int>): Double {
        return productos.sumOf { producto ->
            val cantidad = cantidades[producto.id] ?: 0
            producto.precio * cantidad
        }
    }
    
    /**
     * Calcula el total final incluyendo tarifas
     */
    fun calculateGrandTotal(subtotal: Double, deliveryFee: Double, serviceFee: Double): Double {
        return subtotal + deliveryFee + serviceFee
    }
    
    /**
     * Convierte productos seleccionados a formato de IDs con cantidades
     * Formato: "id:cantidad,id:cantidad"
     */
    fun formatProductosIds(productosSeleccionados: List<Pair<ProductoEntity, Int>>): String {
        return productosSeleccionados.joinToString(",") { (producto, cantidad) ->
            "${producto.id}:$cantidad"
        }
    }
    
    /**
     * Parsea el formato de productos IDs a un mapa de ID -> cantidad
     * Maneja tanto el formato nuevo "id:cantidad,id:cantidad" como el formato legacy "id,id,id"
     */
    fun parseProductosIds(productosIds: String): Map<Int, Int> {
        if (productosIds.isEmpty()) return emptyMap()
        
        return productosIds.split(",").mapNotNull { item ->
            val parts = item.split(":")
            if (parts.size == 2) {
                // Formato nuevo: "id:cantidad"
                val id = parts[0].toIntOrNull()
                val cantidad = parts[1].toIntOrNull()
                if (id != null && cantidad != null) {
                    id to cantidad
                } else null
            } else if (parts.size == 1) {
                // Formato legacy: "id" (asumimos cantidad = 1)
                val id = parts[0].toIntOrNull()
                if (id != null) {
                    id to 1
                } else null
            } else null
        }.toMap()
    }
    
    /**
     * Valida que haya productos seleccionados para crear un pedido
     */
    fun hasValidProductsForOrder(productosSeleccionados: List<Pair<ProductoEntity, Int>>): Boolean {
        return productosSeleccionados.isNotEmpty() && 
               productosSeleccionados.any { it.second > 0 }
    }
}