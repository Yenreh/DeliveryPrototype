package com.example.deliveryprototype.utils

import com.example.deliveryprototype.model.ProductoEntity
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for OrderCalculationUtils
 */
class OrderCalculationUtilsTest {

    @Test
    fun testCalculateSubtotal() {
        val productos = listOf(
            Pair(ProductoEntity(1, "Pan", "Pan fresco", 1500.0, 100, 1), 2),
            Pair(ProductoEntity(2, "Leche", "Leche entera", 2500.0, 50, 1), 1)
        )
        
        val subtotal = OrderCalculationUtils.calculateSubtotal(productos)
        
        // Expected: 1500 * 2 + 2500 * 1 = 5500
        assertEquals(5500.0, subtotal, 0.01)
    }

    @Test
    fun testCalculateSubtotalWithMap() {
        val productos = listOf(
            ProductoEntity(1, "Pan", "Pan fresco", 1500.0, 100, 1),
            ProductoEntity(2, "Leche", "Leche entera", 2500.0, 50, 1)
        )
        
        val cantidades = mapOf(1 to 2, 2 to 1)
        
        val subtotal = OrderCalculationUtils.calculateSubtotal(productos, cantidades)
        
        // Expected: 1500 * 2 + 2500 * 1 = 5500
        assertEquals(5500.0, subtotal, 0.01)
    }

    @Test
    fun testCalculateGrandTotal() {
        val subtotal = 5500.0
        val deliveryFee = 2000.0
        val serviceFee = 1000.0
        
        val grandTotal = OrderCalculationUtils.calculateGrandTotal(subtotal, deliveryFee, serviceFee)
        
        // Expected: 5500 + 2000 + 1000 = 8500
        assertEquals(8500.0, grandTotal, 0.01)
    }

    @Test
    fun testFormatProductosIds() {
        val productos = listOf(
            Pair(ProductoEntity(1, "Pan", "Pan fresco", 1500.0, 100, 1), 2),
            Pair(ProductoEntity(2, "Leche", "Leche entera", 2500.0, 50, 1), 1)
        )
        
        val formatted = OrderCalculationUtils.formatProductosIds(productos)
        
        assertEquals("1:2,2:1", formatted)
    }

    @Test
    fun testParseProductosIds() {
        val productosIds = "1:2,2:1,3:3"
        
        val parsed = OrderCalculationUtils.parseProductosIds(productosIds)
        
        val expected = mapOf(1 to 2, 2 to 1, 3 to 3)
        assertEquals(expected, parsed)
    }

    @Test
    fun testParseProductosIdsLegacyFormat() {
        val productosIds = "1,2,2,3,3,3"
        
        val parsed = OrderCalculationUtils.parseProductosIds(productosIds)
        
        // In legacy format, each ID counts as quantity 1
        val expected = mapOf(1 to 1, 2 to 1, 3 to 1)
        assertEquals(expected, parsed)
    }

    @Test
    fun testParseProductosIdsEmpty() {
        val productosIds = ""
        
        val parsed = OrderCalculationUtils.parseProductosIds(productosIds)
        
        assertTrue(parsed.isEmpty())
    }

    @Test
    fun testHasValidProductsForOrder() {
        val validProducts = listOf(
            Pair(ProductoEntity(1, "Pan", "Pan fresco", 1500.0, 100, 1), 2),
            Pair(ProductoEntity(2, "Leche", "Leche entera", 2500.0, 50, 1), 1)
        )
        
        val invalidProducts = listOf(
            Pair(ProductoEntity(1, "Pan", "Pan fresco", 1500.0, 100, 1), 0)
        )
        
        val emptyProducts = emptyList<Pair<ProductoEntity, Int>>()
        
        assertTrue(OrderCalculationUtils.hasValidProductsForOrder(validProducts))
        assertFalse(OrderCalculationUtils.hasValidProductsForOrder(invalidProducts))
        assertFalse(OrderCalculationUtils.hasValidProductsForOrder(emptyProducts))
    }
}