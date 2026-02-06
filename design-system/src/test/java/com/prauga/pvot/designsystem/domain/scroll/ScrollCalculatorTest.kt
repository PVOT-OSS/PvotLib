// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.scroll

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Property-based tests for ScrollCalculator.
 * 
 * **Property 11: Pure Function Transformations**
 * For any data transformation function in the domain layer, calling it with the same
 * inputs multiple times should always produce the same output (referential transparency).
 */
class ScrollCalculatorTest {
    
    private lateinit var calculator: ScrollCalculator
    
    @Before
    fun setup() {
        calculator = ScrollCalculator()
    }
    
    // Property 11: Pure Function Transformations
    @Test
    fun `calculateVisibleRange should return same result for same inputs`() {
        val firstVisibleIndex = 5
        val scrollOffset = 120f
        val itemHeightPx = 40f
        val visibleItemsCount = 7
        
        // Call multiple times with same inputs
        val result1 = calculator.calculateVisibleRange(
            firstVisibleIndex, scrollOffset, itemHeightPx, visibleItemsCount
        )
        val result2 = calculator.calculateVisibleRange(
            firstVisibleIndex, scrollOffset, itemHeightPx, visibleItemsCount
        )
        val result3 = calculator.calculateVisibleRange(
            firstVisibleIndex, scrollOffset, itemHeightPx, visibleItemsCount
        )
        
        // All results should be identical
        assertEquals(result1, result2)
        assertEquals(result1, result3)
    }
    
    @Test
    fun `calculateVisibleRange should be pure across multiple instances`() {
        val calculator1 = ScrollCalculator()
        val calculator2 = ScrollCalculator()
        
        val firstVisibleIndex = 10
        val scrollOffset = 80f
        val itemHeightPx = 40f
        val visibleItemsCount = 5
        
        val result1 = calculator1.calculateVisibleRange(
            firstVisibleIndex, scrollOffset, itemHeightPx, visibleItemsCount
        )
        val result2 = calculator2.calculateVisibleRange(
            firstVisibleIndex, scrollOffset, itemHeightPx, visibleItemsCount
        )
        
        // Different instances should produce same results
        assertEquals(result1, result2)
    }
    
    @Test
    fun `isItemVisible should return same result for same inputs`() {
        val itemIndex = 7
        val visibleRange = 5..10
        
        // Call multiple times
        val result1 = calculator.isItemVisible(itemIndex, visibleRange)
        val result2 = calculator.isItemVisible(itemIndex, visibleRange)
        val result3 = calculator.isItemVisible(itemIndex, visibleRange)
        
        // All results should be identical
        assertEquals(result1, result2)
        assertEquals(result1, result3)
    }
    
    @Test
    fun `calculateVisibleRange should include buffer items`() {
        val firstVisibleIndex = 5
        val scrollOffset = 0f
        val itemHeightPx = 40f
        val visibleItemsCount = 5
        
        val range = calculator.calculateVisibleRange(
            firstVisibleIndex, scrollOffset, itemHeightPx, visibleItemsCount
        )
        
        // Should include one buffer item before
        assertTrue(range.first <= firstVisibleIndex - 1)
        
        // Should include one buffer item after
        assertTrue(range.last >= firstVisibleIndex + visibleItemsCount + 1)
    }
    
    @Test
    fun `calculateVisibleRange should handle zero first index`() {
        val firstVisibleIndex = 0
        val scrollOffset = 0f
        val itemHeightPx = 40f
        val visibleItemsCount = 5
        
        val range = calculator.calculateVisibleRange(
            firstVisibleIndex, scrollOffset, itemHeightPx, visibleItemsCount
        )
        
        // Should not go below 0
        assertTrue(range.first >= 0)
        
        // Should include visible items plus buffer
        assertTrue(range.last >= visibleItemsCount)
    }
    
    @Test
    fun `calculateVisibleRange should be consistent across different scroll offsets`() {
        val firstVisibleIndex = 10
        val itemHeightPx = 40f
        val visibleItemsCount = 7
        
        // Different scroll offsets shouldn't change the range significantly
        val range1 = calculator.calculateVisibleRange(
            firstVisibleIndex, 0f, itemHeightPx, visibleItemsCount
        )
        val range2 = calculator.calculateVisibleRange(
            firstVisibleIndex, 20f, itemHeightPx, visibleItemsCount
        )
        
        // Ranges should be similar (scroll offset doesn't affect range calculation in current impl)
        assertEquals(range1, range2)
    }
    
    @Test
    fun `isItemVisible should correctly identify items in range`() {
        val visibleRange = 5..10
        
        // Items in range
        assertTrue(calculator.isItemVisible(5, visibleRange))
        assertTrue(calculator.isItemVisible(7, visibleRange))
        assertTrue(calculator.isItemVisible(10, visibleRange))
        
        // Items outside range
        assertFalse(calculator.isItemVisible(4, visibleRange))
        assertFalse(calculator.isItemVisible(11, visibleRange))
        assertFalse(calculator.isItemVisible(0, visibleRange))
        assertFalse(calculator.isItemVisible(100, visibleRange))
    }
    
    @Test
    fun `isItemVisible should handle edge cases`() {
        val visibleRange = 0..5
        
        // First item
        assertTrue(calculator.isItemVisible(0, visibleRange))
        
        // Last item
        assertTrue(calculator.isItemVisible(5, visibleRange))
        
        // Just outside
        assertFalse(calculator.isItemVisible(-1, visibleRange))
        assertFalse(calculator.isItemVisible(6, visibleRange))
    }
    
    @Test
    fun `calculateVisibleRange should handle large visible counts`() {
        val firstVisibleIndex = 50
        val scrollOffset = 0f
        val itemHeightPx = 40f
        val visibleItemsCount = 100
        
        val range = calculator.calculateVisibleRange(
            firstVisibleIndex, scrollOffset, itemHeightPx, visibleItemsCount
        )
        
        // Should include all visible items plus buffers
        assertTrue(range.last - range.first >= visibleItemsCount)
    }
    
    @Test
    fun `calculateVisibleRange should be monotonic with firstVisibleIndex`() {
        val scrollOffset = 0f
        val itemHeightPx = 40f
        val visibleItemsCount = 5
        
        val range1 = calculator.calculateVisibleRange(5, scrollOffset, itemHeightPx, visibleItemsCount)
        val range2 = calculator.calculateVisibleRange(10, scrollOffset, itemHeightPx, visibleItemsCount)
        val range3 = calculator.calculateVisibleRange(15, scrollOffset, itemHeightPx, visibleItemsCount)
        
        // As firstVisibleIndex increases, range should shift forward
        assertTrue(range2.first > range1.first)
        assertTrue(range3.first > range2.first)
        assertTrue(range2.last > range1.last)
        assertTrue(range3.last > range2.last)
    }
}
