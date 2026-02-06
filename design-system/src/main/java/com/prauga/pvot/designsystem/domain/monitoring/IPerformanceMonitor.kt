// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.monitoring

/**
 * Interface for monitoring component performance.
 * Tracks recompositions and calculation times to identify performance issues.
 */
interface IPerformanceMonitor {
    /**
     * Records a recomposition event for a component.
     * 
     * @param componentName Name of the component that recomposed
     */
    fun recordRecomposition(componentName: String)
    
    /**
     * Records the duration of a calculation operation.
     * 
     * @param operationName Name of the operation
     * @param durationMs Duration in milliseconds
     */
    fun recordCalculation(operationName: String, durationMs: Long)
    
    /**
     * Retrieves performance metrics for a specific component.
     * 
     * @param componentName Name of the component
     * @return Performance metrics for the component
     */
    fun getMetrics(componentName: String): PerformanceMetrics
    
    /**
     * Logs warnings for components that exceed performance thresholds.
     */
    fun logWarnings()
}
