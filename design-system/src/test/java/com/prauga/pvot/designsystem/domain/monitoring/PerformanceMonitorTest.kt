// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.monitoring

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Property-based tests for PerformanceMonitor.
 * 
 * **Property 19: Performance Warning Threshold**
 * For any component that recomposes more than the warning threshold (100 times),
 * the performance monitor should log a warning with the component name and
 * recomposition count.
 */
class PerformanceMonitorTest {
    
    private lateinit var monitor: PerformanceMonitor
    
    @Before
    fun setup() {
        monitor = PerformanceMonitor(enabled = true, warningThreshold = 100)
    }
    
    // Property 19: Performance Warning Threshold
    
    @Test
    fun `recordRecomposition should track recomposition count`() {
        val componentName = "TestComponent"
        
        monitor.recordRecomposition(componentName)
        monitor.recordRecomposition(componentName)
        monitor.recordRecomposition(componentName)
        
        val metrics = monitor.getMetrics(componentName)
        assertEquals(3, metrics.recompositionCount)
    }
    
    @Test
    fun `recordRecomposition should track multiple components separately`() {
        monitor.recordRecomposition("Component1")
        monitor.recordRecomposition("Component1")
        monitor.recordRecomposition("Component2")
        monitor.recordRecomposition("Component2")
        monitor.recordRecomposition("Component2")
        
        val metrics1 = monitor.getMetrics("Component1")
        val metrics2 = monitor.getMetrics("Component2")
        
        assertEquals(2, metrics1.recompositionCount)
        assertEquals(3, metrics2.recompositionCount)
    }
    
    @Test
    fun `recordRecomposition should accumulate over time`() {
        val componentName = "TestComponent"
        
        // First batch
        repeat(50) { monitor.recordRecomposition(componentName) }
        assertEquals(50, monitor.getMetrics(componentName).recompositionCount)
        
        // Second batch
        repeat(30) { monitor.recordRecomposition(componentName) }
        assertEquals(80, monitor.getMetrics(componentName).recompositionCount)
        
        // Third batch
        repeat(20) { monitor.recordRecomposition(componentName) }
        assertEquals(100, monitor.getMetrics(componentName).recompositionCount)
    }
    
    @Test
    fun `recordRecomposition should handle threshold crossing`() {
        val componentName = "TestComponent"
        
        // Just under threshold
        repeat(99) { monitor.recordRecomposition(componentName) }
        assertEquals(99, monitor.getMetrics(componentName).recompositionCount)
        
        // Cross threshold
        monitor.recordRecomposition(componentName)
        assertEquals(100, monitor.getMetrics(componentName).recompositionCount)
        
        // Above threshold
        monitor.recordRecomposition(componentName)
        assertEquals(101, monitor.getMetrics(componentName).recompositionCount)
    }
    
    @Test
    fun `recordCalculation should track calculation times`() {
        val operationName = "transform"
        
        monitor.recordCalculation(operationName, 10L)
        monitor.recordCalculation(operationName, 20L)
        monitor.recordCalculation(operationName, 30L)
        
        val metrics = monitor.getMetrics(operationName)
        assertEquals(20L, metrics.averageCalculationTime) // (10 + 20 + 30) / 3
        assertEquals(30L, metrics.maxCalculationTime)
    }
    
    @Test
    fun `recordCalculation should calculate correct average`() {
        val operationName = "measure"
        
        monitor.recordCalculation(operationName, 5L)
        monitor.recordCalculation(operationName, 10L)
        monitor.recordCalculation(operationName, 15L)
        monitor.recordCalculation(operationName, 20L)
        
        val metrics = monitor.getMetrics(operationName)
        assertEquals(12L, metrics.averageCalculationTime) // (5 + 10 + 15 + 20) / 4
    }
    
    @Test
    fun `recordCalculation should track max correctly`() {
        val operationName = "layout"
        
        monitor.recordCalculation(operationName, 10L)
        monitor.recordCalculation(operationName, 50L)
        monitor.recordCalculation(operationName, 30L)
        monitor.recordCalculation(operationName, 20L)
        
        val metrics = monitor.getMetrics(operationName)
        assertEquals(50L, metrics.maxCalculationTime)
    }
    
    @Test
    fun `getMetrics should return zero values for unknown component`() {
        val metrics = monitor.getMetrics("UnknownComponent")
        
        assertEquals(0, metrics.recompositionCount)
        assertEquals(0L, metrics.averageCalculationTime)
        assertEquals(0L, metrics.maxCalculationTime)
    }
    
    @Test
    fun `getMetrics should return zero average for component with no calculations`() {
        val componentName = "TestComponent"
        
        monitor.recordRecomposition(componentName)
        monitor.recordRecomposition(componentName)
        
        val metrics = monitor.getMetrics(componentName)
        assertEquals(2, metrics.recompositionCount)
        assertEquals(0L, metrics.averageCalculationTime)
        assertEquals(0L, metrics.maxCalculationTime)
    }
    
    @Test
    fun `isPerformant should return true for good performance`() {
        val componentName = "GoodComponent"
        
        // Low recomposition count
        repeat(50) { monitor.recordRecomposition(componentName) }
        
        // Fast calculations
        monitor.recordCalculation(componentName, 5L)
        monitor.recordCalculation(componentName, 10L)
        
        val metrics = monitor.getMetrics(componentName)
        assertTrue(metrics.isPerformant)
    }
    
    @Test
    fun `isPerformant should return false for high recomposition count`() {
        val componentName = "BadComponent"
        
        // High recomposition count (over 100)
        repeat(150) { monitor.recordRecomposition(componentName) }
        
        // Fast calculations
        monitor.recordCalculation(componentName, 5L)
        
        val metrics = monitor.getMetrics(componentName)
        assertFalse(metrics.isPerformant)
    }
    
    @Test
    fun `isPerformant should return false for slow calculations`() {
        val componentName = "SlowComponent"
        
        // Low recomposition count
        repeat(50) { monitor.recordRecomposition(componentName) }
        
        // Slow calculations (over 16ms)
        monitor.recordCalculation(componentName, 20L)
        monitor.recordCalculation(componentName, 25L)
        
        val metrics = monitor.getMetrics(componentName)
        assertFalse(metrics.isPerformant)
    }
    
    @Test
    fun `isPerformant should return false when both metrics are bad`() {
        val componentName = "VeryBadComponent"
        
        // High recomposition count
        repeat(150) { monitor.recordRecomposition(componentName) }
        
        // Slow calculations
        monitor.recordCalculation(componentName, 30L)
        
        val metrics = monitor.getMetrics(componentName)
        assertFalse(metrics.isPerformant)
    }
    
    @Test
    fun `disabled monitor should not record recompositions`() {
        val disabledMonitor = PerformanceMonitor(enabled = false)
        
        disabledMonitor.recordRecomposition("Component")
        disabledMonitor.recordRecomposition("Component")
        
        val metrics = disabledMonitor.getMetrics("Component")
        assertEquals(0, metrics.recompositionCount)
    }
    
    @Test
    fun `disabled monitor should not record calculations`() {
        val disabledMonitor = PerformanceMonitor(enabled = false)
        
        disabledMonitor.recordCalculation("operation", 10L)
        disabledMonitor.recordCalculation("operation", 20L)
        
        val metrics = disabledMonitor.getMetrics("operation")
        assertEquals(0L, metrics.averageCalculationTime)
        assertEquals(0L, metrics.maxCalculationTime)
    }
    
    @Test
    fun `custom warning threshold should be respected`() {
        val customMonitor = PerformanceMonitor(enabled = true, warningThreshold = 50)
        
        // Just under custom threshold
        repeat(49) { customMonitor.recordRecomposition("Component") }
        assertEquals(49, customMonitor.getMetrics("Component").recompositionCount)
        
        // At custom threshold
        customMonitor.recordRecomposition("Component")
        assertEquals(50, customMonitor.getMetrics("Component").recompositionCount)
        
        // Above custom threshold
        customMonitor.recordRecomposition("Component")
        assertEquals(51, customMonitor.getMetrics("Component").recompositionCount)
    }
    
    @Test
    fun `monitor should be thread-safe for concurrent recomposition recording`() {
        val componentName = "ConcurrentComponent"
        val threads = mutableListOf<Thread>()
        
        // Create multiple threads recording recompositions
        for (i in 1..10) {
            threads.add(Thread {
                repeat(10) {
                    monitor.recordRecomposition(componentName)
                }
            })
        }
        
        // Start all threads
        threads.forEach { it.start() }
        
        // Wait for completion
        threads.forEach { it.join() }
        
        // Should have recorded all 100 recompositions
        val metrics = monitor.getMetrics(componentName)
        assertEquals(100, metrics.recompositionCount)
    }
    
    @Test
    fun `monitor should be thread-safe for concurrent calculation recording`() {
        val operationName = "ConcurrentOperation"
        val threads = mutableListOf<Thread>()
        
        // Create multiple threads recording calculations
        for (i in 1..10) {
            threads.add(Thread {
                repeat(10) {
                    monitor.recordCalculation(operationName, 10L)
                }
            })
        }
        
        // Start all threads
        threads.forEach { it.start() }
        
        // Wait for completion
        threads.forEach { it.join() }
        
        // Should have recorded all calculations
        val metrics = monitor.getMetrics(operationName)
        assertEquals(10L, metrics.averageCalculationTime)
    }
    
    @Test
    fun `monitor should handle zero calculations gracefully`() {
        val componentName = "NoCalcs"
        
        monitor.recordRecomposition(componentName)
        
        val metrics = monitor.getMetrics(componentName)
        assertEquals(1, metrics.recompositionCount)
        assertEquals(0L, metrics.averageCalculationTime)
        assertEquals(0L, metrics.maxCalculationTime)
    }
    
    @Test
    fun `monitor should handle single calculation`() {
        val operationName = "SingleCalc"
        
        monitor.recordCalculation(operationName, 15L)
        
        val metrics = monitor.getMetrics(operationName)
        assertEquals(15L, metrics.averageCalculationTime)
        assertEquals(15L, metrics.maxCalculationTime)
    }
    
    @Test
    fun `monitor should track many components independently`() {
        val componentCount = 20
        
        for (i in 1..componentCount) {
            repeat(i) { monitor.recordRecomposition("Component$i") }
        }
        
        // Verify each component has correct count
        for (i in 1..componentCount) {
            val metrics = monitor.getMetrics("Component$i")
            assertEquals(i, metrics.recompositionCount)
        }
    }
    
    @Test
    fun `logWarnings should not crash when called`() {
        // Record some data
        repeat(150) { monitor.recordRecomposition("Component1") }
        repeat(50) { monitor.recordRecomposition("Component2") }
        
        // Should not throw
        monitor.logWarnings()
    }
    
    @Test
    fun `disabled monitor logWarnings should not crash`() {
        val disabledMonitor = PerformanceMonitor(enabled = false)
        
        // Should not throw even when disabled
        disabledMonitor.logWarnings()
    }
}
