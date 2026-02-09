// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.cache

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit

/**
 * Cache key for text measurement results.
 * 
 * Uniquely identifies a text measurement by its content and style properties.
 */
data class CacheKey(
    val text: String,
    val fontSize: TextUnit,
    val fontWeight: FontWeight?,
    val fontFamily: FontFamily?
)
