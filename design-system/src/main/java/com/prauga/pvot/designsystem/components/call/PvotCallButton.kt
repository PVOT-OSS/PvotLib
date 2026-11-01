// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.call

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.R
import com.prauga.pvot.designsystem.components.pvotGlassSurface
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassDefaults
import com.prauga.pvot.designsystem.effects.PvotGlassMaterial
import com.prauga.pvot.designsystem.effects.pvotPressScale
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.motion.PvotMotion
import com.prauga.pvot.designsystem.theme.PvotSpacing

/**
 * The colors of a [PvotCallButton].
 *
 * @param containerColor Fill of the button while it is off or not a toggle
 * @param contentColor Color of the icon while it is off or not a toggle
 * @param checkedContainerColor Fill of the button while it is on
 * @param checkedContentColor Color of the icon while it is on
 * @param labelColor Color of the label under the button
 */
@Immutable
public data class PvotCallButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val checkedContainerColor: Color,
    val checkedContentColor: Color,
    val labelColor: Color
)

/** Defaults for [PvotCallButton]. */
public object PvotCallButtonDefaults {

    /** Diameter of the button's circle. */
    public val Size: Dp = 64.dp

    /** Width of the whole target, circle and label, so labels of a row of buttons line up. */
    public val Width: Dp = 96.dp

    /** Corner radius the circle morphs to while the button is on. */
    public val CheckedCornerRadius: Dp = 20.dp

    /** Diameter of the dot that says a button opens more choices. */
    public val BadgeSize: Dp = 6.dp

    /** Opacity of a disabled button. */
    public const val DisabledAlpha: Float = 0.38f

    /** Tonal colors, for buttons on a solid surface or grouped on one glass card. */
    @Composable
    public fun colors(): PvotCallButtonColors = PvotCallButtonColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
        checkedContainerColor = MaterialTheme.colorScheme.primary,
        checkedContentColor = MaterialTheme.colorScheme.onPrimary,
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    /** Colors for a button that is its own glass: the glass is the fill while the button is off. */
    @Composable
    public fun glassColors(): PvotCallButtonColors = colors().copy(containerColor = Color.Transparent)
}

/**
 * A call control: a round button with a label under it, such as mute, speaker or keypad.
 *
 * The circle and the label are one target. With [checked] the button is a toggle: while on, the
 * circle morphs to a rounded square in the primary color, TalkBack reads it as a switch with its
 * state, and turning it on or off ticks the matching haptic. A [badge] dot says the button opens
 * more choices, such as the audio route.
 *
 * A row of call buttons is grouped on one glass card and left tonal; pass [backdrop] only for a
 * button that floats on its own.
 *
 * @param icon The button's icon
 * @param label The text under the button, also what TalkBack reads
 * @param onClick Called when the button is tapped
 * @param modifier Modifier for the button
 * @param checked Whether the toggle is on, or null for a button that is not a toggle
 * @param enabled Whether the button responds to taps
 * @param onLongClick Called when the button is held, or null for no long press
 * @param badge Whether to show the dot that says the button opens more choices
 * @param contentDescription What TalkBack reads instead of [label], or null for the label
 * @param backdrop The content behind the button for a button of its own glass, or null for tonal
 * @param material The glass material while [backdrop] is set
 * @param colors The button's colors
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun PvotCallButton(
    icon: Painter,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    checked: Boolean? = null,
    enabled: Boolean = true,
    onLongClick: (() -> Unit)? = null,
    badge: Boolean = false,
    contentDescription: String? = null,
    backdrop: PvotGlassBackdrop? = null,
    material: PvotGlassMaterial = PvotGlassMaterial.Clear,
    colors: PvotCallButtonColors = if (backdrop != null) {
        PvotCallButtonDefaults.glassColors()
    } else {
        PvotCallButtonDefaults.colors()
    }
) {
    val source = remember { MutableInteractionSource() }
    val haptics = LocalHapticFeedback.current
    val on = checked == true
    val spec = tween<Color>(PvotDurations.Medium, easing = PvotMotion.Settle)
    val container by animateColorAsState(
        if (on) colors.checkedContainerColor else colors.containerColor, spec, label = "callButtonFill"
    )
    val content by animateColorAsState(
        if (on) colors.checkedContentColor else colors.contentColor, spec, label = "callButtonIcon"
    )
    val corner by animateDpAsState(
        if (on) PvotCallButtonDefaults.CheckedCornerRadius else PvotCallButtonDefaults.Size / 2,
        tween(PvotDurations.Medium, easing = PvotMotion.Settle),
        label = "callButtonShape"
    )
    val stateOn = stringResource(R.string.pvot_state_on)
    val stateOff = stringResource(R.string.pvot_state_off)
    val description = contentDescription ?: label
    val alpha = if (enabled) 1f else PvotCallButtonDefaults.DisabledAlpha
    Column(
        modifier = modifier
            .width(PvotCallButtonDefaults.Width)
            .pvotPressScale(source)
            .combinedClickable(
                interactionSource = source,
                indication = null,
                enabled = enabled,
                role = if (checked != null) Role.Switch else Role.Button,
                onLongClick = onLongClick,
                onClick = {
                    if (checked != null) {
                        haptics.performHapticFeedback(
                            if (checked) HapticFeedbackType.ToggleOff else HapticFeedbackType.ToggleOn
                        )
                    }
                    onClick()
                }
            )
            .semantics(mergeDescendants = true) {
                this.contentDescription = description
                if (checked != null) {
                    toggleableState = ToggleableState(checked)
                    stateDescription = if (checked) stateOn else stateOff
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val shape = RoundedCornerShape(corner)
        val circle = if (backdrop != null) {
            Modifier.pvotGlassSurface(
                backdrop = backdrop,
                cornerRadius = corner,
                style = PvotGlassDefaults.materialStyle(material),
                role = null
            )
        } else {
            Modifier
        }
        Box(
            modifier = Modifier
                .size(PvotCallButtonDefaults.Size)
                .alpha(alpha)
                .then(circle)
                .clip(shape)
                .background(container, shape),
            contentAlignment = Alignment.Center
        ) {
            Icon(painter = icon, contentDescription = null, tint = content)
            if (badge) {
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(PvotSpacing.S)
                        .size(PvotCallButtonDefaults.BadgeSize)
                        .background(content, CircleShape)
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.labelColor.copy(alpha = colors.labelColor.alpha * alpha),
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.padding(top = PvotSpacing.S)
        )
    }
}
