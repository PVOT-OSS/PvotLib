// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.effects.LocalPvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.pvotPressScale
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.motion.PvotMotion
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing

/**
 * What the selected tab of a [PvotDock] does when it is tapped again, shown as its label.
 *
 * @param icon The action's icon, shown in the pill
 * @param label The action's name, such as "Dial", shown in the pill and read by TalkBack
 * @param onClick Called when the pill is tapped
 */
@Immutable
public class PvotDockAction(
    public val icon: Painter,
    public val label: String,
    public val onClick: () -> Unit
)

/**
 * One tab of a [PvotDock].
 *
 * @param icon The tab's icon
 * @param label The tab's name, read by TalkBack; it is not shown
 * @param action What tapping the tab does once it is selected, or null for nothing
 * @param badgeCount A count on the tab, such as missed calls; 0 for none
 */
@Immutable
public class PvotDockItem(
    public val icon: Painter,
    public val label: String,
    public val action: PvotDockAction? = null,
    public val badgeCount: Int = 0
)

/**
 * The colors of a [PvotDock].
 *
 * @param itemColor Fill of a tab that is not selected
 * @param itemContentColor Icon color of a tab that is not selected
 * @param selectedColor Fill of the selected tab, the top of its gradient
 * @param selectedGradientColor The bottom of the selected tab's gradient
 * @param selectedContentColor Icon and label color of the selected tab
 */
@Immutable
public data class PvotDockColors(
    val itemColor: Color,
    val itemContentColor: Color,
    val selectedColor: Color,
    val selectedGradientColor: Color,
    val selectedContentColor: Color
)

/** Defaults for [PvotDock]. */
public object PvotDockDefaults {

    /** Diameter of a tab, and the height of the selected tab's pill. */
    public val ItemSize: Dp = 56.dp

    /** Space between the tabs, and between the tabs and the capsule's edge. */
    public val Spacing: Dp = PvotSpacing.S

    /** Height of the dock's capsule, not counting the space round it. */
    public val Height: Dp = ItemSize + Spacing * 2

    /** Space round the capsule, at the bottom of the screen. */
    public val OuterPadding: Dp = PvotSpacing.M

    /** Room a screen leaves at its bottom for the dock: its height and the space round it. */
    public val ContentClearance: Dp = Height + OuterPadding * 2

    /** Padding at each side of the selected tab's pill. */
    public val PillHorizontalPadding: Dp = PvotSpacing.L + PvotSpacing.XS

    /** The widest a pill grows; its label is cut short past it. */
    public val PillMaxWidth: Dp = 200.dp

    /** How much darker the bottom of the selected tab's gradient is. */
    public const val GradientDepth: Float = 0.18f

    /** Colors from the theme: neutral circles, the selected tab a primary gradient. */
    @Composable
    public fun colors(): PvotDockColors {
        val primary = MaterialTheme.colorScheme.primary
        return PvotDockColors(
            itemColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            itemContentColor = MaterialTheme.colorScheme.onSurface,
            selectedColor = primary,
            selectedGradientColor = lerp(primary, Color.Black, GradientDepth),
            selectedContentColor = MaterialTheme.colorScheme.onPrimary
        )
    }
}

/**
 * The main navigation as a floating capsule of round tabs, where the selected tab becomes a pill
 * that names what it does: "Dial" on a keypad tab, "Search" on a history, "Add" on a list.
 *
 * Tapping a tab selects it; tapping the selected tab runs its [action][PvotDockItem.action]. A
 * selected tab without an action stays a circle, filled. Over a backdrop the capsule is glass.
 * TalkBack reads each tab with its name and state, and the selected one with its action.
 *
 * The dock floats over the screen's content: leave [PvotDockDefaults.ContentClearance] at the
 * bottom of the content, for example by providing
 * [LocalPvotNavBarSizes][com.prauga.pvot.designsystem.navigation.LocalPvotNavBarSizes] with that height.
 *
 * ```
 * PvotDock(
 *     items = listOf(
 *         PvotDockItem(keypadIcon, "Keypad", PvotDockAction(phoneIcon, "Dial", ::dial)),
 *         PvotDockItem(historyIcon, "Recents", PvotDockAction(searchIcon, "Search", ::search)),
 *     ),
 *     selectedIndex = tab,
 *     onSelect = { tab = it },
 * )
 * ```
 *
 * @param items The tabs, in order
 * @param selectedIndex Index of the selected tab
 * @param onSelect Called with the index of a tab that is tapped while not selected
 * @param modifier Modifier for the dock
 * @param backdrop The content behind the dock, or null for a solid capsule
 * @param colors The dock's colors
 */
@Composable
public fun PvotDock(
    items: List<PvotDockItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    backdrop: PvotGlassBackdrop? = LocalPvotGlassBackdrop.current,
    colors: PvotDockColors = PvotDockDefaults.colors()
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(PvotDockDefaults.OuterPadding),
        contentAlignment = Alignment.Center
    ) {
        PvotCapsule(
            modifier = Modifier
                .height(PvotDockDefaults.Height)
                .selectableGroup(),
            backdrop = backdrop,
            contentPadding = PvotDockDefaults.Spacing,
            spacing = PvotDockDefaults.Spacing
        ) {
            items.forEachIndexed { index, item ->
                DockItem(
                    item = item,
                    selected = index == selectedIndex,
                    onSelect = { onSelect(index) },
                    colors = colors
                )
            }
        }
    }
}

@Composable
private fun DockItem(item: PvotDockItem, selected: Boolean, onSelect: () -> Unit, colors: PvotDockColors) {
    val source = remember { MutableInteractionSource() }
    val haptics = LocalHapticFeedback.current
    val action = item.action
    val pill = selected && action != null
    val fill by animateFloatAsState(
        if (selected) 1f else 0f,
        tween(PvotDurations.Medium, easing = PvotMotion.Settle),
        label = "dockFill"
    )
    val content by animateColorAsState(
        if (selected) colors.selectedContentColor else colors.itemContentColor,
        tween(PvotDurations.Medium, easing = PvotMotion.Settle),
        label = "dockContent"
    )
    val sidePadding by animateDpAsState(
        if (pill) PvotDockDefaults.PillHorizontalPadding else 0.dp,
        tween(PvotDurations.Medium, easing = PvotMotion.Settle),
        label = "dockPadding"
    )
    val run = {
        if (pill) {
            haptics.performHapticFeedback(HapticFeedbackType.Confirm)
            action!!.onClick()
        } else if (!selected) {
            haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
            onSelect()
        }
    }
    Row(
        modifier = Modifier
            .height(PvotDockDefaults.ItemSize)
            .widthIn(min = PvotDockDefaults.ItemSize, max = PvotDockDefaults.PillMaxWidth)
            .pvotPressScale(source)
            .clip(PvotShapes.Pill)
            .background(colors.itemColor)
            .drawBehind {
                if (fill > 0f) {
                    drawRect(
                        Brush.verticalGradient(listOf(colors.selectedColor, colors.selectedGradientColor)),
                        alpha = fill
                    )
                    // A soft light at the top, so the pill reads as lit rather than flat.
                    drawCircle(
                        Brush.radialGradient(
                            listOf(Color.White.copy(alpha = HighlightAlpha), Color.Transparent),
                            center = Offset(size.width / 2f, 0f),
                            radius = size.width * 0.6f
                        ),
                        radius = size.width * 0.6f,
                        center = Offset(size.width / 2f, 0f),
                        alpha = fill
                    )
                }
            }
            .clickable(interactionSource = source, indication = null, role = Role.Tab, onClick = run)
            .semantics(mergeDescendants = true) {
                role = Role.Tab
                this.selected = selected
                contentDescription = item.label
                onClick(label = if (pill) action!!.label else null) { run(); true }
            }
            .padding(horizontal = sidePadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        BadgedBox(
            badge = {
                if (item.badgeCount > 0 && !pill) Badge { Text(badgeText(item.badgeCount)) }
            }
        ) {
            Icon(
                painter = if (pill) action!!.icon else item.icon,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(IconSize)
            )
        }
        AnimatedVisibility(
            visible = pill,
            enter = expandHorizontally(tween(PvotDurations.Medium, easing = PvotMotion.Settle)) +
                fadeIn(tween(PvotDurations.Medium)),
            exit = shrinkHorizontally(tween(PvotDurations.Short, easing = PvotMotion.Settle)) +
                fadeOut(tween(PvotDurations.Short))
        ) {
            Text(
                text = action?.label.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = content,
                maxLines = 1,
                modifier = Modifier.padding(start = PvotSpacing.S)
            )
        }
    }
}

private fun badgeText(count: Int): String = if (count > MaxBadge) "$MaxBadge+" else count.toString()

private val IconSize = 24.dp
private const val HighlightAlpha = 0.22f
private const val MaxBadge = 999
