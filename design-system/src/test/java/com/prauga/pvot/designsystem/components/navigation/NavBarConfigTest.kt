// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.navigation

import androidx.compose.ui.unit.dp
import org.junit.Assert.*
import org.junit.Test

/**
 * Property-based tests for NavBarConfig.
 * 
 * **Property 13: Configuration Defaults**
 * For any configuration object, all optional parameters should have sensible default values
 * that allow the object to be constructed with minimal arguments.
 * 
 */
class NavBarConfigTest {
    
    // Property 13: Configuration Defaults
    @Test
    fun `NavBarConfig should have sensible defaults for all optional parameters`() {
        // Feature: design-system-optimization, Property 13: Configuration Defaults
        
        // Test with various tab counts
        for (tabCount in 1..10) {
            val tabs = (1..tabCount).map { index ->
                PvotTabItem(
                    iconRes = index,
                    labelRes = index,
                    contentDescriptionRes = index
                )
            }
            
            for (selectedIndex in 0 until tabCount) {
                // Create config with only required parameters
                val config = NavBarConfig(
                    tabs = tabs,
                    selectedTab = selectedIndex
                )
                
                // Verify appearance has defaults
                assertNotNull(config.appearance)
                assertNotNull(config.appearance.sizes)
                assertNotNull(config.appearance.colors)
                
                // Verify behavior has defaults
                assertNotNull(config.behavior)
                assertTrue(config.behavior.enableHapticFeedback)
                assertEquals(240, config.behavior.animationDuration)
                assertFalse(config.behavior.enablePerformanceMonitoring)
            }
        }
    }
    
    @Test
    fun `NavBarAppearance should have sensible defaults`() {
        // Feature: design-system-optimization, Property 13: Configuration Defaults
        
        // Test multiple instances to ensure consistency
        for (i in 1..100) {
            val appearance = NavBarAppearance()
            
            // Verify sizes has defaults
            assertNotNull(appearance.sizes)
            assertEquals(64.dp, appearance.sizes.barHeight)
            assertEquals(24.dp, appearance.sizes.collapsedIconSize)
            assertEquals(24.dp, appearance.sizes.expandedIconSize)
            
            // Verify colors has defaults
            assertNotNull(appearance.colors)
        }
    }
    
    @Test
    fun `NavBarBehavior should have sensible defaults`() {
        // Feature: design-system-optimization, Property 13: Configuration Defaults
        
        // Test multiple instances to ensure consistency
        for (i in 1..100) {
            val behavior = NavBarBehavior()
            
            // Verify all defaults are sensible
            assertTrue(behavior.enableHapticFeedback)
            assertEquals(240, behavior.animationDuration)
            assertFalse(behavior.enablePerformanceMonitoring)
        }
    }
    
    @Test
    fun `NavBarConfig should allow minimal construction with defaults`() {
        val tabs = listOf(
            PvotTabItem(iconRes = 1, labelRes = 1, contentDescriptionRes = 1),
            PvotTabItem(iconRes = 2, labelRes = 2, contentDescriptionRes = 2)
        )
        
        // Should be constructible with just required parameters
        val config = NavBarConfig(
            tabs = tabs,
            selectedTab = 0
        )
        
        // Verify it's valid
        assertEquals(2, config.tabs.size)
        assertEquals(0, config.selectedTab)
        assertNotNull(config.appearance)
        assertNotNull(config.behavior)
    }
    
    @Test
    fun `NavBarConfig defaults should be consistent across multiple instances`() {
        val tabs = listOf(
            PvotTabItem(iconRes = 1, labelRes = 1, contentDescriptionRes = 1)
        )
        
        val config1 = NavBarConfig(tabs = tabs, selectedTab = 0)
        val config2 = NavBarConfig(tabs = tabs, selectedTab = 0)
        
        // Defaults should be identical
        assertEquals(config1.behavior.enableHapticFeedback, config2.behavior.enableHapticFeedback)
        assertEquals(config1.behavior.animationDuration, config2.behavior.animationDuration)
        assertEquals(config1.behavior.enablePerformanceMonitoring, config2.behavior.enablePerformanceMonitoring)
        assertEquals(config1.appearance.sizes.barHeight, config2.appearance.sizes.barHeight)
    }
}
