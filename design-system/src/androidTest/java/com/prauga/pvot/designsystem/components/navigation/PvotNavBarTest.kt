// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import com.prauga.pvot.designsystem.test.R
import com.prauga.pvot.designsystem.theme.PvotAppTheme

class PvotNavBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testTabs = listOf(
        PvotTabItem(
            iconRes = R.drawable.ic_test_home,
            labelRes = R.string.test_tab_home,
            contentDescriptionRes = R.string.test_cd_home
        ),
        PvotTabItem(
            iconRes = R.drawable.ic_test_settings,
            labelRes = R.string.test_tab_settings,
            contentDescriptionRes = R.string.test_cd_settings
        )
    )

    @Test
    fun navBar_displaysAllTabs() {
        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 0,
                    onTabClick = {},
                    tabs = testTabs
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Navigate to home").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Navigate to settings").assertIsDisplayed()
    }

    @Test
    fun navBar_selectedTab_showsLabel() {
        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 0,
                    onTabClick = {},
                    tabs = testTabs
                )
            }
        }

        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }

    @Test
    fun navBar_emptyTabs_doesNotCrash() {
        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 0,
                    onTabClick = {},
                    tabs = emptyList()
                )
            }
        }
    }

    @Test
    fun navBar_singleTab_displaysCorrectly() {
        val singleTab = listOf(testTabs.first())

        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 0,
                    onTabClick = {},
                    tabs = singleTab
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Navigate to home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }

    @Test
    fun navBar_clickTab_triggersCallback() {
        var clickedIndex = -1

        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 0,
                    onTabClick = { clickedIndex = it },
                    tabs = testTabs
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Navigate to settings").performClick()

        assertEquals(1, clickedIndex)
    }

    @Test
    fun navBar_clickFirstTab_triggersCallbackWithZero() {
        var clickedIndex = -1

        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 1,
                    onTabClick = { clickedIndex = it },
                    tabs = testTabs
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Navigate to home").performClick()

        assertEquals(0, clickedIndex)
    }

    @Test
    fun navBar_clickSelectedTab_stillTriggersCallback() {
        var clickCount = 0

        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 0,
                    onTabClick = { clickCount++ },
                    tabs = testTabs
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Navigate to home").performClick()

        assertEquals(1, clickCount)
    }

    @Test
    fun navBar_multipleClicks_triggersCallbackEachTime() {
        val clickedIndices = mutableListOf<Int>()

        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 0,
                    onTabClick = { clickedIndices.add(it) },
                    tabs = testTabs
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Navigate to settings").performClick()
        composeTestRule.onNodeWithContentDescription("Navigate to home").performClick()
        composeTestRule.onNodeWithContentDescription("Navigate to settings").performClick()

        assertEquals(listOf(1, 0, 1), clickedIndices)
    }

    @Test
    fun navBar_changesSelection_updatesLabel() {
        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 1,
                    onTabClick = {},
                    tabs = testTabs
                )
            }
        }

        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun navBar_dynamicSelectionChange_updatesDisplay() {
        var selectedTab by mutableIntStateOf(0)

        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = selectedTab,
                    onTabClick = { selectedTab = it },
                    tabs = testTabs
                )
            }
        }

        composeTestRule.onNodeWithText("Home").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Navigate to settings").performClick()

        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun navBar_fourTabs_displaysAllTabs() {
        val fourTabs = listOf(
            PvotTabItem(
                iconRes = R.drawable.ic_test_home,
                labelRes = R.string.test_tab_alarm,
                contentDescriptionRes = R.string.test_cd_alarm
            ),
            PvotTabItem(
                iconRes = R.drawable.ic_test_settings,
                labelRes = R.string.test_tab_clock,
                contentDescriptionRes = R.string.test_cd_clock
            ),
            PvotTabItem(
                iconRes = R.drawable.ic_test_home,
                labelRes = R.string.test_tab_timer,
                contentDescriptionRes = R.string.test_cd_timer
            ),
            PvotTabItem(
                iconRes = R.drawable.ic_test_settings,
                labelRes = R.string.test_tab_stopwatch,
                contentDescriptionRes = R.string.test_cd_stopwatch
            )
        )

        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 0,
                    onTabClick = {},
                    tabs = fourTabs
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Alarm tab").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Clock tab").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Timer tab").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Stopwatch tab").assertIsDisplayed()
    }

    @Test
    fun navBar_fourTabs_selectThirdTab_showsCorrectLabel() {
        val fourTabs = listOf(
            PvotTabItem(
                iconRes = R.drawable.ic_test_home,
                labelRes = R.string.test_tab_alarm,
                contentDescriptionRes = R.string.test_cd_alarm
            ),
            PvotTabItem(
                iconRes = R.drawable.ic_test_settings,
                labelRes = R.string.test_tab_clock,
                contentDescriptionRes = R.string.test_cd_clock
            ),
            PvotTabItem(
                iconRes = R.drawable.ic_test_home,
                labelRes = R.string.test_tab_timer,
                contentDescriptionRes = R.string.test_cd_timer
            ),
            PvotTabItem(
                iconRes = R.drawable.ic_test_settings,
                labelRes = R.string.test_tab_stopwatch,
                contentDescriptionRes = R.string.test_cd_stopwatch
            )
        )

        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 2,
                    onTabClick = {},
                    tabs = fourTabs
                )
            }
        }

        composeTestRule.onNodeWithText("Timer").assertIsDisplayed()
    }

    @Test
    fun navBar_expandedIconOverride_usesOverrideWhenSelected() {
        val tabsWithOverride = listOf(
            PvotTabItem(
                iconRes = R.drawable.ic_test_home,
                labelRes = R.string.test_tab_home,
                contentDescriptionRes = R.string.test_cd_home,
                expandedIconRes = R.drawable.ic_test_add,
                expandedLabelRes = R.string.test_tab_add_new
            ),
            PvotTabItem(
                iconRes = R.drawable.ic_test_settings,
                labelRes = R.string.test_tab_settings,
                contentDescriptionRes = R.string.test_cd_settings
            )
        )

        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 0,
                    onTabClick = {},
                    tabs = tabsWithOverride
                )
            }
        }

        composeTestRule.onNodeWithText("Add New").assertIsDisplayed()
    }

    @Test
    fun navBar_expandedLabelOverride_changesWithSelection() {
        val tabsWithOverride = listOf(
            PvotTabItem(
                iconRes = R.drawable.ic_test_home,
                labelRes = R.string.test_tab_alarm,
                contentDescriptionRes = R.string.test_cd_alarm,
                expandedLabelRes = R.string.test_tab_add_alarm
            ),
            PvotTabItem(
                iconRes = R.drawable.ic_test_settings,
                labelRes = R.string.test_tab_timer,
                contentDescriptionRes = R.string.test_cd_timer,
                expandedLabelRes = R.string.test_tab_add_timer
            )
        )

        var selectedTab by mutableIntStateOf(0)

        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = selectedTab,
                    onTabClick = { selectedTab = it },
                    tabs = tabsWithOverride
                )
            }
        }

        composeTestRule.onNodeWithText("Add Alarm").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Timer tab").performClick()

        composeTestRule.onNodeWithText("Add Timer").assertIsDisplayed()
    }

    @Test
    fun navBar_globalExpandedIconRes_overridesAllTabs() {
        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 0,
                    onTabClick = {},
                    tabs = testTabs,
                    expandedIconRes = R.drawable.ic_test_add
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Navigate to home").assertIsDisplayed()
    }

    @Test
    fun navBar_dynamicTabOverride_updatesWhenStateChanges() {
        var expandedLabel by mutableIntStateOf(R.string.test_tab_start)

        composeTestRule.setContent {
            val dynamicTabs = listOf(
                PvotTabItem(
                    iconRes = R.drawable.ic_test_home,
                    labelRes = R.string.test_tab_stopwatch,
                    contentDescriptionRes = R.string.test_cd_stopwatch,
                    expandedLabelRes = expandedLabel
                )
            )

            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 0,
                    onTabClick = {},
                    tabs = dynamicTabs
                )
            }
        }

        composeTestRule.onNodeWithText("Start").assertIsDisplayed()

        expandedLabel = R.string.test_tab_pause

        composeTestRule.onNodeWithText("Pause").assertIsDisplayed()
    }

    @Test
    fun navBar_customSizes_displaysCorrectly() {
        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 0,
                    onTabClick = {},
                    tabs = testTabs,
                    sizes = PvotNavBarSizes(
                        barHeight = 72.dp,
                        barWidth = 367.dp,
                        collapsedItemSize = 56.dp,
                        expandedWidth = 169.dp,
                        cornerRadius = 100.dp,
                        itemCornerRadius = 28.dp,
                        horizontalPadding = 22.dp,
                        contentPaddingHorizontal = 9.dp,
                        itemSpacing = 4.dp
                    )
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Navigate to home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }

    @Test
    fun navBar_intrinsicSizing_displaysCorrectly() {
        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 0,
                    onTabClick = {},
                    tabs = testTabs,
                    sizes = PvotNavBarSizes(
                        minExpandedWidth = 120.dp,
                        maxExpandedWidth = 200.dp
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }

    @Test
    fun navBar_threeTabs_displaysAllTabs() {
        val threeTabs = listOf(
            PvotTabItem(
                iconRes = R.drawable.ic_test_home,
                labelRes = R.string.test_tab_alarm,
                contentDescriptionRes = R.string.test_cd_alarm
            ),
            PvotTabItem(
                iconRes = R.drawable.ic_test_settings,
                labelRes = R.string.test_tab_clock,
                contentDescriptionRes = R.string.test_cd_clock
            ),
            PvotTabItem(
                iconRes = R.drawable.ic_test_home,
                labelRes = R.string.test_tab_timer,
                contentDescriptionRes = R.string.test_cd_timer
            )
        )

        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 0,
                    onTabClick = {},
                    tabs = threeTabs
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Alarm tab").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Clock tab").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Timer tab").assertIsDisplayed()
    }

    @Test
    fun navBar_fiveTabs_displaysAllTabs() {
        val fiveTabs = listOf(
            PvotTabItem(R.drawable.ic_test_home, R.string.test_tab_alarm, R.string.test_cd_alarm),
            PvotTabItem(
                R.drawable.ic_test_settings,
                R.string.test_tab_clock,
                R.string.test_cd_clock
            ),
            PvotTabItem(R.drawable.ic_test_home, R.string.test_tab_timer, R.string.test_cd_timer),
            PvotTabItem(
                R.drawable.ic_test_settings,
                R.string.test_tab_stopwatch,
                R.string.test_cd_stopwatch
            ),
            PvotTabItem(R.drawable.ic_test_home, R.string.test_tab_home, R.string.test_cd_home)
        )

        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 4,
                    onTabClick = {},
                    tabs = fiveTabs
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Alarm tab").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Clock tab").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Timer tab").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Stopwatch tab").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Navigate to home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }

    @Test
    fun navBar_sixTabs_selectLastTab_showsCorrectLabel() {
        val sixTabs = listOf(
            PvotTabItem(R.drawable.ic_test_home, R.string.test_tab_alarm, R.string.test_cd_alarm),
            PvotTabItem(
                R.drawable.ic_test_settings,
                R.string.test_tab_clock,
                R.string.test_cd_clock
            ),
            PvotTabItem(R.drawable.ic_test_home, R.string.test_tab_timer, R.string.test_cd_timer),
            PvotTabItem(
                R.drawable.ic_test_settings,
                R.string.test_tab_stopwatch,
                R.string.test_cd_stopwatch
            ),
            PvotTabItem(R.drawable.ic_test_home, R.string.test_tab_home, R.string.test_cd_home),
            PvotTabItem(
                R.drawable.ic_test_settings,
                R.string.test_tab_settings,
                R.string.test_cd_settings
            )
        )

        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 5,
                    onTabClick = {},
                    tabs = sixTabs
                )
            }
        }

        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun navBar_dynamicTabList_updatesWhenTabsChange() {
        var tabs by mutableStateOf(
            listOf(
                PvotTabItem(R.drawable.ic_test_home, R.string.test_tab_home, R.string.test_cd_home)
            )
        )

        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 0,
                    onTabClick = {},
                    tabs = tabs
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Navigate to home").assertIsDisplayed()
        composeTestRule.onAllNodesWithContentDescription("Navigate to settings")
            .assertCountEquals(0)

        tabs = listOf(
            PvotTabItem(R.drawable.ic_test_home, R.string.test_tab_home, R.string.test_cd_home),
            PvotTabItem(
                R.drawable.ic_test_settings,
                R.string.test_tab_settings,
                R.string.test_cd_settings
            )
        )

        composeTestRule.onNodeWithContentDescription("Navigate to home").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Navigate to settings").assertIsDisplayed()
    }

    @Test
    fun navBar_manyTabs_doesNotCrash() {
        val manyTabs = (0 until 10).map { index ->
            PvotTabItem(
                iconRes = if (index % 2 == 0) R.drawable.ic_test_home else R.drawable.ic_test_settings,
                labelRes = R.string.test_tab_home,
                contentDescriptionRes = R.string.test_cd_home
            )
        }

        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 5,
                    onTabClick = {},
                    tabs = manyTabs
                )
            }
        }

        // Should not crash with many tabs
    }

    @Test
    fun navBar_outOfBoundsSelectedTab_doesNotCrash() {
        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 999,
                    onTabClick = {},
                    tabs = testTabs
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Navigate to home").assertIsDisplayed()
    }

    @Test
    fun navBar_negativeSelectedTab_doesNotCrash() {
        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = -1,
                    onTabClick = {},
                    tabs = testTabs
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Navigate to home").assertIsDisplayed()
    }
}
