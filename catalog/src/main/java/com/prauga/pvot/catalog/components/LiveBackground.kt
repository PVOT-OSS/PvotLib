// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.catalog.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.components.PvotGlassCard
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassStyle
import com.prauga.pvot.designsystem.navigation.PvotNavBarDefaults
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

val LocalBackgroundBackdrop = staticCompositionLocalOf<PvotGlassBackdrop?> { null }

private val PanelStyle = PvotGlassStyle(referenceSize = 120.dp, blur = 14.dp)

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    PvotGlassCard(
        backdrop = LocalBackgroundBackdrop.current,
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        style = PanelStyle,
        contentPadding = PaddingValues(0.dp),
        content = content
    )
}

@Composable
fun LiveBackground(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val blobs = listOf(
        PvotNavBarDefaults.GradientStart,
        PvotNavBarDefaults.GradientEnd,
        colors.tertiaryContainer,
        colors.primary
    )
    val base = colors.background
    val phase by rememberInfiniteTransition(label = "liveBackground").animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            tween(BackgroundLoopMillis, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "phase"
    )
    Canvas(modifier) {
        drawRect(base)
        blobs.forEachIndexed { index, color ->
            val angle = phase + index * PI.toFloat() / 2f
            val center = Offset(
                size.width * (0.5f + 0.34f * cos(angle * (1 + index % 2))),
                size.height * (0.5f + 0.34f * sin(angle))
            )
            val radius = size.minDimension * 0.6f
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(color.copy(alpha = 0.85f), color.copy(alpha = 0f)),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )
        }
    }
}

private const val BackgroundLoopMillis = 20_000
