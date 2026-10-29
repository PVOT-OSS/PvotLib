// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassDefaults
import com.prauga.pvot.designsystem.effects.pvotGlass
import com.prauga.pvot.designsystem.effects.pvotGlassRim
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.theme.PvotSpacing


/**
 * A floating bottom navigation bar with an item that opens into a pill when selected.
 *
 * Dragging a finger along the bar previews each item it passes over, and lifting the finger
 * selects the item it ended on.
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
 * @param backdrop The content behind the bar, marked with
 * [pvotGlassBackdrop][com.prauga.pvot.designsystem.effects.pvotGlassBackdrop], to draw the bar
 * as glass over it; null for a plain bar
 * @param sizes Size configuration for the bar and its items
 * @param colors Color configuration for the bar and its items
 * @param compact Whether to shrink the bar into its compact form: shorter, with smaller items and
 * no labels. The change animates, so drive it from scroll direction to get out of the way
 */
@Composable
public fun PvotNavBar(
    selectedTab: Int,
    onTabClick: (Int) -> Unit,
    tabs: List<PvotTabItem>,
    modifier: Modifier = Modifier,
    @DrawableRes expandedIconRes: Int? = null,
    @StringRes expandedContentDescriptionRes: Int? = null,
    backdrop: PvotGlassBackdrop? = null,
    sizes: PvotNavBarSizes = LocalPvotNavBarSizes.current,
    colors: PvotNavBarColors =
        if (backdrop != null) PvotNavBarDefaults.glassColors() else PvotNavBarDefaults.themeColors(),
    compact: Boolean = false,
) {
    PvotNavBar(
        modifier = modifier,
        backdrop = backdrop,
        sizes = sizes,
        colors = colors,
        compact = compact
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

/**
 * A floating bottom navigation bar that lays its items out in a [RowScope].
 *
 * The bar handles dragging along it to pick an item. Use [PvotNavBarItem] for the standard item.
 *
 * @param modifier Modifier for the nav bar container
 * @param backdrop The content behind the bar, marked with
 * [pvotGlassBackdrop][com.prauga.pvot.designsystem.effects.pvotGlassBackdrop], to draw the bar
 * as glass over it; null for a plain bar
 * @param sizes Size configuration for the bar
 * @param colors Color configuration for the bar
 * @param compact Whether to shrink the bar into its compact form, see the tab-list overload
 * @param content The bar's items, typically [PvotNavBarItem]
 */
@Composable
public fun PvotNavBar(
    modifier: Modifier = Modifier,
    backdrop: PvotGlassBackdrop? = null,
    sizes: PvotNavBarSizes = LocalPvotNavBarSizes.current,
    colors: PvotNavBarColors =
        if (backdrop != null) PvotNavBarDefaults.glassColors() else PvotNavBarDefaults.themeColors(),
    compact: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    val barShape = RoundedCornerShape(sizes.cornerRadius)
    val shadowColor = if (backdrop != null) {
        Color.Black.copy(alpha = PvotGlassDefaults.ShadowAlpha)
    } else {
        DefaultShadowColor
    }
    val state = remember { NavBarState() }
    val haptics = LocalHapticFeedback.current

    val compactFraction by animateFloatAsState(
        targetValue = if (compact) 1f else 0f,
        animationSpec = tween(PvotDurations.Medium, easing = FastOutSlowInEasing),
        label = "navBarCompact"
    )
    SideEffect { state.compactFraction = compactFraction }
    val barHeight = lerp(sizes.barHeight, sizes.compactBarHeight, compactFraction)

    LaunchedEffect(state.hovered) {
        if (state.hovered != null) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = sizes.horizontalPadding, vertical = PvotSpacing.M),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .barWidthModifier(sizes)
                .height(barHeight)
                .shadow(
                    sizes.barShadowElevation,
                    barShape,
                    clip = false,
                    ambientColor = shadowColor,
                    spotColor = shadowColor
                )
                .then(
                    if (backdrop != null) {
                        Modifier
                            .pvotGlass(backdrop, sizes.cornerRadius)
                            .pvotGlassRim(sizes.cornerRadius)
                    } else {
                        Modifier
                    }
                ),
            shape = barShape,
            color = if (backdrop != null) Color.Transparent else colors.containerColor,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            CompositionLocalProvider(LocalNavBarState provides state) {
                Row(
                    modifier = Modifier
                        .barWidthModifier(sizes)
                        .height(barHeight)
                        .selectableGroup()
                        // Before the gestures, so their coordinates match the items' bounds.
                        .padding(horizontal = sizes.contentPaddingHorizontal)
                        .pointerInput(state) {
                            detectHorizontalDragGestures(
                                onDragStart = { offset -> state.hovered = state.entryAt(offset.x) },
                                onDragEnd = {
                                    state.hovered?.takeIf { !it.selected }?.onClick?.invoke()
                                    state.hovered = null
                                },
                                onDragCancel = { state.hovered = null }
                            ) { change, _ ->
                                state.entryAt(change.position.x)?.let { state.hovered = it }
                                change.consume()
                            }
                        },
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
}

private fun Modifier.barWidthModifier(sizes: PvotNavBarSizes): Modifier = this.then(
    when {
        sizes.barWidth != null -> Modifier.width(sizes.barWidth)
        sizes.fillWidth -> Modifier.fillMaxWidth()
        else -> Modifier.wrapContentWidth()
    }
)
