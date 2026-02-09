// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.scroll

import com.prauga.pvot.designsystem.domain.scroll.ScrollConstants.VISIBLE_RANGE_BUFFER_AFTER
import com.prauga.pvot.designsystem.domain.scroll.ScrollConstants.VISIBLE_RANGE_BUFFER_BEFORE

/**
 * Calculates scroll-related metrics for lazy lists.
 * Pure Kotlin implementation for visibility calculations.
 */
class ScrollCalculator : IScrollCalculator {
    
    /**
     * Calculates the range of items that should be considered visible.
     * 
     * Adds a buffer of 1 item before and after the strictly visible range
     * to ensure smooth rendering during scrolling and to support 3D effects
     * that may extend beyond the visible area.
     * 
     * @param firstVisibleIndex The index of the first visible item
     * @param scrollOffset The scroll offset in pixels (unused in current implementation)
     * @param itemHeightPx The height of each item in pixels (unused in current implementation)
     * @param visibleItemsCount The number of items visible on screen
     * @return The range of item indices that should be rendered
     */
    override fun calculateVisibleRange(
        firstVisibleIndex: Int,
        scrollOffset: Float,
        itemHeightPx: Float,
        visibleItemsCount: Int
    ): IntRange {
        // Add buffer before and after visible range
        val startIndex = (firstVisibleIndex - VISIBLE_RANGE_BUFFER_BEFORE).coerceAtLeast(0)
        val endIndex = firstVisibleIndex + visibleItemsCount + VISIBLE_RANGE_BUFFER_AFTER
        return startIndex..endIndex
    }
    
    /**
     * Checks if an item is within the visible range.
     * 
     * @param itemIndex The index of the item to check
     * @param visibleRange The range of visible items
     * @return true if the item is visible, false otherwise
     */
    override fun isItemVisible(
        itemIndex: Int,
        visibleRange: IntRange
    ): Boolean = itemIndex in visibleRange
}
