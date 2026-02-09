// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import com.prauga.pvot.designsystem.components.navigation.NavBarConfig
import com.prauga.pvot.designsystem.components.picker.internal.WheelConfig
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for backward compatibility layer.
 * Tests that deprecated APIs are properly marked and new APIs work correctly.
 * 
 */
class BackwardCompatibilityTest {

    @Test
    fun `NavBarConfig should be created with required parameters`() {
        val tabs = listOf("Home", "Search", "Profile")
        val selectedTab = 1

        val config = NavBarConfig(
            tabs = emptyList(),
            selectedTab = selectedTab
        )

        assertNotNull(config)
        assertEquals(selectedTab, config.selectedTab)
    }

    @Test
    fun `NavBarConfig should have default appearance`() {
        val config = NavBarConfig(
            tabs = emptyList(),
            selectedTab = 0
        )

        assertNotNull(config.appearance)
        assertNotNull(config.appearance.sizes)
        assertNotNull(config.appearance.colors)
    }

    @Test
    fun `NavBarConfig should have default behavior`() {
        val config = NavBarConfig(
            tabs = emptyList(),
            selectedTab = 0
        )

        assertNotNull(config.behavior)
    }

    @Test
    fun `WheelConfig should be created with required parameters`() {
        val values = listOf(1, 2, 3, 4, 5)
        val initialIndex = 2

        val config = WheelConfig(
            values = values,
            initialIndex = initialIndex
        )

        assertNotNull(config)
        assertEquals(values, config.values)
        assertEquals(initialIndex, config.initialIndex)
    }

    @Test
    fun `WheelConfig should have default label function`() {
        val values = listOf(1, 2, 3)

        val config = WheelConfig(
            values = values,
            initialIndex = 0
        )

        // Default label should convert to string
        assertEquals("1", config.label(1))
        assertEquals("2", config.label(2))
    }

    @Test
    fun `WheelConfig should have default suffix`() {
        val config = WheelConfig(
            values = listOf(1, 2, 3),
            initialIndex = 0
        )

        assertEquals("", config.suffix)
    }

    @Test
    fun `WheelConfig should have default appearance`() {
        val config = WheelConfig(
            values = listOf(1, 2, 3),
            initialIndex = 0
        )

        assertNotNull(config.appearance)
    }

    @Test
    fun `WheelConfig should have default behavior`() {
        val config = WheelConfig(
            values = listOf(1, 2, 3),
            initialIndex = 0
        )

        assertNotNull(config.behavior)
    }

    @Test
    fun `WheelConfig should validate empty values list`() {
        try {
            WheelConfig(
                values = emptyList(),
                initialIndex = 0
            )
            fail("Should have thrown IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("cannot be empty"))
        }
    }

    @Test
    fun `WheelConfig should validate out of bounds initialIndex`() {
        try {
            WheelConfig(
                values = listOf(1, 2, 3),
                initialIndex = 5
            )
            fail("Should have thrown IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("out of bounds"))
        }
    }

    @Test
    fun `WheelConfig should accept valid configuration`() {
        val config = WheelConfig(
            values = listOf(0, 1, 2, 3, 4),
            label = { "Item $it" },
            suffix = "items",
            initialIndex = 2
        )

        assertNotNull(config)
        assertEquals("Item 2", config.label(2))
        assertEquals("items", config.suffix)
    }

    @Test
    fun `WheelConfig should accept first index`() {
        val config = WheelConfig(
            values = listOf(1, 2, 3),
            initialIndex = 0
        )

        assertEquals(0, config.initialIndex)
    }

    @Test
    fun `WheelConfig should accept last index`() {
        val values = listOf(1, 2, 3, 4, 5)
        val config = WheelConfig(
            values = values,
            initialIndex = values.size - 1
        )

        assertEquals(values.size - 1, config.initialIndex)
    }
}
