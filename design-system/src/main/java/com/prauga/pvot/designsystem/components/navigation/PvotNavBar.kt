// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.theme.PvotTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.HazeTint

val ItemHorizontalPadding = 16.dp
val IconTextGap = 8.dp

/**
 * A floating bottom navigation bar with animated pill-style items.
 *
 * When a tab is expanded, icons and content descriptions resolve in order:
 * 1. Nav bar level ([expandedIconRes]/[expandedContentDescriptionRes])
 * 2. Tab level ([TabItem.expandedIconRes])
 * 3. Fallback to collapsed values ([TabItem.iconRes]/[TabItem.contentDescriptionRes])
 */
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
    hazeState: HazeState? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = sizes.horizontalPadding, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .barWidthModifier(sizes)
                .height(sizes.barHeight)
                .shadow(30.dp, RoundedCornerShape(sizes.cornerRadius), clip = false)
                .clip(RoundedCornerShape(sizes.cornerRadius))
                .then(
                    if (hazeState != null) {
                        Modifier.hazeChild(
                            state = hazeState,
                            shape = RoundedCornerShape(sizes.cornerRadius),
                            style = dev.chrisbanes.haze.HazeStyle(
                                backgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                blurRadius = 30.dp,
                                tints = listOf(HazeTint(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)))
                            )
                        )
                    } else Modifier
                ),
            shape = RoundedCornerShape(sizes.cornerRadius),
            color = if (hazeState != null) Color.Transparent else colors.containerColor,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .barWidthModifier(sizes)
                    .height(sizes.barHeight)
                    .padding(horizontal = sizes.contentPaddingHorizontal),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    sizes.itemSpacing,
                    Alignment.CenterHorizontally
                )
            ) {
                tabs.forEachIndexed { index, tab ->
                    PillNavItem(
                        tab = tab,
                        selected = index == selectedTab,
                        onClick = { onTabClick(index) },
                        expandedIconRes = expandedIconRes ?: tab.expandedIconRes ?: tab.iconRes,
                        expandedLabelRes = tab.expandedLabelRes ?: tab.labelRes,
                        expandedContentDescriptionRes = expandedContentDescriptionRes
                            ?: tab.contentDescriptionRes,
                        sizes = sizes,
                        colors = colors
                    )
                }
            }
        }
    }
}

private fun Modifier.barWidthModifier(sizes: PvotNavBarSizes): Modifier = this.then(
    when {
        sizes.barWidth != null -> Modifier.width(sizes.barWidth)
        sizes.fillWidth -> Modifier.fillMaxWidth()
        else -> Modifier.wrapContentWidth()
    }
)

@Composable
fun calculateExpandedWidth(labelText: String, sizes: PvotNavBarSizes): Dp {
    if (sizes.expandedWidth != Dp.Unspecified) {
        return sizes.expandedWidth
    }

    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = sizes.labelFontSize
    )
    val textLayoutResult = remember(labelText, textStyle) {
        textMeasurer.measure(labelText, textStyle)
    }

    val density = LocalDensity.current
    val intrinsicWidth = with(density) {
        sizes.expandedIconSize + IconTextGap + textLayoutResult.size.width.toDp() + (ItemHorizontalPadding * 2)
    }

    return intrinsicWidth
        .coerceAtLeast(sizes.minExpandedWidth.takeIf { it != Dp.Unspecified } ?: 0.dp)
        .coerceAtMost(sizes.maxExpandedWidth.takeIf { it != Dp.Unspecified } ?: Dp.Infinity)
}