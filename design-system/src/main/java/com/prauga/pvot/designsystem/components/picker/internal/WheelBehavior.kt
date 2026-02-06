// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.picker.internal

/**
 * Behavioral configuration for wheel picker.
 *
 * @param enableHapticFeedback Whether to provide haptic feedback on value change
 * @param enable3DEffect Whether to enable 3D transformation effects
 * @param maxRotationDegrees Maximum rotation angle for 3D effect
 * @param minScale Minimum scale factor for distant items
 * @param minAlpha Minimum alpha (transparency) for distant items
 */
internal data class WheelBehavior(
    val enableHapticFeedback: Boolean = true,
    val enable3DEffect: Boolean = true,
    val maxRotationDegrees: Float = 60f,
    val minScale: Float = 0.7f,
    val minAlpha: Float = 0.3f
)
