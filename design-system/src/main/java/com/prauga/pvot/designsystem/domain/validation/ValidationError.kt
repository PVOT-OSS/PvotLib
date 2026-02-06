// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.validation

/**
 * Represents a validation error with details about what went wrong.
 * 
 * @param parameter The name of the parameter that failed validation
 * @param message A human-readable description of the validation failure
 * @param expectedValue Optional description of what value was expected
 */
data class ValidationError(
    val parameter: String,
    val message: String,
    val expectedValue: String? = null
) {
    /**
     * Converts the validation error to a display-friendly string.
     * Format: "{parameter}: {message} (expected: {expectedValue})"
     */
    fun toDisplayString(): String {
        return "$parameter: $message" + 
            (expectedValue?.let { " (expected: $it)" } ?: "")
    }
}
