// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.prauga.pvot.designsystem.effects.LocalPvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassDefaults
import com.prauga.pvot.designsystem.effects.PvotGlassMaterial
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing

/** Defaults for [PvotCapsule]. */
public object PvotCapsuleDefaults {

    /** Space between the capsule's edge and the controls in it. */
    public val ContentPadding: Dp = PvotSpacing.S

    /** Space between the controls in the capsule. */
    public val Spacing: Dp = PvotSpacing.S

    /** How much the glass hides what is behind it: enough for a label inside to read. */
    public val Material: PvotGlassMaterial = PvotGlassMaterial.Regular

    /** Fill of the capsule when it has no backdrop to be glass over. */
    @Composable public fun containerColor(): Color = MaterialTheme.colorScheme.surfaceContainer
}

/**
 * A pill that groups a row of floating controls, such as a dock's tabs or a call's buttons.
 *
 * Over a backdrop it is glass; without one it is a solid pill in [containerColor]. With the glass
 * turned to Solid (the user's setting or battery saver) the glass is an opaque surface on its own.
 *
 * @param modifier Modifier for the capsule
 * @param backdrop The content behind the capsule, or null for a solid capsule
 * @param contentPadding Space between the capsule's edge and its controls
 * @param spacing Space between the controls
 * @param containerColor Fill of a solid capsule
 * @param content The controls, laid out in a row
 */
@Composable
public fun PvotCapsule(
    modifier: Modifier = Modifier,
    backdrop: PvotGlassBackdrop? = LocalPvotGlassBackdrop.current,
    contentPadding: Dp = PvotCapsuleDefaults.ContentPadding,
    spacing: Dp = PvotCapsuleDefaults.Spacing,
    containerColor: Color = PvotCapsuleDefaults.containerColor(),
    content: @Composable RowScope.() -> Unit,
) {
    val surface =
        if (backdrop != null) {
            Modifier.pvotGlassSurface(
                backdrop = backdrop,
                cornerRadius = PvotGlassDefaults.CapsuleRadius,
                style = PvotGlassDefaults.materialStyle(PvotCapsuleDefaults.Material),
                role = null,
            )
        } else {
            Modifier.background(containerColor, PvotShapes.Pill)
        }
    Row(
        modifier = modifier.then(surface).padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}
