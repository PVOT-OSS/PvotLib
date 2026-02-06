// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.monitoring

/**
 * Represents performance metrics for a component.
 * 
 * @param recompositionCount Number of times the component has recomposed
 * @param averageCalculationTime Average time in milliseconds for calculations
 * @param maxCalculationTime Maximum time in milliseconds for a single calculation
 */
data class PerformanceMetrics(
    val recompositionCount: Int,
    val averageCalculationTime: Long,
    val maxCalculationTime: Long
) {
    /**
     * Indicates whether the component is performing well.
     * A component is considered performant if:
     * - Recomposition count is below 100
     * - Average calculation time is below 16ms (60fps threshold)
     */
    val isPerformant: Boolean
        get() = recompositionCount < 100 && averageCalculationTime < 16
}
