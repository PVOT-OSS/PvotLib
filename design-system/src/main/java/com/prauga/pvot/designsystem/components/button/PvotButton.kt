// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.button

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ColorScheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.effects.pvotPressScale
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.motion.PvotMotion
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing

/** How prominent a [PvotButton] is. */
public enum class PvotButtonStyle {
    /** The main action of a screen, filled in the brand color. */
    Filled,

    /** A secondary action, filled with a soft tint. */
    Tonal,

    /** A quiet action with no fill, such as Cancel or Learn more. */
    Text,

    /** An action that destroys something, filled in the error color. */
    Destructive
}

/**
 * Color configuration for [PvotButton].
 *
 * @param containerColor Fill of an enabled button
 * @param contentColor Color of the label and icon of an enabled button
 * @param disabledContainerColor Fill of a disabled button
 * @param disabledContentColor Color of the label and icon of a disabled button
 */
@Immutable
public data class PvotButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color
)

/** Defaults for [PvotButton]. */
public object PvotButtonDefaults {

    /** Shape of the button, a pill. */
    public val Shape: Shape = PvotShapes.Pill

    /** The shortest a button gets, the minimum touch target. */
    public val MinHeight: Dp = 48.dp

    /** The narrowest a button gets, so a short label still makes a comfortable target. */
    public val MinWidth: Dp = 64.dp

    /** Padding around the label. */
    public val ContentPadding: PaddingValues =
        PaddingValues(horizontal = PvotSpacing.L * 1.5f, vertical = PvotSpacing.M)

    /** Padding around the label of a [PvotButtonStyle.Text] button, which has no fill to frame. */
    public val TextContentPadding: PaddingValues =
        PaddingValues(horizontal = PvotSpacing.M, vertical = PvotSpacing.M)

    /** Size of the icon before the label. */
    public val IconSize: Dp = 18.dp

    /** Button colors for [style] derived from [colorScheme]. */
    public fun colors(colorScheme: ColorScheme, style: PvotButtonStyle): PvotButtonColors {
        val disabledContent = colorScheme.onSurface.copy(alpha = 0.38f)
        val disabledContainer = colorScheme.onSurface.copy(alpha = 0.12f)
        return when (style) {
            PvotButtonStyle.Filled -> PvotButtonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                disabledContainerColor = disabledContainer,
                disabledContentColor = disabledContent
            )
            PvotButtonStyle.Tonal -> PvotButtonColors(
                containerColor = colorScheme.primary.copy(alpha = 0.14f),
                contentColor = colorScheme.primary,
                disabledContainerColor = disabledContainer,
                disabledContentColor = disabledContent
            )
            PvotButtonStyle.Text -> PvotButtonColors(
                containerColor = Color.Transparent,
                contentColor = colorScheme.primary,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = disabledContent
            )
            PvotButtonStyle.Destructive -> PvotButtonColors(
                containerColor = colorScheme.error,
                contentColor = colorScheme.onError,
                disabledContainerColor = disabledContainer,
                disabledContentColor = disabledContent
            )
        }
    }

    /** Button colors for [style] from the theme's color scheme. */
    @Composable
    public fun themeColors(style: PvotButtonStyle): PvotButtonColors =
        colors(MaterialTheme.colorScheme, style)
}

/**
 * A pill-shaped button with a label and an optional icon before it.
 *
 * Pick how prominent it is with [style]: one [PvotButtonStyle.Filled] button for the main action
 * of a screen, [PvotButtonStyle.Tonal] or [PvotButtonStyle.Text] for the rest, and
 * [PvotButtonStyle.Destructive] for an action that destroys something. It squashes slightly
 * under the finger and is at least 48dp tall. Stretch it with `Modifier.fillMaxWidth()`.
 *
 * @param text Label of the button
 * @param onClick Called when the button is tapped
 * @param modifier Modifier for the button
 * @param style How prominent the button is
 * @param icon Icon before the label, tinted with the label
 * @param enabled Whether the button responds to taps
 * @param colors The button's colors, by default those of [style]
 * @param contentPadding Padding around the label
 * @param interactionSource The source driving the press animation, hoist it to react to presses
 */
@Composable
public fun PvotButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: PvotButtonStyle = PvotButtonStyle.Filled,
    icon: Painter? = null,
    enabled: Boolean = true,
    colors: PvotButtonColors = PvotButtonDefaults.themeColors(style),
    contentPadding: PaddingValues = if (style == PvotButtonStyle.Text) {
        PvotButtonDefaults.TextContentPadding
    } else {
        PvotButtonDefaults.ContentPadding
    },
    interactionSource: MutableInteractionSource? = null
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val container by animateColorAsState(
        targetValue = if (enabled) colors.containerColor else colors.disabledContainerColor,
        animationSpec = tween(PvotDurations.Short, easing = PvotMotion.Settle),
        label = "pvotButtonContainer"
    )
    val content by animateColorAsState(
        targetValue = if (enabled) colors.contentColor else colors.disabledContentColor,
        animationSpec = tween(PvotDurations.Short, easing = PvotMotion.Settle),
        label = "pvotButtonContent"
    )
    Row(
        modifier = modifier
            .pvotPressScale(source)
            .defaultMinSize(minWidth = PvotButtonDefaults.MinWidth, minHeight = PvotButtonDefaults.MinHeight)
            .clip(PvotButtonDefaults.Shape)
            .background(container)
            .clickable(
                interactionSource = source,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(PvotSpacing.S, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(PvotButtonDefaults.IconSize)
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = content,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
