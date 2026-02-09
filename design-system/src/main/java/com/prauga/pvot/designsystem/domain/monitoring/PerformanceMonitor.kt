// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.monitoring

import android.util.Log

/**
 * Implementation of performance monitor with recomposition tracking.
 * Thread-safe implementation for concurrent access.
 * 
 * @param enabled Whether performance monitoring is enabled
 * @param warningThreshold Recomposition count threshold for warnings
 */
class PerformanceMonitor(
    private val enabled: Boolean = false,
    private val warningThreshold: Int = 100
) : IPerformanceMonitor {
    
    private val recompositions = mutableMapOf<String, Int>()
    private val calculations = mutableMapOf<String, MutableList<Long>>()
    
    /**
     * Records a recomposition event for a component.
     * Only records if monitoring is enabled.
     */
    override fun recordRecomposition(componentName: String) {
        if (!enabled) return
        
        synchronized(recompositions) {
            recompositions[componentName] = (recompositions[componentName] ?: 0) + 1
        }
    }
    
    /**
     * Records the duration of a calculation operation.
     * Only records if monitoring is enabled.
     */
    override fun recordCalculation(operationName: String, durationMs: Long) {
        if (!enabled) return
        
        synchronized(calculations) {
            calculations.getOrPut(operationName) { mutableListOf() }.add(durationMs)
        }
    }
    
    /**
     * Retrieves performance metrics for a specific component.
     * Returns metrics with zero values if no data is available.
     */
    override fun getMetrics(componentName: String): PerformanceMetrics {
        val count = synchronized(recompositions) {
            recompositions[componentName] ?: 0
        }
        
        val times = synchronized(calculations) {
            calculations[componentName]?.toList() ?: emptyList()
        }
        
        return PerformanceMetrics(
            recompositionCount = count,
            averageCalculationTime = if (times.isNotEmpty()) times.average().toLong() else 0L,
            maxCalculationTime = times.maxOrNull() ?: 0L
        )
    }
    
    /**
     * Logs warnings for components that exceed the recomposition threshold.
     * Only logs if monitoring is enabled.
     */
    override fun logWarnings() {
        if (!enabled) return
        
        synchronized(recompositions) {
            recompositions.forEach { (component, count) ->
                if (count > warningThreshold) {
                    Log.w(
                        "PerformanceMonitor",
                        "$component recomposed $count times (threshold: $warningThreshold)"
                    )
                }
            }
        }
    }
}
