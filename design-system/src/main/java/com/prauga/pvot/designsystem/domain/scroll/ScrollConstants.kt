// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.scroll

/**
 * Constants for scroll calculations.
 * Centralizes buffer and range calculation values.
 *
 * These constants define how many items before and after the visible range
 * should be rendered to ensure smooth scrolling and support 3D effects that
 * may extend beyond the strictly visible area.
 */
internal object ScrollConstants {
    /**
     * Number of items to render before the first visible item.
     * Provides smooth rendering during upward scrolling and supports 3D effects.
     * Valid range: 0 to 3
     */
    const val VISIBLE_RANGE_BUFFER_BEFORE = 1
    
    /**
     * Number of items to render after the last visible item.
     * Provides smooth rendering during downward scrolling and supports 3D effects.
     * Valid range: 0 to 3
     */
    const val VISIBLE_RANGE_BUFFER_AFTER = 1
}
