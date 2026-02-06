// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.animation

/**
 * Thread-safe animation coordinator implementation.
 * 
 * Limits concurrent animations to prevent performance degradation.
 * When the limit is reached, only already-registered animations are allowed to continue.
 * 
 * @param maxConcurrentAnimations Maximum number of animations that can run simultaneously (default: 10)
 */
class AnimationCoordinator(
    private val maxConcurrentAnimations: Int = 10
) : IAnimationCoordinator {
    
    private val activeAnimations = mutableSetOf<String>()
    
    override fun registerAnimation(id: String) {
        synchronized(activeAnimations) {
            activeAnimations.add(id)
        }
    }
    
    override fun unregisterAnimation(id: String) {
        synchronized(activeAnimations) {
            activeAnimations.remove(id)
        }
    }
    
    override fun shouldAnimate(id: String): Boolean {
        return synchronized(activeAnimations) {
            activeAnimations.size < maxConcurrentAnimations || id in activeAnimations
        }
    }
    
    override fun getActiveAnimationCount(): Int {
        return synchronized(activeAnimations) {
            activeAnimations.size
        }
    }
}
