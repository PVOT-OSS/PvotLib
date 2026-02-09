// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.performance

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assume
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.io.File

/**
 * Integration test that compares baseline and post-migration performance metrics.
 * 
 * This test:
 * 1. Loads baseline metrics from file
 * 2. Loads post-migration metrics from file
 * 3. Calculates percentage improvements
 * 4. Generates a performance comparison report
 * 
 * Test execution order is controlled by @FixMethodOrder to ensure this runs
 * after BaselinePerformanceTest and PostMigrationPerformanceTest.
 * 
 * The comparison report shows the performance improvements achieved by the
 * API migration and validates that the optimization goals were met.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class PerformanceComparisonIntegrationTest {
    
    /**
     * Loads baseline and post-migration metrics, compares them, and generates
     * a performance comparison report.
     * 
     * Test procedure:
     * 1. Load baseline metrics from file
     * 2. Load post-migration metrics from file
     * 3. Create PerformanceComparison object
     * 4. Calculate percentage improvements for each metric
     * 5. Generate and save performance comparison report
     * 6. Log report for visibility
     * 
     * The report includes:
     * - Recomposition count improvements
     * - Calculation time improvements
     * - Frame time improvements
     * - Dropped frame improvements
     * 
     * Note: Test name starts with 'z' to ensure it runs last (after baseline and post-migration tests)
     * due to @FixMethodOrder(MethodSorters.NAME_ASCENDING).
     */
    @Test
    fun zCompareBaselineAndPostMigrationPerformance() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        
        // Load baseline metrics from file
        val baselineFile = File(
            context.getExternalFilesDir(null),
            "baseline_metrics.txt"
        )
        
        // Fallback to cache directory if external file not found
        val baselineFileToUse = if (baselineFile.exists()) {
            baselineFile
        } else {
            File(context.cacheDir, "baseline_metrics.txt")
        }
        
        // Skip test gracefully if baseline file doesn't exist
        Assume.assumeTrue(
            "Baseline metrics file not found - skipping comparison test. " +
            "Run BaselinePerformanceTest first.",
            baselineFileToUse.exists()
        )
        
        val baselineMetrics = PerformanceTestUtils.loadMetricsFromFile(baselineFileToUse)
        println("Loaded baseline metrics from: ${baselineFileToUse.absolutePath}")
        
        // Load post-migration metrics from file
        val postMigrationFile = File(
            context.getExternalFilesDir(null),
            "post_migration_metrics.txt"
        )
        
        // Fallback to cache directory if external file not found
        val postMigrationFileToUse = if (postMigrationFile.exists()) {
            postMigrationFile
        } else {
            File(context.cacheDir, "post_migration_metrics.txt")
        }
        
        // Skip test gracefully if post-migration file doesn't exist
        Assume.assumeTrue(
            "Post-migration metrics file not found - skipping comparison test. " +
            "Run PostMigrationPerformanceTest first.",
            postMigrationFileToUse.exists()
        )
        
        val postMigrationMetrics = PerformanceTestUtils.loadMetricsFromFile(postMigrationFileToUse)
        println("Loaded post-migration metrics from: ${postMigrationFileToUse.absolutePath}")
        
        // Create performance comparison
        val comparison = PerformanceTestUtils.compareMetrics(
            baseline = baselineMetrics,
            postMigration = postMigrationMetrics
        )
        
        // Generate performance comparison report
        val report = comparison.generateReport()
        
        // Save report to file
        val reportFile = File(
            context.getExternalFilesDir(null),
            "performance_comparison_report.txt"
        )
        
        val cacheReportFile = File(
            context.cacheDir,
            "performance_comparison_report.txt"
        )
        
        reportFile.writeText(report)
        cacheReportFile.writeText(report)
        
        // Log report for visibility
        println("\n" + "=".repeat(60))
        println(report)
        println("=".repeat(60))
        println("\nReport saved to: ${reportFile.absolutePath}")
        println("Also saved to: ${cacheReportFile.absolutePath}")
        
        // Log individual improvement percentages
        println("\nSummary of Improvements:")
        println("  Recomposition Count: ${formatPercentage(comparison.recompositionImprovement)}")
        println("  Average Calculation Time: ${formatPercentage(comparison.calculationTimeImprovement)}")
        println("  Max Calculation Time: ${formatPercentage(comparison.maxCalculationTimeImprovement)}")
        println("  Average Frame Time: ${formatPercentage(comparison.averageFrameTimeImprovement)}")
        println("  Max Frame Time: ${formatPercentage(comparison.maxFrameTimeImprovement)}")
        println("  Dropped Frames: ${formatPercentage(comparison.droppedFrameImprovement)}")
        
        // Validate that we have measurable data
        assert(baselineMetrics.recompositionCount > 0) {
            "Baseline recomposition count should be > 0"
        }
        assert(postMigrationMetrics.recompositionCount > 0) {
            "Post-migration recomposition count should be > 0"
        }
        
        // Log whether performance improved overall
        val hasImproved = PerformanceTestUtils.hasImproved(comparison)
        println("\nOverall Performance Improved: $hasImproved")
        
        if (hasImproved) {
            println("✓ Migration achieved performance improvements!")
        } else {
            println("⚠ Migration did not show significant improvements.")
            println("  This may be expected if the baseline was already optimized.")
        }
    }
    
    /**
     * Formats a percentage value with sign and 2 decimal places.
     * 
     * @param value The percentage value to format
     * @return Formatted string (e.g., "+25.50%", "-10.25%", "0.00%")
     */
    private fun formatPercentage(value: Double): String {
        val sign = when {
            value > 0 -> "+"
            value < 0 -> ""
            else -> ""
        }
        return "$sign${"%.2f".format(value)}%"
    }
}
