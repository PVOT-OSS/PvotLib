// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.config

/**
 * Performance configuration for the design system.
 *
 * @param enableCaching Whether to enable caching for expensive operations
 * @param enablePerformanceMonitoring Whether to enable performance monitoring
 * @param maxCacheSize Maximum number of entries in caches
 * @param maxConcurrentAnimations Maximum number of concurrent animations
 * @param recompositionWarningThreshold Threshold for recomposition count warnings
 */
data class PerformanceConfig(
    val enableCaching: Boolean = true,
    val enablePerformanceMonitoring: Boolean = false,
    val maxCacheSize: Int = 100,
    val maxConcurrentAnimations: Int = 10,
    val recompositionWarningThreshold: Int = 100
)
