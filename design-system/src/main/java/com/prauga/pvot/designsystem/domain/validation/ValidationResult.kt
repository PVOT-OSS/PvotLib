// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.validation

/**
 * Represents the result of a validation operation.
 */
sealed class ValidationResult {
    /**
     * Indicates that the validation passed successfully.
     */
    object Valid : ValidationResult()
    
    /**
     * Indicates that the validation failed with one or more errors.
     * @param errors List of validation errors that occurred
     */
    data class Invalid(val errors: List<ValidationError>) : ValidationResult()
}
