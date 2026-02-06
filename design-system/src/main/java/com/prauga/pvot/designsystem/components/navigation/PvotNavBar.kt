// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.navigation

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

val ItemHorizontalPadding = 16.dp
val IconTextGap = 8.dp

/**
 * A floating bottom navigation bar with animated pill-style items.
 *
 * This is the new optimized version that uses NavBarConfig for simplified configuration.
 */
@Composable
fun PvotNavBar(
    config: NavBarConfig,
    onTabClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    textCache: ITextMeasurementCache = remember { TextMeasurementCache() },
    animationCoordinator: IAnimationCoordinator = remember { AnimationCoordinator() },
    validationEngine: IValidationEngine = remember { ValidationEngine() }
) {
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
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(
                horizontal = config.appearance.sizes.horizontalPadding,
                vertical = 12.dp
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .barWidthModifier(config.appearance.sizes)
                .height(config.appearance.sizes.barHeight)
                .clip(RoundedCornerShape(config.appearance.sizes.cornerRadius))
                .shadow(
                    18.dp,
                    RoundedCornerShape(config.appearance.sizes.cornerRadius),
                    clip = false
                ),
            shape = RoundedCornerShape(config.appearance.sizes.cornerRadius),
            color = config.appearance.colors.containerColor,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
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