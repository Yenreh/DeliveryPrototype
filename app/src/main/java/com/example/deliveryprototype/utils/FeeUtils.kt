package com.example.deliveryprototype.utils

/**
 * Utilidades para cálculo de tarifas de envío y servicio
 */
object FeeUtils {
    
    /**
     * Calcula la tarifa de envío basada en la distancia
     * Por ahora retorna un valor fijo de $2000 COP
     */
    fun calculateDeliveryFee(): Double {
        // TODO: Implementar cálculo real basado en distancia
        return 2000.0
    }
    
    /**
     * Calcula la tarifa de servicio basada en condiciones
     * Por ahora retorna un valor fijo de $1000 COP
     */
    fun calculateServiceFee(): Double {
        // TODO: Implementar cálculo real basado en condiciones
        return 1000.0
    }
    
    /**
     * Formatea un valor monetario para mostrar en la UI
     */
    fun formatMoney(amount: Double): String {
        return "$ ${"%.2f".format(amount)}"
    }
}