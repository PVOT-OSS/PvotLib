// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.testutil

import com.prauga.pvot.designsystem.domain.animation.IAnimationCoordinator
import com.prauga.pvot.designsystem.domain.cache.ITextMeasurementCache
import com.prauga.pvot.designsystem.domain.monitoring.IPerformanceMonitor
import com.prauga.pvot.designsystem.domain.monitoring.PerformanceMetrics
import com.prauga.pvot.designsystem.domain.scroll.IScrollCalculator
import com.prauga.pvot.designsystem.domain.transform.ITransformEngine
import com.prauga.pvot.designsystem.domain.transform.ItemTransform
import com.prauga.pvot.designsystem.domain.validation.IValidationEngine
import com.prauga.pvot.designsystem.domain.validation.ValidationResult
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * Factory for creating mock domain layer dependencies.
 * Provides pre-configured mocks with sensible default behaviors.
 */
object MockFactories {
    
    /**
     * Creates a mock TransformEngine with default behavior.
     */
    fun createMockTransformEngine(): ITransformEngine = mock {
        whenever(it.calculateTransform(any(), any(), any(), any(), any())).thenReturn(
            ItemTransform(rotationX = 0f, scale = 1f, alpha = 1f)
        )
    }
    
    /**
     * Creates a mock ScrollCalculator with default behavior.
     */
    fun createMockScrollCalculator(): IScrollCalculator = mock {
        whenever(it.calculateVisibleRange(any(), any(), any(), any())).thenReturn(0..5)
        whenever(it.isItemVisible(any(), any())).thenReturn(true)
    }
    
    /**
     * Creates a mock TextMeasurementCache with default behavior.
     */
    fun createMockTextCache(): ITextMeasurementCache = mock()
    
    /**
     * Creates a mock AnimationCoordinator with default behavior.
     */
    fun createMockAnimationCoordinator(
        shouldAnimate: Boolean = true,
        activeCount: Int = 0
    ): IAnimationCoordinator = mock {
        whenever(it.shouldAnimate(any())).thenReturn(shouldAnimate)
        whenever(it.getActiveAnimationCount()).thenReturn(activeCount)
    }
    
    /**
     * Creates a mock ValidationEngine with default behavior.
     */
    fun createMockValidationEngine(
        isValid: Boolean = true
    ): IValidationEngine = mock {
        val result = if (isValid) ValidationResult.Valid else ValidationResult.Invalid(emptyList())
        whenever(it.validateNavBarConfig(any(), any())).thenReturn(result)
        whenever(it.validateWheelConfig(any(), any())).thenReturn(result)
    }
    
    /**
     * Creates a mock PerformanceMonitor with default behavior.
     */
    fun createMockPerformanceMonitor(): IPerformanceMonitor = mock {
        whenever(it.getMetrics(any())).thenReturn(
            PerformanceMetrics(
                recompositionCount = 0,
                averageCalculationTime = 0L,
                maxCalculationTime = 0L
            )
        )
    }
}
