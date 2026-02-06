// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.navigation

/**
 * Simplified navigation bar configuration.
 * Reduces parameter count from 15+ to 3 top-level parameters.
 *
 * @param tabs List of navigation tab items
 * @param selectedTab Index of the currently selected tab
 * @param appearance Visual appearance configuration
 * @param behavior Behavioral configuration
 */
data class NavBarConfig(
    val tabs: List<PvotTabItem>,
    val selectedTab: Int,
    val appearance: NavBarAppearance = NavBarAppearance(),
    val behavior: NavBarBehavior = NavBarBehavior()
)
