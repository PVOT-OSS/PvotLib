// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.navigation

import com.prauga.pvot.designsystem.domain.animation.AnimationCoordinator
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Property-based tests for animation coordination in PvotNavBar.
 * 
 * **Property 10: Animation Prioritization**
 * For any set of animations, when the number of active animations exceeds the maximum,
 * visible animations should be prioritized over off-screen animations.
 * 
 */
class PvotNavBarAnimationCoordinationTest {

    private lateinit var coordinator: AnimationCoordinator

    @Before
    fun setup() {
        coordinator = AnimationCoordinator(maxConcurrentAnimations = 10)
    }

    // Property 10: Animation Prioritization
    @Test
    fun `animation coordinator should respect max concurrent animations limit`() {
        val maxAnimations = 5
        val testCoordinator = AnimationCoordinator(maxConcurrentAnimations = maxAnimations)

        // Register max animations
        for (i in 1..maxAnimations) {
            testCoordinator.registerAnimation("nav_item_$i")
        }

        // Active count should not exceed max
        assertTrue(testCoordinator.getActiveAnimationCount() <= maxAnimations)
    }

    @Test
    fun `registered animations should be allowed to animate`() {
        val animationId = "nav_item_0"

        coordinator.registerAnimation(animationId)

        // Registered animation should be allowed
        assertTrue(coordinator.shouldAnimate(animationId))
    }

    @Test
    fun `unregistered animations should be blocked when limit reached`() {
        val maxAnimations = 3
        val testCoordinator = AnimationCoordinator(maxConcurrentAnimations = maxAnimations)

        // Register max animations
        for (i in 0 until maxAnimations) {
            testCoordinator.registerAnimation("nav_item_$i")
        }

        // New unregistered animation should be blocked
        assertFalse(testCoordinator.shouldAnimate("new_nav_item"))
    }

    @Test
    fun `unregistering animation should free up slot`() {
        val maxAnimations = 3
        val testCoordinator = AnimationCoordinator(maxConcurrentAnimations = maxAnimations)

        // Register max animations
        for (i in 0 until maxAnimations) {
            testCoordinator.registerAnimation("nav_item_$i")
        }

        // Unregister one
        testCoordinator.unregisterAnimation("nav_item_0")

        // Active count should decrease
        assertEquals(maxAnimations - 1, testCoordinator.getActiveAnimationCount())
    }

    @Test
    fun `animation count should never exceed limit`() {
        val maxAnimations = 5
        val testCoordinator = AnimationCoordinator(maxConcurrentAnimations = maxAnimations)

        // Try to register many animations
        for (i in 0 until 20) {
            testCoordinator.registerAnimation("nav_item_$i")
        }

        // Count should not exceed max
        assertTrue(testCoordinator.getActiveAnimationCount() <= maxAnimations)
    }

    @Test
    fun `multiple register calls for same ID should not increase count`() {
        val animationId = "nav_item_0"

        coordinator.registerAnimation(animationId)
        val countAfterFirst = coordinator.getActiveAnimationCount()

        // Register same ID again
        coordinator.registerAnimation(animationId)
        val countAfterSecond = coordinator.getActiveAnimationCount()

        // Count should be the same
        assertEquals(countAfterFirst, countAfterSecond)
    }

    @Test
    fun `unregistering non-existent animation should be safe`() {
        val initialCount = coordinator.getActiveAnimationCount()

        // Unregister non-existent animation
        coordinator.unregisterAnimation("non_existent")

        // Count should not change
        assertEquals(initialCount, coordinator.getActiveAnimationCount())
    }

    @Test
    fun `registered animations should always be allowed even at limit`() {
        val maxAnimations = 3
        val testCoordinator = AnimationCoordinator(maxConcurrentAnimations = maxAnimations)

        // Register animations up to limit
        for (i in 0 until maxAnimations) {
            testCoordinator.registerAnimation("nav_item_$i")
        }

        // All registered animations should still be allowed
        for (i in 0 until maxAnimations) {
            assertTrue(testCoordinator.shouldAnimate("nav_item_$i"))
        }
    }

    @Test
    fun `animation coordinator should handle nav bar use case`() {
        // Simulate navigation bar with 5 tabs
        val tabCount = 5

        for (i in 0 until tabCount) {
            val animationId = "nav_item_$i"
            coordinator.registerAnimation(animationId)
            assertTrue(coordinator.shouldAnimate(animationId))
        }

        // All tabs should be able to animate
        assertEquals(tabCount, coordinator.getActiveAnimationCount())
    }
}
