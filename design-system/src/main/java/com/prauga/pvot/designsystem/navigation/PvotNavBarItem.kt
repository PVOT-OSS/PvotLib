// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.prauga.pvot.designsystem.effects.pvotPressScale
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.theme.PvotShapes
import kotlin.math.roundToInt

/**
 * A pill-style item for the slot-based [PvotNavBar].
 *
 * The item is a bare icon when inactive and opens into a filled pill revealing [label] when
 * active. An item is active while it is selected, or while the user's finger is over it during a
 * drag along the bar. One eased progress value drives the fill, the width, the padding and the
 * label, so the item opens and closes in a single smooth motion. While the bar is compact the
 * label stays hidden and the active item is a short pill around its icon. Its width follows the content,
 * so [icon] and [label] may be any composable. They take their tint and text style from
 * `LocalContentColor` and `LocalTextStyle`, so a plain `Icon` and `Text` pick up the right color.
 *
 * @param selected Whether this item is the selected one
 * @param onClick Called when the user taps this item, or drags onto it and lets go
 * @param icon The item's icon, shown in both the collapsed and expanded state
 * @param modifier Modifier for this item
 * @param label The item's label, revealed while active, or null for an icon-only item
 * @param sizes Size configuration for this item
 * @param colors Color configuration for this item
 */
@Composable
public fun RowScope.PvotNavBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    sizes: PvotNavBarSizes = LocalPvotNavBarSizes.current,
    colors: PvotNavBarColors = PvotNavBarDefaults.themeColors()
) {
    val interactionSource = remember { MutableInteractionSource() }
    val barState = LocalNavBarState.current
    val entry = remember { NavItemEntry() }

    SideEffect {
        entry.selected = selected
        entry.onClick = onClick
    }
    DisposableEffect(barState) {
        barState?.entries?.add(entry)
        onDispose { barState?.entries?.remove(entry) }
    }

    // While dragging, only the item under the finger is open.
    val active = barState?.hovered?.let { it === entry } ?: selected

    val progress by animateFloatAsState(
        targetValue = if (active) 1f else 0f,
        animationSpec = tween(PvotDurations.Medium, easing = FastOutSlowInEasing),
        label = "navItemProgress"
    )
    val contentColor by animateColorAsState(
        targetValue = if (active) colors.iconSelectedColor else colors.iconUnselectedColor,
        animationSpec = tween(PvotDurations.Medium, easing = FastOutSlowInEasing),
        label = "navItemColor"
    )

    val compactFraction = barState?.compactFraction ?: 0f
    val collapsed = lerp(sizes.collapsedItemSize, sizes.compactItemSize, compactFraction)
    // The label and the pill's side padding fade out as the bar goes compact.
    val reveal = progress * (1f - compactFraction)
    // While compact, the active item is a pill around its icon: wider than the others, not a circle.
    val pillExtra = (sizes.compactPillWidth - sizes.compactItemSize).coerceAtLeast(0.dp) *
        (progress * compactFraction)
    val minWidth = if (sizes.minExpandedWidth != Dp.Unspecified) {
        lerp(collapsed, sizes.minExpandedWidth.coerceAtLeast(collapsed), reveal)
    } else collapsed
    val maxWidth = if (sizes.maxExpandedWidth != Dp.Unspecified) {
        lerp(collapsed, sizes.maxExpandedWidth.coerceAtLeast(collapsed), reveal)
    } else Dp.Infinity
    val sizeModifier = if (sizes.expandedWidth != Dp.Unspecified) {
        Modifier.width(lerp(collapsed, sizes.expandedWidth, reveal) + pillExtra)
    } else {
        Modifier.widthIn(min = minWidth + pillExtra, max = maxWidth + pillExtra)
    }

    Box(
        modifier = modifier
            .height(collapsed)
            .then(sizeModifier)
            .onGloballyPositioned { entry.bounds = it.boundsInParent() }
            .clip(PvotShapes.Pill)
            .drawBehind {
                drawRect(color = colors.collapsedChipColor)
                if (progress > 0f) drawRect(brush = colors.gradient, alpha = progress)
            }
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
            .pvotPressScale(interactionSource),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalTextStyle provides MaterialTheme.typography.labelMedium.copy(
                fontSize = sizes.labelFontSize,
                fontWeight = FontWeight.SemiBold
            )
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = PvotNavBarDefaults.ItemHorizontalPadding * reveal
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                icon()

                if (label != null && reveal > 0f) {
                    Box(
                        modifier = Modifier
                            .revealWidth { reveal }
                            .graphicsLayer { alpha = ((reveal - 0.25f) / 0.75f).coerceIn(0f, 1f) }
                            .padding(start = PvotNavBarDefaults.IconTextGap)
                    ) {
                        label()
                    }
                }
            }
        }
    }
}

/** Lays the content out at its full size but reports [fraction] of its width, clipped. */
private fun Modifier.revealWidth(fraction: () -> Float): Modifier = this
    .clipToBounds()
    .layout { measurable, constraints ->
        val placeable = measurable.measure(constraints.copy(maxWidth = Constraints.Infinity))
        val width = (placeable.width * fraction()).roundToInt()
        layout(width, placeable.height) { placeable.placeRelative(0, 0) }
    }
