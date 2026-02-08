// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.examples

import com.prauga.pvot.designsystem.domain.validation.ValidationEngine
import com.prauga.pvot.designsystem.domain.validation.ValidationResult
import com.prauga.pvot.designsystem.testutil.TestDataGenerators
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Example unit tests for error conditions.
 * 
 * This example demonstrates:
 * - How to test error handling and validation
 * - How to verify error messages are descriptive
 * - How to test graceful degradation
 * 
 * Error condition tests ensure that the system handles invalid
 * inputs gracefully and provides helpful feedback to developers.
 */
class ErrorConditionTestExample {
    
    private val validationEngine = ValidationEngine()
    
    /**
     * Error Condition: Empty tab list
     * 
     * Providing an empty tab list should result in a validation error
     * with a descriptive message.
     */
    @Test
    fun `empty tab list produces validation error`() {
        val result = validationEngine.validateNavBarConfig(emptyList<Any>(), 0)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        
        // Should have exactly one error
        assertEquals(1, errors.size)
        
        // Error should be for tabs parameter
        assertEquals("tabs", errors[0].parameter)
        
        // Error message should mention "empty"
        assertTrue(errors[0].message.contains("empty", ignoreCase = true))
        
        // Should provide expected value
        assertTrue(errors[0].expectedValue?.contains("1") == true)
    }
    
    /**
     * Error Condition: Out of bounds selected tab
     * 
     * Selecting a tab index outside the valid range should produce
     * a validation error.
     */
    @Test
    fun `out of bounds selected tab produces validation error`() {
        val tabs = listOf("Tab1", "Tab2", "Tab3")
        
        val result = validationEngine.validateNavBarConfig(tabs, 5)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        
        // Should have error for selectedTab
        val tabError = errors.find { it.parameter == "selectedTab" }
        assertTrue(tabError != null)
        
        // Error message should mention "bounds"
        assertTrue(tabError!!.message.contains("bounds", ignoreCase = true))
        
        // Should provide expected range
        assertTrue(tabError.expectedValue?.contains("0") == true)
        assertTrue(tabError.expectedValue?.contains("2") == true)
    }
    
    /**
     * Error Condition: Negative selected tab
     * 
     * Negative indices should be rejected.
     */
    @Test
    fun `negative selected tab produces validation error`() {
        val tabs = listOf("Tab1", "Tab2", "Tab3")
        
        val result = validationEngine.validateNavBarConfig(tabs, -1)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        
        // Should have error for selectedTab
        assertTrue(errors.any { it.parameter == "selectedTab" })
    }
    
    /**
     * Error Condition: Multiple validation errors
     * 
     * When multiple validation rules are violated, all errors
     * should be reported.
     */
    @Test
    fun `multiple validation errors are all reported`() {
        val result = validationEngine.validateNavBarConfig(emptyList<Any>(), -1)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        
        // Should have multiple errors
        assertTrue(errors.size >= 2)
        
        // Should have error for tabs
        assertTrue(errors.any { it.parameter == "tabs" })
        
        // Should have error for selectedTab
        assertTrue(errors.any { it.parameter == "selectedTab" })
    }
    
    /**
     * Error Condition: Error message format
     * 
     * All error messages should follow the expected format.
     */
    @Test
    fun `error messages follow expected format`() {
        val result = validationEngine.validateNavBarConfig(emptyList<Any>(), 0)
        
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        
        errors.forEach { error ->
            // Parameter name should not be empty
            assertTrue(error.parameter.isNotBlank())
            
            // Message should not be empty
            assertTrue(error.message.isNotBlank())
            
            // Display string should contain parameter and message
            val displayString = error.toDisplayString()
            assertTrue(displayString.contains(error.parameter))
            assertTrue(displayString.contains(error.message))
        }
    }
}
