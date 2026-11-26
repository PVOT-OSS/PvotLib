// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.motion

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.IntOffset

/** Page transitions for stacked navigation: a pushed page covers the one below it, which shifts aside. */
public object PvotPageTransitions {

    /** Length of a page push or pop. */
    public const val DurationMillis: Int = 350

    private const val UNDERLAP_DIVISOR = 4

    private fun spec(): FiniteAnimationSpec<IntOffset> = tween(DurationMillis, easing = PvotMotion.Settle)

    /** A new page slides in from the end edge over the current one. */
    public fun push(): ContentTransform =
        slideInHorizontally(spec()) { it } togetherWith
            slideOutHorizontally(spec()) { -it / UNDERLAP_DIVISOR }

    /**
     * The top page slides out to the end edge, revealing the page below. Also use it for predictive back,
     * where the system drives its progress from the gesture.
     */
    public fun pop(): ContentTransform =
        (slideInHorizontally(spec()) { -it / UNDERLAP_DIVISOR } togetherWith slideOutHorizontally(spec()) { it })
            .apply { targetContentZIndex = -1f }
}
