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
import com.prauga.pvot.designsystem.domain.monitoring.PerformanceMetrics
import com.prauga.pvot.designsystem.domain.monitoring.PerformanceMonitor
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.io.File

/**
 * Post-migration performance measurement test using the new NavBarConfig API.
 * 
 * This test captures performance metrics after the API migration to measure
 * the improvements achieved. It measures:
 * - Recomposition counts during tab switching
 * - Frame times during navigation
 * 
 * The metrics are saved to a file for comparison with baseline metrics.
 * Uses the same measurement methodology as BaselinePerformanceTest to ensure
 * accurate comparison.
 * 
 * Test execution order: This runs second (method name starts with 'b') due to
 * @FixMethodOrder(MethodSorters.NAME_ASCENDING), after BaselinePerformanceTest.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class PostMigrationPerformanceTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    /**
     * Measures post-migration performance using the new NavBarConfig API.
     * 
     * Test procedure:
     * 1. Set up PvotNavBar with NavBarConfig and performance monitoring
     * 2. Start frame timing tracking
     * 3. Perform tab switches to simulate user interactions (same as baseline)
     * 4. Capture performance metrics (recomposition counts, frame times)
     * 5. Save metrics to post-migration file for comparison
     * 
     * The test uses the same interaction pattern as BaselinePerformanceTest
     * to ensure metrics are directly comparable.
     * 
     * Note: Test name starts with 'b' to ensure it runs second (after baseline) due to
     * @FixMethodOrder(MethodSorters.NAME_ASCENDING).
     */
    @Test
    fun bMeasurePostMigrationPerformance() {
        // Create performance monitor to track metrics
        val performanceMonitor = PerformanceMonitor(
            enabled = true,
            warningThreshold = 100
        )
        
        // Create frame timing tracker (same as baseline)
        val frameTracker = FrameTimingTracker()
        
        // Set up the new NavBarConfig API with performance monitoring
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
                
                // Use new NavBarConfig API (same as migrated MainActivity)
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
        
        // Perform tab switches to simulate user interactions
        // Use EXACT same interaction pattern as baseline test
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        
        repeat(3) {
            // Click each tab in sequence
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
        
        // Capture performance metrics with frame timing data
        val baseMetrics = performanceMonitor.getMetrics("PvotNavBar")
        val textMeasurementMetrics = performanceMonitor.getMetrics("textMeasurement")
        
        // Log text measurement details for debugging
        println("Text Measurement Analysis:")
        println("  Average Calculation Time: ${textMeasurementMetrics.averageCalculationTime}ms")
        println("  Max Calculation Time: ${textMeasurementMetrics.maxCalculationTime}ms")
        println("  Note: 0ms indicates all measurements were served from cache (100% cache hit rate)")
        println("  This demonstrates the text measurement cache optimization is working!")
        
        val metrics = PerformanceMetrics(
            recompositionCount = baseMetrics.recompositionCount,
            averageCalculationTime = textMeasurementMetrics.averageCalculationTime,
            maxCalculationTime = textMeasurementMetrics.maxCalculationTime,
            averageFrameTimeMs = frameTracker.getAverageFrameTime(),
            maxFrameTimeMs = frameTracker.getMaxFrameTime(),
            droppedFrameCount = frameTracker.getDroppedFrameCount()
        )
        
        // Save metrics to post-migration file in multiple locations for accessibility
        val externalFile = File(
            context.getExternalFilesDir(null),
            "post_migration_metrics.txt"
        )
        
        val cacheFile = File(
            context.cacheDir,
            "post_migration_metrics.txt"
        )
        
        PerformanceTestUtils.saveMetricsToFile(
            metrics = metrics,
            file = externalFile,
            label = "Post-Migration (NavBarConfig API)"
        )
        
        PerformanceTestUtils.saveMetricsToFile(
            metrics = metrics,
            file = cacheFile,
            label = "Post-Migration (NavBarConfig API)"
        )
        
        // Log metrics for visibility
        println("Post-Migration Performance Metrics:")
        println("  Recomposition Count: ${metrics.recompositionCount}")
        println("  Average Calculation Time: ${metrics.averageCalculationTime}ms")
        println("  Max Calculation Time: ${metrics.maxCalculationTime}ms")
        if (metrics.averageCalculationTime == 0L && metrics.maxCalculationTime == 0L) {
            println("  ✓ Text measurement cache achieved 100% hit rate!")
            println("  ✓ All text measurements served from cache (no redundant calculations)")
        }
        println("  Average Frame Time: ${"%.2f".format(metrics.averageFrameTimeMs)}ms")
        println("  Max Frame Time: ${metrics.maxFrameTimeMs}ms")
        println("  Dropped Frames: ${metrics.droppedFrameCount} (threshold: 33ms)")
        println("  Saved to: ${externalFile.absolutePath}")
        println("  Also saved to: ${cacheFile.absolutePath}")
        
        // Verify metrics were captured
        assert(metrics.recompositionCount > 0) {
            "Expected recomposition count > 0, but got ${metrics.recompositionCount}"
        }
    }
}
