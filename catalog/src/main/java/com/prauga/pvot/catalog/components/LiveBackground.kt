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

private val PanelStyle = PvotGlassStyle(referenceSize = 120.dp, blur = 8.dp)

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
        colors.primary,
        colors.secondaryContainer
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
            val speed = BlobSpeeds[index % BlobSpeeds.size]
            val angle = phase * speed + index * 1.3f
            val center = Offset(
                size.width * (0.5f + 0.4f * cos(angle)),
                size.height * (0.5f + 0.4f * sin(angle * 2f + index))
            )
            val radius = size.width * (0.45f + 0.08f * sin(phase * 2f + index))
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(color, color.copy(alpha = 0.6f), color.copy(alpha = 0f)),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )
        }
    }
}

private const val BackgroundLoopMillis = 30_000

private val BlobSpeeds = listOf(3f, -2f, 4f, -3f, 2f)
