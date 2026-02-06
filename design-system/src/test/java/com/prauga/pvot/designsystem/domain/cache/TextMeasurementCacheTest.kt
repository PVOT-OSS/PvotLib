// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.cache

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

/**
 * Property-based tests for TextMeasurementCache.
 * 
 * **Property 4: Text Measurement Caching**
 * For any label text and text style combination, measuring the same combination
 * multiple times should return the cached result after the first measurement.
 * 
 * **Property 5: Unique Label-Style Measurement**
 * For any set of tabs in a navigation bar, each unique label-style combination
 * should be measured exactly once during rendering.
 * 
 * **Property 6: Selective Cache Invalidation**
 * For any cached text measurement, changing a different label should not
 * invalidate the cache entry for the original label.
 * 
 * Note: These tests use mocking for the TextMeasurer since it requires Compose runtime.
 * Integration tests should verify actual measurement behavior.
 */
class TextMeasurementCacheTest {
    
    private lateinit var cache: TextMeasurementCache
    private lateinit var mockMeasurer: TextMeasurer
    private lateinit var mockResult: TextLayoutResult
    
    @Before
    fun setup() {
        cache = TextMeasurementCache(maxSize = 10)
        mockMeasurer = mock(TextMeasurer::class.java)
        mockResult = mock(TextLayoutResult::class.java)
        
        // Default behavior: return mock result
        `when`(mockMeasurer.measure(anyString(), any(TextStyle::class.java)))
            .thenReturn(mockResult)
    }
    
    // Property 4: Text Measurement Caching
    @Test
    fun `measure should cache result and return same instance on subsequent calls`() {
        val text = "Home"
        val style = TextStyle(fontSize = 16.sp)
        
        // First measurement
        val result1 = cache.measure(text, style, mockMeasurer)
        
        // Second measurement should return cached result
        val result2 = cache.measure(text, style, mockMeasurer)
        
        // Should return same instance (cached)
        assertSame(result1, result2)
        
        // Measurer should only be called once
        verify(mockMeasurer, times(1)).measure(text, style)
    }
    
    @Test
    fun `measure should cache multiple different text-style combinations`() {
        val text1 = "Home"
        val text2 = "Settings"
        val style1 = TextStyle(fontSize = 16.sp)
        val style2 = TextStyle(fontSize = 18.sp)
        
        val mockResult1 = mock(TextLayoutResult::class.java)
        val mockResult2 = mock(TextLayoutResult::class.java)
        
        `when`(mockMeasurer.measure(text1, style1)).thenReturn(mockResult1)
        `when`(mockMeasurer.measure(text2, style2)).thenReturn(mockResult2)
        
        // Measure different combinations
        val result1a = cache.measure(text1, style1, mockMeasurer)
        val result2a = cache.measure(text2, style2, mockMeasurer)
        
        // Measure again
        val result1b = cache.measure(text1, style1, mockMeasurer)
        val result2b = cache.measure(text2, style2, mockMeasurer)
        
        // Each should return cached result
        assertSame(result1a, result1b)
        assertSame(result2a, result2b)
        
        // Each unique combination measured once
        verify(mockMeasurer, times(1)).measure(text1, style1)
        verify(mockMeasurer, times(1)).measure(text2, style2)
    }
    
    // Property 5: Unique Label-Style Measurement
    @Test
    fun `same text with different styles should be measured separately`() {
        val text = "Home"
        val style1 = TextStyle(fontSize = 16.sp)
        val style2 = TextStyle(fontSize = 18.sp)
        
        val mockResult1 = mock(TextLayoutResult::class.java)
        val mockResult2 = mock(TextLayoutResult::class.java)
        
        `when`(mockMeasurer.measure(text, style1)).thenReturn(mockResult1)
        `when`(mockMeasurer.measure(text, style2)).thenReturn(mockResult2)
        
        val result1 = cache.measure(text, style1, mockMeasurer)
        val result2 = cache.measure(text, style2, mockMeasurer)
        
        // Different styles should produce different cached results
        assertNotSame(result1, result2)
        
        // Each style measured once
        verify(mockMeasurer, times(1)).measure(text, style1)
        verify(mockMeasurer, times(1)).measure(text, style2)
    }
    
    @Test
    fun `different text with same style should be measured separately`() {
        val text1 = "Home"
        val text2 = "Settings"
        val style = TextStyle(fontSize = 16.sp)
        
        val mockResult1 = mock(TextLayoutResult::class.java)
        val mockResult2 = mock(TextLayoutResult::class.java)
        
        `when`(mockMeasurer.measure(text1, style)).thenReturn(mockResult1)
        `when`(mockMeasurer.measure(text2, style)).thenReturn(mockResult2)
        
        val result1 = cache.measure(text1, style, mockMeasurer)
        val result2 = cache.measure(text2, style, mockMeasurer)
        
        // Different text should produce different cached results
        assertNotSame(result1, result2)
        
        // Each text measured once
        verify(mockMeasurer, times(1)).measure(text1, style)
        verify(mockMeasurer, times(1)).measure(text2, style)
    }
    
    @Test
    fun `cache should distinguish between different font weights`() {
        val text = "Home"
        val styleNormal = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal)
        val styleBold = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
        
        val mockResult1 = mock(TextLayoutResult::class.java)
        val mockResult2 = mock(TextLayoutResult::class.java)
        
        `when`(mockMeasurer.measure(text, styleNormal)).thenReturn(mockResult1)
        `when`(mockMeasurer.measure(text, styleBold)).thenReturn(mockResult2)
        
        val result1 = cache.measure(text, styleNormal, mockMeasurer)
        val result2 = cache.measure(text, styleBold, mockMeasurer)
        
        // Different font weights should be cached separately
        assertNotSame(result1, result2)
        
        verify(mockMeasurer, times(1)).measure(text, styleNormal)
        verify(mockMeasurer, times(1)).measure(text, styleBold)
    }
    
    @Test
    fun `cache should distinguish between different font families`() {
        val text = "Home"
        val styleDefault = TextStyle(fontSize = 16.sp, fontFamily = FontFamily.Default)
        val styleMonospace = TextStyle(fontSize = 16.sp, fontFamily = FontFamily.Monospace)
        
        val mockResult1 = mock(TextLayoutResult::class.java)
        val mockResult2 = mock(TextLayoutResult::class.java)
        
        `when`(mockMeasurer.measure(text, styleDefault)).thenReturn(mockResult1)
        `when`(mockMeasurer.measure(text, styleMonospace)).thenReturn(mockResult2)
        
        val result1 = cache.measure(text, styleDefault, mockMeasurer)
        val result2 = cache.measure(text, styleMonospace, mockMeasurer)
        
        // Different font families should be cached separately
        assertNotSame(result1, result2)
        
        verify(mockMeasurer, times(1)).measure(text, styleDefault)
        verify(mockMeasurer, times(1)).measure(text, styleMonospace)
    }
    
    // Property 6: Selective Cache Invalidation
    @Test
    fun `invalidate should only remove entries for specified text`() {
        val text1 = "Home"
        val text2 = "Settings"
        val style = TextStyle(fontSize = 16.sp)
        
        val mockResult1 = mock(TextLayoutResult::class.java)
        val mockResult2 = mock(TextLayoutResult::class.java)
        
        `when`(mockMeasurer.measure(text1, style)).thenReturn(mockResult1)
        `when`(mockMeasurer.measure(text2, style)).thenReturn(mockResult2)
        
        // Measure both
        cache.measure(text1, style, mockMeasurer)
        cache.measure(text2, style, mockMeasurer)
        
        // Invalidate only text1
        cache.invalidate(text1)
        
        // text1 should be re-measured
        cache.measure(text1, style, mockMeasurer)
        verify(mockMeasurer, times(2)).measure(text1, style)
        
        // text2 should still be cached
        cache.measure(text2, style, mockMeasurer)
        verify(mockMeasurer, times(1)).measure(text2, style)
    }
    
    @Test
    fun `invalidate should remove all style variations of specified text`() {
        val text = "Home"
        val style1 = TextStyle(fontSize = 16.sp)
        val style2 = TextStyle(fontSize = 18.sp)
        
        val mockResult1 = mock(TextLayoutResult::class.java)
        val mockResult2 = mock(TextLayoutResult::class.java)
        
        `when`(mockMeasurer.measure(text, style1)).thenReturn(mockResult1)
        `when`(mockMeasurer.measure(text, style2)).thenReturn(mockResult2)
        
        // Measure with both styles
        cache.measure(text, style1, mockMeasurer)
        cache.measure(text, style2, mockMeasurer)
        
        // Invalidate the text
        cache.invalidate(text)
        
        // Both styles should be re-measured
        cache.measure(text, style1, mockMeasurer)
        cache.measure(text, style2, mockMeasurer)
        
        verify(mockMeasurer, times(2)).measure(text, style1)
        verify(mockMeasurer, times(2)).measure(text, style2)
    }
    
    @Test
    fun `clear should remove all cached entries`() {
        val text1 = "Home"
        val text2 = "Settings"
        val style = TextStyle(fontSize = 16.sp)
        
        val mockResult1 = mock(TextLayoutResult::class.java)
        val mockResult2 = mock(TextLayoutResult::class.java)
        
        `when`(mockMeasurer.measure(text1, style)).thenReturn(mockResult1)
        `when`(mockMeasurer.measure(text2, style)).thenReturn(mockResult2)
        
        // Measure both
        cache.measure(text1, style, mockMeasurer)
        cache.measure(text2, style, mockMeasurer)
        
        // Clear cache
        cache.clear()
        
        // Both should be re-measured
        cache.measure(text1, style, mockMeasurer)
        cache.measure(text2, style, mockMeasurer)
        
        verify(mockMeasurer, times(2)).measure(text1, style)
        verify(mockMeasurer, times(2)).measure(text2, style)
    }
    
    @Test
    fun `cache should respect max size limit`() {
        val smallCache = TextMeasurementCache(maxSize = 2)
        val style = TextStyle(fontSize = 16.sp)
        
        val mockResult1 = mock(TextLayoutResult::class.java)
        val mockResult2 = mock(TextLayoutResult::class.java)
        val mockResult3 = mock(TextLayoutResult::class.java)
        
        `when`(mockMeasurer.measure("Text1", style)).thenReturn(mockResult1)
        `when`(mockMeasurer.measure("Text2", style)).thenReturn(mockResult2)
        `when`(mockMeasurer.measure("Text3", style)).thenReturn(mockResult3)
        
        // Fill cache
        smallCache.measure("Text1", style, mockMeasurer)
        smallCache.measure("Text2", style, mockMeasurer)
        
        // Add third item (should evict oldest)
        smallCache.measure("Text3", style, mockMeasurer)
        
        // Text1 should have been evicted (LRU), so it will be re-measured
        smallCache.measure("Text1", style, mockMeasurer)
        verify(mockMeasurer, times(2)).measure("Text1", style)
        
        // Text2 and Text3 should still be cached
        smallCache.measure("Text2", style, mockMeasurer)
        smallCache.measure("Text3", style, mockMeasurer)
        verify(mockMeasurer, times(1)).measure("Text2", style)
        verify(mockMeasurer, times(1)).measure("Text3", style)
    }
}
