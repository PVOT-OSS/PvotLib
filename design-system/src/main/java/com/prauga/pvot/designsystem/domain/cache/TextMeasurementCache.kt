// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.cache

import android.util.Log
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
 * @param enableDebugLogging Whether to enable debug logging for cache operations
 */
class TextMeasurementCache(
    private val maxSize: Int = 100,
    private val enableDebugLogging: Boolean = false
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
        
        val cached = cache.get(key)
        if (cached != null) {
            if (enableDebugLogging) {
                Log.d("DesignSystem", "Text measurement cache hit for: $text")
            }
            return cached
        }
        
        if (enableDebugLogging) {
            Log.d("DesignSystem", "Text measurement cache miss for: $text, measuring...")
        }
        
        val result = measurer.measure(text, textStyle)
        cache.put(key, result)
        return result
    }
    
    override fun invalidate(text: String) {
        val keysToRemove = cache.snapshot().keys.filter { it.text == text }
        keysToRemove.forEach { cache.remove(it) }
        
        if (enableDebugLogging && keysToRemove.isNotEmpty()) {
            Log.d("DesignSystem", "Invalidated ${keysToRemove.size} cache entries for: $text")
        }
    }
    
    override fun clear() {
        val size = cache.size()
        cache.evictAll()
        
        if (enableDebugLogging) {
            Log.d("DesignSystem", "Cleared text measurement cache ($size entries)")
        }
    }
}
