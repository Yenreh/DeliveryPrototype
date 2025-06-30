package com.example.deliveryprototype.utils

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for FeeUtils
 */
class FeeUtilsTest {
    
    @Test
    fun calculateDeliveryFee_returnsCorrectAmount() {
        val deliveryFee = FeeUtils.calculateDeliveryFee()
        assertEquals(2000.0, deliveryFee, 0.0)
    }
    
    @Test
    fun calculateServiceFee_returnsCorrectAmount() {
        val serviceFee = FeeUtils.calculateServiceFee()
        assertEquals(1000.0, serviceFee, 0.0)
    }
    
    @Test
    fun formatMoney_formatsCorrectly() {
        val formatted = FeeUtils.formatMoney(2000.0)
        assertEquals("$ 2000.00", formatted)
    }
    
    @Test
    fun formatMoney_formatsDecimalsCorrectly() {
        val formatted = FeeUtils.formatMoney(1234.56)
        assertEquals("$ 1234.56", formatted)
    }
}