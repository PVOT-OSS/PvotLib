// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.performance

import com.prauga.pvot.designsystem.domain.monitoring.PerformanceMetrics
import com.prauga.pvot.designsystem.domain.monitoring.PerformanceMonitor
import java.io.File

/**
 * Utilities for capturing and managing performance metrics during testing.
 * Provides functions for baseline measurement, post-migration measurement,
 * and metric comparison.
 */
object PerformanceTestUtils {
    
    /**
     * Captures performance metrics from a PerformanceMonitor.
     * 
     * @param monitor The performance monitor to capture metrics from
     * @param componentName Name of the component to capture metrics for
     * @return PerformanceMetrics captured from the monitor
     */
    fun captureMetrics(
        monitor: PerformanceMonitor,
        componentName: String
    ): PerformanceMetrics {
        return monitor.getMetrics(componentName)
    }
    
    /**
     * Saves performance metrics to a file for later comparison.
     * Metrics are saved in a simple text format with key-value pairs.
     * 
     * @param metrics The metrics to save
     * @param file The file to save metrics to
     * @param label Label for this measurement (e.g., "baseline", "post-migration")
     */
    fun saveMetricsToFile(
        metrics: PerformanceMetrics,
        file: File,
        label: String
    ) {
        val content = buildString {
            appendLine("# Performance Metrics - $label")
            appendLine("recompositionCount=${metrics.recompositionCount}")
            appendLine("averageCalculationTime=${metrics.averageCalculationTime}")
            appendLine("maxCalculationTime=${metrics.maxCalculationTime}")
        }
        file.writeText(content)
    }
    
    /**
     * Loads performance metrics from a file.
     * 
     * @param file The file to load metrics from
     * @return PerformanceMetrics loaded from the file
     * @throws IllegalArgumentException if file format is invalid
     */
    fun loadMetricsFromFile(file: File): PerformanceMetrics {
        val lines = file.readLines()
        val data = lines
            .filter { it.contains("=") && !it.startsWith("#") }
            .associate {
                val (key, value) = it.split("=", limit = 2)
                key.trim() to value.trim()
            }
        
        return PerformanceMetrics(
            recompositionCount = data["recompositionCount"]?.toIntOrNull() 
                ?: throw IllegalArgumentException("Missing or invalid recompositionCount"),
            averageCalculationTime = data["averageCalculationTime"]?.toLongOrNull()
                ?: throw IllegalArgumentException("Missing or invalid averageCalculationTime"),
            maxCalculationTime = data["maxCalculationTime"]?.toLongOrNull()
                ?: throw IllegalArgumentException("Missing or invalid maxCalculationTime")
        )
    }
    
    /**
     * Creates a PerformanceComparison from baseline and post-migration metrics.
     * 
     * @param baseline Baseline performance metrics
     * @param postMigration Post-migration performance metrics
     * @return PerformanceComparison object for analysis
     */
    fun compareMetrics(
        baseline: PerformanceMetrics,
        postMigration: PerformanceMetrics
    ): PerformanceComparison {
        return PerformanceComparison(
            baseline = baseline,
            postMigration = postMigration
        )
    }
    
    /**
     * Validates that performance has improved after migration.
     * 
     * @param comparison The performance comparison to validate
     * @return true if performance improved, false otherwise
     */
    fun hasImproved(comparison: PerformanceComparison): Boolean {
        return comparison.recompositionImprovement > 0 ||
               comparison.calculationTimeImprovement > 0
    }
}
