// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.navigation

/**
 * Behavioral configuration for navigation bar.
 *
 * @param enableHapticFeedback Whether to provide haptic feedback on tab selection
 * @param animationDuration Duration of tab selection animation in milliseconds
 * @param enablePerformanceMonitoring Whether to enable performance monitoring
 */
data class NavBarBehavior(
    val enableHapticFeedback: Boolean = true,
    val animationDuration: Int = 240,
    val enablePerformanceMonitoring: Boolean = false
)
