// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.performance

import android.view.Choreographer
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
 * Frame timing tracker using Choreographer to measure frame rendering times.
 * 
 * Note: In instrumented tests, this measures the time between test interaction cycles
 * (click + animation + waitForIdle), not individual animation frames. The metrics
 * represent overall interaction performance rather than per-frame rendering times.
 */
class FrameTimingTracker {
    private val frameTimes = mutableListOf<Long>()
    private var lastFrameTime = 0L
    private var isTracking = false
    
    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (isTracking && lastFrameTime > 0) {
                val frameTimeMs = (frameTimeNanos - lastFrameTime) / 1_000_000
                synchronized(frameTimes) {
                    frameTimes.add(frameTimeMs)
                }
            }
            lastFrameTime = frameTimeNanos
            
            if (isTracking) {
                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    Choreographer.getInstance().postFrameCallback(this)
                }
            }
        }
    }
    
    fun startTracking() {
        isTracking = true
        lastFrameTime = 0
        synchronized(frameTimes) {
            frameTimes.clear()
        }
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            Choreographer.getInstance().postFrameCallback(frameCallback)
        }
    }
    
    fun stopTracking() {
        isTracking = false
    }
    
    fun getAverageFrameTime(): Double {
        return synchronized(frameTimes) {
            if (frameTimes.isNotEmpty()) {
                frameTimes.average()
            } else {
                0.0
            }
        }
    }
    
    fun getMaxFrameTime(): Long {
        return synchronized(frameTimes) {
            frameTimes.maxOrNull() ?: 0L
        }
    }
    
    fun getDroppedFrameCount(): Int {
        // In test environment, we measure interaction cycles (click + animation + idle)
        // not individual animation frames. A "dropped" interaction is one that takes
        // significantly longer than average, indicating performance issues.
        // Use 2x the 60fps target (33ms) as threshold for test interactions.
        return synchronized(frameTimes) {
            frameTimes.count { it > 33 }
        }
    }
}

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
 * Test execution order: This runs first (method name starts with 'a') due to
 * @FixMethodOrder(MethodSorters.NAME_ASCENDING).
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class BaselinePerformanceTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    /**
     * Measures baseline performance using the deprecated PvotNavBar API.
     * 
     * Test procedure:
     * 1. Set up PvotNavBar with deprecated API and performance monitoring
     * 2. Start frame timing tracking
     * 3. Perform tab switches to simulate user interactions
     * 4. Capture performance metrics (recomposition counts, frame times)
     * 5. Save metrics to baseline file for later comparison
     * 
     * The test uses the same API signature as MainActivity to ensure accurate
     * baseline measurement.
     * 
     * Note: Test name starts with 'a' to ensure it runs first due to
     * @FixMethodOrder(MethodSorters.NAME_ASCENDING).
     */
    @Test
    fun aMeasureBaselinePerformance() {
        // Create performance monitor to track metrics
        val performanceMonitor = PerformanceMonitor(
            enabled = true,
            warningThreshold = 100
        )
        
        // Create frame timing tracker
        val frameTracker = FrameTimingTracker()
        
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
        
        // Start frame timing tracking
        frameTracker.startTracking()
        
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
        
        // Stop frame timing tracking
        frameTracker.stopTracking()
        
        // Capture performance metrics with frame timing data
        val baseMetrics = performanceMonitor.getMetrics("PvotNavBar")
        val textMeasurementMetrics = performanceMonitor.getMetrics("textMeasurement")
        
        val metrics = PerformanceMetrics(
            recompositionCount = baseMetrics.recompositionCount,
            averageCalculationTime = textMeasurementMetrics.averageCalculationTime,
            maxCalculationTime = textMeasurementMetrics.maxCalculationTime,
            averageFrameTimeMs = frameTracker.getAverageFrameTime(),
            maxFrameTimeMs = frameTracker.getMaxFrameTime(),
            droppedFrameCount = frameTracker.getDroppedFrameCount()
        )
        
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
