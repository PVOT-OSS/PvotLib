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
import com.prauga.pvot.designsystem.navigation.PvotNavBarDefaults
import com.prauga.pvot.designsystem.navigation.PvotNavBarColors

class PvotAppThemeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun theme_providesNavBarColors() {
        var navBarColors: PvotNavBarColors? = null

        composeTestRule.setContent {
            PvotAppTheme {
                navBarColors = PvotNavBarDefaults.themeColors()
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
        var iconSelectedColor: Color? = null
        var onPrimary: Color? = null

        composeTestRule.setContent {
            PvotAppTheme(dynamicColor = false) {
                val colors = PvotNavBarDefaults.themeColors()
                iconSelectedColor = colors.iconSelectedColor
                onPrimary = MaterialTheme.colorScheme.onPrimary
                Text("Test")
            }
        }

        assertEquals(onPrimary, iconSelectedColor)
    }

    @Test
    fun theme_withoutDynamicColor_reportsStaticColor() {
        var isDynamicColor: Boolean? = null

        composeTestRule.setContent {
            PvotAppTheme(dynamicColor = false) {
                isDynamicColor = PvotTheme.isDynamicColor
                Text("Test")
            }
        }

        assertEquals(false, isDynamicColor)
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
