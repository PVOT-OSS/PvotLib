// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.monitoring

/**
 * Represents performance metrics for a component.
 * 
 * @param recompositionCount Number of times the component has recomposed
 * @param averageCalculationTime Average time in milliseconds for calculations
 * @param maxCalculationTime Maximum time in milliseconds for a single calculation
 * @param averageFrameTimeMs Average frame rendering time in milliseconds
 * @param maxFrameTimeMs Maximum frame rendering time in milliseconds
 * @param droppedFrameCount Number of frames that exceeded 16ms (dropped frames)
 */
data class PerformanceMetrics(
    val recompositionCount: Int,
    val averageCalculationTime: Long,
    val maxCalculationTime: Long,
    val averageFrameTimeMs: Double = 0.0,
    val maxFrameTimeMs: Long = 0L,
    val droppedFrameCount: Int = 0
) {
    /**
     * Indicates whether the component is performing well.
     * A component is considered performant if:
     * - Recomposition count is below 100
     * - Average calculation time is below 16ms (60fps threshold)
     * - Average frame time is below 16ms (60fps threshold)
     * - Dropped frame count is minimal (less than 5% of total frames)
     */
    val isPerformant: Boolean
        get() = recompositionCount < 100 && 
                averageCalculationTime < 16 && 
                averageFrameTimeMs < 16.0
    
    /**
     * Calculates the percentage of dropped frames.
     * Assumes 60fps target, so total expected frames = recompositionCount.
     */
    val droppedFramePercentage: Double
        get() = if (recompositionCount > 0) {
            (droppedFrameCount.toDouble() / recompositionCount) * 100
        } else {
            0.0
        }
}
