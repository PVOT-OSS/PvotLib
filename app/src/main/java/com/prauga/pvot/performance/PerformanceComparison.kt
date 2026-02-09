// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.performance

import com.prauga.pvot.designsystem.domain.monitoring.PerformanceMetrics

/**
 * Compares performance metrics before and after migration.
 * Calculates percentage improvements for each metric.
 * 
 * @param baseline Performance metrics before migration
 * @param postMigration Performance metrics after migration
 */
data class PerformanceComparison(
    val baseline: PerformanceMetrics,
    val postMigration: PerformanceMetrics
) {
    /**
     * Percentage improvement in recomposition count.
     * Positive values indicate fewer recompositions (better performance).
     * Negative values indicate more recompositions (worse performance).
     */
    val recompositionImprovement: Double
        get() = if (baseline.recompositionCount == 0) {
            0.0
        } else {
            ((baseline.recompositionCount - postMigration.recompositionCount).toDouble() 
                / baseline.recompositionCount) * 100
        }
    
    /**
     * Percentage improvement in average calculation time.
     * Positive values indicate faster calculations (better performance).
     * Negative values indicate slower calculations (worse performance).
     */
    val calculationTimeImprovement: Double
        get() = if (baseline.averageCalculationTime == 0L) {
            0.0
        } else {
            ((baseline.averageCalculationTime - postMigration.averageCalculationTime).toDouble()
                / baseline.averageCalculationTime) * 100
        }
    
    /**
     * Percentage improvement in maximum calculation time.
     * Positive values indicate better worst-case performance.
     * Negative values indicate worse worst-case performance.
     */
    val maxCalculationTimeImprovement: Double
        get() = if (baseline.maxCalculationTime == 0L) {
            0.0
        } else {
            ((baseline.maxCalculationTime - postMigration.maxCalculationTime).toDouble()
                / baseline.maxCalculationTime) * 100
        }
    
    /**
     * Percentage improvement in average frame time.
     * Positive values indicate smoother rendering (better performance).
     * Negative values indicate slower rendering (worse performance).
     */
    val averageFrameTimeImprovement: Double
        get() = if (baseline.averageFrameTimeMs == 0.0) {
            0.0
        } else {
            ((baseline.averageFrameTimeMs - postMigration.averageFrameTimeMs)
                / baseline.averageFrameTimeMs) * 100
        }
    
    /**
     * Percentage improvement in maximum frame time.
     * Positive values indicate better worst-case frame rendering.
     * Negative values indicate worse worst-case frame rendering.
     */
    val maxFrameTimeImprovement: Double
        get() = if (baseline.maxFrameTimeMs == 0L) {
            0.0
        } else {
            ((baseline.maxFrameTimeMs - postMigration.maxFrameTimeMs).toDouble()
                / baseline.maxFrameTimeMs) * 100
        }
    
    /**
     * Percentage improvement in dropped frame count.
     * Positive values indicate fewer dropped frames (better performance).
     * Negative values indicate more dropped frames (worse performance).
     */
    val droppedFrameImprovement: Double
        get() = if (baseline.droppedFrameCount == 0) {
            0.0
        } else {
            ((baseline.droppedFrameCount - postMigration.droppedFrameCount).toDouble()
                / baseline.droppedFrameCount) * 100
        }
    
    /**
     * Generates a human-readable performance comparison report.
     * Includes all metrics and their percentage improvements.
     * 
     * @return Formatted report string
     */
    fun generateReport(): String {
        return """
            Performance Comparison Report
            =============================
            Recomposition Count:
              Before: ${baseline.recompositionCount}
              After: ${postMigration.recompositionCount}
              Improvement: ${formatPercentage(recompositionImprovement)}
            
            Average Calculation Time:
              Before: ${baseline.averageCalculationTime}ms
              After: ${postMigration.averageCalculationTime}ms
              Improvement: ${formatPercentage(calculationTimeImprovement)}
            
            Max Calculation Time:
              Before: ${baseline.maxCalculationTime}ms
              After: ${postMigration.maxCalculationTime}ms
              Improvement: ${formatPercentage(maxCalculationTimeImprovement)}
            
            Average Frame Time:
              Before: ${"%.2f".format(baseline.averageFrameTimeMs)}ms
              After: ${"%.2f".format(postMigration.averageFrameTimeMs)}ms
              Improvement: ${formatPercentage(averageFrameTimeImprovement)}
            
            Max Frame Time:
              Before: ${baseline.maxFrameTimeMs}ms
              After: ${postMigration.maxFrameTimeMs}ms
              Improvement: ${formatPercentage(maxFrameTimeImprovement)}
            
            Dropped Frames:
              Before: ${baseline.droppedFrameCount} (${formatPercentage(baseline.droppedFramePercentage)})
              After: ${postMigration.droppedFrameCount} (${formatPercentage(postMigration.droppedFramePercentage)})
              Improvement: ${formatPercentage(droppedFrameImprovement)}
        """.trimIndent()
    }
    
    /**
     * Formats a percentage value with sign and 2 decimal places.
     * 
     * @param value The percentage value to format
     * @return Formatted string (e.g., "+25.50%", "-10.25%", "0.00%")
     */
    private fun formatPercentage(value: Double): String {
        val sign = when {
            value > 0 -> "+"
            value < 0 -> ""
            else -> ""
        }
        return "$sign${"%.2f".format(value)}%"
    }
}
