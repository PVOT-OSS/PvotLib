// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import com.prauga.pvot.designsystem.components.navigation.NavBarCollapsedChip
import com.prauga.pvot.designsystem.components.navigation.NavBarIconSelected
import com.prauga.pvot.designsystem.components.navigation.PvotNavBarColors

class PvotAppThemeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun theme_providesNavBarColors() {
        var navBarColors: PvotNavBarColors? = null

        composeTestRule.setContent {
            PvotAppTheme {
                navBarColors = PvotTheme.navBarColors
                Text("Test")
            }
        }

        assertNotNull(navBarColors)
        assertNotNull(navBarColors?.gradient)
        assertNotNull(navBarColors?.collapsedChipColor)
        assertNotNull(navBarColors?.containerColor)
        assertNotNull(navBarColors?.iconSelectedColor)
        assertNotNull(navBarColors?.iconUnselectedColor)
    }

    @Test
    fun theme_providesDefaultColors() {
        var collapsedChipColor: Color? = null
        var iconSelectedColor: Color? = null

        composeTestRule.setContent {
            PvotAppTheme {
                collapsedChipColor = PvotTheme.navBarColors.collapsedChipColor
                iconSelectedColor = PvotTheme.navBarColors.iconSelectedColor
                Text("Test")
            }
        }

        assertEquals(NavBarCollapsedChip, collapsedChipColor)
        assertEquals(NavBarIconSelected, iconSelectedColor)
    }

    @Test
    fun theme_customNavBarColors_areApplied() {
        val customChipColor = Color.Red
        val customIconColor = Color.Blue

        var appliedChipColor: Color? = null
        var appliedIconColor: Color? = null

        composeTestRule.setContent {
            PvotAppTheme(
                navBarColors = PvotNavBarColors(
                    gradient = Brush.horizontalGradient(listOf(Color.Red, Color.Blue)),
                    collapsedChipColor = customChipColor,
                    containerColor = Color.Black,
                    iconSelectedColor = customIconColor,
                    iconUnselectedColor = Color.Gray
                )
            ) {
                appliedChipColor = PvotTheme.navBarColors.collapsedChipColor
                appliedIconColor = PvotTheme.navBarColors.iconSelectedColor
                Text("Test")
            }
        }

        assertEquals(customChipColor, appliedChipColor)
        assertEquals(customIconColor, appliedIconColor)
    }

    @Test
    fun theme_providesMaterialTheme() {
        var primaryColor: Color? = null

        composeTestRule.setContent {
            PvotAppTheme {
                primaryColor = MaterialTheme.colorScheme.primary
                Text("Test")
            }
        }

        assertNotNull(primaryColor)
    }

    @Test
    fun theme_darkMode_providesDarkColors() {
        var primaryColor: Color? = null

        composeTestRule.setContent {
            PvotAppTheme(darkTheme = true, dynamicColor = false) {
                primaryColor = MaterialTheme.colorScheme.primary
                Text("Test")
            }
        }

        assertEquals(Purple80, primaryColor)
    }

    @Test
    fun theme_lightMode_providesLightColors() {
        var primaryColor: Color? = null

        composeTestRule.setContent {
            PvotAppTheme(darkTheme = false, dynamicColor = false) {
                primaryColor = MaterialTheme.colorScheme.primary
                Text("Test")
            }
        }

        assertEquals(Purple40, primaryColor)
    }
}
