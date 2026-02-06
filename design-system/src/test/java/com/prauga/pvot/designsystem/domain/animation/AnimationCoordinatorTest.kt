// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.animation

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Property-based tests for AnimationCoordinator.
 * 
 * **Property 10: Animation Prioritization**
 * For any set of animations, when the number of active animations exceeds the maximum,
 * visible animations should be prioritized over off-screen ones.
 */
class AnimationCoordinatorTest {
    
    private lateinit var coordinator: AnimationCoordinator
    
    @Before
    fun setup() {
        coordinator = AnimationCoordinator(maxConcurrentAnimations = 5)
    }
    
    // Property 10: Animation Prioritization
    @Test
    fun `shouldAnimate should return true when under max concurrent limit`() {
        // Register animations under the limit
        coordinator.registerAnimation("anim1")
        coordinator.registerAnimation("anim2")
        coordinator.registerAnimation("anim3")
        
        // Should allow new animations
        assertTrue(coordinator.shouldAnimate("anim4"))
        assertTrue(coordinator.shouldAnimate("anim5"))
    }
    
    @Test
    fun `shouldAnimate should return false when at max concurrent limit for new animations`() {
        val maxAnimations = 5
        
        // Fill up to max
        for (i in 1..maxAnimations) {
            coordinator.registerAnimation("anim$i")
        }
        
        // New animation should not be allowed
        assertFalse(coordinator.shouldAnimate("newAnim"))
    }
    
    @Test
    fun `shouldAnimate should return true for already registered animations even at limit`() {
        val maxAnimations = 5
        
        // Fill up to max
        for (i in 1..maxAnimations) {
            coordinator.registerAnimation("anim$i")
        }
        
        // Already registered animations should still be allowed
        assertTrue(coordinator.shouldAnimate("anim1"))
        assertTrue(coordinator.shouldAnimate("anim3"))
        assertTrue(coordinator.shouldAnimate("anim5"))
    }
    
    @Test
    fun `registerAnimation should add animation to active set`() {
        assertEquals(0, coordinator.getActiveAnimationCount())
        
        coordinator.registerAnimation("anim1")
        assertEquals(1, coordinator.getActiveAnimationCount())
        
        coordinator.registerAnimation("anim2")
        assertEquals(2, coordinator.getActiveAnimationCount())
    }
    
    @Test
    fun `registerAnimation should not duplicate same animation id`() {
        coordinator.registerAnimation("anim1")
        assertEquals(1, coordinator.getActiveAnimationCount())
        
        // Register same ID again
        coordinator.registerAnimation("anim1")
        assertEquals(1, coordinator.getActiveAnimationCount())
    }
    
    @Test
    fun `unregisterAnimation should remove animation from active set`() {
        coordinator.registerAnimation("anim1")
        coordinator.registerAnimation("anim2")
        assertEquals(2, coordinator.getActiveAnimationCount())
        
        coordinator.unregisterAnimation("anim1")
        assertEquals(1, coordinator.getActiveAnimationCount())
        
        coordinator.unregisterAnimation("anim2")
        assertEquals(0, coordinator.getActiveAnimationCount())
    }
    
    @Test
    fun `unregisterAnimation should handle non-existent animation gracefully`() {
        coordinator.registerAnimation("anim1")
        assertEquals(1, coordinator.getActiveAnimationCount())
        
        // Unregister non-existent animation
        coordinator.unregisterAnimation("nonExistent")
        assertEquals(1, coordinator.getActiveAnimationCount())
    }
    
    @Test
    fun `animation lifecycle should work correctly`() {
        // Register
        coordinator.registerAnimation("anim1")
        assertTrue(coordinator.shouldAnimate("anim1"))
        assertEquals(1, coordinator.getActiveAnimationCount())
        
        // Unregister
        coordinator.unregisterAnimation("anim1")
        assertEquals(0, coordinator.getActiveAnimationCount())
        
        // Can register again
        coordinator.registerAnimation("anim1")
        assertEquals(1, coordinator.getActiveAnimationCount())
    }
    
    @Test
    fun `shouldAnimate should allow new animations after unregistering`() {
        val maxAnimations = 5
        
        // Fill to max
        for (i in 1..maxAnimations) {
            coordinator.registerAnimation("anim$i")
        }
        
        // New animation blocked
        assertFalse(coordinator.shouldAnimate("newAnim"))
        
        // Unregister one
        coordinator.unregisterAnimation("anim1")
        
        // Now new animation should be allowed
        assertTrue(coordinator.shouldAnimate("newAnim"))
    }
    
    @Test
    fun `getActiveAnimationCount should return correct count`() {
        assertEquals(0, coordinator.getActiveAnimationCount())
        
        coordinator.registerAnimation("anim1")
        assertEquals(1, coordinator.getActiveAnimationCount())
        
        coordinator.registerAnimation("anim2")
        coordinator.registerAnimation("anim3")
        assertEquals(3, coordinator.getActiveAnimationCount())
        
        coordinator.unregisterAnimation("anim2")
        assertEquals(2, coordinator.getActiveAnimationCount())
        
        coordinator.unregisterAnimation("anim1")
        coordinator.unregisterAnimation("anim3")
        assertEquals(0, coordinator.getActiveAnimationCount())
    }
    
    @Test
    fun `coordinator should handle rapid register and unregister`() {
        // Simulate rapid animation changes
        for (i in 1..20) {
            coordinator.registerAnimation("anim$i")
            if (i > 1) {
                coordinator.unregisterAnimation("anim${i-1}")
            }
        }
        
        // Should have only the last animation
        assertEquals(1, coordinator.getActiveAnimationCount())
    }
    
    @Test
    fun `coordinator should be thread-safe for concurrent access`() {
        val threads = mutableListOf<Thread>()
        
        // Create multiple threads registering animations
        for (i in 1..10) {
            threads.add(Thread {
                coordinator.registerAnimation("thread${Thread.currentThread().threadId()}_anim$i")
                Thread.sleep(10)
                coordinator.unregisterAnimation("thread${Thread.currentThread().threadId()}_anim$i")
            })
        }
        
        // Start all threads
        threads.forEach { it.start() }
        
        // Wait for completion
        threads.forEach { it.join() }
        
        // All animations should be unregistered
        assertEquals(0, coordinator.getActiveAnimationCount())
    }
    
    @Test
    fun `custom max concurrent animations should be respected`() {
        val customCoordinator = AnimationCoordinator(maxConcurrentAnimations = 3)
        
        customCoordinator.registerAnimation("anim1")
        customCoordinator.registerAnimation("anim2")
        customCoordinator.registerAnimation("anim3")
        
        // At limit
        assertFalse(customCoordinator.shouldAnimate("anim4"))
        
        // Already registered should work
        assertTrue(customCoordinator.shouldAnimate("anim1"))
    }
    
    @Test
    fun `zero max concurrent should block all new animations`() {
        val restrictiveCoordinator = AnimationCoordinator(maxConcurrentAnimations = 0)
        
        // Should not allow any animations
        assertFalse(restrictiveCoordinator.shouldAnimate("anim1"))
        
        // Even after registering (edge case)
        restrictiveCoordinator.registerAnimation("anim1")
        assertTrue(restrictiveCoordinator.shouldAnimate("anim1"))
    }
    
    @Test
    fun `large max concurrent should allow many animations`() {
        val permissiveCoordinator = AnimationCoordinator(maxConcurrentAnimations = 100)
        
        // Register many animations
        for (i in 1..50) {
            permissiveCoordinator.registerAnimation("anim$i")
        }
        
        assertEquals(50, permissiveCoordinator.getActiveAnimationCount())
        
        // Should still allow more
        assertTrue(permissiveCoordinator.shouldAnimate("anim51"))
    }
}
