// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.call

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.R
import com.prauga.pvot.designsystem.components.PvotSlideTarget
import com.prauga.pvot.designsystem.components.PvotSlideToAct
import com.prauga.pvot.designsystem.components.keypad.PvotKeypad
import com.prauga.pvot.designsystem.components.keypad.PvotKeypadDefaults
import com.prauga.pvot.designsystem.components.keypad.PvotKeypadDisplay
import com.prauga.pvot.designsystem.components.list.PvotAvatar
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PvotCallSurfaceTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun aToggleCallButtonIsASwitchWithItsState() {
        composeTestRule.setContent {
            PvotAppTheme {
                var on by remember { mutableStateOf(false) }
                PvotCallButton(
                    painterResource(R.drawable.pvot_ic_phone),
                    "Mute",
                    onClick = { on = !on },
                    checked = on,
                )
            }
        }
        val button = composeTestRule.onNodeWithContentDescription("Mute")
        button.assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Switch))
        button
            .assertIsOff()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Off"))
        button.performClick()
        button
            .assertIsOn()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "On"))
    }

    @Test
    fun aPlainCallButtonIsAButton() {
        var taps = 0
        composeTestRule.setContent {
            PvotAppTheme {
                PvotCallButton(
                    painterResource(R.drawable.pvot_ic_phone),
                    "Add call",
                    onClick = { taps++ },
                )
            }
        }
        val button = composeTestRule.onNodeWithContentDescription("Add call")
        button.assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
        button.performClick()
        assertEquals(1, taps)
    }

    @Test
    fun aDisabledCallButtonIgnoresTaps() {
        var taps = 0
        composeTestRule.setContent {
            PvotAppTheme {
                PvotCallButton(
                    painterResource(R.drawable.pvot_ic_phone),
                    "Hold",
                    onClick = { taps++ },
                    enabled = false,
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Hold").assertIsNotEnabled().performClick()
        assertEquals(0, taps)
    }

    @Test
    fun theActionButtonReadsItsLabel() {
        var taps = 0
        composeTestRule.setContent {
            PvotAppTheme {
                PvotCallActionButton(
                    PvotCallAction.End,
                    painterResource(R.drawable.pvot_ic_phone_off),
                    "End call",
                    { taps++ },
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("End call").performClick()
        assertEquals(1, taps)
    }

    @Test
    fun aKeyReportsDownThenUp() {
        val events = mutableListOf<Pair<String, Boolean>>()
        composeTestRule.setContent {
            PvotAppTheme {
                PvotKeypad(onKeyPress = { key, pressed -> events += key.digit to pressed })
            }
        }
        composeTestRule.onNodeWithContentDescription("5, JKL").performClick()
        composeTestRule.waitForIdle()
        assertEquals(listOf("5" to true, "5" to false), events)
    }

    @Test
    fun aKeyWithALongPressOffersIt() {
        var held: String? = null
        composeTestRule.setContent {
            PvotAppTheme {
                PvotKeypad(
                    onKeyPress = { _, _ -> },
                    keys = PvotKeypadDefaults.keys(zeroLongPressLabel = "Plus"),
                    onKeyLongPress = {
                        held = it.digit
                        true
                    },
                )
            }
        }
        composeTestRule
            .onNodeWithContentDescription("0, +")
            .performSemanticsAction(SemanticsActions.OnLongClick)
        assertEquals("0", held)
    }

    @Test
    fun backspaceTapsAndHolds() {
        var taps = 0
        var holds = 0
        composeTestRule.setContent {
            PvotAppTheme {
                PvotKeypadDisplay(
                    digits = "555",
                    onBackspace = { taps++ },
                    onBackspaceLongPress = { holds++ },
                )
            }
        }
        val backspace = composeTestRule.onNodeWithContentDescription("Backspace")
        backspace.performClick()
        backspace.performSemanticsAction(SemanticsActions.OnLongClick)
        assertEquals(1, taps)
        assertEquals(1, holds)
    }

    @Test
    fun slidingUpActs() {
        var answered = false
        composeTestRule.setContent {
            PvotAppTheme {
                PvotSlideToAct(
                    painterResource(R.drawable.pvot_ic_phone),
                    "Answer",
                    up = PvotSlideTarget("Answer", { answered = true }, 120.dp),
                    down = PvotSlideTarget("Decline", {}, 56.dp),
                    nudge = false,
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Answer").performTouchInput {
            swipeUp(centerY, centerY - 160.dp.toPx())
        }
        composeTestRule.waitForIdle()
        assertTrue(answered)
    }

    @Test
    fun aShortPullDownDeclines() {
        var declined = false
        composeTestRule.setContent {
            PvotAppTheme {
                PvotSlideToAct(
                    painterResource(R.drawable.pvot_ic_phone),
                    "Answer",
                    up = PvotSlideTarget("Answer", {}, 120.dp),
                    down = PvotSlideTarget("Decline", { declined = true }, 56.dp),
                    nudge = false,
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Answer").performTouchInput {
            swipeDown(centerY, centerY + 80.dp.toPx())
        }
        composeTestRule.waitForIdle()
        assertTrue(declined)
    }

    @Test
    fun talkBackGetsEachTargetAsAnAction() {
        var declined = false
        composeTestRule.setContent {
            PvotAppTheme {
                PvotSlideToAct(
                    painterResource(R.drawable.pvot_ic_phone),
                    "Answer",
                    up = PvotSlideTarget("Answer", {}, 120.dp),
                    down = PvotSlideTarget("Decline", { declined = true }, 56.dp),
                )
            }
        }
        val actions =
            composeTestRule
                .onNodeWithContentDescription("Answer")
                .fetchSemanticsNode()
                .config[SemanticsActions.CustomActions]
        assertEquals(listOf("Answer", "Decline"), actions.map { it.label })
        composeTestRule.runOnIdle { actions[1].action() }
        assertTrue(declined)
    }

    @Test
    fun anAvatarIsDecorativeUnlessDescribed() {
        composeTestRule.setContent {
            PvotAppTheme { PvotAvatar(name = "Ada Lovelace", contentDescription = "Ada's photo") }
        }
        composeTestRule
            .onNodeWithContentDescription("Ada's photo")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.ContentDescription))
    }
}
