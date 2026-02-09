// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.performance

import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Test suite that runs all performance measurement tests in the correct order.
 * 
 * This suite ensures:
 * 1. BaselinePerformanceTest runs first and captures baseline metrics
 * 2. PostMigrationPerformanceTest runs second and captures post-migration metrics
 * 3. PerformanceComparisonIntegrationTest runs last and compares the metrics
 * 
 * By running all tests in a single suite, the metric files persist across test
 * executions, allowing the comparison test to access both baseline and post-migration
 * metrics.
 * 
 * Usage:
 * ```
 * ./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.prauga.pvot.performance.PerformanceMigrationTestSuite
 * ```
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    BaselinePerformanceTest::class,
    PostMigrationPerformanceTest::class,
    PerformanceComparisonIntegrationTest::class
)
class PerformanceMigrationTestSuite
