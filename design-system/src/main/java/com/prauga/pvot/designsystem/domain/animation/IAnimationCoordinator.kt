// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.domain.animation

/**
 * Coordinates animations to prevent performance issues.
 * 
 * Tracks active animations and limits concurrent animations to maintain performance.
 */
interface IAnimationCoordinator {
    /**
     * Registers an animation as active.
     * 
     * @param id Unique identifier for the animation
     */
    fun registerAnimation(id: String)
    
    /**
     * Unregisters an animation when it completes.
     * 
     * @param id Unique identifier for the animation
     */
    fun unregisterAnimation(id: String)
    
    /**
     * Checks if an animation should run based on current load.
     * 
     * @param id Unique identifier for the animation
     * @return true if the animation should run, false if it should be skipped
     */
    fun shouldAnimate(id: String): Boolean
    
    /**
     * Gets the current number of active animations.
     * 
     * @return Count of active animations
     */
    fun getActiveAnimationCount(): Int
}
