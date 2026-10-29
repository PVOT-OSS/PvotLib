// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassDefaults
import com.prauga.pvot.designsystem.effects.PvotGlassStyle
import com.prauga.pvot.designsystem.theme.PvotSpacing

/** Defaults for [PvotGlassCard]. */
public object PvotGlassCardDefaults {

    /** The card's corner radius. */
    public val CornerRadius: Dp = 16.dp

    /** Padding round the card's content. */
    public val ContentPadding: PaddingValues = PaddingValues(PvotSpacing.L)
}

/**
 * A card of glass over [backdrop], with a rim of light that follows the phone's tilt.
 *
 * Over a backdrop it floats on a soft shadow; pressed, it swells and brightens.
 *
 * Pass [onClick] to make the whole card act as a button; leave it null for a static card.
 *
 * @param backdrop The content behind the card, marked with
 * [pvotGlassBackdrop][com.prauga.pvot.designsystem.effects.pvotGlassBackdrop], or null for
 * the tint alone
 * @param modifier Modifier for the card
 * @param onClick Called when the card is tapped, or null for a non-clickable card
 * @param enabled Whether the card responds to taps, only meaningful with [onClick]
 * @param cornerRadius The card's corner radius
 * @param style How the glass looks
 * @param rim Whether to draw the rim of light round the card
 * @param contentPadding Padding round the card's content
 * @param interactionSource The source driving the press animation, hoist it to react to
 * presses elsewhere
 */
@Composable
public fun PvotGlassCard(
    backdrop: PvotGlassBackdrop?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    cornerRadius: Dp = PvotGlassCardDefaults.CornerRadius,
    style: PvotGlassStyle = PvotGlassDefaults.Style,
    rim: Boolean = true,
    contentPadding: PaddingValues = PvotGlassCardDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = modifier.glassSurface(
        backdrop = backdrop,
        cornerRadius = cornerRadius,
        style = style,
        rim = rim,
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource
    )
    Column(
        modifier = cardModifier.padding(contentPadding),
        content = content
    )
}
