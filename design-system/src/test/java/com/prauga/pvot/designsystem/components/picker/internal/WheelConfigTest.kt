// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.picker.internal

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for WheelConfig validation.
 * 
 */
class WheelConfigTest {
    
    @Test(expected = IllegalArgumentException::class)
    fun `empty values list should throw exception`() {
        // Feature: design-system-optimization
        
        WheelConfig(
            values = emptyList(),
            initialIndex = 0
        )
    }
    
    @Test
    fun `empty values list should throw exception with descriptive message`() {
        // Feature: design-system-optimization
        
        try {
            WheelConfig(
                values = emptyList(),
                initialIndex = 0
            )
            fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("Values list cannot be empty") == true)
        }
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `negative initialIndex should throw exception`() {
        // Feature: design-system-optimization
        
        WheelConfig(
            values = listOf(1, 2, 3),
            initialIndex = -1
        )
    }
    
    @Test
    fun `negative initialIndex should throw exception with descriptive message`() {
        // Feature: design-system-optimization
        
        try {
            WheelConfig(
                values = listOf(1, 2, 3),
                initialIndex = -1
            )
            fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("Initial index") == true)
            assertTrue(e.message?.contains("out of bounds") == true)
            assertTrue(e.message?.contains("0..2") == true)
        }
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `initialIndex beyond list size should throw exception`() {
        // Feature: design-system-optimization
        
        WheelConfig(
            values = listOf(1, 2, 3),
            initialIndex = 5
        )
    }
    
    @Test
    fun `initialIndex beyond list size should throw exception with descriptive message`() {
        // Feature: design-system-optimization
        
        try {
            WheelConfig(
                values = listOf(1, 2, 3),
                initialIndex = 5
            )
            fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("Initial index 5") == true)
            assertTrue(e.message?.contains("out of bounds") == true)
            assertTrue(e.message?.contains("0..2") == true)
        }
    }
    
    @Test
    fun `valid configuration with single value should succeed`() {
        // Feature: design-system-optimization
        
        val config = WheelConfig(
            values = listOf(42),
            initialIndex = 0
        )
        
        assertEquals(1, config.values.size)
        assertEquals(42, config.values[0])
        assertEquals(0, config.initialIndex)
    }
    
    @Test
    fun `valid configuration with multiple values should succeed`() {
        // Feature: design-system-optimization
        
        val config = WheelConfig(
            values = listOf(1, 2, 3, 4, 5),
            initialIndex = 2
        )
        
        assertEquals(5, config.values.size)
        assertEquals(2, config.initialIndex)
    }
    
    @Test
    fun `valid configuration with first index should succeed`() {
        // Feature: design-system-optimization
        
        val config = WheelConfig(
            values = listOf(10, 20, 30),
            initialIndex = 0
        )
        
        assertEquals(0, config.initialIndex)
    }
    
    @Test
    fun `valid configuration with last index should succeed`() {
        // Feature: design-system-optimization
        
        val config = WheelConfig(
            values = listOf(10, 20, 30),
            initialIndex = 2
        )
        
        assertEquals(2, config.initialIndex)
    }
    
    @Test
    fun `valid configuration should have default appearance and behavior`() {
        // Feature: design-system-optimization
        
        val config = WheelConfig(
            values = listOf(1, 2, 3),
            initialIndex = 0
        )
        
        assertNotNull(config.appearance)
        assertNotNull(config.behavior)
    }
    
    @Test
    fun `valid configuration should have default label function`() {
        // Feature: design-system-optimization
        
        val config = WheelConfig(
            values = listOf(1, 2, 3),
            initialIndex = 0
        )
        
        assertEquals("1", config.label(1))
        assertEquals("2", config.label(2))
        assertEquals("3", config.label(3))
    }
    
    @Test
    fun `valid configuration should have empty suffix by default`() {
        // Feature: design-system-optimization
        
        val config = WheelConfig(
            values = listOf(1, 2, 3),
            initialIndex = 0
        )
        
        assertEquals("", config.suffix)
    }
    
    @Test
    fun `custom label function should work`() {
        // Feature: design-system-optimization
        
        val config = WheelConfig(
            values = listOf(1, 2, 3),
            label = { value -> "Item $value" },
            initialIndex = 0
        )
        
        assertEquals("Item 1", config.label(1))
        assertEquals("Item 2", config.label(2))
    }
    
    @Test
    fun `custom suffix should work`() {
        // Feature: design-system-optimization
        
        val config = WheelConfig(
            values = listOf(1, 2, 3),
            suffix = "hours",
            initialIndex = 0
        )
        
        assertEquals("hours", config.suffix)
    }
    
    @Test
    fun `error message should include parameter name and expected value`() {
        // Feature: design-system-optimization
        
        try {
            WheelConfig(
                values = listOf(1, 2, 3, 4, 5),
                initialIndex = 10
            )
            fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // Error message should contain parameter name
            assertTrue(e.message?.contains("Initial index") == true)
            // Error message should contain actual value
            assertTrue(e.message?.contains("10") == true)
            // Error message should contain expected range
            assertTrue(e.message?.contains("0..4") == true)
        }
    }
}
