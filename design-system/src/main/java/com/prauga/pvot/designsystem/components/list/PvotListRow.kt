// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.effects.pvotPressScale
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.motion.PvotMotion
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing

/**
 * Color configuration for [PvotListRow] and the rows built on it.
 *
 * @param containerColor Fill of the row at rest, usually transparent
 * @param selectedContainerColor Fill of a selected row
 * @param titleColor Color of the title
 * @param supportingTextColor Color of the supporting text
 * @param leadingColor Content color of the leading slot, such as an icon
 * @param trailingColor Content color of the trailing slot
 * @param iconContainerColor Fill of the circle behind the icon of a settings row
 * @param iconColor Tint of the icon of a settings row
 * @param valueColor Color of the current value of a [PvotPreferenceRow]
 */
@Immutable
public data class PvotListRowColors(
    val containerColor: Color,
    val selectedContainerColor: Color,
    val titleColor: Color,
    val supportingTextColor: Color,
    val leadingColor: Color,
    val trailingColor: Color,
    val iconContainerColor: Color,
    val iconColor: Color,
    val valueColor: Color
)

/** Defaults for [PvotListRow], [PvotSwitchRow] and [PvotPreferenceRow]. */
public object PvotListRowDefaults {

    /** Shape of the row, which clips the selection fill and the press. */
    public val Shape: Shape = PvotShapes.Card

    /** The shortest a row gets, comfortably above the 48dp touch target. */
    public val MinHeight: Dp = 56.dp

    /** Padding inside the row. */
    public val ContentPadding: PaddingValues =
        PaddingValues(horizontal = PvotSpacing.M, vertical = PvotSpacing.S)

    /** Size of a leading avatar, letter tile or icon circle. */
    public val LeadingSize: Dp = 40.dp

    /** Opacity of a disabled row. */
    public const val DisabledAlpha: Float = 0.38f

    /** Row colors derived from [colorScheme], each overridable. */
    public fun colors(
        colorScheme: ColorScheme,
        containerColor: Color = Color.Transparent,
        selectedContainerColor: Color = colorScheme.primary.copy(alpha = 0.14f),
        titleColor: Color = colorScheme.onSurface,
        supportingTextColor: Color = colorScheme.onSurfaceVariant,
        leadingColor: Color = colorScheme.onSurfaceVariant,
        trailingColor: Color = colorScheme.onSurfaceVariant,
        iconContainerColor: Color = colorScheme.primary.copy(alpha = 0.14f),
        iconColor: Color = colorScheme.primary,
        valueColor: Color = colorScheme.primary
    ): PvotListRowColors = PvotListRowColors(
        containerColor = containerColor,
        selectedContainerColor = selectedContainerColor,
        titleColor = titleColor,
        supportingTextColor = supportingTextColor,
        leadingColor = leadingColor,
        trailingColor = trailingColor,
        iconContainerColor = iconContainerColor,
        iconColor = iconColor,
        valueColor = valueColor
    )

    /** Row colors from the theme's color scheme. */
    @Composable
    public fun themeColors(): PvotListRowColors = colors(MaterialTheme.colorScheme)
}

/**
 * A row in a list: a leading visual, a title with supporting text under it, and a trailing slot.
 *
 * The leading slot takes an avatar (an image clipped to a circle of
 * [PvotListRowDefaults.LeadingSize]), a [PvotLetterTile] or an icon. Give the row [onClick] or
 * [onLongClick] and the whole row becomes the touch target, squashing slightly under the finger;
 * leave both null for a static row. A long press also ticks the haptics.
 *
 * @param title Main text of the row, one line
 * @param modifier Modifier for the row
 * @param supportingText Secondary text under the title, up to two lines
 * @param leading Content at the start, such as an avatar or [PvotLetterTile]
 * @param trailing Content at the end, such as a time, an icon button or a chevron
 * @param onClick Called when the row is tapped
 * @param onLongClick Called when the row is long-pressed, for example to start selecting
 * @param onClickLabel Spoken description of what a tap does, such as "Call"
 * @param selected Whether the row is selected, which fills it with a tint of the brand color
 * @param enabled Whether the row responds to touch; a disabled row is dimmed
 * @param colors The row's colors
 * @param interactionSource The source driving the press animation, hoist it to react to presses
 */
@Composable
public fun PvotListRow(
    title: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    colors: PvotListRowColors = PvotListRowDefaults.themeColors(),
    interactionSource: MutableInteractionSource? = null
) {
    PvotListRow(
        title = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        modifier = modifier,
        supportingText = supportingText?.let {
            { Text(text = it, maxLines = 2, overflow = TextOverflow.Ellipsis) }
        },
        leading = leading,
        trailing = trailing,
        onClick = onClick,
        onLongClick = onLongClick,
        onClickLabel = onClickLabel,
        selected = selected,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource
    )
}

/**
 * A [PvotListRow] whose title and supporting text are slots, for rich text such as a highlighted
 * search match or a call type icon in front of the detail.
 *
 * The slots are styled for you: [title] in `bodyLarge` and the title color, [supportingText] in
 * `bodyMedium` and the supporting text color.
 *
 * @param title Main content of the row
 * @param modifier Modifier for the row
 * @param supportingText Secondary content under the title
 * @param leading Content at the start, such as an avatar or [PvotLetterTile]
 * @param trailing Content at the end, such as a time, an icon button or a chevron
 * @param onClick Called when the row is tapped
 * @param onLongClick Called when the row is long-pressed
 * @param onClickLabel Spoken description of what a tap does
 * @param selected Whether the row is selected
 * @param enabled Whether the row responds to touch; a disabled row is dimmed
 * @param colors The row's colors
 * @param interactionSource The source driving the press animation
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun PvotListRow(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    supportingText: (@Composable () -> Unit)? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    colors: PvotListRowColors = PvotListRowDefaults.themeColors(),
    interactionSource: MutableInteractionSource? = null
) {
    val clickable = onClick != null || onLongClick != null
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val touch = if (clickable) {
        Modifier
            .pvotPressScale(source)
            .clip(PvotListRowDefaults.Shape)
            .combinedClickable(
                interactionSource = source,
                indication = null,
                enabled = enabled,
                onClickLabel = onClickLabel,
                role = if (onClick != null) Role.Button else null,
                onLongClick = onLongClick,
                onClick = onClick ?: {}
            )
    } else {
        Modifier.clip(PvotListRowDefaults.Shape)
    }
    PvotRowLayout(
        modifier = modifier.then(touch),
        title = title,
        supportingText = supportingText,
        leading = leading,
        trailing = trailing,
        selected = selected,
        enabled = enabled,
        colors = colors
    )
}

/**
 * The layout shared by every Pvot row: the fill, the padding, the slots and their styles. The
 * caller adds the touch handling to [modifier], after the clip.
 */
@Composable
internal fun PvotRowLayout(
    modifier: Modifier,
    title: @Composable () -> Unit,
    supportingText: (@Composable () -> Unit)?,
    leading: (@Composable () -> Unit)?,
    trailing: (@Composable () -> Unit)?,
    selected: Boolean,
    enabled: Boolean,
    colors: PvotListRowColors
) {
    val container by animateColorAsState(
        targetValue = if (selected) colors.selectedContainerColor else colors.containerColor,
        animationSpec = tween(PvotDurations.Short, easing = PvotMotion.Settle),
        label = "pvotListRowContainer"
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = PvotListRowDefaults.MinHeight)
            .background(container)
            .padding(PvotListRowDefaults.ContentPadding)
            .alpha(if (enabled) 1f else PvotListRowDefaults.DisabledAlpha),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PvotSpacing.L)
    ) {
        if (leading != null) {
            CompositionLocalProvider(LocalContentColor provides colors.leadingColor, content = leading)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            CompositionLocalProvider(
                LocalContentColor provides colors.titleColor,
                LocalTextStyle provides MaterialTheme.typography.bodyLarge,
                content = title
            )
            if (supportingText != null) {
                CompositionLocalProvider(
                    LocalContentColor provides colors.supportingTextColor,
                    LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                    content = supportingText
                )
            }
        }
        if (trailing != null) {
            CompositionLocalProvider(
                LocalContentColor provides colors.trailingColor,
                LocalTextStyle provides MaterialTheme.typography.labelLarge,
                content = trailing
            )
        }
    }
}

/** The tinted circle that holds the icon of a settings row. */
@Composable
internal fun PvotRowIcon(colors: PvotListRowColors, icon: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(PvotListRowDefaults.LeadingSize)
            .background(colors.iconContainerColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides colors.iconColor, content = icon)
    }
}
