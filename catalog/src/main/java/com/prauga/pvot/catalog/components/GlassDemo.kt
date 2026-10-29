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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.prauga.pvot.catalog.R
import com.prauga.pvot.designsystem.components.PvotGlassCard
import com.prauga.pvot.designsystem.components.PvotGlassIconButton
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.pvotGlass
import com.prauga.pvot.designsystem.effects.pvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.pvotGlassRim
import com.prauga.pvot.designsystem.effects.rememberPvotGlassBackdrop
import com.prauga.pvot.designsystem.navigation.PvotNavBarDefaults
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GlassDemo(modifier: Modifier = Modifier) {
    val backdrop = rememberPvotGlassBackdrop()
    var taps by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            LiveBackdrop(
                modifier = Modifier
                    .fillMaxSize()
                    .pvotGlassBackdrop(backdrop)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(46.dp)
                    .pvotGlass(backdrop)
                    .pvotGlassRim(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = stringResource(R.string.catalog_glass_search),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            GlassButtons(
                backdrop = backdrop,
                onClick = { taps++ },
                modifier = Modifier.align(Alignment.Center)
            )

            PvotGlassCard(
                backdrop = backdrop,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                onClick = { taps++ }
            ) {
                Text(
                    text = stringResource(R.string.catalog_glass_card),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Text(
            text = stringResource(R.string.catalog_glass_taps, taps),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
private fun GlassButtons(
    backdrop: PvotGlassBackdrop,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        listOf(R.drawable.ic_home, R.drawable.ic_apps, R.drawable.ic_catalog).forEach { icon ->
            PvotGlassIconButton(onClick = onClick, backdrop = backdrop) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun LiveBackdrop(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val blobs = listOf(
        PvotNavBarDefaults.GradientStart,
        PvotNavBarDefaults.GradientEnd,
        colors.tertiaryContainer,
        colors.primary
    )
    val base = colors.surfaceVariant
    val phase by rememberInfiniteTransition(label = "liveBackdrop").animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(BackdropLoopMillis, easing = LinearEasing), RepeatMode.Restart),
        label = "phase"
    )
    Canvas(modifier) {
        drawRect(base)
        blobs.forEachIndexed { index, color ->
            val angle = phase + index * PI.toFloat() / 2f
            val center = Offset(
                size.width * (0.5f + 0.32f * cos(angle * (1 + index % 2))),
                size.height * (0.5f + 0.3f * sin(angle))
            )
            val radius = size.minDimension * 0.42f
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(color, color.copy(alpha = 0f)),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )
        }
        val stripe = size.height / StripeCount
        repeat(StripeCount) { line ->
            if (line % 2 == 0) {
                drawRect(
                    color = Color.White.copy(alpha = 0.12f),
                    topLeft = Offset(0f, line * stripe),
                    size = Size(size.width, stripe / 3f)
                )
            }
        }
    }
}

private const val BackdropLoopMillis = 14_000
private const val StripeCount = 14
