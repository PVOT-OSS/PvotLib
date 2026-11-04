// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.components.PvotCard
import com.prauga.pvot.designsystem.components.PvotCardDefaults
import com.prauga.pvot.designsystem.components.list.PvotAvatarStack
import com.prauga.pvot.designsystem.theme.PvotSpacing

/** Defaults for [PvotEventCard]. */
public object PvotEventCardDefaults {

    /** Width of the accent bar along the card's start edge. */
    public val AccentWidth: Dp = 6.dp

    /** Fill of the card, a step above the surface it sits on. */
    @Composable public fun containerColor(): Color = MaterialTheme.colorScheme.surfaceContainerHigh
}

/**
 * An event or a task in a list: a [PvotCard] with the event's [accent] color as a bar along the
 * start edge, its [title] and [supportingText] (usually the time), the [guests] as a
 * [PvotAvatarStack] by the title, and an optional [action] at the bottom end, such as a "Join"
 * [PvotButton][com.prauga.pvot.designsystem.components.button.PvotButton] for a meeting. [done]
 * strikes the title through; [dense] fits a tighter list.
 *
 * @param title The event's title
 * @param onClick Called when the card is tapped
 * @param accent The event's color
 * @param modifier Modifier for the card
 * @param supportingText The line under the title, or empty for none
 * @param guests Names of the people invited, shown as avatars
 * @param done Whether the title is struck through, such as a finished task
 * @param dense Whether the card uses less vertical space
 * @param containerColor Fill of the card
 * @param action Content at the bottom end, or null for none
 */
@Composable
public fun PvotEventCard(
    title: String,
    onClick: () -> Unit,
    accent: Color,
    modifier: Modifier = Modifier,
    supportingText: String = "",
    guests: List<String> = emptyList(),
    done: Boolean = false,
    dense: Boolean = false,
    containerColor: Color = PvotEventCardDefaults.containerColor(),
    action: (@Composable () -> Unit)? = null,
) {
    PvotCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = PvotCardDefaults.colors(containerColor = containerColor),
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            Box(
                Modifier.width(PvotEventCardDefaults.AccentWidth)
                    .fillMaxHeight()
                    .background(accent, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            )
            Column(
                Modifier.weight(1f)
                    .padding(
                        horizontal = PvotSpacing.M + 2.dp,
                        vertical = if (dense) PvotSpacing.S else PvotSpacing.M + 2.dp,
                    ),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = title,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = if (dense) 1 else 2,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (done) TextDecoration.LineThrough else null,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (guests.isNotEmpty()) {
                        Spacer(Modifier.width(PvotSpacing.S))
                        PvotAvatarStack(guests, ring = containerColor)
                    }
                }
                if (supportingText.isNotEmpty()) {
                    Text(
                        text = supportingText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }
                if (action != null) {
                    Row(Modifier.fillMaxWidth()) {
                        Spacer(Modifier.weight(1f))
                        action()
                    }
                }
            }
        }
    }
}
