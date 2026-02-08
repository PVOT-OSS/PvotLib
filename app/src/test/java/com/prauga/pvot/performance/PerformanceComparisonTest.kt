// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.performance

import com.prauga.pvot.designsystem.domain.monitoring.PerformanceMetrics
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for PerformanceComparison to verify improvement calculations.
 */
class PerformanceComparisonTest {
    
    @Test
    fun `recompositionImprovement calculates correctly for improvement`() {
        val baseline = PerformanceMetrics(100, 20L, 40L)
        val postMigration = PerformanceMetrics(70, 15L, 30L)
        val comparison = PerformanceComparison(baseline, postMigration)
        
        assertEquals(30.0, comparison.recompositionImprovement, 0.01)
    }
    
    @Test
    fun `recompositionImprovement calculates correctly for degradation`() {
        val baseline = PerformanceMetrics(70, 15L, 30L)
        val postMigration = PerformanceMetrics(100, 20L, 40L)
        val comparison = PerformanceComparison(baseline, postMigration)
        
        assertEquals(-42.86, comparison.recompositionImprovement, 0.01)
    }
    
    @Test
    fun `recompositionImprovement handles zero baseline`() {
        val baseline = PerformanceMetrics(0, 20L, 40L)
        val postMigration = PerformanceMetrics(10, 15L, 30L)
        val comparison = PerformanceComparison(baseline, postMigration)
        
        assertEquals(0.0, comparison.recompositionImprovement, 0.01)
    }
    
    @Test
    fun `calculationTimeImprovement calculates correctly`() {
        val baseline = PerformanceMetrics(100, 20L, 40L)
        val postMigration = PerformanceMetrics(70, 15L, 30L)
        val comparison = PerformanceComparison(baseline, postMigration)
        
        assertEquals(25.0, comparison.calculationTimeImprovement, 0.01)
    }
    
    @Test
    fun `maxCalculationTimeImprovement calculates correctly`() {
        val baseline = PerformanceMetrics(100, 20L, 40L)
        val postMigration = PerformanceMetrics(70, 15L, 30L)
        val comparison = PerformanceComparison(baseline, postMigration)
        
        assertEquals(25.0, comparison.maxCalculationTimeImprovement, 0.01)
    }
    
    @Test
    fun `generateReport includes all metrics`() {
        val baseline = PerformanceMetrics(100, 20L, 40L)
        val postMigration = PerformanceMetrics(70, 15L, 30L)
        val comparison = PerformanceComparison(baseline, postMigration)
        
        val report = comparison.generateReport()
        
        assertTrue(report.contains("Before: 100"))
        assertTrue(report.contains("After: 70"))
        assertTrue(report.contains("+30.00%"))
        assertTrue(report.contains("Before: 20ms"))
        assertTrue(report.contains("After: 15ms"))
        assertTrue(report.contains("+25.00%"))
    }
}
