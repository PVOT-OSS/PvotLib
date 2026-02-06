// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.validation

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ValidationEngine edge cases.
 * Tests specific edge cases, error message formats, and boundary conditions.
 */
class ValidationEngineEdgeCasesTest {
    
    private lateinit var engine: ValidationEngine
    
    @Before
    fun setup() {
        engine = ValidationEngine()
    }
    
    // Empty list edge cases
    
    @Test
    fun `empty tab list should produce clear error message`() {
        val result = engine.validateNavBarConfig(emptyList<String>(), 0)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        val error = errors.first { it.parameter == "tabs" }
        
        // Verify error message format
        assertTrue(error.message.contains("cannot be empty"))
        assertNotNull(error.expectedValue)
        assertTrue(error.expectedValue!!.contains("At least 1"))
    }
    
    @Test
    fun `empty values list should produce clear error message`() {
        val result = engine.validateWheelConfig(emptyList<Int>(), 0)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        val error = errors.first { it.parameter == "values" }
        
        // Verify error message format
        assertTrue(error.message.contains("cannot be empty"))
        assertNotNull(error.expectedValue)
        assertTrue(error.expectedValue!!.contains("At least 1"))
    }
    
    // Out-of-bounds edge cases
    
    @Test
    fun `selectedTab at exact list size should be invalid`() {
        val tabs = listOf("A", "B", "C")
        val result = engine.validateNavBarConfig(tabs, 3) // size is 3, max index is 2
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        val error = errors.first { it.parameter == "selectedTab" }
        
        assertTrue(error.message.contains("out of bounds"))
        assertEquals("0 to 2", error.expectedValue)
    }
    
    @Test
    fun `initialIndex at exact list size should be invalid`() {
        val values = listOf(1, 2, 3, 4)
        val result = engine.validateWheelConfig(values, 4) // size is 4, max index is 3
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        val error = errors.first { it.parameter == "initialIndex" }
        
        assertTrue(error.message.contains("out of bounds"))
        assertEquals("0 to 3", error.expectedValue)
    }
    
    @Test
    fun `very large negative index should be caught`() {
        val tabs = listOf("A", "B")
        val result = engine.validateNavBarConfig(tabs, -1000)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        assertTrue(errors.any { it.parameter == "selectedTab" })
    }
    
    @Test
    fun `very large positive index should be caught`() {
        val values = listOf(1, 2, 3)
        val result = engine.validateWheelConfig(values, 1000)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        assertTrue(errors.any { it.parameter == "initialIndex" })
    }
    
    // Error message format tests
    
    @Test
    fun `error message should include parameter name`() {
        val result = engine.validateNavBarConfig(emptyList<String>(), -1)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        
        // All errors should have parameter names
        errors.forEach { error ->
            assertNotNull(error.parameter)
            assertTrue(error.parameter.isNotEmpty())
        }
    }
    
    @Test
    fun `error message should include expected value for out of bounds`() {
        val tabs = listOf("A", "B", "C")
        val result = engine.validateNavBarConfig(tabs, 5)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        val error = errors.first { it.parameter == "selectedTab" }
        
        assertNotNull(error.expectedValue)
        assertTrue(error.expectedValue!!.matches(Regex("\\d+ to \\d+")))
    }
    
    @Test
    fun `error message format should be consistent across validators`() {
        val navBarResult = engine.validateNavBarConfig(emptyList<String>(), 0)
        val wheelResult = engine.validateWheelConfig(emptyList<Int>(), 0)
        
        val navBarError = (navBarResult as ValidationResult.Invalid).errors[0]
        val wheelError = (wheelResult as ValidationResult.Invalid).errors[0]
        
        // Both should follow same format pattern
        assertTrue(navBarError.message.contains("cannot be empty"))
        assertTrue(wheelError.message.contains("cannot be empty"))
        assertTrue(navBarError.expectedValue!!.contains("At least 1"))
        assertTrue(wheelError.expectedValue!!.contains("At least 1"))
    }
    
    @Test
    fun `toDisplayString should produce user-friendly error messages`() {
        val result = engine.validateNavBarConfig(listOf("A"), 5)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        val displayString = errors[0].toDisplayString()
        
        // Should contain all key information
        assertTrue(displayString.contains("selectedTab"))
        assertTrue(displayString.contains("out of bounds"))
        assertTrue(displayString.contains("expected:"))
    }
    
    // Multiple error scenarios
    
    @Test
    fun `multiple errors should all be reported`() {
        val result = engine.validateNavBarConfig(emptyList<String>(), -1)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        
        // Should have both errors
        assertEquals(2, errors.size)
        val parameterNames = errors.map { it.parameter }.toSet()
        assertTrue(parameterNames.contains("tabs"))
        assertTrue(parameterNames.contains("selectedTab"))
    }
    
    @Test
    fun `multiple errors should each have clear messages`() {
        val result = engine.validateWheelConfig(emptyList<Int>(), 10)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        
        assertEquals(2, errors.size)
        
        // Each error should be complete
        errors.forEach { error ->
            assertNotNull(error.parameter)
            assertNotNull(error.message)
            assertTrue(error.message.isNotEmpty())
        }
    }
    
    // Boundary value tests
    
    @Test
    fun `index at zero should be valid`() {
        val tabs = listOf("A", "B", "C")
        val result = engine.validateNavBarConfig(tabs, 0)
        
        assertTrue(result is ValidationResult.Valid)
    }
    
    @Test
    fun `index at max valid value should be valid`() {
        val values = listOf(1, 2, 3, 4, 5)
        val result = engine.validateWheelConfig(values, 4) // last valid index
        
        assertTrue(result is ValidationResult.Valid)
    }
    
    @Test
    fun `index one past max should be invalid`() {
        val tabs = listOf("A", "B")
        val result = engine.validateNavBarConfig(tabs, 2) // size is 2, max index is 1
        
        assertTrue(result is ValidationResult.Invalid)
    }
    
    @Test
    fun `index one before min should be invalid`() {
        val values = listOf(1, 2, 3)
        val result = engine.validateWheelConfig(values, -1)
        
        assertTrue(result is ValidationResult.Invalid)
    }
    
    // Single item list edge cases
    
    @Test
    fun `single item list with valid index should pass`() {
        val tabs = listOf("OnlyTab")
        val result = engine.validateNavBarConfig(tabs, 0)
        
        assertTrue(result is ValidationResult.Valid)
    }
    
    @Test
    fun `single item list with invalid index should fail`() {
        val values = listOf(42)
        val result = engine.validateWheelConfig(values, 1)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        val error = errors.first { it.parameter == "initialIndex" }
        assertEquals("0 to 0", error.expectedValue)
    }
    
    // Large list edge cases
    
    @Test
    fun `very large list should validate correctly`() {
        val largeTabs = (1..1000).map { "Tab$it" }
        
        // Valid index
        assertTrue(engine.validateNavBarConfig(largeTabs, 500) is ValidationResult.Valid)
        
        // Invalid index
        assertTrue(engine.validateNavBarConfig(largeTabs, 1000) is ValidationResult.Invalid)
    }
    
    @Test
    fun `error message for large list should show correct range`() {
        val largeValues = (1..100).toList()
        val result = engine.validateWheelConfig(largeValues, 150)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        val error = errors.first { it.parameter == "initialIndex" }
        
        assertEquals("0 to 99", error.expectedValue)
    }
    
    // Type safety tests
    
    @Test
    fun `validation should work with any list type`() {
        // Test with different types
        val stringList = listOf("A", "B")
        val intList = listOf(1, 2, 3)
        val anyList = listOf(Any(), Any())
        
        assertTrue(engine.validateNavBarConfig(stringList, 0) is ValidationResult.Valid)
        assertTrue(engine.validateWheelConfig(intList, 1) is ValidationResult.Valid)
        assertTrue(engine.validateNavBarConfig(anyList, 1) is ValidationResult.Valid)
    }
    
    // Consistency tests
    
    @Test
    fun `validation should be consistent across multiple calls`() {
        val tabs = listOf("A", "B", "C")
        val selectedTab = 5
        
        val result1 = engine.validateNavBarConfig(tabs, selectedTab)
        val result2 = engine.validateNavBarConfig(tabs, selectedTab)
        val result3 = engine.validateNavBarConfig(tabs, selectedTab)
        
        // All should be invalid
        assertTrue(result1 is ValidationResult.Invalid)
        assertTrue(result2 is ValidationResult.Invalid)
        assertTrue(result3 is ValidationResult.Invalid)
        
        // Error counts should match
        assertEquals(
            (result1 as ValidationResult.Invalid).errors.size,
            (result2 as ValidationResult.Invalid).errors.size
        )
        assertEquals(
            result1.errors.size,
            (result3 as ValidationResult.Invalid).errors.size
        )
    }
    
    @Test
    fun `validation should be independent across different instances`() {
        val engine1 = ValidationEngine()
        val engine2 = ValidationEngine()
        
        val tabs = emptyList<String>()
        val result1 = engine1.validateNavBarConfig(tabs, 0)
        val result2 = engine2.validateNavBarConfig(tabs, 0)
        
        // Both should produce same validation result
        assertTrue(result1 is ValidationResult.Invalid)
        assertTrue(result2 is ValidationResult.Invalid)
        assertEquals(
            (result1 as ValidationResult.Invalid).errors.size,
            (result2 as ValidationResult.Invalid).errors.size
        )
    }
}
