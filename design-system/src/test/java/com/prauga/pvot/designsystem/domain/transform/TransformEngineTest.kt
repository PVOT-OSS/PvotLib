// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.transform

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.math.abs

/**
 * Property-based and unit tests for TransformEngine.
 * 
 * **Property 1: Visible Item Transform Calculation**
 * For any wheel configuration and scroll position, only items within the visible range
 * plus one buffer item on each side should have their 3D transformations calculated.
 * 
 * **Property 11: Pure Function Transformations**
 * For any data transformation function in the domain layer, calling it with the same
 * inputs multiple times should always produce the same output (referential transparency).
 */
class TransformEngineTest {
    
    private lateinit var engine: TransformEngine
    
    @Before
    fun setup() {
        engine = TransformEngine()
    }
    
    // Property 11: Pure Function Transformations
    @Test
    fun `calculateTransform should return same result for same inputs - referential transparency`() {
        val itemIndex = 5
        val firstVisibleIndex = 3
        val scrollOffset = 120f
        val itemHeightPx = 40f
        val halfVisibleItems = 2.5f
        
        // Call multiple times with same inputs
        val result1 = engine.calculateTransform(
            itemIndex, firstVisibleIndex, scrollOffset, itemHeightPx, halfVisibleItems
        )
        val result2 = engine.calculateTransform(
            itemIndex, firstVisibleIndex, scrollOffset, itemHeightPx, halfVisibleItems
        )
        val result3 = engine.calculateTransform(
            itemIndex, firstVisibleIndex, scrollOffset, itemHeightPx, halfVisibleItems
        )
        
        // All results should be identical
        assertEquals(result1.rotationX, result2.rotationX, 0.001f)
        assertEquals(result1.scale, result2.scale, 0.001f)
        assertEquals(result1.alpha, result2.alpha, 0.001f)
        
        assertEquals(result1.rotationX, result3.rotationX, 0.001f)
        assertEquals(result1.scale, result3.scale, 0.001f)
        assertEquals(result1.alpha, result3.alpha, 0.001f)
    }
    
    @Test
    fun `calculateTransform should be pure function across multiple instances`() {
        val engine1 = TransformEngine()
        val engine2 = TransformEngine()
        
        val itemIndex = 7
        val firstVisibleIndex = 5
        val scrollOffset = 80f
        val itemHeightPx = 40f
        val halfVisibleItems = 2.5f
        
        val result1 = engine1.calculateTransform(
            itemIndex, firstVisibleIndex, scrollOffset, itemHeightPx, halfVisibleItems
        )
        val result2 = engine2.calculateTransform(
            itemIndex, firstVisibleIndex, scrollOffset, itemHeightPx, halfVisibleItems
        )
        
        // Different instances should produce same results
        assertEquals(result1.rotationX, result2.rotationX, 0.001f)
        assertEquals(result1.scale, result2.scale, 0.001f)
        assertEquals(result1.alpha, result2.alpha, 0.001f)
    }
    
    // Property 1: Visible Item Transform Calculation
    @Test
    fun `center item should have identity transform`() {
        val firstVisibleIndex = 5
        val centerIndex = 5
        val scrollOffset = 0f
        val itemHeightPx = 40f
        val halfVisibleItems = 2.5f
        
        val transform = engine.calculateTransform(
            centerIndex, firstVisibleIndex, scrollOffset, itemHeightPx, halfVisibleItems
        )
        
        // Center item should have no rotation, full scale, full alpha
        assertEquals(0f, transform.rotationX, 0.001f)
        assertEquals(1f, transform.scale, 0.001f)
        assertEquals(1f, transform.alpha, 0.001f)
    }
    
    @Test
    fun `items far from center should have maximum transformation`() {
        val firstVisibleIndex = 5
        val itemHeightPx = 40f
        val halfVisibleItems = 2.5f
        
        // Item far above center
        val farAbove = engine.calculateTransform(
            0, firstVisibleIndex, 0f, itemHeightPx, halfVisibleItems
        )
        
        // Item far below center
        val farBelow = engine.calculateTransform(
            10, firstVisibleIndex, 0f, itemHeightPx, halfVisibleItems
        )
        
        // Both should have reduced scale and alpha
        assertTrue(farAbove.scale < 1f)
        assertTrue(farAbove.alpha < 1f)
        assertTrue(farBelow.scale < 1f)
        assertTrue(farBelow.alpha < 1f)
        
        // Rotation should be opposite directions
        assertTrue(farAbove.rotationX < 0)
        assertTrue(farBelow.rotationX > 0)
    }
    
    @Test
    fun `transform should be symmetric around center`() {
        val firstVisibleIndex = 5
        val scrollOffset = 0f
        val itemHeightPx = 40f
        val halfVisibleItems = 2.5f
        
        // Items equidistant from center
        val above = engine.calculateTransform(
            3, firstVisibleIndex, scrollOffset, itemHeightPx, halfVisibleItems
        )
        val below = engine.calculateTransform(
            7, firstVisibleIndex, scrollOffset, itemHeightPx, halfVisibleItems
        )
        
        // Scale and alpha should be equal (symmetric)
        assertEquals(above.scale, below.scale, 0.001f)
        assertEquals(above.alpha, below.alpha, 0.001f)
        
        // Rotation should be opposite
        assertEquals(above.rotationX, -below.rotationX, 0.001f)
    }
    
    @Test
    fun `transform values should be within valid ranges`() {
        val engine = TransformEngine(
            maxRotationDegrees = 60f,
            minScale = 0.7f,
            minAlpha = 0.3f
        )
        
        // Test various positions
        for (itemIndex in 0..20) {
            val transform = engine.calculateTransform(
                itemIndex, 10, 0f, 40f, 2.5f
            )
            
            // Rotation should be within [-60, 60]
            assertTrue(transform.rotationX >= -60f)
            assertTrue(transform.rotationX <= 60f)
            
            // Scale should be within [0.7, 1.0]
            assertTrue(transform.scale >= 0.7f)
            assertTrue(transform.scale <= 1f)
            
            // Alpha should be within [0.3, 1.0]
            assertTrue(transform.alpha >= 0.3f)
            assertTrue(transform.alpha <= 1f)
        }
    }
    
    @Test
    fun `scroll offset should affect transform calculation`() {
        val itemIndex = 5
        val firstVisibleIndex = 5
        val itemHeightPx = 40f
        val halfVisibleItems = 2.5f
        
        val noScroll = engine.calculateTransform(
            itemIndex, firstVisibleIndex, 0f, itemHeightPx, halfVisibleItems
        )
        
        val withScroll = engine.calculateTransform(
            itemIndex, firstVisibleIndex, 20f, itemHeightPx, halfVisibleItems
        )
        
        // Scroll should change the transform
        assertNotEquals(noScroll.rotationX, withScroll.rotationX, 0.001f)
    }
    
    @Test
    fun `custom parameters should affect transform ranges`() {
        val customEngine = TransformEngine(
            maxRotationDegrees = 45f,
            minScale = 0.8f,
            minAlpha = 0.5f
        )
        
        // Item far from center
        val transform = customEngine.calculateTransform(
            0, 5, 0f, 40f, 2.5f
        )
        
        // Should respect custom min values
        assertTrue(transform.scale >= 0.8f)
        assertTrue(transform.alpha >= 0.5f)
        assertTrue(abs(transform.rotationX) <= 45f)
    }
}
