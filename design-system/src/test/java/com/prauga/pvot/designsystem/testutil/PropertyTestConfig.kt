// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.testutil

import io.kotest.core.config.AbstractProjectConfig

/**
 * Global configuration for Kotest property-based tests.
 * Sets default iteration counts and test behavior.
 */
object PropertyTestConfig : AbstractProjectConfig() {
    
    /**
     * Default number of iterations for property tests.
     * Minimum 100 iterations as specified in design document.
     */
    const val DEFAULT_ITERATIONS = 100
    
    /**
     * Increased iterations for critical properties.
     */
    const val CRITICAL_ITERATIONS = 500
    
    /**
     * Quick iterations for smoke tests.
     */
    const val QUICK_ITERATIONS = 20
}
