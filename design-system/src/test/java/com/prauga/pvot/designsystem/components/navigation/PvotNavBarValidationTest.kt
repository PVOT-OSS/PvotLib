// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.navigation

import com.prauga.pvot.designsystem.domain.validation.ValidationEngine
import com.prauga.pvot.designsystem.domain.validation.ValidationResult
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for PvotNavBar validation.
 * Tests that invalid configurations are properly validated.
 * 
 */
class PvotNavBarValidationTest {

    private lateinit var validationEngine: ValidationEngine

    @Before
    fun setup() {
        validationEngine = ValidationEngine()
    }

    @Test
    fun `invalid config with empty tabs should be rejected`() {
        val tabs = emptyList<String>()
        val selectedTab = 0

        val result = validationEngine.validateNavBarConfig(tabs, selectedTab)

        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        assertTrue(errors.any { it.parameter == "tabs" })
        assertTrue(errors.any { it.message.contains("cannot be empty") })
    }

    @Test
    fun `invalid config with out of bounds selectedTab should be rejected`() {
        val tabs = listOf("Home", "Search")
        val selectedTab = 5

        val result = validationEngine.validateNavBarConfig(tabs, selectedTab)

        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        assertTrue(errors.any { it.parameter == "selectedTab" })
        assertTrue(errors.any { it.message.contains("out of bounds") })
    }

    @Test
    fun `error message should include parameter name and expected value`() {
        val tabs = emptyList<String>()
        val selectedTab = 0

        val result = validationEngine.validateNavBarConfig(tabs, selectedTab)

        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        val tabsError = errors.first { it.parameter == "tabs" }
        
        assertNotNull(tabsError.expectedValue)
        assertTrue(tabsError.expectedValue!!.contains("At least"))
    }

    @Test
    fun `valid config should pass validation`() {
        val tabs = listOf("Home", "Search", "Profile")
        val selectedTab = 1

        val result = validationEngine.validateNavBarConfig(tabs, selectedTab)

        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `negative selectedTab should be rejected`() {
        val tabs = listOf("Home")
        val selectedTab = -1

        val result = validationEngine.validateNavBarConfig(tabs, selectedTab)

        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        assertTrue(errors.any { it.parameter == "selectedTab" })
    }
}
