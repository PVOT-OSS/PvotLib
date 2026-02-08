// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.examples

import com.prauga.pvot.designsystem.domain.transform.TransformEngine
import com.prauga.pvot.designsystem.testutil.PropertyTestConfig
import com.prauga.pvot.designsystem.testutil.PropertyTestGenerators
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.floats.shouldBeBetween
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

/**
 * Example property-based tests for TransformEngine.
 * 
 * This example demonstrates:
 * - How to use Kotest property testing with custom generators
 * - How to validate universal properties across random inputs
 * - How to reference design document properties
 * - How to configure iteration counts
 * 
 * Property tests verify that certain properties hold true for ALL valid inputs,
 * not just specific examples. This catches edge cases that unit tests might miss.
 */
class TransformEnginePropertyTestExample : StringSpec({
    
    val engine = TransformEngine()
    
    /**
     * Property 11: Pure Function Transformations
     * 
     * Feature: design-system-optimization
     * Property: For any data transformation function in the domain layer,
     * calling it with the same inputs multiple times should always produce
     * the same output (referential transparency).
     * 
     */
    "Property 11: TransformEngine calculations are pure functions".config(
        invocations = PropertyTestConfig.DEFAULT_ITERATIONS
    ) {
        checkAll(
            PropertyTestGenerators.itemIndex(),
            PropertyTestGenerators.itemIndex(),
            PropertyTestGenerators.scrollOffset(),
            PropertyTestGenerators.itemHeightPx(),
            PropertyTestGenerators.visibleItemCount()
        ) { itemIndex, firstVisibleIndex, scrollOffset, itemHeightPx, visibleCount ->
            val halfVisibleItems = visibleCount / 2f
            
            // Calculate transform twice with same inputs
            val result1 = engine.calculateTransform(
                itemIndex = itemIndex,
                firstVisibleIndex = firstVisibleIndex,
                scrollOffset = scrollOffset,
                itemHeightPx = itemHeightPx,
                halfVisibleItems = halfVisibleItems
            )
            
            val result2 = engine.calculateTransform(
                itemIndex = itemIndex,
                firstVisibleIndex = firstVisibleIndex,
                scrollOffset = scrollOffset,
                itemHeightPx = itemHeightPx,
                halfVisibleItems = halfVisibleItems
            )
            
            // Results should be identical (pure function)
            result1 shouldBe result2
        }
    }
    
    /**
     * Property: Transform values are always within valid ranges
     * 
     * This property ensures that no matter what inputs are provided,
     * the transform values stay within physically meaningful bounds.
     */
    "Transform values are always within valid ranges".config(
        invocations = PropertyTestConfig.DEFAULT_ITERATIONS
    ) {
        checkAll(
            PropertyTestGenerators.itemIndex(),
            PropertyTestGenerators.itemIndex(),
            PropertyTestGenerators.scrollOffset(),
            PropertyTestGenerators.itemHeightPx(),
            PropertyTestGenerators.visibleItemCount()
        ) { itemIndex, firstVisibleIndex, scrollOffset, itemHeightPx, visibleCount ->
            val halfVisibleItems = visibleCount / 2f
            
            val transform = engine.calculateTransform(
                itemIndex = itemIndex,
                firstVisibleIndex = firstVisibleIndex,
                scrollOffset = scrollOffset,
                itemHeightPx = itemHeightPx,
                halfVisibleItems = halfVisibleItems
            )
            
            // Rotation should be within configured bounds
            transform.rotationX.shouldBeBetween(-60f, 60f, 0.01f)
            
            // Scale should be between min and max
            transform.scale.shouldBeBetween(0.7f, 1f, 0.01f)
            
            // Alpha should be between min and max
            transform.alpha.shouldBeBetween(0.3f, 1f, 0.01f)
        }
    }
    
    /**
     * Property: Center item has identity transform
     * 
     * When an item is at the center position, it should have no rotation,
     * full scale, and full alpha.
     */
    "Center item has identity transform".config(
        invocations = PropertyTestConfig.DEFAULT_ITERATIONS
    ) {
        checkAll(
            PropertyTestGenerators.itemIndex(),
            PropertyTestGenerators.itemHeightPx(),
            PropertyTestGenerators.visibleItemCount()
        ) { centerIndex, itemHeightPx, visibleCount ->
            val halfVisibleItems = visibleCount / 2f
            
            // Item at center with no scroll offset
            val transform = engine.calculateTransform(
                itemIndex = centerIndex,
                firstVisibleIndex = centerIndex,
                scrollOffset = 0f,
                itemHeightPx = itemHeightPx,
                halfVisibleItems = halfVisibleItems
            )
            
            // Center item should have identity transform
            transform.rotationX.shouldBeBetween(-0.01f, 0.01f, 0.01f)
            transform.scale.shouldBeBetween(0.99f, 1.01f, 0.01f)
            transform.alpha.shouldBeBetween(0.99f, 1.01f, 0.01f)
        }
    }
})
