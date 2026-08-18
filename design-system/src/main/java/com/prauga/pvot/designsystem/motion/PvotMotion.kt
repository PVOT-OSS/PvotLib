// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.motion

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.unit.dp

/** Shared motion defaults. Every value is overridable at the call site. */
object PvotMotion {

    /** Expo-out: arrives fast, eases to rest. */
    val Settle = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)

    const val RevealDurationMillis = 260

    const val StaggerStepMillis = 20

    /** Past this many items the delay stops growing, so long lists never wait. */
    const val MaxStaggeredItems = 3

    val RevealRise = 12.dp

    const val PressedScale = 0.97f

    /** Slightly underdamped, so a press settles with some life. */
    val PressSpring: SpringSpec<Float> = pressSpring()

    /** [PressSpring] for animations of other types. */
    fun <T> pressSpring(): SpringSpec<T> = spring(
        dampingRatio = 0.55f,
        stiffness = Spring.StiffnessMediumLow,
    )
}
