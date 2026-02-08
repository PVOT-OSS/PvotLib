// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.examples

import com.prauga.pvot.designsystem.domain.scroll.ScrollCalculator
import com.prauga.pvot.designsystem.domain.transform.TransformEngine
import com.prauga.pvot.designsystem.testutil.TestDataGenerators
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Example unit tests for edge cases.
 * 
 * This example demonstrates:
 * - How to test specific boundary conditions
 * - How to test edge cases that might not be caught by property tests
 * - How to test specific numeric values
 * - How to structure unit tests for clarity
 * 
 * Unit tests complement property tests by verifying specific examples
 * and edge cases that are important to get right.
 */
class EdgeCaseTestExample {
    
    private val transformEngine = TransformEngine()
    private val scrollCalculator = ScrollCalculator()
    
    /**
     * Edge Case: Zero scroll offset
     * 
     * When scroll offset is exactly zero, the first visible item
     * should be at the center position.
     */
    @Test
    fun `zero scroll offset produces center transform for first item`() {
        val transform = transformEngine.calculateTransform(
            itemIndex = 0,
            firstVisibleIndex = 0,
            scrollOffset = 0f,
            itemHeightPx = 40f,
            halfVisibleItems = 2.5f
        )
        
        // Center item should have minimal rotation
        assertTrue(transform.rotationX in -0.1f..0.1f)
        
        // Center item should have full scale
        assertTrue(transform.scale in 0.99f..1.01f)
        
        // Center item should have full alpha
        assertTrue(transform.alpha in 0.99f..1.01f)
    }
    
    /**
     * Edge Case: Maximum distance from center
     * 
     * Items at the maximum distance should have maximum rotation,
     * minimum scale, and minimum alpha.
     */
    @Test
    fun `maximum distance produces maximum transform values`() {
        val transform = transformEngine.calculateTransform(
            itemIndex = 10,
            firstVisibleIndex = 0,
            scrollOffset = 0f,
            itemHeightPx = 40f,
            halfVisibleItems = 2.5f
        )
        
        // Far item should have maximum rotation
        assertTrue(transform.rotationX >= 50f || transform.rotationX <= -50f)
        
        // Far item should have minimum scale
        assertTrue(transform.scale <= 0.75f)
        
        // Far item should have minimum alpha
        assertTrue(transform.alpha <= 0.35f)
    }
    
    /**
     * Edge Case: Single visible item
     * 
     * When only one item is visible, the visible range should
     * still include buffer items.
     */
    @Test
    fun `single visible item includes buffer range`() {
        val visibleRange = scrollCalculator.calculateVisibleRange(
            firstVisibleIndex = 5,
            scrollOffset = 0f,
            itemHeightPx = 40f,
            visibleItemsCount = 1
        )
        
        // Should include buffer items
        assertTrue(visibleRange.first <= 4)
        assertTrue(visibleRange.last >= 6)
    }
    
    /**
     * Edge Case: First item visible
     * 
     * When the first item is visible, the range should not
     * include negative indices.
     */
    @Test
    fun `first item visible range does not include negative indices`() {
        val visibleRange = scrollCalculator.calculateVisibleRange(
            firstVisibleIndex = 0,
            scrollOffset = 0f,
            itemHeightPx = 40f,
            visibleItemsCount = 5
        )
        
        // Should not include negative indices
        assertTrue(visibleRange.first >= 0)
    }
    
    /**
     * Edge Case: Large scroll offset
     * 
     * Large scroll offsets should still produce valid transforms.
     */
    @Test
    fun `large scroll offset produces valid transform`() {
        val transform = transformEngine.calculateTransform(
            itemIndex = 50,
            firstVisibleIndex = 45,
            scrollOffset = 999f,
            itemHeightPx = 40f,
            halfVisibleItems = 2.5f
        )
        
        // All values should be within valid ranges
        assertTrue(transform.rotationX in -60f..60f)
        assertTrue(transform.scale in 0.7f..1f)
        assertTrue(transform.alpha in 0.3f..1f)
    }
    
    /**
     * Edge Case: Fractional visible items
     * 
     * When visible items count is odd, half visible items
     * will be fractional (e.g., 2.5).
     */
    @Test
    fun `fractional half visible items produces valid transform`() {
        val transform = transformEngine.calculateTransform(
            itemIndex = 3,
            firstVisibleIndex = 2,
            scrollOffset = 20f,
            itemHeightPx = 40f,
            halfVisibleItems = 2.5f
        )
        
        // Should produce valid transform
        assertTrue(transform.rotationX in -60f..60f)
        assertTrue(transform.scale in 0.7f..1f)
        assertTrue(transform.alpha in 0.3f..1f)
    }
    
    /**
     * Edge Case: Item exactly at boundary
     * 
     * Items at the exact boundary of the visible range
     * should be considered visible.
     */
    @Test
    fun `item at boundary is considered visible`() {
        val visibleRange = 5..10
        
        // Boundary items should be visible
        assertTrue(scrollCalculator.isItemVisible(5, visibleRange))
        assertTrue(scrollCalculator.isItemVisible(10, visibleRange))
        
        // Items outside boundary should not be visible
        assertTrue(!scrollCalculator.isItemVisible(4, visibleRange))
        assertTrue(!scrollCalculator.isItemVisible(11, visibleRange))
    }
}
