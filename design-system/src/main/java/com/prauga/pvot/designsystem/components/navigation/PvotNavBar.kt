// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.navigation

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.domain.animation.AnimationCoordinator
import com.prauga.pvot.designsystem.domain.animation.IAnimationCoordinator
import com.prauga.pvot.designsystem.domain.cache.ITextMeasurementCache
import com.prauga.pvot.designsystem.domain.cache.TextMeasurementCache
import com.prauga.pvot.designsystem.domain.validation.IValidationEngine
import com.prauga.pvot.designsystem.domain.validation.ValidationEngine
import com.prauga.pvot.designsystem.domain.validation.ValidationResult
import com.prauga.pvot.designsystem.theme.PvotTheme
import com.prauga.pvot.designsystem.components.navigation.NavBarConstants.CONTAINER_VERTICAL_PADDING
import com.prauga.pvot.designsystem.components.navigation.NavBarConstants.SURFACE_SHADOW_ELEVATION
import com.prauga.pvot.designsystem.components.navigation.NavBarConstants.SURFACE_SHADOW_ELEVATION_M3
import com.prauga.pvot.designsystem.components.navigation.NavBarConstants.SURFACE_TONAL_ELEVATION

val ItemHorizontalPadding = 16.dp
val IconTextGap = 8.dp

/**
 * A floating bottom navigation bar with animated pill-style items.
 *
 * This is the optimized version that uses NavBarConfig for simplified configuration,
 * text measurement caching for performance, and animation coordination to prevent
 * frame drops when multiple tabs animate simultaneously.
 *
 * ## Performance Characteristics
 * - Text measurements are cached to avoid repeated calculations
 * - Animations are coordinated to maintain smooth 60fps performance
 * - Configuration is validated at component entry to catch errors early
 * - Optional performance monitoring tracks recomposition counts
 *
 * ## State Management
 * - Uses remember for text measurement cache (persists across recompositions)
 * - Uses LaunchedEffect for performance monitoring side effects
 * - Tab selection state is hoisted to parent component
 *
 * @param config Navigation bar configuration including tabs, selection, appearance, and behavior
 * @param onTabClick Callback invoked when a tab is clicked, receives the tab index
 * @param modifier Modifier to be applied to the navigation bar container
 * @param textCache Cache for text measurements (default: new TextMeasurementCache)
 * @param animationCoordinator Coordinator for managing concurrent animations (default: new AnimationCoordinator)
 * @param validationEngine Engine for validating configuration (default: new ValidationEngine)
 * @param performanceMonitor Optional monitor for tracking performance metrics
 * @throws IllegalArgumentException if config validation fails
 *
 * @see NavBarConfig
 * @see TextMeasurementCache
 * @see AnimationCoordinator
 */
@Composable
fun PvotNavBar(
    config: NavBarConfig,
    onTabClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    textCache: ITextMeasurementCache = remember { TextMeasurementCache() },
    animationCoordinator: IAnimationCoordinator = remember { AnimationCoordinator() },
    validationEngine: IValidationEngine = remember { ValidationEngine() },
    performanceMonitor: com.prauga.pvot.designsystem.domain.monitoring.IPerformanceMonitor? = null
) {
    // Record recomposition for performance monitoring
    try {
        performanceMonitor?.recordRecomposition("PvotNavBar")
    } catch (e: Exception) {
        Log.e("DesignSystem", "Failed to record recomposition for PvotNavBar", e)
        // Continue with basic functionality
    }
    
    // Validate configuration at component entry
    val validationResult = remember(config) {
        validationEngine.validateNavBarConfig(config.tabs, config.selectedTab)
    }
    
    when (validationResult) {
        is ValidationResult.Invalid -> {
            val errorMessage = validationResult.errors.joinToString("\n") { error ->
                "${error.parameter}: ${error.message}" +
                    (error.expectedValue?.let { " (expected: $it)" } ?: "")
            }
            throw IllegalArgumentException("Invalid NavBarConfig:\n$errorMessage")
        }
        ValidationResult.Valid -> Unit
    }
    
    // Log performance warnings when thresholds are exceeded
    androidx.compose.runtime.LaunchedEffect(performanceMonitor) {
        performanceMonitor?.logWarnings()
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(
                horizontal = config.appearance.sizes.horizontalPadding,
                vertical = CONTAINER_VERTICAL_PADDING
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .barWidthModifier(config.appearance.sizes)
                .height(config.appearance.sizes.barHeight)
                .clip(RoundedCornerShape(config.appearance.sizes.cornerRadius))
                .shadow(
                    SURFACE_SHADOW_ELEVATION,
                    RoundedCornerShape(config.appearance.sizes.cornerRadius),
                    clip = false
                ),
            shape = RoundedCornerShape(config.appearance.sizes.cornerRadius),
            color = config.appearance.colors.containerColor,
            tonalElevation = SURFACE_TONAL_ELEVATION,
            shadowElevation = SURFACE_SHADOW_ELEVATION_M3
        ) {
            Row(
                modifier = Modifier
                    .barWidthModifier(config.appearance.sizes)
                    .height(config.appearance.sizes.barHeight)
                    .padding(horizontal = config.appearance.sizes.contentPaddingHorizontal),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    config.appearance.sizes.itemSpacing,
                    Alignment.CenterHorizontally
                )
            ) {
                config.tabs.forEachIndexed { index, tab ->
                    val animationId = "nav_item_$index"
                    
                    PillNavItem(
                        tab = tab,
                        selected = index == config.selectedTab,
                        onClick = { onTabClick(index) },
                        sizes = config.appearance.sizes,
                        colors = config.appearance.colors,
                        textCache = textCache,
                        shouldAnimate = animationCoordinator.shouldAnimate(animationId),
                        onAnimationStart = { animationCoordinator.registerAnimation(animationId) },
                        onAnimationEnd = { animationCoordinator.unregisterAnimation(animationId) }
                    )
                }
            }
        }
    }
}

/**
 * Legacy version of PvotNavBar for backward compatibility.
 * 
 * @deprecated Use the version that accepts NavBarConfig instead.
 * This overload will be removed in a future version.
 */
@Deprecated(
    message = "Use PvotNavBar(config: NavBarConfig, ...) instead",
    replaceWith = ReplaceWith(
        "PvotNavBar(config = NavBarConfig(tabs = tabs, selectedTab = selectedTab, " +
            "appearance = NavBarAppearance(sizes = sizes, colors = colors)), onTabClick = onTabClick, modifier = modifier)"
    )
)
@Composable
fun PvotNavBar(
    selectedTab: Int,
    onTabClick: (Int) -> Unit,
    tabs: List<PvotTabItem>,
    modifier: Modifier = Modifier,
    @DrawableRes expandedIconRes: Int? = null,
    @StringRes expandedContentDescriptionRes: Int? = null,
    sizes: PvotNavBarSizes = PvotTheme.navBarSizes,
    colors: PvotNavBarColors = PvotTheme.navBarColors,
) {
    // Convert old parameters to NavBarConfig and delegate to new implementation
    val config = NavBarConfig(
        tabs = tabs,
        selectedTab = selectedTab,
        appearance = NavBarAppearance(sizes = sizes, colors = colors)
    )
    
    // Call the new optimized version
    PvotNavBar(
        config = config,
        onTabClick = onTabClick,
        modifier = modifier
    )
}

private fun Modifier.barWidthModifier(sizes: PvotNavBarSizes): Modifier = this.then(
    when {
        sizes.barWidth != null -> Modifier.width(sizes.barWidth)
        sizes.fillWidth -> Modifier.fillMaxWidth()
        else -> Modifier.wrapContentWidth()
    }
)

/**
 * Calculates the expanded width for a navigation tab item.
 *
 * Uses text measurement caching to avoid repeated calculations for the same label.
 * The width is calculated based on icon size, text width, and padding, then clamped
 * to the configured min/max bounds.
 *
 * ## Performance Optimization
 * - Text measurements are cached using the provided textCache
 * - Results are memoized using remember with appropriate keys
 * - Only recalculates when label text or text style changes
 *
 * @param labelText The text label to measure
 * @param sizes Size configuration containing icon sizes, padding, and width constraints
 * @param textCache Cache for storing text measurement results
 * @return The calculated width for the expanded tab item
 *
 * @see TextMeasurementCache
 * @see PvotNavBarSizes
 */
@Composable
fun calculateExpandedWidth(
    labelText: String,
    sizes: PvotNavBarSizes,
    textCache: ITextMeasurementCache
): Dp {
    if (sizes.expandedWidth != Dp.Unspecified) {
        return sizes.expandedWidth
    }

    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = sizes.labelFontSize
    )
    
    // Use cache for text measurement
    val textLayoutResult = remember(labelText, textStyle) {
        textCache.measure(labelText, textStyle, textMeasurer)
    }

    val density = LocalDensity.current
    val intrinsicWidth = with(density) {
        sizes.expandedIconSize + IconTextGap + textLayoutResult.size.width.toDp() + (ItemHorizontalPadding * 2)
    }

    return intrinsicWidth
        .coerceAtLeast(sizes.minExpandedWidth.takeIf { it != Dp.Unspecified } ?: 0.dp)
        .coerceAtMost(sizes.maxExpandedWidth.takeIf { it != Dp.Unspecified } ?: Dp.Infinity)
}