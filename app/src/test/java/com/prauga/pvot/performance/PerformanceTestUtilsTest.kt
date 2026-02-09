// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.performance

import com.prauga.pvot.designsystem.domain.monitoring.PerformanceMetrics
import com.prauga.pvot.designsystem.domain.monitoring.PerformanceMonitor
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/**
 * Tests for PerformanceTestUtils to verify metric capture and comparison utilities.
 */
class PerformanceTestUtilsTest {
    
    @Test
    fun `captureMetrics returns metrics from monitor`() {
        val monitor = PerformanceMonitor(enabled = true)
        monitor.recordRecomposition("TestComponent")
        monitor.recordRecomposition("TestComponent")
        monitor.recordCalculation("TestComponent", 10L)
        monitor.recordCalculation("TestComponent", 20L)
        
        val metrics = PerformanceTestUtils.captureMetrics(monitor, "TestComponent")
        
        assertEquals(2, metrics.recompositionCount)
        assertEquals(15L, metrics.averageCalculationTime)
        assertEquals(20L, metrics.maxCalculationTime)
    }
    
    @Test
    fun `saveMetricsToFile and loadMetricsFromFile work correctly`() {
        val metrics = PerformanceMetrics(
            recompositionCount = 50,
            averageCalculationTime = 12L,
            maxCalculationTime = 25L
        )
        val tempFile = File.createTempFile("metrics", ".txt")
        tempFile.deleteOnExit()
        
        PerformanceTestUtils.saveMetricsToFile(metrics, tempFile, "test")
        val loaded = PerformanceTestUtils.loadMetricsFromFile(tempFile)
        
        assertEquals(metrics.recompositionCount, loaded.recompositionCount)
        assertEquals(metrics.averageCalculationTime, loaded.averageCalculationTime)
        assertEquals(metrics.maxCalculationTime, loaded.maxCalculationTime)
    }
    
    @Test
    fun `compareMetrics creates PerformanceComparison`() {
        val baseline = PerformanceMetrics(100, 20L, 40L)
        val postMigration = PerformanceMetrics(70, 15L, 30L)
        
        val comparison = PerformanceTestUtils.compareMetrics(baseline, postMigration)
        
        assertEquals(baseline, comparison.baseline)
        assertEquals(postMigration, comparison.postMigration)
        assertEquals(30.0, comparison.recompositionImprovement, 0.01)
        assertEquals(25.0, comparison.calculationTimeImprovement, 0.01)
    }
    
    @Test
    fun `hasImproved returns true when performance improved`() {
        val baseline = PerformanceMetrics(100, 20L, 40L)
        val postMigration = PerformanceMetrics(70, 15L, 30L)
        val comparison = PerformanceComparison(baseline, postMigration)
        
        assertTrue(PerformanceTestUtils.hasImproved(comparison))
    }
    
    @Test
    fun `hasImproved returns false when performance degraded`() {
        val baseline = PerformanceMetrics(70, 15L, 30L)
        val postMigration = PerformanceMetrics(100, 20L, 40L)
        val comparison = PerformanceComparison(baseline, postMigration)
        
        assertFalse(PerformanceTestUtils.hasImproved(comparison))
    }
}
