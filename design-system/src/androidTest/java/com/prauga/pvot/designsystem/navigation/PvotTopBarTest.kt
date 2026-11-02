// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.prauga.pvot.designsystem.components.PvotScreenScaffold
import com.prauga.pvot.designsystem.components.StillBackground
import com.prauga.pvot.designsystem.effects.LocalPvotGlassIntensity
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PvotTopBarTest {

    @get:Rule val composeTestRule = createComposeRule()

    private val isHeading = SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading)

    @Test
    fun theTitleIsOneHeading() {
        composeTestRule.setContent { PvotAppTheme { PvotTopBar(title = "Calls", backdrop = null) } }
        composeTestRule.onAllNodes(isHeading).assertCountEquals(1)
    }

    @Test
    fun theBackButtonCallsOnBack() {
        var back = false
        composeTestRule.setContent {
            PvotAppTheme { PvotTopBar(title = "Calls", onBack = { back = true }, backdrop = null) }
        }
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assertTrue(back)
    }

    @Test
    fun aGlassBarDrawsCompactAtEveryIntensity() {
        var intensity by mutableFloatStateOf(0f)
        composeTestRule.setContent {
            PvotAppTheme {
                CompositionLocalProvider(LocalPvotGlassIntensity provides intensity) {
                    PvotScreenScaffold(
                        background = StillBackground,
                        topBar = { PvotTopBar(title = "Calls", compact = true) },
                    ) {}
                }
            }
        }
        for (value in listOf(0f, 0.1f, 1f)) {
            intensity = value
            composeTestRule.waitForIdle()
            composeTestRule.onAllNodes(isHeading).assertCountEquals(1)
        }
    }
}
