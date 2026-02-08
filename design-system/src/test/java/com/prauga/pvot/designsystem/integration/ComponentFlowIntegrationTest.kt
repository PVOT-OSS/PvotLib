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
 * Integration tests for complete component flows.
 * 
 * Tests complete workflows from configuration to rendering,
 * including validation, caching, and performance monitoring.
 * 
 */
class ComponentFlowIntegrationTest {
    
    /**
     * Test: Complete navigation bar flow
     * 
     * Tests the full lifecycle of a navigation bar from configuration
     * through validation, caching, and animation coordination.
     */
    @Test
    fun `complete navigation bar flow works correctly`() {
        // Step 1: Create dependencies
        val textCache = TextMeasurementCache(maxSize = 20)
        val animationCoordinator = AnimationCoordinator(maxConcurrentAnimations = 5)
        val validationEngine = ValidationEngine()
        val performanceMonitor = PerformanceMonitor(enabled = true)
        
        // Step 2: Create configuration
        val config = TestDataGenerators.createNavBarConfig(
            tabCount = 4,
            selectedTab = 0
        )
        
        // Step 3: Validate configuration
        val validationResult = validationEngine.validateNavBarConfig(config.tabs, config.selectedTab)
        assertTrue("Configuration should be valid", validationResult is ValidationResult.Valid)
        
        // Step 4: Simulate tab interactions
        config.tabs.forEachIndexed { index, _ ->
            val animationId = "nav_item_$index"
            
            // Check if animation should run
            if (animationCoordinator.shouldAnimate(animationId)) {
                // Register animation
                animationCoordinator.registerAnimation(animationId)
                
                // Record performance
                performanceMonitor.recordRecomposition("PvotNavBar")
                performanceMonitor.recordRecomposition("PillNavItem_$index")
                
                // Simulate animation completion
                animationCoordinator.unregisterAnimation(animationId)
            }
        }
        
        // Step 5: Verify state
        assertEquals("All animations should be complete", 
            0, animationCoordinator.getActiveAnimationCount())
        
        val navBarMetrics = performanceMonitor.getMetrics("PvotNavBar")
        assertTrue("Should have recorded recompositions", 
            navBarMetrics.recompositionCount > 0)
        
        // Step 6: Verify cache is functional
        textCache.clear()
        // Cache should be empty after clear
    }
    
    /**
     * Test: Complete wheel picker flow
     * 
     * Tests the full lifecycle of a wheel picker from configuration
     * through validation, transform calculations, and scroll handling.
     */
    @Test
    fun `complete wheel picker flow works correctly`() {
        // Step 1: Create dependencies
        val transformEngine = TransformEngine()
        val scrollCalculator = ScrollCalculator()
        val validationEngine = ValidationEngine()
        val performanceMonitor = PerformanceMonitor(enabled = true)
        
        // Step 2: Create configuration
        val config = WheelConfig(
            values = (0..59).toList(),
            initialIndex = 30
        )
        
        // Step 3: Validate configuration
        val validationResult = validationEngine.validateWheelConfig(config.values, config.initialIndex)
        assertTrue("Configuration should be valid", validationResult is ValidationResult.Valid)
        
        // Step 4: Simulate scrolling
        val scrollPositions = listOf(
            Triple(28, 0f, 40f),
            Triple(29, 20f, 40f),
            Triple(30, 0f, 40f),
            Triple(31, 20f, 40f),
            Triple(32, 0f, 40f)
        )
        
        scrollPositions.forEach { (firstVisible, offset, itemHeight) ->
            // Calculate visible range
            val visibleRange = scrollCalculator.calculateVisibleRange(
                firstVisibleIndex = firstVisible,
                scrollOffset = offset,
                itemHeightPx = itemHeight,
                visibleItemsCount = 5
            )
            
            // Calculate transforms for visible items
            visibleRange.forEach { itemIndex ->
                val transform = transformEngine.calculateTransform(
                    itemIndex = itemIndex,
                    firstVisibleIndex = firstVisible,
                    scrollOffset = offset,
                    itemHeightPx = itemHeight,
                    halfVisibleItems = 2.5f
                )
                
                // Verify transform is valid
                assertTrue("Rotation should be in range", 
                    transform.rotationX in -60f..60f)
                assertTrue("Scale should be in range", 
                    transform.scale in 0.7f..1f)
                assertTrue("Alpha should be in range", 
                    transform.alpha in 0.3f..1f)
                
                // Record performance
                performanceMonitor.recordRecomposition("WheelItem_$itemIndex")
            }
        }
        
        // Step 5: Verify performance
        val metrics = performanceMonitor.getMetrics("WheelItem_30")
        assertTrue("Should have recorded recompositions", 
            metrics.recompositionCount > 0)
    }
    
    /**
     * Test: Performance monitoring integration
     * 
     * Tests that performance monitoring works correctly across
     * multiple components and operations.
     */
    @Test
    fun `performance monitoring integration works correctly`() {
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
        
        // Record some calculations
        performanceMonitor.recordCalculation("transform", 5L)
        performanceMonitor.recordCalculation("transform", 10L)
        performanceMonitor.recordCalculation("transform", 8L)
        
        performanceMonitor.recordCalculation("textMeasure", 2L)
        performanceMonitor.recordCalculation("textMeasure", 3L)
        
        // Verify all metrics
        val wheelMetrics = performanceMonitor.getMetrics("WheelEngine")
        assertEquals(10, wheelMetrics.recompositionCount)
        
        val navBarMetrics = performanceMonitor.getMetrics("PvotNavBar")
        assertEquals(5, navBarMetrics.recompositionCount)
        
        val pillMetrics = performanceMonitor.getMetrics("PillNavItem")
        assertEquals(3, pillMetrics.recompositionCount)
        
        val transformMetrics = performanceMonitor.getMetrics("transform")
        assertTrue("Should have average time", transformMetrics.averageCalculationTime > 0)
        assertEquals(10L, transformMetrics.maxCalculationTime)
        
        val textMetrics = performanceMonitor.getMetrics("textMeasure")
        assertTrue("Should have average time", textMetrics.averageCalculationTime > 0)
        assertEquals(3L, textMetrics.maxCalculationTime)
        
        // Log warnings should not throw
        performanceMonitor.logWarnings()
    }
    
    /**
     * Test: Error handling integration
     * 
     * Tests that error handling works correctly across the system.
     */
    @Test
    fun `error handling integration works correctly`() {
        val validationEngine = ValidationEngine()
        
        // Test 1: Invalid NavBar configuration
        val invalidNavBarConfig = NavBarConfig(
            tabs = emptyList(),
            selectedTab = 0
        )
        
        val navBarResult = validationEngine.validateNavBarConfig(invalidNavBarConfig.tabs, invalidNavBarConfig.selectedTab)
        assertTrue("Should detect invalid config", navBarResult is ValidationResult.Invalid)
        
        if (navBarResult is ValidationResult.Invalid) {
            assertTrue("Should have errors", navBarResult.errors.isNotEmpty())
            assertTrue("Should have tabs error", 
                navBarResult.errors.any { it.parameter == "tabs" })
        }
        
        // Test 2: Invalid Wheel configuration
        val invalidWheelConfig = WheelConfig(
            values = listOf(1, 2, 3),
            initialIndex = 10
        )
        
        val wheelResult = validationEngine.validateWheelConfig(invalidWheelConfig.values, invalidWheelConfig.initialIndex)
        assertTrue("Should detect invalid config", wheelResult is ValidationResult.Invalid)
        
        if (wheelResult is ValidationResult.Invalid) {
            assertTrue("Should have errors", wheelResult.errors.isNotEmpty())
            assertTrue("Should have initialIndex error", 
                wheelResult.errors.any { it.parameter == "initialIndex" })
        }
        
        // Test 3: Valid configurations should pass
        val validNavBarConfig = TestDataGenerators.createNavBarConfig(tabCount = 3)
        val validNavBarResult = validationEngine.validateNavBarConfig(validNavBarConfig.tabs, validNavBarConfig.selectedTab)
        assertTrue("Valid config should pass", validNavBarResult is ValidationResult.Valid)
        
        val validWheelConfig = WheelConfig(values = listOf(1, 2, 3), initialIndex = 1)
        val validWheelResult = validationEngine.validateWheelConfig(validWheelConfig.values, validWheelConfig.initialIndex)
        assertTrue("Valid config should pass", validWheelResult is ValidationResult.Valid)
    }
    
    /**
     * Test: Multi-component coordination
     * 
     * Tests that multiple components can work together with shared resources.
     */
    @Test
    fun `multi-component coordination works correctly`() {
        // Shared resources
        val textCache = TextMeasurementCache(maxSize = 50)
        val animationCoordinator = AnimationCoordinator(maxConcurrentAnimations = 10)
        val performanceMonitor = PerformanceMonitor(enabled = true)
        
        // Create multiple NavBar configurations
        val navBar1 = TestDataGenerators.createNavBarConfig(tabCount = 3, selectedTab = 0)
        val navBar2 = TestDataGenerators.createNavBarConfig(tabCount = 4, selectedTab = 1)
        
        // Create multiple Wheel configurations
        val wheel1 = WheelConfig(values = (0..23).toList(), initialIndex = 12)
        val wheel2 = WheelConfig(values = (0..59).toList(), initialIndex = 30)
        
        // Simulate interactions with NavBar 1
        navBar1.tabs.forEachIndexed { index, _ ->
            val animId = "nav1_item_$index"
            if (animationCoordinator.shouldAnimate(animId)) {
                animationCoordinator.registerAnimation(animId)
                performanceMonitor.recordRecomposition("NavBar1_Item_$index")
            }
        }
        
        // Simulate interactions with NavBar 2
        navBar2.tabs.forEachIndexed { index, _ ->
            val animId = "nav2_item_$index"
            if (animationCoordinator.shouldAnimate(animId)) {
                animationCoordinator.registerAnimation(animId)
                performanceMonitor.recordRecomposition("NavBar2_Item_$index")
            }
        }
        
        // Simulate wheel scrolling
        val transformEngine = TransformEngine()
        val scrollCalculator = ScrollCalculator()
        
        listOf(wheel1, wheel2).forEachIndexed { wheelIndex, config ->
            val visibleRange = scrollCalculator.calculateVisibleRange(
                firstVisibleIndex = config.initialIndex,
                scrollOffset = 0f,
                itemHeightPx = 40f,
                visibleItemsCount = 5
            )
            
            visibleRange.forEach { itemIndex ->
                transformEngine.calculateTransform(
                    itemIndex = itemIndex,
                    firstVisibleIndex = config.initialIndex,
                    scrollOffset = 0f,
                    itemHeightPx = 40f,
                    halfVisibleItems = 2.5f
                )
                
                performanceMonitor.recordRecomposition("Wheel${wheelIndex}_Item_$itemIndex")
            }
        }
        
        // Verify coordination
        val activeAnimations = animationCoordinator.getActiveAnimationCount()
        assertTrue("Should have active animations", activeAnimations > 0)
        assertTrue("Should respect max limit", activeAnimations <= 10)
        
        // Verify performance tracking
        val nav1Metrics = performanceMonitor.getMetrics("NavBar1_Item_0")
        assertTrue("Should track NavBar1", nav1Metrics.recompositionCount > 0)
        
        val wheel0Metrics = performanceMonitor.getMetrics("Wheel0_Item_${wheel1.initialIndex}")
        assertTrue("Should track Wheel0", wheel0Metrics.recompositionCount > 0)
        
        // Cleanup
        navBar1.tabs.forEachIndexed { index, _ ->
            animationCoordinator.unregisterAnimation("nav1_item_$index")
        }
        navBar2.tabs.forEachIndexed { index, _ ->
            animationCoordinator.unregisterAnimation("nav2_item_$index")
        }
        
        textCache.clear()
    }
    
    /**
     * Test: Stress test with many operations
     * 
     * Tests system stability under heavy load.
     */
    @Test
    fun `stress test with many operations works correctly`() {
        val transformEngine = TransformEngine()
        val scrollCalculator = ScrollCalculator()
        val performanceMonitor = PerformanceMonitor(enabled = true)
        
        // Perform many transform calculations
        repeat(1000) { iteration ->
            val firstVisible = iteration % 60
            val offset = (iteration % 40).toFloat()
            
            val visibleRange = scrollCalculator.calculateVisibleRange(
                firstVisibleIndex = firstVisible,
                scrollOffset = offset,
                itemHeightPx = 40f,
                visibleItemsCount = 5
            )
            
            visibleRange.forEach { itemIndex ->
                val transform = transformEngine.calculateTransform(
                    itemIndex = itemIndex,
                    firstVisibleIndex = firstVisible,
                    scrollOffset = offset,
                    itemHeightPx = 40f,
                    halfVisibleItems = 2.5f
                )
                
                // Verify transform is always valid
                assertTrue(transform.rotationX in -60f..60f)
                assertTrue(transform.scale in 0.7f..1f)
                assertTrue(transform.alpha in 0.3f..1f)
            }
            
            if (iteration % 100 == 0) {
                performanceMonitor.recordRecomposition("StressTest")
            }
        }
        
        // System should still be functional
        val metrics = performanceMonitor.getMetrics("StressTest")
        assertTrue("Should have recorded some recompositions", 
            metrics.recompositionCount > 0)
    }
}
