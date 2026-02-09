// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.picker.internal

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Constants for WheelEngine component.
 * Centralizes magic numbers for better maintainability.
 *
 * These constants define the visual appearance and behavior of the wheel picker,
 * including text sizes, spacing, and 3D effect parameters.
 */
internal object WheelConstants {
    /**
     * Corner radius for the selection indicator background.
     * Valid range: 0.dp to itemHeight/2
     */
    val SELECTION_INDICATOR_CORNER_RADIUS = 20.dp
    
    /**
     * Font size for the main item text.
     * Valid range: 12.sp to 48.sp
     */
    val ITEM_TEXT_SIZE = 28.sp
    
    /**
     * Font size for the suffix text (smaller than main text).
     * Valid range: 8.sp to 24.sp
     */
    val SUFFIX_TEXT_SIZE = 16.sp
    
    /**
     * Spacing between main text and suffix text.
     * Valid range: 0.dp to 8.dp
     */
    val TEXT_SUFFIX_SPACING = 4.dp
    
    /**
     * Camera distance multiplier for 3D perspective effect.
     * Higher values = less perspective distortion.
     * Valid range: 8f to 16f
     */
    const val CAMERA_DISTANCE_MULTIPLIER = 12f
}
