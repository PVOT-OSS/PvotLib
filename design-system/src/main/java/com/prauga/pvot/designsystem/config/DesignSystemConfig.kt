// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.config

/**
 * Global design system configuration.
 *
 * @param performance Performance-related configuration
 * @param enableHapticFeedback Whether to enable haptic feedback globally
 * @param enableDebugLogging Whether to enable debug logging
 */
data class DesignSystemConfig(
    val performance: PerformanceConfig = PerformanceConfig(),
    val enableHapticFeedback: Boolean = true,
    val enableDebugLogging: Boolean = false
)
