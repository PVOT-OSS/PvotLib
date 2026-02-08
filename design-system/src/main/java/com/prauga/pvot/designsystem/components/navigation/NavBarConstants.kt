// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.navigation

import androidx.compose.ui.unit.dp

/**
 * Constants for PvotNavBar component.
 * Centralizes magic numbers for better maintainability.
 *
 * These constants define the spacing, elevation, and visual properties
 * of the navigation bar container.
 */
internal object NavBarConstants {
    /**
     * Vertical padding around the navigation bar container.
     * Valid range: 0.dp to 24.dp
     */
    val CONTAINER_VERTICAL_PADDING = 12.dp
    
    /**
     * Shadow elevation for the navigation bar surface.
     * Valid range: 0.dp to 32.dp
     */
    val SURFACE_SHADOW_ELEVATION = 18.dp
    
    /**
     * Tonal elevation for the navigation bar surface.
     * Set to 0 for flat appearance.
     */
    val SURFACE_TONAL_ELEVATION = 0.dp
    
    /**
     * Shadow elevation for the navigation bar surface (Material 3).
     * Set to 0 to use custom shadow implementation.
     */
    val SURFACE_SHADOW_ELEVATION_M3 = 0.dp
}

/**
 * Constants for PillNavItem component.
 * Centralizes animation and interaction values.
 *
 * These constants define the timing and visual properties of tab item
 * animations and interactions, including expansion/collapse animations
 * and ripple effects.
 */
internal object PillNavItemConstants {
    /**
     * Duration for width expansion/collapse animation in milliseconds.
     * Valid range: 100 to 500
     */
    const val WIDTH_ANIMATION_DURATION_MS = 240
    
    /**
     * Duration for scale animation in milliseconds.
     * Valid range: 100 to 400
     */
    const val SCALE_ANIMATION_DURATION_MS = 200
    
    /**
     * Scale factor for selected items (normal size).
     */
    const val SELECTED_SCALE = 1f
    
    /**
     * Scale factor for unselected items (slightly smaller).
     * Valid range: 0.9f to 1.0f
     */
    const val UNSELECTED_SCALE = 0.98f
    
    /**
     * Alpha value for ripple effect overlay.
     * Valid range: 0f to 1f
     */
    const val RIPPLE_ALPHA = 0.25f
}
