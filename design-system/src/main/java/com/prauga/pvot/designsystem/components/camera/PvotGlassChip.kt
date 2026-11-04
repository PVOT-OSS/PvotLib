// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.camera

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prauga.pvot.designsystem.components.pvotGlassSurface
import com.prauga.pvot.designsystem.effects.LocalPvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassDefaults
import com.prauga.pvot.designsystem.effects.PvotGlassStyle

/** Defaults for the glass of the camera components. */
public object PvotCameraGlassDefaults {
    /** The tint over a live camera preview: dark enough for white labels to read on any scene. */
    public val FillColor: Color = Color(0x8C3A3A3A)

    /**
     * The glass of camera controls: fully frosted, so the preview behind blurs instead of showing
     * through sharp, with the usual edge lens.
     */
    public val Style: PvotGlassStyle =
        PvotGlassDefaults.Style.copy(fillColor = FillColor, frostMix = 1f)

    /** The glass of the camera's larger panels and sheets: thicker, for rows of text. */
    public val PanelStyle: PvotGlassStyle = Style.copy(fillColor = Color(0xC22C2C2E))
}

/** Defaults for [PvotGlassChip]. */
public object PvotGlassChipDefaults {
    /** The chip's height. */
    public val Height: Dp = 32.dp

    /** The chip's narrowest width, so a short label still makes a comfortable target. */
    public val MinWidth: Dp = 74.dp

    /** Space between the chip's edge and its content. */
    public val ContentPadding: PaddingValues = PaddingValues(horizontal = 12.dp)
}

/**
 * A small glass label over a camera preview, such as the photo format, a recording timer or a
 * status like AE/AF LOCK; with [onClick] it is a button.
 *
 * @param text The label, or null for a chip with only [leading]
 * @param modifier Modifier for the chip
 * @param backdrop The content behind the chip, or null for the tint alone
 * @param onClick Called when the chip is tapped, or null for a label that is not a button
 * @param enabled Whether the chip responds to taps
 * @param cornerRadius The chip's corner radius, [PvotGlassDefaults.CapsuleRadius] for a capsule
 * @param contentColor Color of the label and of [leading], provided as [LocalContentColor]
 * @param style How the glass looks
 * @param contentPadding Space between the chip's edge and its content
 * @param leading Content before the label, such as a recording dot or an icon
 */
@Composable
public fun PvotGlassChip(
    text: String?,
    modifier: Modifier = Modifier,
    backdrop: PvotGlassBackdrop? = LocalPvotGlassBackdrop.current,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    cornerRadius: Dp = PvotGlassDefaults.CapsuleRadius,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    style: PvotGlassStyle = PvotCameraGlassDefaults.Style,
    contentPadding: PaddingValues = PvotGlassChipDefaults.ContentPadding,
    leading: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier =
            modifier
                .height(PvotGlassChipDefaults.Height)
                .defaultMinSize(
                    minWidth = if (text != null) PvotGlassChipDefaults.MinWidth else 0.dp
                )
                .pvotGlassSurface(
                    backdrop = backdrop,
                    cornerRadius = cornerRadius,
                    style = style,
                    onClick = onClick,
                    enabled = enabled,
                )
                .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            leading?.invoke()
            if (text != null) {
                Text(
                    text = text,
                    color = contentColor.copy(alpha = if (enabled) 1f else 0.4f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }
        }
    }
}
