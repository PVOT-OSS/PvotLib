// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.cache

import android.util.LruCache
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle

/**
 * LRU cache implementation for text measurement results.
 * 
 * Caches text measurements to avoid repeated calculations for the same text-style combinations.
 * Thread-safe for concurrent access.
 * 
 * @param maxSize Maximum number of entries to cache (default: 100)
 */
class TextMeasurementCache(
    private val maxSize: Int = 100
) : ITextMeasurementCache {
    
    private val cache = LruCache<CacheKey, TextLayoutResult>(maxSize)
    
    override fun measure(
        text: String,
        textStyle: TextStyle,
        measurer: TextMeasurer
    ): TextLayoutResult {
        val key = CacheKey(
            text = text,
            fontSize = textStyle.fontSize,
            fontWeight = textStyle.fontWeight,
            fontFamily = textStyle.fontFamily
        )
        
        return cache.get(key) ?: run {
            val result = measurer.measure(text, textStyle)
            cache.put(key, result)
            result
        }
    }
    
    override fun invalidate(text: String) {
        cache.snapshot().keys
            .filter { it.text == text }
            .forEach { cache.remove(it) }
    }
    
    override fun clear() {
        cache.evictAll()
    }
}
