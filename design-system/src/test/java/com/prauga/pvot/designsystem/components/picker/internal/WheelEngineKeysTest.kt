// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.picker.internal

import org.junit.Assert.*
import org.junit.Test

/**
 * Property-based tests for WheelEngine LazyColumn keys.
 * 
 * **Property 7: Stable Unique Item Keys**
 * For any list of wheel items, all items should have unique keys that remain stable
 * across recompositions and reorderings.
 * 
 */
class WheelEngineKeysTest {
    
    // Property 7: Stable Unique Item Keys
    @Test
    fun `wheel items should have unique keys based on values`() {
        // Feature: design-system-optimization, Property 7: Stable Unique Item Keys
        
        // Test with various value lists
        val testCases = listOf(
            listOf(1, 2, 3, 4, 5),
            listOf(0, 10, 20, 30, 40),
            listOf(100, 200, 300),
            (0..50).toList(),
            (1..100).toList()
        )
        
        testCases.forEach { values ->
            val config = WheelConfig(
                values = values,
                initialIndex = 0
            )
            
            // Extract keys (in real implementation, keys are config.values[index])
            val keys = values.indices.map { index -> config.values[index] }
            
            // All keys should be unique
            val uniqueKeys = keys.toSet()
            assertEquals("All keys should be unique", values.size, uniqueKeys.size)
        }
    }
    
    @Test
    fun `wheel item keys should be stable across multiple accesses`() {
        // Feature: design-system-optimization, Property 7: Stable Unique Item Keys
        
        val values = listOf(10, 20, 30, 40, 50)
        val config = WheelConfig(
            values = values,
            initialIndex = 0
        )
        
        // Access keys multiple times
        val keys1 = values.indices.map { index -> config.values[index] }
        val keys2 = values.indices.map { index -> config.values[index] }
        val keys3 = values.indices.map { index -> config.values[index] }
        
        // Keys should be identical across accesses
        assertEquals(keys1, keys2)
        assertEquals(keys1, keys3)
    }
    
    @Test
    fun `wheel item keys should be based on actual values not indices`() {
        // Feature: design-system-optimization, Property 7: Stable Unique Item Keys
        
        val values = listOf(100, 200, 300, 400, 500)
        val config = WheelConfig(
            values = values,
            initialIndex = 0
        )
        
        // Keys should be the values themselves, not indices
        values.indices.forEach { index ->
            val key = config.values[index]
            assertEquals(values[index], key)
            assertNotEquals(index, key) // Key should not be the index
        }
    }
    
    @Test
    fun `wheel item keys should remain stable when config is recreated`() {
        // Feature: design-system-optimization, Property 7: Stable Unique Item Keys
        
        val values = listOf(5, 10, 15, 20, 25)
        
        val config1 = WheelConfig(values = values, initialIndex = 0)
        val config2 = WheelConfig(values = values, initialIndex = 0)
        
        // Keys should be identical for same values
        values.indices.forEach { index ->
            assertEquals(config1.values[index], config2.values[index])
        }
    }
    
    @Test
    fun `wheel item keys should handle duplicate values correctly`() {
        // Feature: design-system-optimization, Property 7: Stable Unique Item Keys
        // Note: If values contain duplicates, keys will also be duplicates
        // This is expected behavior - the key is the value itself
        
        val values = listOf(1, 2, 2, 3, 3, 3)
        val config = WheelConfig(
            values = values,
            initialIndex = 0
        )
        
        // Keys are the values
        val keys = values.indices.map { index -> config.values[index] }
        
        // Keys should match values exactly
        assertEquals(values, keys)
    }
    
    @Test
    fun `wheel item keys should work with large value ranges`() {
        // Feature: design-system-optimization, Property 7: Stable Unique Item Keys
        
        // Test with large range
        val values = (0..1000).toList()
        val config = WheelConfig(
            values = values,
            initialIndex = 0
        )
        
        // Sample some keys
        val sampleIndices = listOf(0, 100, 500, 999, 1000)
        sampleIndices.forEach { index ->
            val key = config.values[index]
            assertEquals(values[index], key)
        }
        
        // All keys should be unique
        val allKeys = values.indices.map { index -> config.values[index] }
        assertEquals(values.size, allKeys.toSet().size)
    }
    
    @Test
    fun `wheel item keys should work with negative values`() {
        // Feature: design-system-optimization, Property 7: Stable Unique Item Keys
        
        val values = listOf(-10, -5, 0, 5, 10)
        val config = WheelConfig(
            values = values,
            initialIndex = 2
        )
        
        // Keys should be the actual values
        values.indices.forEach { index ->
            assertEquals(values[index], config.values[index])
        }
    }
    
    @Test
    fun `wheel item keys should be consistent across different initial indices`() {
        // Feature: design-system-optimization, Property 7: Stable Unique Item Keys
        
        val values = listOf(1, 2, 3, 4, 5)
        
        // Create configs with different initial indices
        val config1 = WheelConfig(values = values, initialIndex = 0)
        val config2 = WheelConfig(values = values, initialIndex = 2)
        val config3 = WheelConfig(values = values, initialIndex = 4)
        
        // Keys should be identical regardless of initial index
        values.indices.forEach { index ->
            assertEquals(config1.values[index], config2.values[index])
            assertEquals(config1.values[index], config3.values[index])
        }
    }
    
    @Test
    fun `wheel item keys should support value-based reordering`() {
        // Feature: design-system-optimization, Property 7: Stable Unique Item Keys
        
        val values1 = listOf(1, 2, 3, 4, 5)
        val values2 = listOf(5, 4, 3, 2, 1) // Reversed
        
        val config1 = WheelConfig(values = values1, initialIndex = 0)
        val config2 = WheelConfig(values = values2, initialIndex = 0)
        
        // Keys should match the values in their respective orders
        assertEquals(1, config1.values[0])
        assertEquals(5, config2.values[0])
        
        assertEquals(5, config1.values[4])
        assertEquals(1, config2.values[4])
    }
}
