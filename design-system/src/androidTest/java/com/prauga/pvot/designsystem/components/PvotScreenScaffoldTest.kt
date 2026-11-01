// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.components.background.PvotBackgroundMotion
import com.prauga.pvot.designsystem.components.background.PvotLiveBackground
import com.prauga.pvot.designsystem.effects.LocalPvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.LocalPvotGlassIntensity
import com.prauga.pvot.designsystem.effects.LocalPvotGlassReduced
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassDefaults
import com.prauga.pvot.designsystem.effects.PvotGlassIntensity
import com.prauga.pvot.designsystem.effects.ProvidePvotGlassIntensity
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PvotScreenScaffoldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun theContentIsPaddedBelowTheTopBar() {
        var padding: PaddingValues? = null
        composeTestRule.setContent {
            PvotAppTheme {
                PvotScreenScaffold(background = StillBackground, topBar = { Box(Modifier.fillMaxWidth().height(64.dp)) }) { padding = it }
            }
        }
        composeTestRule.waitForIdle()
        assertEquals(64.dp, padding!!.calculateTopPadding())
    }

    @Test
    fun theChromeGetsTheBackdropAndTheContentDoesNot() {
        var barBackdrop: PvotGlassBackdrop? = null
        var contentBackdrop: PvotGlassBackdrop? = PvotGlassBackdropMarker
        composeTestRule.setContent {
            PvotAppTheme {
                PvotScreenScaffold(
                    background = StillBackground,
                    topBar = { barBackdrop = LocalPvotGlassBackdrop.current },
                ) { contentBackdrop = LocalPvotGlassBackdrop.current }
            }
        }
        composeTestRule.waitForIdle()
        assertNotNull(barBackdrop)
        assertNull(contentBackdrop)
    }

    @Test
    fun aReducedDeviceGetsSolidGlass() {
        var intensity = -1f
        composeTestRule.setContent {
            PvotAppTheme {
                CompositionLocalProvider(LocalPvotGlassReduced provides true) {
                    ProvidePvotGlassIntensity(1f) { intensity = LocalPvotGlassIntensity.current }
                }
            }
        }
        composeTestRule.waitForIdle()
        assertEquals(PvotGlassDefaults.SolidIntensity, intensity)
    }

    @Test
    fun thePreferenceIsHeldToTheDevice() {
        var intensity = -1f
        composeTestRule.setContent {
            PvotAppTheme {
                CompositionLocalProvider(LocalPvotGlassReduced provides false) {
                    intensity = PvotGlassIntensity.effective(5f)
                }
            }
        }
        composeTestRule.waitForIdle()
        assertTrue(intensity <= PvotGlassDefaults.maxIntensity())
        assertTrue(intensity >= 0f)
    }

    private companion object {
        /** A stand-in that the content must replace with null. */
        val PvotGlassBackdropMarker: PvotGlassBackdrop? = null
    }
}

/**
 * A background that does not move: the default one runs a frame loop for as long as it is shown,
 * and the test clock never goes idle under a loop that never ends.
 */
internal val StillBackground: @Composable BoxScope.() -> Unit = {
    PvotLiveBackground(Modifier.fillMaxSize(), motion = PvotBackgroundMotion.Still)
}
