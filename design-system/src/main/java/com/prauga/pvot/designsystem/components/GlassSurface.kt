// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.isFinite
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.LocalPvotGlassIntensity
import com.prauga.pvot.designsystem.effects.PvotGlassDefaults
import com.prauga.pvot.designsystem.effects.PvotGlassStyle
import com.prauga.pvot.designsystem.effects.pvotGlass
import com.prauga.pvot.designsystem.effects.pvotGlassRim
import com.prauga.pvot.designsystem.effects.pvotPressScale
import com.prauga.pvot.designsystem.motion.PvotDurations

@Composable
internal fun Modifier.glassSurface(
    backdrop: PvotGlassBackdrop?,
    cornerRadius: Dp,
    style: PvotGlassStyle,
    rim: Boolean,
    onClick: (() -> Unit)?,
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
): Modifier {
    val intensity = LocalPvotGlassIntensity.current
    val glassStyle = PvotGlassDefaults.style(intensity, style)
    val shape = if (cornerRadius.isFinite) {
        RoundedCornerShape(cornerRadius)
    } else {
        RoundedCornerShape(percent = 50)
    }
    val pressed by interactionSource.collectIsPressedAsState()
    val fill by animateColorAsState(
        targetValue = if (pressed) PvotGlassDefaults.PressedFillColor else glassStyle.fillColor,
        animationSpec = tween(PvotDurations.Short),
        label = "glassFill"
    )
    var modifier = this
    if (onClick != null) {
        modifier = modifier.pvotPressScale(interactionSource, PvotGlassDefaults.PressedScale)
    }
    if (backdrop != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val shadowColor = Color.Black.copy(alpha = PvotGlassDefaults.ShadowAlpha)
        modifier = modifier.shadow(
            PvotGlassDefaults.ShadowElevation,
            shape,
            clip = false,
            ambientColor = shadowColor,
            spotColor = shadowColor
        )
    }
    modifier = with(PvotGlassDefaults) { modifier.edge(intensity, shape) }
        .pvotGlass(backdrop, cornerRadius, glassStyle.copy(fillColor = fill))
        .clip(shape)
    if (rim && intensity > 0f) modifier = modifier.pvotGlassRim(cornerRadius, alpha = intensity)
    if (onClick != null) {
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            enabled = enabled,
            role = Role.Button,
            onClick = onClick
        )
    }
    return modifier
}
