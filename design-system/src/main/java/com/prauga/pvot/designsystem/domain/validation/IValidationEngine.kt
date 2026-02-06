// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.validation

/**
 * Interface for validating component configuration.
 * Provides validation methods for different component types.
 */
interface IValidationEngine {
    /**
     * Validates navigation bar configuration.
     * 
     * @param tabs List of tab items
     * @param selectedTab Index of the currently selected tab
     * @return ValidationResult.Valid if configuration is valid, 
     *         ValidationResult.Invalid with errors otherwise
     */
    fun validateNavBarConfig(tabs: List<*>, selectedTab: Int): ValidationResult
    
    /**
     * Validates wheel picker configuration.
     * 
     * @param values List of values for the wheel
     * @param initialIndex Initial selected index
     * @return ValidationResult.Valid if configuration is valid,
     *         ValidationResult.Invalid with errors otherwise
     */
    fun validateWheelConfig(values: List<*>, initialIndex: Int): ValidationResult
}
