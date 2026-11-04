// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.camera

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prauga.pvot.designsystem.components.pvotGlassSurface
import com.prauga.pvot.designsystem.effects.LocalPvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassStyle
import com.prauga.pvot.designsystem.motion.PvotDurations

/**
 * Color configuration for [PvotModeSelector].
 *
 * @param selectedContentColor Label of the selected mode
 * @param contentColor Labels of the other modes
 */
@Immutable
public data class PvotModeSelectorColors(val selectedContentColor: Color, val contentColor: Color)

/** Defaults for [PvotModeSelector]. */
public object PvotModeSelectorDefaults {
    /** The selector's height. */
    public val Height: Dp = 38.dp

    /** Space on each side of a mode's label, inside the pill. */
    public val ItemPadding: Dp = 16.dp

    /** The glass of the pill under the selected mode. */
    public val IndicatorStyle: PvotGlassStyle =
        PvotCameraGlassDefaults.Style.copy(fillColor = Color(0x9E444444))

    /** Colors from [MaterialTheme]: the primary color for the selected mode. */
    @Composable
    public fun colors(
        selectedContentColor: Color = MaterialTheme.colorScheme.primary,
        contentColor: Color = MaterialTheme.colorScheme.onSurface,
    ): PvotModeSelectorColors = PvotModeSelectorColors(selectedContentColor, contentColor)
}

/**
 * A row of capture modes, such as VIDEO, PHOTO and SCAN, with a glass pill that slides to the one
 * selected.
 *
 * Tapping a mode calls [onSelect], also for the selected one, which a camera can use to open the
 * mode's controls.
 *
 * @param modes Label of each mode, in order
 * @param selectedIndex Index of the selected mode in [modes]
 * @param onSelect Called with the index of the mode tapped
 * @param modifier Modifier for the selector
 * @param backdrop The content behind the pill, or null for the tint alone
 * @param enabled Whether the selector responds
 * @param colors The label colors
 */
@Composable
public fun PvotModeSelector(
    modes: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    backdrop: PvotGlassBackdrop? = LocalPvotGlassBackdrop.current,
    enabled: Boolean = true,
    colors: PvotModeSelectorColors = PvotModeSelectorDefaults.colors(),
) {
    val density = LocalDensity.current
    val haptics = LocalHapticFeedback.current
    val positions = remember { mutableStateMapOf<Int, Pair<Dp, Dp>>() }

    val target = positions[selectedIndex]
    val indicatorX by
        animateDpAsState(target?.first ?: 0.dp, tween(PvotDurations.Long), label = "modeX")
    val indicatorWidth by
        animateDpAsState(target?.second ?: 0.dp, tween(PvotDurations.Long), label = "modeWidth")

    Box(modifier = modifier.height(PvotModeSelectorDefaults.Height)) {
        if (target != null) {
            Box(
                modifier =
                    Modifier.offset(x = indicatorX)
                        .width(indicatorWidth)
                        .height(PvotModeSelectorDefaults.Height)
                        .pvotGlassSurface(
                            backdrop = backdrop,
                            style = PvotModeSelectorDefaults.IndicatorStyle,
                            role = null,
                        )
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            modes.forEachIndexed { index, mode ->
                val selected = index == selectedIndex
                val textColor by
                    animateColorAsState(
                        if (selected) colors.selectedContentColor else colors.contentColor,
                        tween(PvotDurations.Long),
                        label = "modeColor",
                    )

                Box(
                    modifier =
                        Modifier.height(PvotModeSelectorDefaults.Height)
                            .onGloballyPositioned {
                                with(density) {
                                    positions[index] =
                                        it.positionInParent().x.toDp() to it.size.width.toDp()
                                }
                            }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                enabled = enabled,
                                role = Role.Tab,
                            ) {
                                if (!selected) {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                                onSelect(index)
                            }
                            .padding(horizontal = PvotModeSelectorDefaults.ItemPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = mode,
                        color = textColor.copy(alpha = if (enabled) 1f else 0.5f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}
