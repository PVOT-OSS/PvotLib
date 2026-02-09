// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.transform

import android.util.Log
import kotlin.math.abs

/**
 * Calculates 3D transformations for wheel items.
 * Pure Kotlin implementation with pre-calculated constants for performance.
 * 
 * @property maxRotationDegrees Maximum rotation angle in degrees (default: 60°)
 * @property minScale Minimum scale factor for items far from center (default: 0.7)
 * @property minAlpha Minimum alpha/opacity for items far from center (default: 0.3)
 * @property enableDebugLogging Whether to enable debug logging for calculations
 */
class TransformEngine(
    private val maxRotationDegrees: Float = 60f,
    private val minScale: Float = 0.7f,
    private val minAlpha: Float = 0.3f,
    private val enableDebugLogging: Boolean = false
) : ITransformEngine {
    
    // Pre-calculated constants to avoid repeated calculations
    private val scaleRange = 1f - minScale
    private val alphaRange = 1f - minAlpha
    
    /**
     * Calculates the 3D transformation for a wheel item.
     * 
     * The transformation is based on the item's distance from the center of the visible area.
     * Items at the center have no rotation, full scale, and full opacity.
     * Items further from center are rotated, scaled down, and made more transparent.
     * 
     * @param itemIndex The index of the item in the list
     * @param firstVisibleIndex The index of the first visible item
     * @param scrollOffset The scroll offset in pixels
     * @param itemHeightPx The height of each item in pixels
     * @param halfVisibleItems Half the number of visible items (center point)
     * @return The calculated transformation for the item
     */
    override fun calculateTransform(
        itemIndex: Int,
        firstVisibleIndex: Int,
        scrollOffset: Float,
        itemHeightPx: Float,
        halfVisibleItems: Float
    ): ItemTransform {
        // Calculate distance from center in item units
        val distanceFromCenter = (itemIndex - firstVisibleIndex) - (scrollOffset / itemHeightPx)
        
        // Normalize distance to range [-1, 1] where 0 is center
        val normalizedDistance = (distanceFromCenter / halfVisibleItems).coerceIn(-1f, 1f)
        
        // Use absolute distance for scale and alpha (symmetric effect)
        val absDistance = abs(normalizedDistance)
        
        val transform = ItemTransform(
            rotationX = normalizedDistance * maxRotationDegrees,
            scale = 1f - (absDistance * scaleRange),
            alpha = 1f - (absDistance * alphaRange)
        )
        
        // Debug logging when enabled
        if (enableDebugLogging) {
            Log.d(
                "DesignSystem",
                "Transform calculated for item $itemIndex: " +
                "rotation=${transform.rotationX}, scale=${transform.scale}, alpha=${transform.alpha}"
            )
        }
        
        return transform
    }
}
