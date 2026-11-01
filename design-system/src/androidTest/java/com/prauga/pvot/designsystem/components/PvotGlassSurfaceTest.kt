// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.longClick
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.effects.LocalPvotGlassIntensity
import com.prauga.pvot.designsystem.effects.PvotGlassDefaults
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PvotGlassSurfaceTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun show(intensity: Float = PvotGlassDefaults.SolidIntensity, surface: @Composable () -> Unit) {
        composeTestRule.setContent {
            PvotAppTheme {
                CompositionLocalProvider(LocalPvotGlassIntensity provides intensity) { surface() }
            }
        }
    }

    @Test
    fun aSurfaceWithOnClickIsAButton() {
        var clicks = 0
        show {
            Box(Modifier.size(48.dp).testTag("glass").pvotGlassSurface(backdrop = null, onClick = { clicks++ }))
        }
        composeTestRule.onNodeWithTag("glass")
            .assertHasClickAction()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun aDisabledSurfaceIgnoresTaps() {
        var clicked = false
        show {
            Box(Modifier.size(48.dp).testTag("glass").pvotGlassSurface(backdrop = null, onClick = { clicked = true }, enabled = false))
        }
        composeTestRule.onNodeWithTag("glass").performClick()
        assertFalse(clicked)
    }

    @Test
    fun aLongPressReachesOnLongClick() {
        var held = false
        show {
            Box(
                Modifier.size(48.dp).testTag("glass")
                    .pvotGlassSurface(backdrop = null, onClick = {}, onLongClick = { held = true })
            )
        }
        composeTestRule.onNodeWithTag("glass").performTouchInput { longClick() }
        assertTrue(held)
    }

    @Test
    fun fullIntensityDrawsWithoutABackdrop() {
        show(intensity = 1f) {
            PvotGlassSurface(backdrop = null, modifier = Modifier.size(48.dp).testTag("glass")) {}
        }
        composeTestRule.onNodeWithTag("glass").assertExists()
    }
}
