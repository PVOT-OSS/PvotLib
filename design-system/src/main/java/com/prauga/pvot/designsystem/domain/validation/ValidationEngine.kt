// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.validation

import android.util.Log

/**
 * Implementation of validation engine for component configuration.
 * Validates parameters and provides clear error messages.
 */
class ValidationEngine : IValidationEngine {
    
    /**
     * Validates navigation bar configuration.
     * Checks:
     * - Tab list is not empty
     * - Selected tab index is within bounds
     */
    override fun validateNavBarConfig(tabs: List<*>, selectedTab: Int): ValidationResult {
        val errors = mutableListOf<ValidationError>()
        
        if (tabs.isEmpty()) {
            errors.add(ValidationError(
                parameter = "tabs",
                message = "Tab list cannot be empty",
                expectedValue = "At least 1 tab"
            ))
        }
        
        if (selectedTab < 0 || selectedTab >= tabs.size) {
            errors.add(ValidationError(
                parameter = "selectedTab",
                message = "Selected tab index out of bounds",
                expectedValue = "0 to ${tabs.size - 1}"
            ))
        }
        
        // Log validation errors
        if (errors.isNotEmpty()) {
            val errorMessage = errors.joinToString(", ") { "${it.parameter}: ${it.message}" }
            Log.e("DesignSystem", "NavBarConfig validation failed: $errorMessage")
        }
        
        return if (errors.isEmpty()) ValidationResult.Valid
        else ValidationResult.Invalid(errors)
    }
    
    /**
     * Validates wheel picker configuration.
     * Checks:
     * - Values list is not empty
     * - Initial index is within bounds
     */
    override fun validateWheelConfig(values: List<*>, initialIndex: Int): ValidationResult {
        val errors = mutableListOf<ValidationError>()
        
        if (values.isEmpty()) {
            errors.add(ValidationError(
                parameter = "values",
                message = "Values list cannot be empty",
                expectedValue = "At least 1 value"
            ))
        }
        
        if (initialIndex < 0 || initialIndex >= values.size) {
            errors.add(ValidationError(
                parameter = "initialIndex",
                message = "Initial index out of bounds",
                expectedValue = "0 to ${values.size - 1}"
            ))
        }
        
        // Log validation errors
        if (errors.isNotEmpty()) {
            val errorMessage = errors.joinToString(", ") { "${it.parameter}: ${it.message}" }
            Log.e("DesignSystem", "WheelConfig validation failed: $errorMessage")
        }
        
        return if (errors.isEmpty()) ValidationResult.Valid
        else ValidationResult.Invalid(errors)
    }
}
