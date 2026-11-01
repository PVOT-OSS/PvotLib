// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.button

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.effects.pvotPressScale
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.motion.PvotMotion
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing

/**
 * Color configuration for [PvotFab].
 *
 * @param containerColor Fill of the button
 * @param contentColor Color of the icon and label
 */
@Immutable
public data class PvotFabColors(
    val containerColor: Color,
    val contentColor: Color
)

/** Defaults for [PvotFab]. */
public object PvotFabDefaults {

    /** Shape of the button, a circle that grows into a pill when it shows its label. */
    public val Shape: Shape = PvotShapes.Pill

    /** Diameter of the button without its label. */
    public val Size: Dp = 56.dp

    /** Size of the icon. */
    public val IconSize: Dp = 24.dp

    /** Height of the soft shadow under the button. */
    public val Elevation: Dp = 6.dp

    /** Button colors derived from [colorScheme], each overridable. */
    public fun colors(
        colorScheme: ColorScheme,
        containerColor: Color = colorScheme.primary,
        contentColor: Color = colorScheme.onPrimary
    ): PvotFabColors = PvotFabColors(containerColor, contentColor)

    /** Button colors from the theme's color scheme. */
    @Composable
    public fun themeColors(): PvotFabColors = colors(MaterialTheme.colorScheme)
}

/**
 * A floating action button: the one main action of a screen, floating above its content.
 *
 * With [text] and [expanded] it is a pill with the icon and the label; set [expanded] to false,
 * for example while the list under it scrolls down, and the label folds away into a round icon
 * button. It squashes slightly under the finger.
 *
 * @param icon The button's icon
 * @param onClick Called when the button is tapped
 * @param contentDescription What the button does, read by TalkBack; when null, [text] is read
 * @param modifier Modifier for the button
 * @param text Label shown beside the icon while [expanded]
 * @param expanded Whether the label shows
 * @param colors The button's colors
 * @param interactionSource The source driving the press animation, hoist it to react to presses
 */
@Composable
public fun PvotFab(
    icon: Painter,
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    text: String? = null,
    expanded: Boolean = true,
    colors: PvotFabColors = PvotFabDefaults.themeColors(),
    interactionSource: MutableInteractionSource? = null
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val label = contentDescription ?: text
    Row(
        modifier = modifier
            .pvotPressScale(source)
            .heightIn(min = PvotFabDefaults.Size)
            .widthIn(min = PvotFabDefaults.Size)
            .shadow(PvotFabDefaults.Elevation, PvotFabDefaults.Shape)
            .clip(PvotFabDefaults.Shape)
            .background(colors.containerColor)
            .clickable(
                interactionSource = source,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .semantics(mergeDescendants = true) {
                if (label != null) this.contentDescription = label
            }
            .padding(horizontal = (PvotFabDefaults.Size - PvotFabDefaults.IconSize) / 2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = colors.contentColor,
            modifier = Modifier.size(PvotFabDefaults.IconSize)
        )
        AnimatedVisibility(
            visible = text != null && expanded,
            enter = expandHorizontally(tween(PvotDurations.Medium, easing = PvotMotion.Settle)) +
                fadeIn(tween(PvotDurations.Medium)),
            exit = shrinkHorizontally(tween(PvotDurations.Medium, easing = PvotMotion.Settle)) +
                fadeOut(tween(PvotDurations.Short))
        ) {
            Text(
                text = text.orEmpty(),
                style = MaterialTheme.typography.labelLarge,
                color = colors.contentColor,
                maxLines = 1,
                modifier = Modifier.padding(start = PvotSpacing.M, end = PvotSpacing.XS)
            )
        }
    }
}
