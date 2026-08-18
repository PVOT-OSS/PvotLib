// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.modifier

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.prauga.pvot.designsystem.motion.PvotMotion

/**
 * Springs a surface down while pressed.
 *
 * For large filled surfaces — cards, grouped rows — where the shape is the
 * affordance and a ripple reads as a flash on top of it.
 *
 * @param interactionSource The source whose press state drives the scale, the same
 * one passed to the clickable or selectable modifier on this element
 * @param pressedScale Scale factor held while pressed
 */
@Composable
fun Modifier.pvotPressScale(
    interactionSource: InteractionSource,
    pressedScale: Float = PvotMotion.PressedScale,
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = PvotMotion.pressSpring(),
        label = "pvotPressScale",
    )
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
