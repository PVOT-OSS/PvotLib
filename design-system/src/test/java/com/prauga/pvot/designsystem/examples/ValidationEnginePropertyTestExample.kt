// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.examples

import com.prauga.pvot.designsystem.domain.validation.ValidationEngine
import com.prauga.pvot.designsystem.domain.validation.ValidationResult
import com.prauga.pvot.designsystem.testutil.PropertyTestConfig
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

/**
 * Example property-based tests for ValidationEngine.
 * 
 * This example demonstrates:
 * - How to test validation logic with property tests
 * - How to generate both valid and invalid inputs
 * - How to verify error messages contain required information
 * - How to test boundary conditions systematically
 * 
 * Validation is ideal for property testing because validation rules
 * should apply consistently across all possible inputs.
 */
class ValidationEnginePropertyTestExample : StringSpec({
    
    val engine = ValidationEngine()
    
    /**
     * Property 12: Configuration Validation
     * 
     * Feature: design-system-optimization
     * Property: For any invalid configuration (empty tabs, out-of-bounds indices,
     * empty values), the validation engine should reject it and provide an error
     * message containing the parameter name and expected value.
     * 
     */
    "Property 12: Valid tab configuration always passes validation".config(
        invocations = PropertyTestConfig.DEFAULT_ITERATIONS
    ) {
        checkAll(
            Arb.list(Arb.string(), 1..5),
            Arb.int(0..4)
        ) { tabs, selectedTab ->
            // Only test valid combinations
            if (selectedTab < tabs.size) {
                val result = engine.validateNavBarConfig(tabs, selectedTab)
                result shouldBe ValidationResult.Valid
            }
        }
    }
    
    /**
     * Property: Empty tab list is always rejected
     */
    "Empty tab list is always rejected with descriptive error".config(
        invocations = PropertyTestConfig.QUICK_ITERATIONS
    ) {
        val result = engine.validateNavBarConfig(emptyList<Any>(), 0)
        
        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        val errors = (result as ValidationResult.Invalid).errors
        
        // Should have error for tabs parameter
        val tabError = errors.find { it.parameter == "tabs" }
        tabError shouldBe org.mockito.kotlin.notNull()
        tabError!!.message shouldContain "empty"
        tabError.expectedValue shouldContain "At least 1"
    }
    
    /**
     * Property: Out-of-bounds selectedTab is always rejected
     */
    "Out-of-bounds selectedTab is always rejected".config(
        invocations = PropertyTestConfig.DEFAULT_ITERATIONS
    ) {
        checkAll(
            Arb.int(min = 1, max = 5),
            Arb.int(min = 10, max = 100)
        ) { tabCount, invalidIndex ->
            val tabs = (0 until tabCount).map { "Tab$it" }
            
            val result = engine.validateNavBarConfig(tabs, invalidIndex)
            
            result.shouldBeInstanceOf<ValidationResult.Invalid>()
            val errors = (result as ValidationResult.Invalid).errors
            
            // Should have error for selectedTab parameter
            val indexError = errors.find { it.parameter == "selectedTab" }
            indexError shouldBe org.mockito.kotlin.notNull()
            indexError!!.message shouldContain "out of bounds"
        }
    }
    
    /**
     * Property: Negative selectedTab is always rejected
     */
    "Negative selectedTab is always rejected".config(
        invocations = PropertyTestConfig.DEFAULT_ITERATIONS
    ) {
        checkAll(
            Arb.int(min = 1, max = 5),
            Arb.int(min = -100, max = -1)
        ) { tabCount, negativeIndex ->
            val tabs = (0 until tabCount).map { "Tab$it" }
            
            val result = engine.validateNavBarConfig(tabs, negativeIndex)
            
            result.shouldBeInstanceOf<ValidationResult.Invalid>()
        }
    }
    
    /**
     * Property: Error messages always contain parameter name
     * 
     * This ensures all validation errors are actionable.
     */
    "All validation errors contain parameter name".config(
        invocations = PropertyTestConfig.DEFAULT_ITERATIONS
    ) {
        checkAll(
            Arb.int(min = 1, max = 5),
            Arb.int(min = 10, max = 100)
        ) { tabCount, invalidIndex ->
            val tabs = (0 until tabCount).map { "Tab$it" }
            
            val result = engine.validateNavBarConfig(tabs, invalidIndex)
            
            if (result is ValidationResult.Invalid) {
                result.errors.forEach { error ->
                    error.parameter.isNotBlank() shouldBe true
                    error.message.isNotBlank() shouldBe true
                }
            }
        }
    }
})
