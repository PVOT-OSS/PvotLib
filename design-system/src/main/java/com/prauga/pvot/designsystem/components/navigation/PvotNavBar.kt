// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.theme.PvotTheme

val ItemHorizontalPadding = 16.dp
val IconTextGap = 8.dp

/**
 * A floating bottom navigation bar with animated pill-style items.
 *
 * When a tab is expanded, its content resolves as:
 * - icon: [expandedIconRes], else [PvotTabItem.expandedIconRes], else [PvotTabItem.iconRes]
 * - label: [PvotTabItem.expandedLabelRes], else [PvotTabItem.labelRes]
 * - content description: [expandedContentDescriptionRes], else [PvotTabItem.contentDescriptionRes]
 *
 * @param selectedTab Index into [tabs] of the currently selected tab
 * @param onTabClick Called with the index of the tab the user tapped
 * @param tabs The tabs to display, in order
 * @param modifier Modifier for the nav bar container
 * @param expandedIconRes Bar-level expanded icon override, or null to resolve per tab
 * @param expandedContentDescriptionRes Bar-level description override, or null to resolve per tab
 * @param sizes Size configuration for the bar and its items
 * @param colors Color configuration for the bar and its items
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
) {
    PvotNavBar(modifier = modifier, sizes = sizes, colors = colors) {
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

/**
 * A floating bottom navigation bar that lays its items out in a [RowScope].
 *
 * Items size themselves, so an item may carry any icon and label content. Use
 * [PvotNavBarItem] for the standard pill item.
 *
 * @param modifier Modifier for the nav bar container
 * @param sizes Size configuration for the bar
 * @param colors Color configuration for the bar
 * @param content The bar's items, typically [PvotNavBarItem]
 */
@Composable
fun PvotNavBar(
    modifier: Modifier = Modifier,
    sizes: PvotNavBarSizes = PvotTheme.navBarSizes,
    colors: PvotNavBarColors = PvotTheme.navBarColors,
    content: @Composable RowScope.() -> Unit
) {
    val barShape = RoundedCornerShape(sizes.cornerRadius)

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
                .shadow(sizes.barShadowElevation, barShape, clip = false),
            shape = barShape,
            color = colors.containerColor,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .barWidthModifier(sizes)
                    .height(sizes.barHeight)
                    .selectableGroup()
                    .padding(horizontal = sizes.contentPaddingHorizontal),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    sizes.itemSpacing,
                    Alignment.CenterHorizontally
                ),
                content = content
            )
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
internal fun calculateExpandedWidth(labelText: String, sizes: PvotNavBarSizes): Dp {
    if (sizes.expandedWidth != Dp.Unspecified) {
        return sizes.expandedWidth
    }

    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = sizes.labelFontSize
    )
    val textLayoutResult = textMeasurer.measure(labelText, textStyle)

    val density = LocalDensity.current
    val intrinsicWidth = with(density) {
        sizes.expandedIconSize + IconTextGap + textLayoutResult.size.width.toDp() + (ItemHorizontalPadding * 2)
    }

    return intrinsicWidth
        .coerceAtLeast(sizes.minExpandedWidth.takeIf { it != Dp.Unspecified } ?: 0.dp)
        .coerceAtMost(sizes.maxExpandedWidth.takeIf { it != Dp.Unspecified } ?: Dp.Infinity)
}
