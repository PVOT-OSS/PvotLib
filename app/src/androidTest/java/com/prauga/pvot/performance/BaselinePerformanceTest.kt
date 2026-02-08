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
import com.prauga.pvot.designsystem.components.navigation.PvotNavBar
import com.prauga.pvot.designsystem.components.navigation.PvotTabItem
import com.prauga.pvot.designsystem.domain.monitoring.PerformanceMonitor
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Baseline performance measurement test using the deprecated PvotNavBar API.
 * 
 * This test captures performance metrics before the API migration to establish
 * a baseline for comparison. It measures:
 * - Recomposition counts during tab switching
 * - Frame times during navigation
 * 
 * The metrics are saved to a file for later comparison with post-migration metrics.
 * 
 */
@RunWith(AndroidJUnit4::class)
class BaselinePerformanceTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    /**
     * Measures baseline performance using the deprecated PvotNavBar API.
     * 
     * Test procedure:
     * 1. Set up PvotNavBar with deprecated API and performance monitoring
     * 2. Perform tab switches to simulate user interactions
     * 3. Capture performance metrics (recomposition counts, frame times)
     * 4. Save metrics to baseline file for later comparison
     * 
     * The test uses the same API signature as MainActivity to ensure accurate
     * baseline measurement.
     */
    @Test
    fun measureBaselinePerformance() {
        // Create performance monitor to track metrics
        val performanceMonitor = PerformanceMonitor(
            enabled = true,
            warningThreshold = 100
        )
        
        // Set up the deprecated API behavior with performance monitoring
        // Note: We pass performanceMonitor to the deprecated API to enable measurement
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
                
                // Use deprecated API (same as current MainActivity)
                // Pass performanceMonitor to enable baseline measurement
                @Suppress("DEPRECATION")
                PvotNavBar(
                    selectedTab = selectedTab,
                    onTabClick = { selectedTab = it },
                    tabs = tabs,
                    performanceMonitor = performanceMonitor
                )
            }
        }
        
        // Wait for initial composition
        composeTestRule.waitForIdle()
        
        // Perform tab switches to simulate user interactions
        // Switch through all tabs multiple times to get representative metrics
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        
        repeat(3) { cycle ->
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
        
        // Capture performance metrics
        val metrics = performanceMonitor.getMetrics("PvotNavBar")
        
        // Save metrics to baseline file in multiple locations for accessibility
        val externalFile = File(
            context.getExternalFilesDir(null),
            "baseline_metrics.txt"
        )
        
        val cacheFile = File(
            context.cacheDir,
            "baseline_metrics.txt"
        )
        
        PerformanceTestUtils.saveMetricsToFile(
            metrics = metrics,
            file = externalFile,
            label = "Baseline (Deprecated API)"
        )
        
        PerformanceTestUtils.saveMetricsToFile(
            metrics = metrics,
            file = cacheFile,
            label = "Baseline (Deprecated API)"
        )
        
        // Log metrics for visibility
        println("Baseline Performance Metrics:")
        println("  Recomposition Count: ${metrics.recompositionCount}")
        println("  Average Calculation Time: ${metrics.averageCalculationTime}ms")
        println("  Max Calculation Time: ${metrics.maxCalculationTime}ms")
        println("  Saved to: ${externalFile.absolutePath}")
        println("  Also saved to: ${cacheFile.absolutePath}")
        
        // Verify metrics were captured
        assert(metrics.recompositionCount > 0) {
            "Expected recomposition count > 0, but got ${metrics.recompositionCount}"
        }
    }
}
