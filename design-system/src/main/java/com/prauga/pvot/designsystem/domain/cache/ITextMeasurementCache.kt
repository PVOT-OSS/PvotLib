// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.cache

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle

/**
 * Caches text measurement results.
 * Thread-safe for concurrent access.
 */
interface ITextMeasurementCache {
    /**
     * Measures text with the given style, using cache if available.
     * 
     * @param text The text to measure
     * @param textStyle The style to apply
     * @param measurer The text measurer to use for new measurements
     * @return The text layout result (cached or newly measured)
     */
    fun measure(
        text: String,
        textStyle: TextStyle,
        measurer: TextMeasurer
    ): TextLayoutResult
    
    /**
     * Invalidates all cached measurements for the given text.
     * 
     * @param text The text whose measurements should be invalidated
     */
    fun invalidate(text: String)
    
    /**
     * Clears all cached measurements.
     */
    fun clear()
}
