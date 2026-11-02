// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.button

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.R
import com.prauga.pvot.designsystem.effects.pvotPressScale
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.motion.PvotMotion
import com.prauga.pvot.designsystem.theme.PvotShapes

/**
 * The colors of a [PvotIconButton].
 *
 * @param containerColor Fill while the button is off or not a toggle
 * @param contentColor Icon color while the button is off or not a toggle
 * @param checkedContainerColor Fill while the toggle is on
 * @param checkedContentColor Icon color while the toggle is on
 */
@Immutable
public data class PvotIconButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val checkedContainerColor: Color,
    val checkedContentColor: Color,
)

/** Defaults for [PvotIconButton]. */
public object PvotIconButtonDefaults {

    /** Diameter of the button; never under 48dp, the smallest comfortable target. */
    public val Size: Dp = 48.dp

    /** Size of a button grouped in a capsule, such as a call's controls. */
    public val LargeSize: Dp = 56.dp

    /** Size of a button in a dense row, such as on a card; still a 36dp circle, kept apart. */
    public val SmallSize: Dp = 36.dp

    /** Size of the icon in a button of [Size] or larger. */
    public val IconSize: Dp = 24.dp

    /** Size of the icon in a [SmallSize] button. */
    public val SmallIconSize: Dp = 18.dp

    /** Opacity of a disabled button. */
    public const val DisabledAlpha: Float = 0.38f

    /** A filled circle in the strongest neutral fill; on, it turns primary. */
    @Composable
    public fun tonalColors(
        checkedContainerColor: Color = MaterialTheme.colorScheme.primary,
        checkedContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    ): PvotIconButtonColors =
        PvotIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            contentColor = MaterialTheme.colorScheme.onSurface,
            checkedContainerColor = checkedContainerColor,
            checkedContentColor = checkedContentColor,
        )

    /** A primary circle, for the one action a row leads with. */
    @Composable
    public fun filledColors(): PvotIconButtonColors =
        PvotIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            checkedContainerColor = MaterialTheme.colorScheme.primary,
            checkedContentColor = MaterialTheme.colorScheme.onPrimary,
        )

    /** No circle: just the icon, for an action at the end of a list row. */
    @Composable
    public fun plainColors(
        contentColor: Color = MaterialTheme.colorScheme.onSurface
    ): PvotIconButtonColors =
        PvotIconButtonColors(
            containerColor = Color.Transparent,
            contentColor = contentColor,
            checkedContainerColor = MaterialTheme.colorScheme.primary,
            checkedContentColor = MaterialTheme.colorScheme.onPrimary,
        )
}

/**
 * A round button with an icon and no label, such as call back on a card, or mute in a call.
 *
 * With [checked] it is a toggle: the fill turns to the checked color, TalkBack reads it as a switch
 * with its state, and turning it on or off ticks the matching haptic. The circle is the touch
 * target; keep [size] at 48dp or more where it stands alone.
 *
 * @param icon The button's icon
 * @param contentDescription What TalkBack reads
 * @param onClick Called when the button is tapped
 * @param modifier Modifier for the button
 * @param size Diameter of the circle
 * @param checked Whether the toggle is on, or null for a button that is not a toggle
 * @param enabled Whether the button responds to taps
 * @param iconSize Size of the icon
 * @param colors The button's colors
 */
@Composable
public fun PvotIconButton(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = PvotIconButtonDefaults.Size,
    checked: Boolean? = null,
    enabled: Boolean = true,
    iconSize: Dp =
        if (size < PvotIconButtonDefaults.Size) {
            PvotIconButtonDefaults.SmallIconSize
        } else {
            PvotIconButtonDefaults.IconSize
        },
    colors: PvotIconButtonColors = PvotIconButtonDefaults.tonalColors(),
) {
    val source = remember { MutableInteractionSource() }
    val haptics = LocalHapticFeedback.current
    val on = checked == true
    val spec = tween<Color>(PvotDurations.Medium, easing = PvotMotion.Settle)
    val container by
        animateColorAsState(
            if (on) colors.checkedContainerColor else colors.containerColor,
            spec,
            label = "iconButtonFill",
        )
    val content by
        animateColorAsState(
            if (on) colors.checkedContentColor else colors.contentColor,
            spec,
            label = "iconButtonIcon",
        )
    val stateOn = stringResource(R.string.pvot_state_on)
    val stateOff = stringResource(R.string.pvot_state_off)
    Box(
        modifier =
            modifier
                .size(size)
                .alpha(if (enabled) 1f else PvotIconButtonDefaults.DisabledAlpha)
                .pvotPressScale(source)
                .clip(PvotShapes.Pill)
                .background(container)
                .clickable(
                    interactionSource = source,
                    indication = null,
                    enabled = enabled,
                    role = if (checked != null) Role.Switch else Role.Button,
                    onClick = {
                        if (checked != null) {
                            haptics.performHapticFeedback(
                                if (checked) HapticFeedbackType.ToggleOff
                                else HapticFeedbackType.ToggleOn
                            )
                        }
                        onClick()
                    },
                )
                .semantics {
                    this.contentDescription = contentDescription
                    if (checked != null) {
                        toggleableState = ToggleableState(checked)
                        stateDescription = if (checked) stateOn else stateOff
                    }
                },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = content,
            modifier = Modifier.size(iconSize),
        )
    }
}
