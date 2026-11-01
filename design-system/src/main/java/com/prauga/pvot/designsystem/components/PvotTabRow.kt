// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.motion.PvotMotion
import com.prauga.pvot.designsystem.theme.PvotSpacing

/**
 * The colors of a [PvotTabRow].
 *
 * @param selectedContentColor Label of the selected tab
 * @param contentColor Labels of the other tabs
 * @param indicatorColor The line under the selected tab
 * @param dividerColor The hairline under the whole row
 */
@Immutable
public data class PvotTabRowColors(
    val selectedContentColor: Color,
    val contentColor: Color,
    val indicatorColor: Color,
    val dividerColor: Color
)

/** Defaults for [PvotTabRow]. */
public object PvotTabRowDefaults {

    /** Height of a tab; the whole height is its touch target. */
    public val TabHeight: Dp = 48.dp

    /** Thickness of the line under the selected tab. */
    public val IndicatorHeight: Dp = 3.dp

    /** Thickness of the hairline under the row. */
    public val DividerHeight: Dp = 1.dp

    /** Padding at each side of a tab's label; the indicator spans the label and this padding. */
    public val TabHorizontalPadding: Dp = PvotSpacing.M

    /** Colors from the theme: the selected tab `onSurface`, the indicator `onSurface` too. */
    @Composable
    public fun colors(): PvotTabRowColors = PvotTabRowColors(
        selectedContentColor = MaterialTheme.colorScheme.onSurface,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        indicatorColor = MaterialTheme.colorScheme.onSurface,
        dividerColor = MaterialTheme.colorScheme.outlineVariant
    )
}

/**
 * Text tabs with a line under the selected one, which slides to the tab tapped: a light way to
 * switch what a list shows, such as All and Missed calls.
 *
 * Tabs share the row's width equally, or with [scrollable] each takes its label's width and the
 * row scrolls. Each tab is a 48dp target that TalkBack reads as a tab with its position. Use a
 * [PvotSegmentedControl] instead for a choice that changes a setting.
 *
 * @param tabs The tabs' labels, in order
 * @param selectedIndex Index of the selected tab
 * @param onSelect Called with the index of a tab that is tapped
 * @param modifier Modifier for the row
 * @param scrollable Whether tabs take their labels' width and the row scrolls, for many tabs
 * @param colors The row's colors
 */
@Composable
public fun PvotTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    scrollable: Boolean = false,
    colors: PvotTabRowColors = PvotTabRowDefaults.colors()
) {
    val density = LocalDensity.current
    val haptics = LocalHapticFeedback.current
    // Each tab's start and width, measured, so the indicator can slide between them.
    val bounds = remember { mutableStateMapOf<Int, Pair<Dp, Dp>>() }
    val target = bounds[selectedIndex]
    val spec = tween<Dp>(PvotDurations.Medium, easing = PvotMotion.Settle)
    val indicatorStart by animateDpAsState(target?.first ?: 0.dp, spec, label = "tabIndicatorStart")
    val indicatorWidth by animateDpAsState(target?.second ?: 0.dp, spec, label = "tabIndicatorWidth")

    Box(modifier.fillMaxWidth()) {
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(PvotTabRowDefaults.DividerHeight)
                .background(colors.dividerColor)
        )
        Box(
            Modifier
                .then(if (scrollable) Modifier.horizontalScroll(rememberScrollState()) else Modifier.fillMaxWidth())
        ) {
            Row(
                modifier = Modifier
                    .then(if (scrollable) Modifier else Modifier.fillMaxWidth())
                    .selectableGroup(),
                horizontalArrangement = if (scrollable) Arrangement.Start else Arrangement.SpaceEvenly
            ) {
                tabs.forEachIndexed { index, label ->
                    val selected = index == selectedIndex
                    val color by animateColorAsState(
                        if (selected) colors.selectedContentColor else colors.contentColor,
                        tween(PvotDurations.Medium, easing = PvotMotion.Settle),
                        label = "tabLabel"
                    )
                    Box(
                        modifier = Modifier
                            .then(if (scrollable) Modifier else Modifier.weight(1f))
                            .heightIn(min = PvotTabRowDefaults.TabHeight)
                            .selectable(
                                selected = selected,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                role = Role.Tab,
                                onClick = {
                                    if (!selected) haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                    onSelect(index)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleSmall,
                            color = color,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            // Measured with its padding, so the indicator spans a little past the label.
                            modifier = Modifier
                                .onPlaced { coordinates ->
                                    val parent = coordinates.parentLayoutCoordinates
                                    val x = (parent?.positionInParent()?.x ?: 0f) + coordinates.positionInParent().x
                                    with(density) { bounds[index] = x.toDp() to coordinates.size.width.toDp() }
                                }
                                .padding(horizontal = PvotTabRowDefaults.TabHorizontalPadding)
                        )
                    }
                }
            }
            if (target != null) {
                Box(
                    Modifier
                        .align(Alignment.BottomStart)
                        // Measured positions are from the left edge, so the offset is too, in either direction.
                        .absoluteOffset(x = indicatorStart)
                        .width(indicatorWidth)
                        .height(PvotTabRowDefaults.IndicatorHeight)
                        .background(colors.indicatorColor, RoundedCornerShape(topStart = IndicatorCorner, topEnd = IndicatorCorner))
                )
            }
        }
    }
}

private val IndicatorCorner = 3.dp
