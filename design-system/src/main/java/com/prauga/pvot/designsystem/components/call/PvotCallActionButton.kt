// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.call

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.effects.pvotPressScale
import com.prauga.pvot.designsystem.theme.PvotSpacing
import com.prauga.pvot.designsystem.theme.PvotTheme

/** What a [PvotCallActionButton] does, which picks its color. */
public enum class PvotCallAction {
    /** Answer a call or place one: the success color. */
    Answer,

    /** Decline a ringing call: the error color. */
    Decline,

    /** End the call in progress: the error color. */
    End
}

/**
 * The colors of a [PvotCallActionButton].
 *
 * @param containerColor Fill of the button
 * @param contentColor Color of the icon
 * @param labelColor Color of the label under the button
 */
@Immutable
public data class PvotCallActionButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val labelColor: Color
)

/** Defaults for [PvotCallActionButton]. */
public object PvotCallActionButtonDefaults {

    /** Height of the button, and its width unless set: a circle. */
    public val Size: Dp = 72.dp

    /** Width of the end-call pill, wider than tall so it reads apart from the controls. */
    public val EndWidth: Dp = 128.dp

    /** Size of the icon. */
    public val IconSize: Dp = 32.dp

    /** Opacity of a disabled button. */
    public const val DisabledAlpha: Float = 0.38f

    /** Height of a pill with its label inside, such as "End" under a call's controls. */
    public val LabelledHeight: Dp = 64.dp

    /** Padding at each side of a pill with its label inside. */
    public val LabelledHorizontalPadding: Dp = PvotSpacing.L * 2

    /** How much darker the bottom of a labelled pill's gradient is. */
    public const val GradientDepth: Float = 0.2f

    /** The colors for [action]: answer in the success color, decline and end in the error color. */
    @Composable
    public fun colors(action: PvotCallAction): PvotCallActionButtonColors = when (action) {
        PvotCallAction.Answer -> PvotCallActionButtonColors(
            containerColor = PvotTheme.extendedColors.success,
            contentColor = PvotTheme.extendedColors.onSuccess,
            labelColor = MaterialTheme.colorScheme.onSurface
        )
        PvotCallAction.Decline, PvotCallAction.End -> PvotCallActionButtonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
            labelColor = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * The button that answers, declines or ends a call, or places one from the keypad.
 *
 * It is always solid, never glass: the one action that must be found at a glance keeps its color
 * over any background. A circle by default; give the end-call button
 * [PvotCallActionButtonDefaults.EndWidth] for a pill, or set [labelInside] for a pill that says
 * what it does, such as "End".
 *
 * @param action What the button does, which picks its color
 * @param icon The button's icon
 * @param label What the button does, read by TalkBack and shown under it with [showLabel]
 * @param onClick Called when the button is tapped
 * @param modifier Modifier for the button
 * @param size Height of the button, and its width unless [width] is set
 * @param width Width of the button
 * @param showLabel Whether to show [label] under the button
 * @param labelInside Whether to show [label] inside, after the icon, as a pill as wide as it needs
 * @param enabled Whether the button responds to taps
 * @param colors The button's colors
 */
@Composable
public fun PvotCallActionButton(
    action: PvotCallAction,
    icon: Painter,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = PvotCallActionButtonDefaults.Size,
    width: Dp = size,
    showLabel: Boolean = false,
    labelInside: Boolean = false,
    enabled: Boolean = true,
    colors: PvotCallActionButtonColors = PvotCallActionButtonDefaults.colors(action)
) {
    val source = remember { MutableInteractionSource() }
    val alpha = if (enabled) 1f else PvotCallActionButtonDefaults.DisabledAlpha
    Column(
        modifier = modifier
            .pvotPressScale(source)
            .clickable(
                interactionSource = source,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .semantics(mergeDescendants = true) { contentDescription = label },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (labelInside) {
            Row(
                modifier = Modifier
                    .height(PvotCallActionButtonDefaults.LabelledHeight)
                    .widthIn(min = width)
                    .alpha(alpha)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                colors.containerColor,
                                lerp(colors.containerColor, Color.Black, PvotCallActionButtonDefaults.GradientDepth)
                            )
                        )
                    )
                    .padding(horizontal = PvotCallActionButtonDefaults.LabelledHorizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(painter = icon, contentDescription = null, tint = colors.contentColor)
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.contentColor,
                    maxLines = 1,
                    modifier = Modifier.padding(start = PvotSpacing.S)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(width = width, height = size)
                    .alpha(alpha)
                    .clip(CircleShape)
                    .background(colors.containerColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = colors.contentColor,
                    modifier = Modifier.size(PvotCallActionButtonDefaults.IconSize)
                )
            }
        }
        if (showLabel && !labelInside) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = colors.labelColor.copy(alpha = colors.labelColor.alpha * alpha),
                modifier = Modifier.padding(top = PvotSpacing.S)
            )
        }
    }
}
