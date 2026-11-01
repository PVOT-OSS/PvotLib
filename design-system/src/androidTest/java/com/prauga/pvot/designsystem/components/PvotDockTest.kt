// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.prauga.pvot.designsystem.R
import com.prauga.pvot.designsystem.components.button.PvotIconButton
import com.prauga.pvot.designsystem.components.list.PvotListGroup
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PvotDockTest {

    @get:Rule val compose = createComposeRule()

    @Test
    fun tappingATabSelectsItAndTappingThePillRunsItsAction() {
        val actions = mutableListOf<String>()
        compose.setContent {
            PvotAppTheme {
                var tab by remember { mutableIntStateOf(0) }
                PvotDock(
                    items = listOf(
                        PvotDockItem(painterResource(R.drawable.pvot_ic_phone), "Keypad",
                            PvotDockAction(painterResource(R.drawable.pvot_ic_phone), "Dial") { actions += "dial" }),
                        PvotDockItem(painterResource(R.drawable.pvot_ic_user), "Contacts",
                            PvotDockAction(painterResource(R.drawable.pvot_ic_user), "Add") { actions += "add" }),
                        PvotDockItem(painterResource(R.drawable.pvot_ic_x), "Voicemail"),
                    ),
                    selectedIndex = tab,
                    onSelect = { tab = it },
                    backdrop = null,
                )
            }
        }
        compose.onNodeWithText("Dial").assertExists()
        compose.onNodeWithContentDescription("Keypad").assertIsSelected().performClick()
        assertEquals(listOf("dial"), actions)

        compose.onNodeWithContentDescription("Contacts").assertIsNotSelected().performClick()
        compose.onNodeWithContentDescription("Contacts").assertIsSelected()
        compose.onNodeWithText("Add").assertExists()
        assertEquals("selecting a tab runs nothing", listOf("dial"), actions)

        // A tab without an action does nothing when tapped again.
        compose.onNodeWithContentDescription("Voicemail").performClick().performClick()
        compose.onNodeWithContentDescription("Voicemail").assertIsSelected()
        assertEquals(listOf("dial"), actions)
    }

    @Test
    fun theSelectedTabReadsItsActionToTalkBack() {
        compose.setContent {
            PvotAppTheme {
                PvotDock(
                    items = listOf(
                        PvotDockItem(painterResource(R.drawable.pvot_ic_phone), "Recents",
                            PvotDockAction(painterResource(R.drawable.pvot_ic_phone), "Search") {}),
                    ),
                    selectedIndex = 0,
                    onSelect = {},
                    backdrop = null,
                )
            }
        }
        compose.onNodeWithContentDescription("Recents")
            .assert(SemanticsMatcher("click label is Search") { it.config[SemanticsActions.OnClick].label == "Search" })
    }

    @Test
    fun aTabRowSelectsTheTappedTab() {
        compose.setContent {
            PvotAppTheme {
                var tab by remember { mutableIntStateOf(0) }
                PvotTabRow(listOf("All", "Missed"), tab, { tab = it })
            }
        }
        compose.onNodeWithText("All").assertIsSelected()
        compose.onNodeWithText("Missed").performClick().assertIsSelected()
        compose.onNodeWithText("All").assertIsNotSelected()
    }

    @Test
    fun anIconButtonToggleReadsAsASwitch() {
        compose.setContent {
            PvotAppTheme {
                var on by remember { mutableStateOf(false) }
                PvotIconButton(painterResource(R.drawable.pvot_ic_phone), "Mute", { on = !on }, checked = on)
            }
        }
        compose.onNodeWithContentDescription("Mute").assertIsOff().performClick().assertIsOn()
    }

    @Test
    fun aListGroupShowsItsTitleAndRows() {
        compose.setContent {
            PvotAppTheme {
                PvotListGroup(title = "General") {
                    item { Text("One") }
                    item { Text("Two") }
                }
            }
        }
        compose.onNodeWithText("General").assertExists()
        compose.onNodeWithText("One").assertExists()
        compose.onNodeWithText("Two").assertExists()
    }
}
