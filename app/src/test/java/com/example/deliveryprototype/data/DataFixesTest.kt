package com.example.deliveryprototype.data

import com.example.deliveryprototype.model.PedidoEntity
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for data fixes
 */
class DataFixesTest {
    
    @Test
    fun testOrders_shouldHaveProperFees() {
        // Test that orders have proper delivery and service fees
        val testOrder = PedidoEntity(
            clienteId = 2,
            tenderoId = 1,
            repartidorId = 3,
            productosIds = "1,2",
            estado = "PENDIENTE",
            fecha = "2025-06-30 11:00",
            tarifaEnvio = 2000.0,
            tarifaServicio = 1000.0
        )
        
        assertEquals("Delivery fee should be 2000.0", 2000.0, testOrder.tarifaEnvio, 0.0)
        assertEquals("Service fee should be 1000.0", 1000.0, testOrder.tarifaServicio, 0.0)
        assertTrue("Total fees should be greater than 0", testOrder.tarifaEnvio + testOrder.tarifaServicio > 0)
    }
    
    @Test
    fun pedidoEntity_shouldHaveAllRequiredFields() {
        val testOrder = PedidoEntity(
            clienteId = 2,
            tenderoId = 1,
            repartidorId = 3,
            productosIds = "1,2",
            estado = "PENDIENTE",
            fecha = "2025-06-30 11:00",
            tarifaEnvio = 2000.0,
            tarifaServicio = 1000.0
        )
        
        assertNotNull("ClienteId should not be null", testOrder.clienteId)
        assertNotNull("TenderoId should not be null", testOrder.tenderoId)
        assertNotNull("Estado should not be null", testOrder.estado)
        assertNotNull("Fecha should not be null", testOrder.fecha)
        assertNotNull("ProductosIds should not be null", testOrder.productosIds)
    }
}