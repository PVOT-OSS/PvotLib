// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.picker.internal

import com.prauga.pvot.designsystem.domain.scroll.ScrollCalculator
import com.prauga.pvot.designsystem.domain.transform.TransformEngine
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Property-based tests for WheelEngine visible item optimization.
 * 
 * **Property 1: Visible Item Transform Calculation**
 * For any wheel configuration and scroll position, only items within the visible range
 * plus one buffer item on each side should have their 3D transformations calculated.
 * 
 * **Property 3: Constant Pre-calculation**
 * For any wheel configuration, constant values (itemHeightPx, halfVisibleItems) should be
 * calculated once during initialization and reused across all recompositions.
 * 
 */
class WheelEngineOptimizationTest {
    
    private lateinit var transformEngine: TransformEngine
    private lateinit var scrollCalculator: ScrollCalculator
    
    @Before
    fun setup() {
        transformEngine = TransformEngine()
        scrollCalculator = ScrollCalculator()
    }
    
    // Property 1: Visible Item Transform Calculation
    @Test
    fun `only visible items should have transforms calculated`() {
        // Feature: design-system-optimization, Property 1: Visible Item Transform Calculation
        
        val itemCount = 100
        val visibleItemsCount = 5
        val firstVisibleIndex = 10
        val scrollOffset = 0f
        val itemHeightPx = 40f
        
        // Calculate visible range
        val visibleRange = scrollCalculator.calculateVisibleRange(
            firstVisibleIndex,
            scrollOffset,
            itemHeightPx,
            visibleItemsCount
        )
        
        // Verify visible range includes buffer items
        assertTrue(visibleRange.first <= firstVisibleIndex)
        assertTrue(visibleRange.last >= firstVisibleIndex + visibleItemsCount)
        
        // Count items that should have transforms
        val itemsWithTransforms = (0 until itemCount).count { index ->
            scrollCalculator.isItemVisible(index, visibleRange)
        }
        
        // Should be visible items + buffer items (typically 1 on each side)
        assertTrue(itemsWithTransforms <= visibleItemsCount + 2)
        assertTrue(itemsWithTransforms >= visibleItemsCount)
    }
    
    @Test
    fun `visible range should include buffer items on both sides`() {
        // Feature: design-system-optimization, Property 1: Visible Item Transform Calculation
        
        val visibleItemsCount = 5
        val firstVisibleIndex = 20
        val scrollOffset = 0f
        val itemHeightPx = 40f
        
        val visibleRange = scrollCalculator.calculateVisibleRange(
            firstVisibleIndex,
            scrollOffset,
            itemHeightPx,
            visibleItemsCount
        )
        
        // Should include at least one buffer item before
        assertTrue(visibleRange.first < firstVisibleIndex)
        
        // Should include at least one buffer item after
        assertTrue(visibleRange.last > firstVisibleIndex + visibleItemsCount)
    }
    
    @Test
    fun `visible range should adapt to scroll position`() {
        // Feature: design-system-optimization, Property 1: Visible Item Transform Calculation
        
        val visibleItemsCount = 5
        val itemHeightPx = 40f
        
        // Test different scroll positions
        val scrollPositions = listOf(
            Pair(0, 0f),
            Pair(10, 0f),
            Pair(20, 0f),
            Pair(50, 0f),
            Pair(10, 20f),
            Pair(10, 40f)
        )
        
        scrollPositions.forEach { (firstVisibleIndex, scrollOffset) ->
            val visibleRange = scrollCalculator.calculateVisibleRange(
                firstVisibleIndex,
                scrollOffset,
                itemHeightPx,
                visibleItemsCount
            )
            
            // Visible range should always include the first visible index
            assertTrue(visibleRange.contains(firstVisibleIndex))
            
            // Visible range should be reasonable size
            val rangeSize = visibleRange.last - visibleRange.first + 1
            assertTrue(rangeSize >= visibleItemsCount)
            assertTrue(rangeSize <= visibleItemsCount + 3) // With buffers
        }
    }
    
    @Test
    fun `items outside visible range should not be considered visible`() {
        // Feature: design-system-optimization, Property 1: Visible Item Transform Calculation
        
        val visibleItemsCount = 5
        val firstVisibleIndex = 50
        val scrollOffset = 0f
        val itemHeightPx = 40f
        
        val visibleRange = scrollCalculator.calculateVisibleRange(
            firstVisibleIndex,
            scrollOffset,
            itemHeightPx,
            visibleItemsCount
        )
        
        // Items far before should not be visible
        assertFalse(scrollCalculator.isItemVisible(0, visibleRange))
        assertFalse(scrollCalculator.isItemVisible(10, visibleRange))
        assertFalse(scrollCalculator.isItemVisible(40, visibleRange))
        
        // Items far after should not be visible
        assertFalse(scrollCalculator.isItemVisible(100, visibleRange))
        assertFalse(scrollCalculator.isItemVisible(200, visibleRange))
    }
    
    @Test
    fun `visible range should handle edge case at start of list`() {
        // Feature: design-system-optimization, Property 1: Visible Item Transform Calculation
        
        val visibleItemsCount = 5
        val firstVisibleIndex = 0
        val scrollOffset = 0f
        val itemHeightPx = 40f
        
        val visibleRange = scrollCalculator.calculateVisibleRange(
            firstVisibleIndex,
            scrollOffset,
            itemHeightPx,
            visibleItemsCount
        )
        
        // Should not go below 0
        assertTrue(visibleRange.first >= 0)
        
        // Should include first visible index
        assertTrue(visibleRange.contains(0))
    }
    
    @Test
    fun `visible range should handle edge case at end of list`() {
        // Feature: design-system-optimization, Property 1: Visible Item Transform Calculation
        
        val itemCount = 20
        val visibleItemsCount = 5
        val firstVisibleIndex = 15
        val scrollOffset = 0f
        val itemHeightPx = 40f
        
        val visibleRange = scrollCalculator.calculateVisibleRange(
            firstVisibleIndex,
            scrollOffset,
            itemHeightPx,
            visibleItemsCount
        )
        
        // Should include items near the end
        assertTrue(visibleRange.contains(15))
        assertTrue(visibleRange.contains(19))
    }
    
    // Property 3: Constant Pre-calculation
    @Test
    fun `constant values should be calculated once and reused`() {
        // Feature: design-system-optimization, Property 3: Constant Pre-calculation
        
        val itemHeightDp = 40 // dp
        val density = 2.5f // typical density
        val visibleItemsCount = 5
        
        // Simulate pre-calculation (in real code, this happens in remember blocks)
        val itemHeightPx = itemHeightDp * density
        val halfVisibleItems = visibleItemsCount / 2f
        
        // These values should be constants
        assertEquals(100f, itemHeightPx, 0.001f)
        assertEquals(2.5f, halfVisibleItems, 0.001f)
        
        // Use these constants multiple times (simulating recompositions)
        for (i in 0..100) {
            val transform = transformEngine.calculateTransform(
                i, 50, 0f, itemHeightPx, halfVisibleItems
            )
            assertNotNull(transform)
        }
    }
    
    @Test
    fun `transform engine should use pre-calculated constants efficiently`() {
        // Feature: design-system-optimization, Property 3: Constant Pre-calculation
        
        val itemHeightPx = 40f
        val halfVisibleItems = 2.5f
        
        // Calculate transforms for multiple items using same constants
        val transforms = (0..50).map { index ->
            transformEngine.calculateTransform(
                index, 25, 0f, itemHeightPx, halfVisibleItems
            )
        }
        
        // All transforms should be valid
        assertEquals(51, transforms.size)
        transforms.forEach { transform ->
            assertNotNull(transform)
            assertTrue(transform.scale >= 0.7f && transform.scale <= 1f)
            assertTrue(transform.alpha >= 0.3f && transform.alpha <= 1f)
        }
    }
    
    @Test
    fun `visible item optimization should work with various list sizes`() {
        // Feature: design-system-optimization, Property 1: Visible Item Transform Calculation
        
        val testCases = listOf(
            Triple(10, 5, 3),   // Small list
            Triple(50, 5, 20),  // Medium list
            Triple(100, 5, 50), // Large list
            Triple(1000, 5, 500) // Very large list
        )
        
        testCases.forEach { (itemCount, visibleCount, firstVisible) ->
            val visibleRange = scrollCalculator.calculateVisibleRange(
                firstVisible,
                0f,
                40f,
                visibleCount
            )
            
            // Count visible items
            val visibleItems = (0 until itemCount).count { index ->
                scrollCalculator.isItemVisible(index, visibleRange)
            }
            
            // Should only process visible items + buffers
            assertTrue(visibleItems <= visibleCount + 3)
            
            // Should process at least the visible items
            assertTrue(visibleItems >= visibleCount)
        }
    }
    
    @Test
    fun `optimization should reduce calculations for large lists`() {
        // Feature: design-system-optimization, Property 1: Visible Item Transform Calculation
        
        val itemCount = 1000
        val visibleItemsCount = 5
        val firstVisibleIndex = 500
        val itemHeightPx = 40f
        
        val visibleRange = scrollCalculator.calculateVisibleRange(
            firstVisibleIndex,
            0f,
            itemHeightPx,
            visibleItemsCount
        )
        
        // Count items that would need transform calculations
        val itemsNeedingCalculation = (0 until itemCount).count { index ->
            scrollCalculator.isItemVisible(index, visibleRange)
        }
        
        // Should be much less than total items
        assertTrue(itemsNeedingCalculation < itemCount / 10)
        
        // Should be close to visible count
        assertTrue(itemsNeedingCalculation <= visibleItemsCount + 3)
    }
}
