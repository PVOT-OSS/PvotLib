// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.prauga.pvot.designsystem.effects.pvotPressScale
import com.prauga.pvot.designsystem.theme.PvotShapes

/** Defaults for [PvotCard]. */
public object PvotCardDefaults {

    /** How much of the surface variant shows through the card. */
    public const val ContainerAlpha: Float = 0.5f

    /** The card's default shape. */
    public val shape: Shape = PvotShapes.Card

    /** The card's colors, translucent over the surface variant by default. */
    @Composable
    public fun colors(
        containerColor: Color =
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = ContainerAlpha)
    ): CardColors = CardDefaults.cardColors(containerColor = containerColor)

    /** The card's default elevation. */
    @Composable
    public fun elevation(): CardElevation = CardDefaults.cardElevation()
}

/**
 * A rounded, translucent surface for grouping content.
 *
 * Pass [onClick] to make the whole card act as a button; leave it null for a
 * static card.
 *
 * @param modifier Modifier for the card
 * @param onClick Called when the card is tapped, or null for a non-clickable card
 * @param enabled Whether the card responds to taps, only meaningful with [onClick]
 * @param shape The card's shape
 * @param colors The card's colors
 * @param elevation The card's elevation
 * @param border An optional border drawn around the card
 * @param interactionSource The source driving the press animation, hoist it to react
 * to presses elsewhere
 */
@Composable
public fun PvotCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = PvotCardDefaults.shape,
    colors: CardColors = PvotCardDefaults.colors(),
    elevation: CardElevation = PvotCardDefaults.elevation(),
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        // The card's own click overload, so the press highlight is clipped to the card's shape. A
        // clickable around the card would draw it over the card's square bounds instead.
        Card(
            onClick = onClick,
            modifier = modifier
                .pvotPressScale(interactionSource)
                .semantics { role = Role.Button },
            enabled = enabled,
            shape = shape,
            colors = colors,
            elevation = elevation,
            border = border,
            interactionSource = interactionSource,
            content = content
        )
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = colors,
            elevation = elevation,
            border = border,
            content = content
        )
    }
}
