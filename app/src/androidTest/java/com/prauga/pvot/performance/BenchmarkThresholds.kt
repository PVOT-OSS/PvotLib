// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.performance

import android.os.Build

/**
 * Configuration for performance benchmark thresholds.
 * 
 * Provides different threshold values for emulators vs physical devices,
 * accounting for the performance characteristics of each environment.
 * 
 * Thresholds can be customized by:
 * 1. Setting system properties (highest priority)
 * 2. Using default values based on device type detection
 * 
 * System property names:
 * - benchmark.threshold.recomposition.count
 * - benchmark.threshold.avg.frame.time
 * - benchmark.threshold.max.frame.time
 * - benchmark.threshold.dropped.frames
 * 
 * Example usage with custom thresholds:
 * ```
 * adb shell setprop benchmark.threshold.recomposition.count 100
 * ```
 */
data class BenchmarkThresholds(
    val maxRecompositionCount: Int,
    val maxAverageFrameTimeMs: Double,
    val maxFrameTimeMs: Long,
    val maxDroppedFrames: Int
) {
    companion object {
        /**
         * Detects if the current device is an emulator.
         * 
         * Uses multiple heuristics to determine if running on an emulator:
         * - Hardware name contains "goldfish" or "ranchu" (common emulator hardware)
         * - Fingerprint contains "generic" or "emulator"
         * - Model contains "Emulator" or "Android SDK"
         */
        private fun isEmulator(): Boolean {
            return (Build.HARDWARE.contains("goldfish") ||
                    Build.HARDWARE.contains("ranchu") ||
                    Build.FINGERPRINT.contains("generic") ||
                    Build.FINGERPRINT.contains("emulator") ||
                    Build.MODEL.contains("Emulator") ||
                    Build.MODEL.contains("Android SDK"))
        }
        
        /**
         * Gets a system property value as an integer.
         * 
         * @param key The system property key
         * @param defaultValue The default value if property is not set
         * @return The property value as an integer, or defaultValue if not set or invalid
         */
        private fun getSystemPropertyInt(key: String, defaultValue: Int): Int {
            return try {
                System.getProperty(key)?.toIntOrNull() ?: defaultValue
            } catch (e: Exception) {
                defaultValue
            }
        }
        
        /**
         * Gets a system property value as a double.
         * 
         * @param key The system property key
         * @param defaultValue The default value if property is not set
         * @return The property value as a double, or defaultValue if not set or invalid
         */
        private fun getSystemPropertyDouble(key: String, defaultValue: Double): Double {
            return try {
                System.getProperty(key)?.toDoubleOrNull() ?: defaultValue
            } catch (e: Exception) {
                defaultValue
            }
        }
        
        /**
         * Gets a system property value as a long.
         * 
         * @param key The system property key
         * @param defaultValue The default value if property is not set
         * @return The property value as a long, or defaultValue if not set or invalid
         */
        private fun getSystemPropertyLong(key: String, defaultValue: Long): Long {
            return try {
                System.getProperty(key)?.toLongOrNull() ?: defaultValue
            } catch (e: Exception) {
                defaultValue
            }
        }
        
        /**
         * Physical device thresholds (stricter performance requirements).
         * 
         * These values represent optimal performance on real hardware:
         * - Lower recomposition counts (better optimization)
         * - Faster frame times (60fps target)
         * - Fewer dropped frames (smoother animations)
         */
        private val PHYSICAL_DEVICE_THRESHOLDS = BenchmarkThresholds(
            maxRecompositionCount = 120,
            maxAverageFrameTimeMs = 16.0,  // 60fps
            maxFrameTimeMs = 33L,           // 30fps minimum
            maxDroppedFrames = 10
        )
        
        /**
         * Emulator thresholds (more lenient to account for virtualization overhead).
         * 
         * These values account for emulator performance characteristics:
         * - Higher recomposition counts (emulator overhead)
         * - Slower frame times (virtualization impact)
         * - More dropped frames (acceptable for testing environment)
         */
        private val EMULATOR_THRESHOLDS = BenchmarkThresholds(
            maxRecompositionCount = 150,
            maxAverageFrameTimeMs = 20.0,  // ~50fps (accounting for emulator overhead)
            maxFrameTimeMs = 100L,          // Allow occasional spikes
            maxDroppedFrames = 40
        )
        
        /**
         * Gets the appropriate thresholds for the current device.
         * 
         * Priority order:
         * 1. System properties (if set)
         * 2. Device-specific defaults (emulator vs physical device)
         * 
         * @return BenchmarkThresholds configured for the current environment
         */
        fun forCurrentDevice(): BenchmarkThresholds {
            val isEmulator = isEmulator()
            val defaults = if (isEmulator) EMULATOR_THRESHOLDS else PHYSICAL_DEVICE_THRESHOLDS
            
            // Allow overriding via system properties
            return BenchmarkThresholds(
                maxRecompositionCount = getSystemPropertyInt(
                    "benchmark.threshold.recomposition.count",
                    defaults.maxRecompositionCount
                ),
                maxAverageFrameTimeMs = getSystemPropertyDouble(
                    "benchmark.threshold.avg.frame.time",
                    defaults.maxAverageFrameTimeMs
                ),
                maxFrameTimeMs = getSystemPropertyLong(
                    "benchmark.threshold.max.frame.time",
                    defaults.maxFrameTimeMs
                ),
                maxDroppedFrames = getSystemPropertyInt(
                    "benchmark.threshold.dropped.frames",
                    defaults.maxDroppedFrames
                )
            )
        }
        
        /**
         * Gets thresholds explicitly for physical devices.
         * Useful for CI/CD pipelines running on real hardware.
         */
        fun forPhysicalDevice(): BenchmarkThresholds = PHYSICAL_DEVICE_THRESHOLDS
        
        /**
         * Gets thresholds explicitly for emulators.
         * Useful for local development and CI/CD pipelines using emulators.
         */
        fun forEmulator(): BenchmarkThresholds = EMULATOR_THRESHOLDS
    }
    
    /**
     * Returns a human-readable description of the thresholds.
     */
    override fun toString(): String {
        return """
            Benchmark Thresholds:
              Max Recomposition Count: $maxRecompositionCount
              Max Average Frame Time: ${maxAverageFrameTimeMs}ms
              Max Frame Time: ${maxFrameTimeMs}ms
              Max Dropped Frames: $maxDroppedFrames
        """.trimIndent()
    }
}
