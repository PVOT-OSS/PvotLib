// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.scroll

/**
 * Interface for calculating scroll-related metrics for lazy lists.
 * Handles visibility calculations and item range determination.
 */
interface IScrollCalculator {
    /**
     * Calculates the range of items that should be considered visible
     * based on the current scroll position.
     * 
     * Includes a buffer of items outside the strictly visible area
     * to ensure smooth rendering during scrolling.
     * 
     * @param firstVisibleIndex The index of the first visible item
     * @param scrollOffset The scroll offset in pixels
     * @param itemHeightPx The height of each item in pixels
     * @param visibleItemsCount The number of items visible on screen
     * @return The range of item indices that should be rendered
     */
    fun calculateVisibleRange(
        firstVisibleIndex: Int,
        scrollOffset: Float,
        itemHeightPx: Float,
        visibleItemsCount: Int
    ): IntRange
    
    /**
     * Checks if an item at the given index is within the visible range.
     * 
     * @param itemIndex The index of the item to check
     * @param visibleRange The range of visible items
     * @return true if the item is visible, false otherwise
     */
    fun isItemVisible(
        itemIndex: Int,
        visibleRange: IntRange
    ): Boolean
}
