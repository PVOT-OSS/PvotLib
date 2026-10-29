// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassDefaults
import com.prauga.pvot.designsystem.effects.PvotGlassStyle

/** Defaults for [PvotGlassIconButton]. */
public object PvotGlassIconButtonDefaults {

    /** The button's diameter. */
    public val Size: Dp = 52.dp
}

/**
 * A round button of glass over [backdrop], for an icon floating above the content.
 *
 * Over a backdrop it floats on a soft shadow; pressed, it swells and brightens.
 *
 * @param onClick Called when the button is tapped
 * @param backdrop The content behind the button, marked with
 * [pvotGlassBackdrop][com.prauga.pvot.designsystem.effects.pvotGlassBackdrop], or null for
 * the tint alone
 * @param modifier Modifier for the button
 * @param enabled Whether the button responds to taps
 * @param size The button's diameter
 * @param style How the glass looks
 * @param contentColor Color of the icon, provided as [LocalContentColor]
 * @param interactionSource The source driving the press animation, hoist it to react to
 * presses elsewhere
 * @param content The icon
 */
@Composable
public fun PvotGlassIconButton(
    onClick: () -> Unit,
    backdrop: PvotGlassBackdrop?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = PvotGlassIconButtonDefaults.Size,
    style: PvotGlassStyle = PvotGlassDefaults.Style,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(size)
            .glassSurface(
                backdrop = backdrop,
                cornerRadius = PvotGlassDefaults.CapsuleRadius,
                style = style,
                rim = true,
                onClick = onClick,
                enabled = enabled,
                interactionSource = interactionSource
            ),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
    }
}
