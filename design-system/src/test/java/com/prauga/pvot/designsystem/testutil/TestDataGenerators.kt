// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.testutil

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.prauga.pvot.designsystem.components.navigation.NavBarAppearance
import com.prauga.pvot.designsystem.components.navigation.NavBarBehavior
import com.prauga.pvot.designsystem.components.navigation.NavBarConfig
import com.prauga.pvot.designsystem.components.navigation.PvotTabItem
import com.prauga.pvot.designsystem.domain.transform.ItemTransform
import com.prauga.pvot.designsystem.domain.validation.ValidationError

/**
 * Generators for test data objects.
 * Provides factory methods for creating valid test data with customizable parameters.
 */
object TestDataGenerators {
    
    /**
     * Creates a valid NavBarConfig for testing.
     */
    fun createNavBarConfig(
        tabCount: Int = 3,
        selectedTab: Int = 0,
        appearance: NavBarAppearance = NavBarAppearance(),
        behavior: NavBarBehavior = NavBarBehavior()
    ): NavBarConfig {
        require(tabCount > 0) { "Tab count must be positive" }
        require(selectedTab in 0 until tabCount) { "Selected tab must be in range" }
        
        val tabs = (0 until tabCount).map { index ->
            createTabItem(iconRes = index, labelRes = index)
        }
        
        return NavBarConfig(
            tabs = tabs,
            selectedTab = selectedTab,
            appearance = appearance,
            behavior = behavior
        )
    }
    
    /**
     * Creates a PvotTabItem for testing.
     */
    fun createTabItem(
        @DrawableRes iconRes: Int = 0,
        @StringRes labelRes: Int = 0,
        @StringRes contentDescriptionRes: Int = 0
    ): PvotTabItem = PvotTabItem(
        iconRes = iconRes,
        labelRes = labelRes,
        contentDescriptionRes = contentDescriptionRes
    )
    
    /**
     * Creates an ItemTransform for testing.
     */
    fun createItemTransform(
        rotationX: Float = 0f,
        scale: Float = 1f,
        alpha: Float = 1f
    ): ItemTransform = ItemTransform(
        rotationX = rotationX,
        scale = scale,
        alpha = alpha
    )
    
    /**
     * Creates a ValidationError for testing.
     */
    fun createValidationError(
        parameter: String = "testParam",
        message: String = "Test error message",
        expectedValue: String? = null
    ): ValidationError = ValidationError(
        parameter = parameter,
        message = message,
        expectedValue = expectedValue
    )
    
    /**
     * Creates a list of integers for wheel values.
     */
    fun createIntRange(start: Int = 0, end: Int = 59): List<Int> = (start..end).toList()
    
    /**
     * Creates a list of tabs with specified count.
     */
    fun createTabList(count: Int): List<PvotTabItem> {
        require(count > 0) { "Count must be positive" }
        return (0 until count).map { index ->
            createTabItem(iconRes = index, labelRes = index)
        }
    }
}
