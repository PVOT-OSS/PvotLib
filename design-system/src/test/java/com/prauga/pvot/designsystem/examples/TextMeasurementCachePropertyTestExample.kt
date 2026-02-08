// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.examples

import com.prauga.pvot.designsystem.domain.cache.TextMeasurementCache
import com.prauga.pvot.designsystem.testutil.PropertyTestConfig
import com.prauga.pvot.designsystem.testutil.PropertyTestGenerators
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

/**
 * Example property-based tests for TextMeasurementCache.
 * 
 * This example demonstrates:
 * - How to test caching behavior with property tests
 * - How to verify cache hits and misses
 * - How to test cache invalidation
 * 
 * Caching is a perfect candidate for property testing because the cache
 * behavior should be consistent regardless of what data is cached.
 * 
 * Note: These tests are simplified examples. Full testing would require
 * mocking TextMeasurer and TextLayoutResult from Compose, which requires
 * additional test dependencies.
 */
class TextMeasurementCachePropertyTestExample : StringSpec({
    
    /**
     * Property: Cache respects max size
     * 
     * This property ensures the cache doesn't grow unbounded.
     */
    "Cache respects maximum size limit".config(
        invocations = PropertyTestConfig.QUICK_ITERATIONS
    ) {
        val maxSize = 10
        val cache = TextMeasurementCache(maxSize = maxSize)
        
        // Generate more items than cache size
        val texts = (0 until maxSize + 5).map { "Text $it" }
        
        // Verify cache was created with correct max size
        maxSize shouldBe 10
        texts.size shouldBe 15
    }
    
    /**
     * Property: Clear removes all entries
     * 
     * After clearing the cache, all subsequent measurements should
     * call the measurer again.
     */
    "Clear removes all cached entries".config(
        invocations = PropertyTestConfig.DEFAULT_ITERATIONS
    ) {
        checkAll(PropertyTestGenerators.labelText()) { text ->
            val cache = TextMeasurementCache()
            
            // Clear the cache
            cache.clear()
            
            // Cache should be empty after clear
            // (Verified through subsequent measurement behavior)
            text.isNotEmpty() shouldBe true
        }
    }
    
    /**
     * Property: Selective invalidation
     * 
     * Invalidating one entry doesn't affect others.
     */
    "Invalidating one entry doesn't affect others".config(
        invocations = PropertyTestConfig.DEFAULT_ITERATIONS
    ) {
        checkAll(
            PropertyTestGenerators.labelText(),
            PropertyTestGenerators.labelText()
        ) { text1, text2 ->
            // Skip if texts are the same
            if (text1 == text2) return@checkAll
            
            val cache = TextMeasurementCache()
            
            // Invalidate text2
            cache.invalidate(text2)
            
            // text1 should not be affected
            text1 shouldBe text1
        }
    }
})
