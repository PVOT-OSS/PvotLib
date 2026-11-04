// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Defaults for [PvotAvatarStack]. */
public object PvotAvatarStackDefaults {

    /** Size of each avatar, small enough to sit beside a card's title. */
    public val Size: Dp = 28.dp

    /** How many avatars show before the rest become a "+n" count. */
    public const val MaxShown: Int = 3

    /** How much of each avatar shows past the one in front of it. */
    public const val Overlap: Float = 2f / 3f
}

/**
 * People as overlapping avatars, such as an event's guests on its card: the first [maxShown]
 * [names] as [PvotAvatar]s, then "+n" for the rest. Each avatar gets a [ring] in the color behind
 * the stack so the overlaps read apart. The stack is read out as one label: [contentDescription],
 * or the names shown and how many more there are.
 *
 * @param names The people, in order
 * @param modifier Modifier for the stack
 * @param size Size of each avatar
 * @param maxShown How many avatars show before the "+n" count
 * @param ring Color of the ring round each avatar, the color the stack sits on
 * @param contentDescription What accessibility services say for the whole stack
 */
@Composable
public fun PvotAvatarStack(
    names: List<String>,
    modifier: Modifier = Modifier,
    size: Dp = PvotAvatarStackDefaults.Size,
    maxShown: Int = PvotAvatarStackDefaults.MaxShown,
    ring: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentDescription: String? = null,
) {
    if (names.isEmpty()) return
    val shown = names.take(maxShown.coerceAtLeast(1))
    val more = names.size - shown.size
    val spoken = contentDescription ?: (shown.joinToString(", ") + if (more > 0) " +$more" else "")
    val step = size * PvotAvatarStackDefaults.Overlap
    Row(
        modifier = modifier.clearAndSetSemantics { this.contentDescription = spoken },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            shown.forEachIndexed { index, name ->
                PvotAvatar(
                    name = name,
                    size = size,
                    ring = ring,
                    modifier = Modifier.padding(start = step * index),
                )
            }
        }
        if (more > 0) {
            Text(
                text = "+$more",
                modifier = Modifier.padding(start = 4.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
