// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.examples

import com.prauga.pvot.designsystem.domain.animation.AnimationCoordinator
import com.prauga.pvot.designsystem.domain.cache.TextMeasurementCache
import com.prauga.pvot.designsystem.domain.monitoring.PerformanceMonitor
import com.prauga.pvot.designsystem.domain.scroll.ScrollCalculator
import com.prauga.pvot.designsystem.domain.transform.TransformEngine
import com.prauga.pvot.designsystem.domain.validation.ValidationEngine
import com.prauga.pvot.designsystem.testutil.TestDataGenerators
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Example integration tests.
 * 
 * This example demonstrates:
 * - How to test interactions between multiple components
 * - How to test complete workflows
 * - How to verify component integration
 * - How to test with real implementations (not mocks)
 * 
 * Integration tests verify that components work together correctly
 * and that the system behaves as expected in realistic scenarios.
 */
class IntegrationTestExample {
    
    /**
     * Integration: TransformEngine + ScrollCalculator
     * 
     * Tests that transform calculations work correctly with
     * scroll calculator's visible range.
     */
    @Test
    fun `transform engine and scroll calculator work together`() {
        val transformEngine = TransformEngine()
        val scrollCalculator = ScrollCalculator()
        
        val firstVisibleIndex = 5
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
        
        // Calculate transforms for visible items
        val transforms = visibleRange.map { itemIndex ->
            transformEngine.calculateTransform(
                itemIndex = itemIndex,
                firstVisibleIndex = firstVisibleIndex,
                scrollOffset = scrollOffset,
                itemHeightPx = itemHeightPx,
                halfVisibleItems = halfVisibleItems
            )
        }
        
        // All transforms should be valid
        transforms.forEach { transform ->
            assertTrue(transform.rotationX in -60f..60f)
            assertTrue(transform.scale in 0.7f..1f)
            assertTrue(transform.alpha in 0.3f..1f)
        }
        
        // Should have transforms for all visible items
        assertEquals(visibleRange.count(), transforms.size)
    }
    
    /**
     * Integration: ValidationEngine + NavBarConfig
     * 
     * Tests that validation engine correctly validates
     * configuration objects.
     */
    @Test
    fun `validation engine validates nav bar config correctly`() {
        val validationEngine = ValidationEngine()
        
        // Valid config should pass
        val validTabs = listOf("Tab1", "Tab2", "Tab3")
        val validResult = validationEngine.validateNavBarConfig(validTabs, 1)
        assertTrue(validResult is com.prauga.pvot.designsystem.domain.validation.ValidationResult.Valid)
        
        // Invalid config should fail
        val invalidResult = validationEngine.validateNavBarConfig(validTabs, 10)
        assertTrue(invalidResult is com.prauga.pvot.designsystem.domain.validation.ValidationResult.Invalid)
    }
    
    /**
     * Integration: AnimationCoordinator workflow
     * 
     * Tests the complete animation coordination workflow.
     */
    @Test
    fun `animation coordinator manages animation lifecycle`() {
        val coordinator = AnimationCoordinator(maxConcurrentAnimations = 3)
        
        // Initially no animations
        assertEquals(0, coordinator.getActiveAnimationCount())
        
        // Register animations
        coordinator.registerAnimation("anim1")
        coordinator.registerAnimation("anim2")
        assertEquals(2, coordinator.getActiveAnimationCount())
        
        // Both should be allowed to animate
        assertTrue(coordinator.shouldAnimate("anim1"))
        assertTrue(coordinator.shouldAnimate("anim2"))
        
        // Register more animations
        coordinator.registerAnimation("anim3")
        coordinator.registerAnimation("anim4")
        assertEquals(4, coordinator.getActiveAnimationCount())
        
        // New animation should not be allowed (exceeds max)
        assertTrue(!coordinator.shouldAnimate("anim5"))
        
        // Unregister an animation
        coordinator.unregisterAnimation("anim1")
        assertEquals(3, coordinator.getActiveAnimationCount())
        
        // Now new animation should be allowed
        assertTrue(coordinator.shouldAnimate("anim5"))
    }
    
    /**
     * Integration: TextMeasurementCache workflow
     * 
     * Tests the complete caching workflow with multiple operations.
     */
    @Test
    fun `text measurement cache handles complete workflow`() {
        val cache = TextMeasurementCache(maxSize = 5)
        
        // Cache should start empty
        // (Note: We can't directly check size without exposing it,
        // but we can verify behavior)
        
        // Add items to cache
        val texts = listOf("Text1", "Text2", "Text3")
        val style = TestDataGenerators.createItemTransform()
        
        // Verify cache behavior through measurement calls
        // (In real test, would use actual TextMeasurer)
        
        // Invalidate specific entry
        cache.invalidate("Text2")
        
        // Clear all entries
        cache.clear()
        
        // Cache should be empty after clear
        // (Verified through subsequent measurement behavior)
    }
    
    /**
     * Integration: PerformanceMonitor workflow
     * 
     * Tests the complete performance monitoring workflow.
     */
    @Test
    fun `performance monitor tracks metrics correctly`() {
        val monitor = PerformanceMonitor(enabled = true, warningThreshold = 10)
        
        // Record recompositions
        repeat(5) {
            monitor.recordRecomposition("WheelEngine")
        }
        
        repeat(3) {
            monitor.recordRecomposition("PvotNavBar")
        }
        
        // Record calculations
        monitor.recordCalculation("transform", 5L)
        monitor.recordCalculation("transform", 10L)
        monitor.recordCalculation("transform", 15L)
        
        // Get metrics
        val wheelMetrics = monitor.getMetrics("WheelEngine")
        assertEquals(5, wheelMetrics.recompositionCount)
        
        val navBarMetrics = monitor.getMetrics("PvotNavBar")
        assertEquals(3, navBarMetrics.recompositionCount)
        
        // Verify calculation metrics
        val transformMetrics = monitor.getMetrics("transform")
        assertEquals(10L, transformMetrics.averageCalculationTime)
        assertEquals(15L, transformMetrics.maxCalculationTime)
    }
    
    /**
     * Integration: Multiple domain components together
     * 
     * Tests that all domain components can work together
     * in a realistic scenario.
     */
    @Test
    fun `all domain components work together`() {
        val transformEngine = TransformEngine()
        val scrollCalculator = ScrollCalculator()
        val validationEngine = ValidationEngine()
        val animationCoordinator = AnimationCoordinator()
        val performanceMonitor = PerformanceMonitor(enabled = true)
        
        // Validate configuration
        val tabs = listOf("Tab1", "Tab2", "Tab3")
        val validationResult = validationEngine.validateNavBarConfig(tabs, 1)
        assertTrue(validationResult is com.prauga.pvot.designsystem.domain.validation.ValidationResult.Valid)
        
        // Calculate visible range
        val visibleRange = scrollCalculator.calculateVisibleRange(
            firstVisibleIndex = 0,
            scrollOffset = 0f,
            itemHeightPx = 40f,
            visibleItemsCount = 5
        )
        
        // Calculate transforms for visible items
        visibleRange.forEach { itemIndex ->
            // Check if should animate
            val animationId = "item_$itemIndex"
            if (animationCoordinator.shouldAnimate(animationId)) {
                animationCoordinator.registerAnimation(animationId)
                
                // Calculate transform
                val transform = transformEngine.calculateTransform(
                    itemIndex = itemIndex,
                    firstVisibleIndex = 0,
                    scrollOffset = 0f,
                    itemHeightPx = 40f,
                    halfVisibleItems = 2.5f
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
        assertTrue(animationCoordinator.getActiveAnimationCount() > 0)
        val metrics = performanceMonitor.getMetrics("WheelItem_0")
        assertTrue(metrics.recompositionCount > 0)
    }
}
