// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.transform

/**
 * Interface for calculating 3D transformations for wheel items.
 * Pure function interface - no Compose dependencies.
 */
interface ITransformEngine {
    /**
     * Calculates the 3D transformation for a wheel item based on its position
     * relative to the center of the visible area.
     * 
     * @param itemIndex The index of the item in the list
     * @param firstVisibleIndex The index of the first visible item
     * @param scrollOffset The scroll offset in pixels
     * @param itemHeightPx The height of each item in pixels
     * @param halfVisibleItems Half the number of visible items (center point)
     * @return The calculated transformation for the item
     */
    fun calculateTransform(
        itemIndex: Int,
        firstVisibleIndex: Int,
        scrollOffset: Float,
        itemHeightPx: Float,
        halfVisibleItems: Float
    ): ItemTransform
}
