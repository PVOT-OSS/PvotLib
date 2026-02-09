// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.testutil

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.prauga.pvot.designsystem.components.navigation.NavBarAppearance
import com.prauga.pvot.designsystem.components.navigation.NavBarBehavior
import com.prauga.pvot.designsystem.components.navigation.NavBarConfig
import com.prauga.pvot.designsystem.components.navigation.PvotTabItem
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string

/**
 * Custom Kotest property test generators for design system domain types.
 * Provides arbitraries for generating random valid test data.
 */
object PropertyTestGenerators {
    
    /**
     * Generates random valid scroll offsets.
     */
    fun scrollOffset(): Arb<Float> = Arb.float(min = 0f, max = 1000f)
    
    /**
     * Generates random valid item heights in pixels.
     */
    fun itemHeightPx(): Arb<Float> = Arb.float(min = 20f, max = 100f)
    
    /**
     * Generates random valid item indices.
     */
    fun itemIndex(): Arb<Int> = Arb.int(min = 0, max = 100)
    
    /**
     * Generates random valid visible item counts.
     */
    fun visibleItemCount(): Arb<Int> = Arb.int(min = 3, max = 11)
    
    /**
     * Generates random valid rotation degrees.
     */
    fun rotationDegrees(): Arb<Float> = Arb.float(min = -90f, max = 90f)
    
    /**
     * Generates random valid scale values.
     */
    fun scaleValue(): Arb<Float> = Arb.float(min = 0.5f, max = 1.5f)
    
    /**
     * Generates random valid alpha values.
     */
    fun alphaValue(): Arb<Float> = Arb.float(min = 0f, max = 1f)
    
    /**
     * Generates random valid text strings for labels.
     */
    fun labelText(): Arb<String> = Arb.string(minSize = 1, maxSize = 20)
    
    /**
     * Generates random valid lists of integers for wheel values.
     */
    fun wheelValues(): Arb<List<Int>> = Arb.list(
        gen = Arb.int(min = 0, max = 100),
        range = 1..60
    )
    
    /**
     * Generates random valid NavBarConfig objects.
     */
    fun navBarConfig(): Arb<NavBarConfig> = arbitrary {
        val tabCount = Arb.int(min = 1, max = 5).bind()
        val tabs = (0 until tabCount).map { index ->
            PvotTabItem(
                iconRes = index,
                labelRes = index,
                contentDescriptionRes = index
            )
        }
        val selectedTab = Arb.int(min = 0, max = tabCount - 1).bind()
        
        NavBarConfig(
            tabs = tabs,
            selectedTab = selectedTab,
            appearance = NavBarAppearance(),
            behavior = NavBarBehavior()
        )
    }
    
    /**
     * Generates random animation IDs.
     */
    fun animationId(): Arb<String> = Arb.string(minSize = 5, maxSize = 20)
        .map { "anim_$it" }
    
    /**
     * Generates random component names for performance monitoring.
     */
    fun componentName(): Arb<String> = arbitrary {
        val components = listOf(
            "WheelEngine",
            "PvotNavBar",
            "PillNavItem",
            "WheelItem"
        )
        components.random()
    }
    
    /**
     * Generates random recomposition counts.
     */
    fun recompositionCount(): Arb<Int> = Arb.int(min = 0, max = 200)
    
    /**
     * Generates random calculation durations in milliseconds.
     */
    fun calculationDuration(): Arb<Long> = Arb.int(min = 0, max = 100)
        .map { it.toLong() }
}
