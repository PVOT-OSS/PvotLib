// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.catalog.legacy

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
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.navigation.PvotTabItem
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing

// The nav bar PvotLib shipped before the new UI, kept so "Enable New UI" can switch back to it.

private class LegacyNavSizes {
    val barHeight = 72.dp
    val collapsedItemSize = 56.dp
    val expandedIconSize = 22.dp
    val collapsedIconSize = 22.dp
    val maxExpandedWidth = 200.dp
    val barShadowElevation = 18.dp
    val cornerRadius = 100.dp
    val itemCornerRadius = 28.dp
    val labelFontSize = 16.sp
    val horizontalPadding = 22.dp
    val contentPaddingHorizontal = 9.dp
    val itemSpacing = PvotSpacing.XS
}

private class LegacyNavColors {
    val gradient = Brush.horizontalGradient(listOf(Color(0xFF9B84FF), Color(0xFF6B66FF)))
    val collapsedChipColor = Color(0xFF4D4D4F)
    val containerColor = Color(0x1AFFFFFF)
    val iconSelectedColor = Color.White
    val iconUnselectedColor = Color(0xFFECECEC)
    val rippleColor = Color.White.copy(alpha = 0.25f)
}

private val LegacySizes = LegacyNavSizes()
private val LegacyColors = LegacyNavColors()

/** The pre-redesign nav bar, with the same tab-list API as `PvotNavBar`. */
@Composable
internal fun LegacyNavBar(
    selectedTab: Int,
    onTabClick: (Int) -> Unit,
    tabs: List<PvotTabItem>,
    modifier: Modifier = Modifier
) {
    LegacyBarContainer(modifier) {
        tabs.forEachIndexed { index, tab ->
            LegacyPillNavItem(
                tab = tab,
                selected = index == selectedTab,
                onClick = { onTabClick(index) },
                expandedIconRes = tab.expandedIconRes ?: tab.iconRes,
                expandedLabelRes = tab.expandedLabelRes ?: tab.labelRes,
                expandedContentDescriptionRes = tab.contentDescriptionRes,
                sizes = LegacySizes,
                colors = LegacyColors
            )
        }
    }
}

@Composable
private fun LegacyBarContainer(modifier: Modifier, content: @Composable RowScope.() -> Unit) {
    val sizes = LegacySizes
    val barShape = RoundedCornerShape(sizes.cornerRadius)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = sizes.horizontalPadding, vertical = PvotSpacing.M),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .height(sizes.barHeight)
                .shadow(sizes.barShadowElevation, barShape, clip = false),
            shape = barShape,
            color = LegacyColors.containerColor,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .wrapContentWidth()
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

@Composable
private fun legacyExpandedWidth(labelText: String, sizes: LegacyNavSizes): Dp {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = sizes.labelFontSize
    )
    val textWidth = textMeasurer.measure(labelText, textStyle).size.width
    val density = LocalDensity.current
    return with(density) {
        sizes.expandedIconSize + PvotSpacing.S +
            textWidth.toDp() + (PvotSpacing.L * 2)
    }.coerceAtMost(sizes.maxExpandedWidth)
}

@Composable
private fun LegacyPillNavItem(
    tab: PvotTabItem,
    selected: Boolean,
    onClick: () -> Unit,
    @DrawableRes expandedIconRes: Int,
    @StringRes expandedLabelRes: Int,
    @StringRes expandedContentDescriptionRes: Int,
    sizes: LegacyNavSizes,
    colors: LegacyNavColors,
    modifier: Modifier = Modifier
) {
    val labelText = stringResource(id = expandedLabelRes)
    val targetExpandedWidth = legacyExpandedWidth(labelText, sizes)

    val width by animateDpAsState(
        targetValue = if (selected) targetExpandedWidth else sizes.collapsedItemSize,
        animationSpec = tween(PvotDurations.Medium, easing = FastOutSlowInEasing),
        label = "itemWidth"
    )

    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.98f,
        animationSpec = tween(PvotDurations.Short),
        label = "scale"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val iconTint = if (selected) colors.iconSelectedColor else colors.iconUnselectedColor
    val displayedIconRes = if (selected) expandedIconRes else tab.iconRes
    val displayedContentDescriptionRes =
        if (selected) expandedContentDescriptionRes else tab.contentDescriptionRes

    Box(
        modifier = modifier
            .width(width)
            .height(sizes.collapsedItemSize)
            .clip(if (selected) RoundedCornerShape(sizes.itemCornerRadius) else PvotShapes.Pill)
            .background(
                if (selected) colors.gradient else SolidColor(colors.collapsedChipColor)
            )
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
            .indication(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = colors.rippleColor)
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (selected) PvotSpacing.L else 0.dp
            ),
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
                    modifier = Modifier.padding(start = PvotSpacing.S),
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