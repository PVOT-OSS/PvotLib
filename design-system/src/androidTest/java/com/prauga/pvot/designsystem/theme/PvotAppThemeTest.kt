// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import com.prauga.pvot.designsystem.components.navigation.NavBarCollapsedChipDark
import com.prauga.pvot.designsystem.components.navigation.NavBarIconSelectedDark
import com.prauga.pvot.designsystem.components.navigation.PvotNavBarColors

class PvotAppThemeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun theme_providesNavBarColors() {
        var navBarColors: PvotNavBarColors? = null

        composeTestRule.setContent {
            PvotAppTheme(darkTheme = true) {
                navBarColors = PvotTheme.navBarColors
                Text("Test")
            }
        }

        assertNotNull(navBarColors)
        assertNotNull(navBarColors?.selectedChipColor)
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
            PvotAppTheme(darkTheme = true) {
                collapsedChipColor = PvotTheme.navBarColors.collapsedChipColor
                iconSelectedColor = PvotTheme.navBarColors.iconSelectedColor
                Text("Test")
            }
        }

        assertEquals(NavBarCollapsedChipDark, collapsedChipColor)
        assertEquals(NavBarIconSelectedDark, iconSelectedColor)
    }

    @Test
    fun theme_customNavBarColors_areApplied() {
        val customSelectedChipColor = Color.Green
        val customChipColor = Color.Red
        val customIconColor = Color.Blue

        var appliedSelectedChipColor: Color? = null
        var appliedChipColor: Color? = null
        var appliedIconColor: Color? = null

        composeTestRule.setContent {
            PvotAppTheme(
                navBarColors = PvotNavBarColors(
                    selectedChipColor = customSelectedChipColor,
                    collapsedChipColor = customChipColor,
                    containerColor = Color.Black,
                    iconSelectedColor = customIconColor,
                    iconUnselectedColor = Color.Gray
                )
            ) {
                appliedSelectedChipColor = PvotTheme.navBarColors.selectedChipColor
                appliedChipColor = PvotTheme.navBarColors.collapsedChipColor
                appliedIconColor = PvotTheme.navBarColors.iconSelectedColor
                Text("Test")
            }
        }

        assertEquals(customSelectedChipColor, appliedSelectedChipColor)
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

        assertEquals(PvotPrimaryDark, primaryColor)
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

        assertEquals(PvotPrimaryLight, primaryColor)
    }
}
