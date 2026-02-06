// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.transform

/**
 * Represents a 3D transformation for a wheel item.
 * 
 * @property rotationX The rotation around the X-axis in degrees
 * @property scale The scale factor for the item (1.0 = normal size)
 * @property alpha The opacity of the item (0.0 = transparent, 1.0 = opaque)
 */
data class ItemTransform(
    val rotationX: Float,
    val scale: Float,
    val alpha: Float
) {
    companion object {
        /**
         * Identity transformation with no rotation, normal scale, and full opacity.
         */
        val Identity = ItemTransform(
            rotationX = 0f,
            scale = 1f,
            alpha = 1f
        )
    }
}
