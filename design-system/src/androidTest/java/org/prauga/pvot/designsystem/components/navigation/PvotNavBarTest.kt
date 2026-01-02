package org.prauga.pvot.designsystem.components.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.prauga.pvot.designsystem.test.R
import org.prauga.pvot.designsystem.theme.PvotAppTheme

class PvotNavBarTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val testTabs =
        listOf(
            TabItem(
                iconRes = R.drawable.ic_test_home,
                labelRes = R.string.test_tab_home,
                contentDescriptionRes = R.string.test_cd_home,
            ),
            TabItem(
                iconRes = R.drawable.ic_test_settings,
                labelRes = R.string.test_tab_settings,
                contentDescriptionRes = R.string.test_cd_settings,
            ),
        )

    @Test
    fun navBar_displaysAllTabs() {
        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 0,
                    onTabClick = {},
                    tabs = testTabs,
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
                    tabs = testTabs,
                )
            }
        }

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
                    tabs = testTabs,
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
                    tabs = testTabs,
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Navigate to home").performClick()

        assertEquals(0, clickedIndex)
    }

    @Test
    fun navBar_changesSelection_updatesLabel() {
        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 1,
                    onTabClick = {},
                    tabs = testTabs,
                )
            }
        }

        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun navBar_emptyTabs_doesNotCrash() {
        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 0,
                    onTabClick = {},
                    tabs = emptyList(),
                )
            }
        }

        // Should not crash
    }

    @Test
    fun navBar_singleTab_displaysCorrectly() {
        val singleTab = listOf(testTabs.first())

        composeTestRule.setContent {
            PvotAppTheme {
                PvotNavBar(
                    selectedTab = 0,
                    onTabClick = {},
                    tabs = singleTab,
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Navigate to home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }
}
