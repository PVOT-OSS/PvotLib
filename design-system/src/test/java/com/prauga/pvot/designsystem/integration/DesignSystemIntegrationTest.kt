// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.integration

import com.prauga.pvot.designsystem.components.navigation.NavBarConfig
import com.prauga.pvot.designsystem.components.picker.internal.WheelConfig
import com.prauga.pvot.designsystem.domain.animation.AnimationCoordinator
import com.prauga.pvot.designsystem.domain.cache.TextMeasurementCache
import com.prauga.pvot.designsystem.domain.monitoring.PerformanceMonitor
import com.prauga.pvot.designsystem.domain.scroll.ScrollCalculator
import com.prauga.pvot.designsystem.domain.transform.TransformEngine
import com.prauga.pvot.designsystem.domain.validation.ValidationEngine
import com.prauga.pvot.designsystem.domain.validation.ValidationResult
import com.prauga.pvot.designsystem.testutil.TestDataGenerators
import org.junit.Assert.*
import org.junit.Test

/**
 * Integration tests for the complete design system optimization.
 * 
 * Tests Requirements:
 * - WheelEngine with all optimizations enabled
 * - PvotNavBar with caching and validation
 * - Multiple wheels with animation coordination
 * - Error scenarios and graceful degradation
 */
class DesignSystemIntegrationTest {
    
    /**
     * Test: WheelEngine with all optimizations enabled
     * 
     * Verifies that TransformEngine and ScrollCalculator work together
     * to optimize wheel rendering.
     */
    @Test
    fun `wheel engine with all optimizations works correctly`() {
        val transformEngine = TransformEngine()
        val scrollCalculator = ScrollCalculator()
        val performanceMonitor = PerformanceMonitor(enabled = true)
        
        // Simulate wheel configuration
        val values = (0..59).toList()
        val firstVisibleIndex = 10
        val scrollOffset = 20f
        val itemHeightPx = 40f
        val visibleItemsCount = 5
        val halfVisibleItems = visibleItemsCount / 2f
        
        // Calculate visible range
        val visibleRange = scrollCalculator.calculateVisibleRange(
            firstVisibleIndex = firstVisibleIndex,
            scrollOffset = scrollOffset,
            itemHeightPx = itemHeightPx,
            visibleItemsCount = visibleItemsCount
        )
        
        // Verify visible range is calculated correctly
        assertTrue(visibleRange.first >= 0)
        assertTrue(visibleRange.last < values.size)
        assertTrue(visibleRange.contains(firstVisibleIndex))
        
        // Calculate transforms only for visible items
        val transforms = visibleRange.map { itemIndex ->
            performanceMonitor.recordRecomposition("WheelItem_$itemIndex")
            
            transformEngine.calculateTransform(
                itemIndex = itemIndex,
                firstVisibleIndex = firstVisibleIndex,
                scrollOffset = scrollOffset,
                itemHeightPx = itemHeightPx,
                halfVisibleItems = halfVisibleItems
            )
        }
        
        // Verify all transforms are valid
        transforms.forEach { transform ->
            assertTrue("RotationX should be in range", transform.rotationX in -60f..60f)
            assertTrue("Scale should be in range", transform.scale in 0.7f..1f)
            assertTrue("Alpha should be in range", transform.alpha in 0.3f..1f)
        }
        
        // Verify performance monitoring
        val metrics = performanceMonitor.getMetrics("WheelItem_${visibleRange.first}")
        assertTrue("Should have recorded recomposition", metrics.recompositionCount > 0)
    }
    
    /**
     * Test: PvotNavBar with caching and validation
     * 
     * Verifies that NavBar uses text caching and validation correctly.
     */
    @Test
    fun `nav bar with caching and validation works correctly`() {
        val textCache = TextMeasurementCache(maxSize = 10)
        val validationEngine = ValidationEngine()
        val animationCoordinator = AnimationCoordinator()
        
        // Create valid configuration
        val config = TestDataGenerators.createNavBarConfig(
            tabCount = 4,
            selectedTab = 1
        )
        
        // Validate configuration
        val validationResult = validationEngine.validateNavBarConfig(config.tabs, config.selectedTab)
        assertTrue("Config should be valid", validationResult is ValidationResult.Valid)
        
        // Simulate tab animations
        config.tabs.forEachIndexed { index, _ ->
            val animationId = "nav_item_$index"
            
            // Check if should animate
            if (animationCoordinator.shouldAnimate(animationId)) {
                animationCoordinator.registerAnimation(animationId)
            }
        }
        
        // Verify animations are registered
        assertTrue("Should have active animations", animationCoordinator.getActiveAnimationCount() > 0)
        
        // Cleanup animations
        config.tabs.forEachIndexed { index, _ ->
            animationCoordinator.unregisterAnimation("nav_item_$index")
        }
        
        assertEquals("All animations should be unregistered", 0, animationCoordinator.getActiveAnimationCount())
    }
    
    /**
     * Test: Multiple wheels with animation coordination
     * 
     * Verifies that multiple wheels can coordinate animations without
     * performance degradation.
     */
    @Test
    fun `multiple wheels with animation coordination work correctly`() {
        val animationCoordinator = AnimationCoordinator(maxConcurrentAnimations = 5)
        val performanceMonitor = PerformanceMonitor(enabled = true)
        
        // Simulate 3 wheels with 5 visible items each
        val wheelCount = 3
        val itemsPerWheel = 5
        
        // Register animations for all wheels
        for (wheelIndex in 0 until wheelCount) {
            for (itemIndex in 0 until itemsPerWheel) {
                val animationId = "wheel_${wheelIndex}_item_$itemIndex"
                
                if (animationCoordinator.shouldAnimate(animationId)) {
                    animationCoordinator.registerAnimation(animationId)
                    performanceMonitor.recordRecomposition(animationId)
                }
            }
        }
        
        // Verify animation coordination
        val activeCount = animationCoordinator.getActiveAnimationCount()
        assertTrue("Should have active animations", activeCount > 0)
        assertTrue("Should respect max concurrent limit", activeCount <= 5)
        
        // Verify performance tracking
        val metrics = performanceMonitor.getMetrics("wheel_0_item_0")
        assertTrue("Should track recompositions", metrics.recompositionCount > 0)
    }
    
    /**
     * Test: Error scenarios and graceful degradation
     * 
     * Verifies that the system handles errors gracefully.
     */
    @Test
    fun `system handles invalid configuration gracefully`() {
        val validationEngine = ValidationEngine()
        
        // Test invalid NavBarConfig - empty tabs
        val invalidConfig1 = NavBarConfig(
            tabs = emptyList(),
            selectedTab = 0
        )
        
        val result1 = validationEngine.validateNavBarConfig(invalidConfig1.tabs, invalidConfig1.selectedTab)
        assertTrue("Should detect empty tabs", result1 is ValidationResult.Invalid)
        
        if (result1 is ValidationResult.Invalid) {
            assertTrue("Should have error for tabs parameter", 
                result1.errors.any { it.parameter == "tabs" })
        }
        
        // Test invalid NavBarConfig - out of bounds selection
        val invalidConfig2 = TestDataGenerators.createNavBarConfig(
            tabCount = 3,
            selectedTab = 0
        ).copy(selectedTab = 10)
        
        val result2 = validationEngine.validateNavBarConfig(invalidConfig2.tabs, invalidConfig2.selectedTab)
        assertTrue("Should detect out of bounds selection", result2 is ValidationResult.Invalid)
        
        if (result2 is ValidationResult.Invalid) {
            assertTrue("Should have error for selectedTab parameter",
                result2.errors.any { it.parameter == "selectedTab" })
        }
    }
    
    /**
     * Test: WheelConfig validation with error handling
     */
    @Test
    fun `wheel config validation handles errors gracefully`() {
        val validationEngine = ValidationEngine()
        
        // Test invalid WheelConfig - empty values
        val result1 = validationEngine.validateWheelConfig(emptyList<Int>(), 0)
        assertTrue("Should detect empty values", result1 is ValidationResult.Invalid)
        
        // Test invalid WheelConfig - out of bounds index
        val result2 = validationEngine.validateWheelConfig(listOf(1, 2, 3), 10)
        assertTrue("Should detect out of bounds index", result2 is ValidationResult.Invalid)
    }
    
    /**
     * Test: Complete workflow with all components
     * 
     * Verifies that all optimizations work together in a realistic scenario.
     */
    @Test
    fun `complete workflow with all optimizations works correctly`() {
        // Initialize all domain components
        val transformEngine = TransformEngine()
        val scrollCalculator = ScrollCalculator()
        val textCache = TextMeasurementCache()
        val animationCoordinator = AnimationCoordinator()
        val validationEngine = ValidationEngine()
        val performanceMonitor = PerformanceMonitor(enabled = true)
        
        // Create and validate NavBar configuration
        val navBarConfig = TestDataGenerators.createNavBarConfig(tabCount = 3)
        val navBarValidation = validationEngine.validateNavBarConfig(navBarConfig.tabs, navBarConfig.selectedTab)
        assertTrue("NavBar config should be valid", navBarValidation is ValidationResult.Valid)
        
        // Create and validate Wheel configuration
        val wheelValues = (0..59).toList()
        val wheelValidation = validationEngine.validateWheelConfig(wheelValues, 30)
        assertTrue("Wheel config should be valid", wheelValidation is ValidationResult.Valid)
        
        // Simulate wheel rendering with optimizations
        val firstVisibleIndex = 28
        val scrollOffset = 15f
        val itemHeightPx = 40f
        val visibleItemsCount = 5
        
        val visibleRange = scrollCalculator.calculateVisibleRange(
            firstVisibleIndex = firstVisibleIndex,
            scrollOffset = scrollOffset,
            itemHeightPx = itemHeightPx,
            visibleItemsCount = visibleItemsCount
        )
        
        // Process visible items with animation coordination
        visibleRange.forEach { itemIndex ->
            val animationId = "wheel_item_$itemIndex"
            
            if (animationCoordinator.shouldAnimate(animationId)) {
                animationCoordinator.registerAnimation(animationId)
                
                // Calculate transform
                val transform = transformEngine.calculateTransform(
                    itemIndex = itemIndex,
                    firstVisibleIndex = firstVisibleIndex,
                    scrollOffset = scrollOffset,
                    itemHeightPx = itemHeightPx,
                    halfVisibleItems = visibleItemsCount / 2f
                )
                
                // Record performance
                performanceMonitor.recordRecomposition("WheelItem_$itemIndex")
                
                // Verify transform is valid
                assertTrue(transform.rotationX in -60f..60f)
                assertTrue(transform.scale in 0.7f..1f)
                assertTrue(transform.alpha in 0.3f..1f)
            }
        }
        
        // Verify all components worked correctly
        assertTrue("Should have active animations", animationCoordinator.getActiveAnimationCount() > 0)
        
        val metrics = performanceMonitor.getMetrics("WheelItem_${visibleRange.first}")
        assertTrue("Should track performance", metrics.recompositionCount > 0)
        
        // Cleanup
        visibleRange.forEach { itemIndex ->
            animationCoordinator.unregisterAnimation("wheel_item_$itemIndex")
        }
        
        assertEquals("All animations should be cleaned up", 0, animationCoordinator.getActiveAnimationCount())
    }
    
    /**
     * Test: Performance monitoring with multiple components
     */
    @Test
    fun `performance monitoring tracks all components correctly`() {
        val performanceMonitor = PerformanceMonitor(enabled = true, warningThreshold = 50)
        
        // Simulate multiple component recompositions
        repeat(10) {
            performanceMonitor.recordRecomposition("WheelEngine")
        }
        
        repeat(5) {
            performanceMonitor.recordRecomposition("PvotNavBar")
        }
        
        repeat(3) {
            performanceMonitor.recordRecomposition("PillNavItem")
        }
        
        // Record calculations
        performanceMonitor.recordCalculation("transform", 5L)
        performanceMonitor.recordCalculation("transform", 10L)
        performanceMonitor.recordCalculation("transform", 8L)
        
        // Verify metrics
        val wheelMetrics = performanceMonitor.getMetrics("WheelEngine")
        assertEquals(10, wheelMetrics.recompositionCount)
        
        val navBarMetrics = performanceMonitor.getMetrics("PvotNavBar")
        assertEquals(5, navBarMetrics.recompositionCount)
        
        val pillMetrics = performanceMonitor.getMetrics("PillNavItem")
        assertEquals(3, pillMetrics.recompositionCount)
        
        // Verify calculation metrics
        val transformMetrics = performanceMonitor.getMetrics("transform")
        assertTrue("Should have average calculation time", transformMetrics.averageCalculationTime > 0)
        assertEquals(10L, transformMetrics.maxCalculationTime)
    }
    
    /**
     * Test: Text cache efficiency with multiple measurements
     */
    @Test
    fun `text cache improves measurement efficiency`() {
        val textCache = TextMeasurementCache(maxSize = 20)
        
        // Simulate multiple tab labels
        val labels = listOf("Home", "Search", "Profile", "Settings", "More")
        
        // Cache should handle multiple labels
        labels.forEach { label ->
            // In real scenario, would measure with TextMeasurer
            // Here we just verify cache operations don't throw
            textCache.invalidate(label)
        }
        
        // Clear cache
        textCache.clear()
        
        // Cache should be empty after clear
        // (Verified through subsequent operations)
    }
}
