// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.pvot.designsystem.components.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.prauga.pvot.designsystem.theme.PvotTheme

/**
 * A floating bottom navigation bar with animated pill-style items.
 *
 * @param selectedTab The index of the currently selected tab.
 * @param onTabClick Callback invoked when a tab is clicked, providing the tab index.
 * @param tabs List of [TabItem] representing the navigation items.
 * @param modifier Modifier for the navigation bar container.
 * @param expandedIconRes Drawable resource for the icon shown when a tab is expanded/selected.
 * @param expandedContentDescriptionRes String resource for the content description when expanded.
 * @param barHeight Height of the navigation bar.
 * @param collapsedItemSize Size of collapsed navigation items.
 * @param expandedItemWidth Width of the expanded/selected navigation item.
 * @param cornerRadius Corner radius of the navigation bar.
 * @param gradient Brush used for the selected item background. Defaults to theme colors.
 * @param collapsedChipColor Background color for collapsed items. Defaults to theme colors.
 * @param containerColor Background color for the navigation bar container. Defaults to theme colors.
 */
@Composable
fun PvotNavBar(
    selectedTab: Int,
    onTabClick: (Int) -> Unit,
    tabs: List<TabItem>,
    modifier: Modifier = Modifier,
    @DrawableRes expandedIconRes: Int? = null,
    @StringRes expandedContentDescriptionRes: Int? = null,
    barHeight: Dp = 72.dp,
    barWidth: Dp? = null,
    collapsedItemSize: Dp = 56.dp,
    expandedItemWidth: Dp = 169.dp,
    cornerRadius: Dp = 100.dp,
    itemCornerRadius: Dp = 28.dp,
    horizontalPadding: Dp = 22.dp,
    contentPaddingHorizontal: Dp = 9.dp,
    itemSpacing: Dp = 4.dp,
    fillWidth: Boolean = false,
    gradient: Brush = PvotTheme.navBarColors.gradient,
    collapsedChipColor: Color = PvotTheme.navBarColors.collapsedChipColor,
    containerColor: Color = PvotTheme.navBarColors.containerColor,
) {
    val navBarColors = PvotTheme.navBarColors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = horizontalPadding, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .then(
                    when {
                        barWidth != null -> Modifier.width(barWidth)
                        fillWidth -> Modifier.fillMaxWidth()
                        else -> Modifier.wrapContentWidth()
                    }
                )
                .height(barHeight)
                .clip(RoundedCornerShape(cornerRadius))
                .shadow(18.dp, RoundedCornerShape(cornerRadius), clip = false),
            shape = RoundedCornerShape(cornerRadius),
            color = containerColor,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .then(
                        when {
                            barWidth != null -> Modifier.fillMaxWidth()
                            fillWidth -> Modifier.fillMaxWidth()
                            else -> Modifier.wrapContentWidth()
                        }
                    )
                    .height(barHeight)
                    .padding(horizontal = contentPaddingHorizontal),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(itemSpacing, Alignment.CenterHorizontally)
            ) {
                tabs.forEachIndexed { index, tab ->
                    PillNavItem(
                        iconRes = tab.iconRes,
                        labelRes = tab.labelRes,
                        contentDescriptionRes = tab.contentDescriptionRes,
                        expandedIconRes = expandedIconRes ?: tab.expandedIconRes ?: tab.iconRes,
                        expandedLabelRes = tab.expandedLabelRes ?: tab.labelRes,
                        expandedContentDescriptionRes = expandedContentDescriptionRes ?: tab.contentDescriptionRes,
                        selected = index == selectedTab,
                        onClick = { onTabClick(index) },
                        collapsedItemSize = collapsedItemSize,
                        expandedItemWidth = expandedItemWidth,
                        itemCornerRadius = itemCornerRadius,
                        gradient = gradient,
                        collapsedChipColor = collapsedChipColor,
                        iconSelectedColor = navBarColors.iconSelectedColor,
                        iconUnselectedColor = navBarColors.iconUnselectedColor
                    )
                }
            }
        }
    }
}

@Composable
private fun PillNavItem(
    @DrawableRes iconRes: Int,
    @StringRes labelRes: Int,
    @StringRes contentDescriptionRes: Int,
    @DrawableRes expandedIconRes: Int,
    @StringRes expandedLabelRes: Int,
    @StringRes expandedContentDescriptionRes: Int,
    selected: Boolean,
    onClick: () -> Unit,
    collapsedItemSize: Dp,
    expandedItemWidth: Dp,
    itemCornerRadius: Dp,
    gradient: Brush,
    collapsedChipColor: Color,
    iconSelectedColor: Color,
    iconUnselectedColor: Color
) {
    val width by animateDpAsState(
        targetValue = if (selected) expandedItemWidth else collapsedItemSize,
        animationSpec = tween(240, easing = FastOutSlowInEasing),
        label = "itemWidth"
    )
    val height = collapsedItemSize

    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.98f,
        animationSpec = tween(200),
        label = "scale"
    )
    val iconTint = if (selected) iconSelectedColor else iconUnselectedColor
    val interaction = remember { MutableInteractionSource() }

    val displayedIconRes = if (selected) expandedIconRes else iconRes
    val displayedContentDescriptionRes =
        if (selected) expandedContentDescriptionRes else contentDescriptionRes

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(if (selected) RoundedCornerShape(itemCornerRadius) else CircleShape)
            .background(if (selected) Color.Transparent else collapsedChipColor)
            .then(if (selected) Modifier.background(brush = gradient) else Modifier)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
            .indication(
                interactionSource = interaction,
                indication = ripple(
                    bounded = true,
                    color = Color.White.copy(alpha = 0.25f)
                )
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = if (selected) 14.dp else 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = displayedIconRes),
                contentDescription = stringResource(id = displayedContentDescriptionRes),
                modifier = Modifier.size(20.dp),
                tint = iconTint
            )

            AnimatedVisibility(
                visible = selected,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Text(
                    text = stringResource(id = expandedLabelRes),
                    modifier = Modifier.padding(start = 8.dp),
                    color = iconSelectedColor,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Represents a navigation tab item.
 *
 * @param iconRes Drawable resource for the tab icon (collapsed state).
 * @param labelRes String resource for the tab label.
 * @param contentDescriptionRes String resource for accessibility content description.
 * @param expandedIconRes Optional drawable resource for the icon when expanded/selected. If null, uses iconRes.
 * @param expandedLabelRes Optional string resource for the label when expanded/selected. If null, uses labelRes.
 */
data class TabItem(
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int,
    @StringRes val contentDescriptionRes: Int,
    @DrawableRes val expandedIconRes: Int? = null,
    @StringRes val expandedLabelRes: Int? = null
)
