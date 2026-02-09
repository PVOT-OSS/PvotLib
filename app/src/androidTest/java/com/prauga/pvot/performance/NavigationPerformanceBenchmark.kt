// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.performance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.prauga.pvot.R
import com.prauga.pvot.designsystem.components.navigation.NavBarConfig
import com.prauga.pvot.designsystem.components.navigation.PvotNavBar
import com.prauga.pvot.designsystem.components.navigation.PvotTabItem
import com.prauga.pvot.designsystem.domain.monitoring.PerformanceMonitor
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Performance benchmark for navigation bar interactions.
 * 
 * This benchmark measures:
 * - Recomposition counts during tab switches
 * - Frame rendering times
 * - Performance consistency across multiple iterations
 * 
 * The benchmark runs 100+ iterations for statistical significance and
 * validates that performance meets established thresholds.
 * 
 * Thresholds are automatically adjusted based on device type (emulator vs physical device).
 * Custom thresholds can be set via system properties. See BenchmarkThresholds for details.
 * 
 */
@RunWith(AndroidJUnit4::class)
class NavigationPerformanceBenchmark {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    /**
     * Benchmark thresholds configured for the current device.
     * Automatically detects emulator vs physical device and applies appropriate limits.
     */
    private val thresholds = BenchmarkThresholds.forCurrentDevice()
    
    /**
     * Benchmark test for tab switch performance.
     * 
     * This test:
     * 1. Sets up PvotNavBar with performance monitoring
     * 2. Performs 100+ tab switch iterations
     * 3. Measures recomposition counts and frame times
     * 4. Validates metrics against established thresholds
     * 5. Fails if any threshold is exceeded
     * 
     * The test provides statistical significance through multiple iterations
     * and ensures performance remains within acceptable bounds.
     * 
     */
    @Test
    fun benchmarkTabSwitchPerformance() {
        // Create performance monitor
        val performanceMonitor = PerformanceMonitor(
            enabled = true,
            warningThreshold = thresholds.maxRecompositionCount
        )
        
        // Create frame timing tracker
        val frameTracker = FrameTimingTracker()
        
        // Set up navigation bar with performance monitoring
        composeTestRule.setContent {
            PvotAppTheme {
                var selectedTab by remember { mutableIntStateOf(0) }
                
                val tabs = listOf(
                    PvotTabItem(
                        iconRes = R.drawable.ic_home,
                        labelRes = R.string.tab_home,
                        contentDescriptionRes = R.string.cd_home
                    ),
                    PvotTabItem(
                        iconRes = R.drawable.ic_apps,
                        labelRes = R.string.tab_apps,
                        contentDescriptionRes = R.string.cd_apps
                    ),
                    PvotTabItem(
                        iconRes = R.drawable.ic_catalog,
                        labelRes = R.string.tab_catalog,
                        contentDescriptionRes = R.string.cd_catalog
                    ),
                    PvotTabItem(
                        iconRes = R.drawable.ic_about,
                        labelRes = R.string.tab_about,
                        contentDescriptionRes = R.string.cd_about
                    )
                )
                
                val config = NavBarConfig(
                    tabs = tabs,
                    selectedTab = selectedTab
                )
                
                PvotNavBar(
                    config = config,
                    onTabClick = { selectedTab = it },
                    performanceMonitor = performanceMonitor
                )
            }
        }
        
        // Wait for initial composition
        composeTestRule.waitForIdle()
        
        // Start frame timing tracking
        frameTracker.startTracking()
        
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        
        // Perform 100+ tab switch iterations for statistical significance
        // Each iteration switches through all 4 tabs = 400+ total tab switches
        repeat(25) {
            // Switch through all tabs
            composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_home))
                .performClick()
            composeTestRule.waitForIdle()
            
            composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_apps))
                .performClick()
            composeTestRule.waitForIdle()
            
            composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_catalog))
                .performClick()
            composeTestRule.waitForIdle()
            
            composeTestRule.onNodeWithContentDescription(context.getString(R.string.cd_about))
                .performClick()
            composeTestRule.waitForIdle()
        }
        
        // Stop frame timing tracking
        frameTracker.stopTracking()
        
        // Capture performance metrics
        val navBarMetrics = performanceMonitor.getMetrics("PvotNavBar")
        val averageFrameTime = frameTracker.getAverageFrameTime()
        val maxFrameTime = frameTracker.getMaxFrameTime()
        val droppedFrames = frameTracker.getDroppedFrameCount()
        
        // Log benchmark results
        println("=".repeat(60))
        println("Navigation Performance Benchmark Results")
        println("=".repeat(60))
        println("Device Type: ${if (android.os.Build.FINGERPRINT.contains("generic")) "Emulator" else "Physical Device"}")
        println("Thresholds: ${thresholds}")
        println()
        println("Iterations: 100 tab switches (25 cycles × 4 tabs)")
        println()
        println("Recomposition Metrics:")
        println("  Count: ${navBarMetrics.recompositionCount}")
        println("  Threshold: ${thresholds.maxRecompositionCount}")
        println("  Status: ${if (navBarMetrics.recompositionCount <= thresholds.maxRecompositionCount) "✓ PASS" else "✗ FAIL"}")
        println()
        println("Frame Timing Metrics:")
        println("  Average Frame Time: ${"%.2f".format(averageFrameTime)}ms")
        println("  Threshold: ${thresholds.maxAverageFrameTimeMs}ms")
        println("  Status: ${if (averageFrameTime <= thresholds.maxAverageFrameTimeMs) "✓ PASS" else "✗ FAIL"}")
        println()
        println("  Max Frame Time: ${maxFrameTime}ms")
        println("  Threshold: ${thresholds.maxFrameTimeMs}ms")
        println("  Status: ${if (maxFrameTime <= thresholds.maxFrameTimeMs) "✓ PASS" else "✗ FAIL"}")
        println()
        println("  Dropped Frames: $droppedFrames")
        println("  Threshold: ${thresholds.maxDroppedFrames}")
        println("  Status: ${if (droppedFrames <= thresholds.maxDroppedFrames) "✓ PASS" else "✗ FAIL"}")
        println("=".repeat(60))
        
        // Validate metrics against thresholds
        // Fail the test if any threshold is exceeded
        val failures = mutableListOf<String>()
        
        if (navBarMetrics.recompositionCount > thresholds.maxRecompositionCount) {
            failures.add(
                "Recomposition count ${navBarMetrics.recompositionCount} exceeds threshold ${thresholds.maxRecompositionCount}"
            )
        }
        
        if (averageFrameTime > thresholds.maxAverageFrameTimeMs) {
            failures.add(
                "Average frame time ${"%.2f".format(averageFrameTime)}ms exceeds threshold ${thresholds.maxAverageFrameTimeMs}ms"
            )
        }
        
        if (maxFrameTime > thresholds.maxFrameTimeMs) {
            failures.add(
                "Max frame time ${maxFrameTime}ms exceeds threshold ${thresholds.maxFrameTimeMs}ms"
            )
        }
        
        if (droppedFrames > thresholds.maxDroppedFrames) {
            failures.add(
                "Dropped frames $droppedFrames exceeds threshold ${thresholds.maxDroppedFrames}"
            )
        }
        
        // Assert all thresholds are met
        if (failures.isNotEmpty()) {
            throw AssertionError(
                "Performance benchmark failed:\n" + failures.joinToString("\n") { "  - $it" }
            )
        }
    }
}
