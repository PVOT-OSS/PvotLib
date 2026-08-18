// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.modifier

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Dp
import com.prauga.pvot.designsystem.motion.PvotMotion

/**
 * Fades and lifts content into place, staggered by [index].
 *
 * The delay stops growing past [maxStaggered] so a long list does not end up
 * waiting on an animation that started off screen.
 */
@Composable
fun Modifier.pvotReveal(
    index: Int = 0,
    rise: Dp = PvotMotion.RevealRise,
    durationMillis: Int = PvotMotion.RevealDurationMillis,
    staggerStepMillis: Int = PvotMotion.StaggerStepMillis,
    maxStaggered: Int = PvotMotion.MaxStaggeredItems,
    easing: Easing = PvotMotion.Settle,
): Modifier {
    val risePx = with(LocalDensity.current) { rise.toPx() }
    val inspecting = LocalInspectionMode.current
    var appeared by remember { mutableStateOf(inspecting) }

    val progress by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(
            durationMillis = durationMillis,
            delayMillis = index.coerceAtMost(maxStaggered) * staggerStepMillis,
            easing = easing,
        ),
        label = "pvotReveal",
    )

    LaunchedEffect(Unit) { appeared = true }

    return this.graphicsLayer {
        alpha = progress
        translationY = (1f - progress) * risePx
    }
}
