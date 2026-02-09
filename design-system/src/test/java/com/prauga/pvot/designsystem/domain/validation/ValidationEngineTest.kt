// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.validation

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Property-based and unit tests for ValidationEngine.
 * 
 * **Property 12: Configuration Validation**
 * For any invalid configuration (empty tabs, out-of-bounds indices, empty values),
 * the validation engine should reject it and provide an error message containing
 * the parameter name and expected value.
 */
class ValidationEngineTest {
    
    private lateinit var engine: ValidationEngine
    
    @Before
    fun setup() {
        engine = ValidationEngine()
    }
    
    // Property 12: Configuration Validation - NavBar Tests
    
    @Test
    fun `validateNavBarConfig should accept valid configuration`() {
        val tabs = listOf("Home", "Search", "Profile")
        val selectedTab = 1
        
        val result = engine.validateNavBarConfig(tabs, selectedTab)
        
        assertTrue(result is ValidationResult.Valid)
    }
    
    @Test
    fun `validateNavBarConfig should reject empty tab list`() {
        val tabs = emptyList<String>()
        val selectedTab = 0
        
        val result = engine.validateNavBarConfig(tabs, selectedTab)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        assertEquals(1, errors.size)
        assertEquals("tabs", errors[0].parameter)
        assertTrue(errors[0].message.contains("cannot be empty"))
        assertEquals("At least 1 tab", errors[0].expectedValue)
    }
    
    @Test
    fun `validateNavBarConfig should reject negative selected tab index`() {
        val tabs = listOf("Home", "Search", "Profile")
        val selectedTab = -1
        
        val result = engine.validateNavBarConfig(tabs, selectedTab)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        assertTrue(errors.any { it.parameter == "selectedTab" })
        val error = errors.first { it.parameter == "selectedTab" }
        assertTrue(error.message.contains("out of bounds"))
        assertEquals("0 to 2", error.expectedValue)
    }
    
    @Test
    fun `validateNavBarConfig should reject selected tab index beyond list size`() {
        val tabs = listOf("Home", "Search")
        val selectedTab = 5
        
        val result = engine.validateNavBarConfig(tabs, selectedTab)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        assertTrue(errors.any { it.parameter == "selectedTab" })
        val error = errors.first { it.parameter == "selectedTab" }
        assertTrue(error.message.contains("out of bounds"))
        assertEquals("0 to 1", error.expectedValue)
    }
    
    @Test
    fun `validateNavBarConfig should accept first tab as selected`() {
        val tabs = listOf("Home", "Search", "Profile")
        val selectedTab = 0
        
        val result = engine.validateNavBarConfig(tabs, selectedTab)
        
        assertTrue(result is ValidationResult.Valid)
    }
    
    @Test
    fun `validateNavBarConfig should accept last tab as selected`() {
        val tabs = listOf("Home", "Search", "Profile")
        val selectedTab = 2
        
        val result = engine.validateNavBarConfig(tabs, selectedTab)
        
        assertTrue(result is ValidationResult.Valid)
    }
    
    @Test
    fun `validateNavBarConfig should report multiple errors`() {
        val tabs = emptyList<String>()
        val selectedTab = -1
        
        val result = engine.validateNavBarConfig(tabs, selectedTab)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        assertEquals(2, errors.size)
        assertTrue(errors.any { it.parameter == "tabs" })
        assertTrue(errors.any { it.parameter == "selectedTab" })
    }
    
    @Test
    fun `validateNavBarConfig should accept single tab`() {
        val tabs = listOf("Home")
        val selectedTab = 0
        
        val result = engine.validateNavBarConfig(tabs, selectedTab)
        
        assertTrue(result is ValidationResult.Valid)
    }
    
    @Test
    fun `validateNavBarConfig should accept many tabs`() {
        val tabs = (1..20).map { "Tab$it" }
        val selectedTab = 10
        
        val result = engine.validateNavBarConfig(tabs, selectedTab)
        
        assertTrue(result is ValidationResult.Valid)
    }
    
    // Property 12: Configuration Validation - Wheel Tests
    
    @Test
    fun `validateWheelConfig should accept valid configuration`() {
        val values = listOf(1, 2, 3, 4, 5)
        val initialIndex = 2
        
        val result = engine.validateWheelConfig(values, initialIndex)
        
        assertTrue(result is ValidationResult.Valid)
    }
    
    @Test
    fun `validateWheelConfig should reject empty values list`() {
        val values = emptyList<Int>()
        val initialIndex = 0
        
        val result = engine.validateWheelConfig(values, initialIndex)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        assertEquals(1, errors.size)
        assertEquals("values", errors[0].parameter)
        assertTrue(errors[0].message.contains("cannot be empty"))
        assertEquals("At least 1 value", errors[0].expectedValue)
    }
    
    @Test
    fun `validateWheelConfig should reject negative initial index`() {
        val values = listOf(1, 2, 3, 4, 5)
        val initialIndex = -1
        
        val result = engine.validateWheelConfig(values, initialIndex)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        assertTrue(errors.any { it.parameter == "initialIndex" })
        val error = errors.first { it.parameter == "initialIndex" }
        assertTrue(error.message.contains("out of bounds"))
        assertEquals("0 to 4", error.expectedValue)
    }
    
    @Test
    fun `validateWheelConfig should reject initial index beyond list size`() {
        val values = listOf(1, 2, 3)
        val initialIndex = 10
        
        val result = engine.validateWheelConfig(values, initialIndex)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        assertTrue(errors.any { it.parameter == "initialIndex" })
        val error = errors.first { it.parameter == "initialIndex" }
        assertTrue(error.message.contains("out of bounds"))
        assertEquals("0 to 2", error.expectedValue)
    }
    
    @Test
    fun `validateWheelConfig should accept first value as initial`() {
        val values = listOf(1, 2, 3, 4, 5)
        val initialIndex = 0
        
        val result = engine.validateWheelConfig(values, initialIndex)
        
        assertTrue(result is ValidationResult.Valid)
    }
    
    @Test
    fun `validateWheelConfig should accept last value as initial`() {
        val values = listOf(1, 2, 3, 4, 5)
        val initialIndex = 4
        
        val result = engine.validateWheelConfig(values, initialIndex)
        
        assertTrue(result is ValidationResult.Valid)
    }
    
    @Test
    fun `validateWheelConfig should report multiple errors`() {
        val values = emptyList<Int>()
        val initialIndex = -1
        
        val result = engine.validateWheelConfig(values, initialIndex)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        assertEquals(2, errors.size)
        assertTrue(errors.any { it.parameter == "values" })
        assertTrue(errors.any { it.parameter == "initialIndex" })
    }
    
    @Test
    fun `validateWheelConfig should accept single value`() {
        val values = listOf(42)
        val initialIndex = 0
        
        val result = engine.validateWheelConfig(values, initialIndex)
        
        assertTrue(result is ValidationResult.Valid)
    }
    
    @Test
    fun `validateWheelConfig should accept many values`() {
        val values = (0..100).toList()
        val initialIndex = 50
        
        val result = engine.validateWheelConfig(values, initialIndex)
        
        assertTrue(result is ValidationResult.Valid)
    }
    
    // ValidationError tests
    
    @Test
    fun `ValidationError toDisplayString should format correctly with expected value`() {
        val error = ValidationError(
            parameter = "tabs",
            message = "Tab list cannot be empty",
            expectedValue = "At least 1 tab"
        )
        
        val displayString = error.toDisplayString()
        
        assertEquals("tabs: Tab list cannot be empty (expected: At least 1 tab)", displayString)
    }
    
    @Test
    fun `ValidationError toDisplayString should format correctly without expected value`() {
        val error = ValidationError(
            parameter = "config",
            message = "Invalid configuration",
            expectedValue = null
        )
        
        val displayString = error.toDisplayString()
        
        assertEquals("config: Invalid configuration", displayString)
    }
    
    // Edge cases and boundary tests
    
    @Test
    fun `validateNavBarConfig should handle boundary at list size`() {
        val tabs = listOf("A", "B", "C")
        
        // Valid: last index
        assertTrue(engine.validateNavBarConfig(tabs, 2) is ValidationResult.Valid)
        
        // Invalid: one past last
        assertTrue(engine.validateNavBarConfig(tabs, 3) is ValidationResult.Invalid)
    }
    
    @Test
    fun `validateWheelConfig should handle boundary at list size`() {
        val values = listOf(1, 2, 3)
        
        // Valid: last index
        assertTrue(engine.validateWheelConfig(values, 2) is ValidationResult.Valid)
        
        // Invalid: one past last
        assertTrue(engine.validateWheelConfig(values, 3) is ValidationResult.Invalid)
    }
    
    @Test
    fun `validation should work with different list types`() {
        // String list
        val stringTabs = listOf("Home", "Search")
        assertTrue(engine.validateNavBarConfig(stringTabs, 0) is ValidationResult.Valid)
        
        // Int list
        val intValues = listOf(1, 2, 3)
        assertTrue(engine.validateWheelConfig(intValues, 0) is ValidationResult.Valid)
        
        // Mixed object list
        val objectTabs = listOf(Any(), Any(), Any())
        assertTrue(engine.validateNavBarConfig(objectTabs, 1) is ValidationResult.Valid)
    }
}
