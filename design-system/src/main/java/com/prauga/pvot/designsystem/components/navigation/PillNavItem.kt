// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.navigation

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.domain.cache.ITextMeasurementCache
import com.prauga.pvot.designsystem.domain.cache.TextMeasurementCache

/**
 * Optimized PillNavItem with animation coordination and text caching.
 */
@Composable
fun PillNavItem(
    tab: PvotTabItem,
    selected: Boolean,
    onClick: () -> Unit,
    sizes: PvotNavBarSizes,
    colors: PvotNavBarColors,
    textCache: ITextMeasurementCache,
    shouldAnimate: Boolean,
    onAnimationStart: () -> Unit,
    onAnimationEnd: () -> Unit
) {
    val labelText = stringResource(id = tab.expandedLabelRes ?: tab.labelRes)
    val targetExpandedWidth = calculateExpandedWidth(labelText, sizes, textCache)

    val width by animateDpAsState(
        targetValue = if (selected) targetExpandedWidth else sizes.collapsedItemSize,
        animationSpec = if (shouldAnimate) {
            tween(240, easing = FastOutSlowInEasing)
        } else {
            tween(0)
        },
        label = "itemWidth",
        finishedListener = { onAnimationEnd() }
    )

    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.98f,
        animationSpec = if (shouldAnimate) {
            tween(200)
        } else {
            tween(0)
        },
        label = "scale"
    )

    // Register animation when starting
    if (selected && shouldAnimate) {
        onAnimationStart()
    }

    val interactionSource = remember { MutableInteractionSource() }
    val iconTint = if (selected) colors.iconSelectedColor else colors.iconUnselectedColor
    val displayedIconRes = if (selected) {
        tab.expandedIconRes ?: tab.iconRes
    } else {
        tab.iconRes
    }
    val displayedContentDescriptionRes = tab.contentDescriptionRes

    Box(
        modifier = Modifier
            .width(width)
            .height(sizes.collapsedItemSize)
            .clip(if (selected) RoundedCornerShape(sizes.itemCornerRadius) else CircleShape)
            .background(if (selected) Color.Transparent else colors.collapsedChipColor)
            .then(if (selected) Modifier.background(brush = colors.gradient) else Modifier)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .indication(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.25f))
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = if (selected) ItemHorizontalPadding else 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = displayedIconRes),
                contentDescription = stringResource(id = displayedContentDescriptionRes),
                modifier = Modifier.size(if (selected) sizes.expandedIconSize else sizes.collapsedIconSize),
                tint = iconTint
            )

            AnimatedVisibility(
                visible = selected,
                enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
                exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start)
            ) {
                Text(
                    text = labelText,
                    modifier = Modifier.padding(start = IconTextGap),
                    color = colors.iconSelectedColor,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = sizes.labelFontSize,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Legacy PillNavItem for backward compatibility.
 * 
 * @deprecated Use the version with animation coordination parameters instead.
 */
@Deprecated(
    message = "Use PillNavItem with animation coordination parameters",
    level = DeprecationLevel.WARNING
)
@Composable
fun PillNavItem(
    tab: PvotTabItem,
    selected: Boolean,
    onClick: () -> Unit,
    @DrawableRes expandedIconRes: Int,
    @StringRes expandedLabelRes: Int,
    @StringRes expandedContentDescriptionRes: Int,
    sizes: PvotNavBarSizes,
    colors: PvotNavBarColors
) {
    val labelText = stringResource(id = expandedLabelRes)
    val textCache = remember { TextMeasurementCache() }
    val targetExpandedWidth = calculateExpandedWidth(labelText, sizes, textCache)

    val width by animateDpAsState(
        targetValue = if (selected) targetExpandedWidth else sizes.collapsedItemSize,
        animationSpec = tween(240, easing = FastOutSlowInEasing),
        label = "itemWidth"
    )

    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.98f,
        animationSpec = tween(200),
        label = "scale"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val iconTint = if (selected) colors.iconSelectedColor else colors.iconUnselectedColor
    val displayedIconRes = if (selected) expandedIconRes else tab.iconRes
    val displayedContentDescriptionRes =
        if (selected) expandedContentDescriptionRes else tab.contentDescriptionRes

    Box(
        modifier = Modifier
            .width(width)
            .height(sizes.collapsedItemSize)
            .clip(if (selected) RoundedCornerShape(sizes.itemCornerRadius) else CircleShape)
            .background(if (selected) Color.Transparent else colors.collapsedChipColor)
            .then(if (selected) Modifier.background(brush = colors.gradient) else Modifier)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .indication(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.25f))
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = if (selected) ItemHorizontalPadding else 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = displayedIconRes),
                contentDescription = stringResource(id = displayedContentDescriptionRes),
                modifier = Modifier.size(if (selected) sizes.expandedIconSize else sizes.collapsedIconSize),
                tint = iconTint
            )

            AnimatedVisibility(
                visible = selected,
                enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
                exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start)
            ) {
                Text(
                    text = labelText,
                    modifier = Modifier.padding(start = IconTextGap),
                    color = colors.iconSelectedColor,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = sizes.labelFontSize,
                    maxLines = 1
                )
            }
        }
    }
}