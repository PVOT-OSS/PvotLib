// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isFinite
import com.prauga.pvot.designsystem.effects.LocalPvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassDefaults
import com.prauga.pvot.designsystem.effects.PvotGlassStyle
import com.prauga.pvot.designsystem.effects.pvotGlass
import com.prauga.pvot.designsystem.effects.pvotGlassRim
import com.prauga.pvot.designsystem.effects.pvotPressScale
import com.prauga.pvot.designsystem.motion.PvotDurations

/**
 * Draws this element as a glass control over [backdrop], the way every glass component does: it
 * swells while pressed, floats on a soft shadow, keeps a hairline edge where the rim's light fades,
 * is clipped to its shape, and, with [onClick], acts as a button.
 *
 * Use it to build a glass control the library has no component for; [PvotGlassCard] and
 * [PvotGlassIconButton] are built on it.
 *
 * @param backdrop The content behind the glass, marked with
 *   [pvotGlassBackdrop][com.prauga.pvot.designsystem.effects.pvotGlassBackdrop], or null for the
 *   tint alone
 * @param cornerRadius The glass's corner radius, [PvotGlassDefaults.CapsuleRadius] for a capsule
 * @param style How the glass looks at full intensity
 * @param rim Whether to draw the rim of light that follows the phone's tilt
 * @param onClick Called when the element is tapped, or null for a surface that is not a button
 * @param onLongClick Called when the element is held, only meaningful with [onClick]
 * @param enabled Whether the element responds to taps
 * @param role What the element is to TalkBack, only meaningful with [onClick]
 * @param interactionSource The source driving the press animation, hoist it to react to presses
 *   elsewhere; null to keep one here
 */
@Composable
public fun Modifier.pvotGlassSurface(
    backdrop: PvotGlassBackdrop?,
    cornerRadius: Dp = PvotGlassDefaults.CapsuleRadius,
    style: PvotGlassStyle = PvotGlassDefaults.Style,
    rim: Boolean = true,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    role: Role? = Role.Button,
    interactionSource: MutableInteractionSource? = null,
): Modifier =
    glassSurface(
        backdrop = backdrop,
        cornerRadius = cornerRadius,
        style = style,
        rim = rim,
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        role = role,
        interactionSource = interactionSource,
        intensity = PvotGlassDefaults.currentIntensity(),
    )

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun Modifier.glassSurface(
    backdrop: PvotGlassBackdrop?,
    cornerRadius: Dp = PvotGlassDefaults.CapsuleRadius,
    style: PvotGlassStyle = PvotGlassDefaults.Style,
    rim: Boolean = true,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    role: Role? = Role.Button,
    interactionSource: MutableInteractionSource? = null,
    intensity: Float,
): Modifier {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val glassStyle = PvotGlassDefaults.style(intensity, style)
    val shape =
        if (cornerRadius.isFinite) {
            RoundedCornerShape(cornerRadius)
        } else {
            RoundedCornerShape(percent = 50)
        }
    val pressed by source.collectIsPressedAsState()
    val fill by
        animateColorAsState(
            targetValue = if (pressed) PvotGlassDefaults.PressedFillColor else glassStyle.fillColor,
            animationSpec = tween(PvotDurations.Short),
            label = "glassFill",
        )
    var modifier = this
    if (onClick != null) {
        modifier = modifier.pvotPressScale(source, PvotGlassDefaults.PressedScale)
    }
    if (backdrop != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val shadowColor = Color.Black.copy(alpha = PvotGlassDefaults.ShadowAlpha)
        modifier =
            modifier.shadow(
                PvotGlassDefaults.ShadowElevation,
                shape,
                clip = false,
                ambientColor = shadowColor,
                spotColor = shadowColor,
            )
    }
    modifier =
        with(PvotGlassDefaults) { modifier.edge(intensity, shape) }
            .pvotGlass(backdrop, cornerRadius, glassStyle.copy(fillColor = fill))
            .clip(shape)
    if (rim && intensity > 0f) modifier = modifier.pvotGlassRim(cornerRadius, alpha = intensity)
    if (onClick != null) {
        modifier =
            if (onLongClick != null) {
                modifier.combinedClickable(
                    interactionSource = source,
                    indication = LocalIndication.current,
                    enabled = enabled,
                    role = role,
                    onLongClick = onLongClick,
                    onClick = onClick,
                )
            } else {
                modifier.clickable(
                    interactionSource = source,
                    indication = LocalIndication.current,
                    enabled = enabled,
                    role = role,
                    onClick = onClick,
                )
            }
    }
    return modifier
}

/** Defaults for [PvotGlassSurface]. */
public object PvotGlassSurfaceDefaults {

    /** Padding round the surface's content: none, so the surface fits its content. */
    public val ContentPadding: PaddingValues = PaddingValues(0.dp)
}

/**
 * A box of glass over [backdrop], drawn with [pvotGlassSurface], for a glass control of your own.
 *
 * @param backdrop The content behind the glass, marked with
 *   [pvotGlassBackdrop][com.prauga.pvot.designsystem.effects.pvotGlassBackdrop], or null for the
 *   tint alone
 * @param modifier Modifier for the surface
 * @param cornerRadius The glass's corner radius, [PvotGlassDefaults.CapsuleRadius] for a capsule
 * @param style How the glass looks at full intensity
 * @param rim Whether to draw the rim of light that follows the phone's tilt
 * @param onClick Called when the surface is tapped, or null for a surface that is not a button
 * @param enabled Whether the surface responds to taps
 * @param contentAlignment Where the content sits in the surface
 * @param contentPadding Padding round the content
 * @param content The surface's content
 */
@Composable
public fun PvotGlassSurface(
    backdrop: PvotGlassBackdrop? = LocalPvotGlassBackdrop.current,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = PvotGlassDefaults.CapsuleRadius,
    style: PvotGlassStyle = PvotGlassDefaults.Style,
    rim: Boolean = true,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    contentAlignment: Alignment = Alignment.Center,
    contentPadding: PaddingValues = PvotGlassSurfaceDefaults.ContentPadding,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier =
            modifier
                .pvotGlassSurface(
                    backdrop = backdrop,
                    cornerRadius = cornerRadius,
                    style = style,
                    rim = rim,
                    onClick = onClick,
                    enabled = enabled,
                )
                .padding(contentPadding),
        contentAlignment = contentAlignment,
        content = content,
    )
}
