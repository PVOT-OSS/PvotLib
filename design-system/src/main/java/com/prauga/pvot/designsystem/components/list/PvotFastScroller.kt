// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.motion.PvotMotion
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * A section a [PvotFastScroller] can jump to, such as the contacts starting with one letter.
 *
 * @param label Short text shown in the bubble while the section is picked, usually one letter
 * @param index Index in the list of the section's first item, usually its header
 */
@Immutable
public data class PvotFastScrollSection(val label: String, val index: Int)

/**
 * Colors of a [PvotFastScroller].
 *
 * @param thumbColor The thumb while the list scrolls
 * @param activeThumbColor The thumb while the finger is on the scroller
 * @param bubbleContainerColor The bubble that shows the picked section
 * @param bubbleContentColor The bubble's label
 */
@Immutable
public data class PvotFastScrollerColors(
    val thumbColor: Color,
    val activeThumbColor: Color,
    val bubbleContainerColor: Color,
    val bubbleContentColor: Color
)

/** Defaults for [PvotFastScroller]. */
public object PvotFastScrollerDefaults {

    /** Width of the strip that takes the finger, at the end of the list. */
    public val TouchWidth: Dp = 48.dp

    /** Height of the thumb. */
    public val ThumbHeight: Dp = 48.dp

    /** Width of the thumb while the list scrolls. */
    public val ThumbWidth: Dp = 4.dp

    /** Width of the thumb while the finger is on the scroller. */
    public val ActiveThumbWidth: Dp = 8.dp

    /** Diameter of the bubble. */
    public val BubbleSize: Dp = 64.dp

    /** How long the thumb stays after the list stops, in milliseconds. */
    public const val HideDelayMillis: Int = 1200

    /** The scroller's colors taken from [colorScheme]. */
    public fun colors(colorScheme: ColorScheme): PvotFastScrollerColors = PvotFastScrollerColors(
        thumbColor = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        activeThumbColor = colorScheme.primary,
        bubbleContainerColor = colorScheme.primary,
        bubbleContentColor = colorScheme.onPrimary
    )

    /** The scroller's colors for the current theme. */
    @Composable
    public fun themeColors(): PvotFastScrollerColors = colors(MaterialTheme.colorScheme)
}

/**
 * A fast scroller for a long list split into sections, such as contacts by first letter.
 *
 * Place it over the end edge of the list, for example with `Modifier.align(Alignment.CenterEnd)`
 * in a `Box` that holds the list. A thin thumb shows while the list scrolls and fades away when it
 * stops. Touching or dragging along the end edge jumps to the section under the finger, shows its
 * label in a bubble beside the finger and ticks the phone on each new section. It hides itself
 * when the list fits on screen. TalkBack users scroll the list itself, so the scroller is hidden
 * from accessibility services.
 *
 * @param state The state of the list the scroller moves
 * @param sections The list's sections, in list order
 * @param modifier Modifier for the scroller; give it the list's height
 * @param colors The scroller's colors
 */
@Composable
public fun PvotFastScroller(
    state: LazyListState,
    sections: List<PvotFastScrollSection>,
    modifier: Modifier = Modifier,
    colors: PvotFastScrollerColors = PvotFastScrollerDefaults.themeColors()
) {
    val scrollable by remember(state) {
        derivedStateOf { state.canScrollForward || state.canScrollBackward }
    }
    if (!scrollable || sections.isEmpty()) return

    var dragging by remember { mutableStateOf(false) }
    var dragFraction by remember { mutableFloatStateOf(0f) }
    var picked by remember { mutableStateOf<PvotFastScrollSection?>(null) }
    val currentSections by rememberUpdatedState(sections)
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val listFraction by remember(state) {
        derivedStateOf {
            val info = state.layoutInfo
            val hidden = info.totalItemsCount - info.visibleItemsInfo.size
            if (hidden <= 0) 0f else (state.firstVisibleItemIndex.toFloat() / hidden).coerceIn(0f, 1f)
        }
    }
    val shown = dragging || state.isScrollInProgress
    val alpha by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = if (shown) {
            tween(PvotDurations.Short)
        } else {
            tween(PvotDurations.Long, delayMillis = PvotFastScrollerDefaults.HideDelayMillis)
        },
        label = "fastScrollerAlpha"
    )
    val thumbWidth by animateDpAsState(
        targetValue = if (dragging) {
            PvotFastScrollerDefaults.ActiveThumbWidth
        } else {
            PvotFastScrollerDefaults.ThumbWidth
        },
        animationSpec = tween(PvotDurations.Short, easing = PvotMotion.Settle),
        label = "fastScrollerThumbWidth"
    )
    val thumbColor by animateColorAsState(
        targetValue = if (dragging) colors.activeThumbColor else colors.thumbColor,
        animationSpec = tween(PvotDurations.Short),
        label = "fastScrollerThumbColor"
    )

    BoxWithConstraints(modifier = modifier.fillMaxHeight().clearAndSetSemantics {}) {
        val density = LocalDensity.current
        val heightPx = constraints.maxHeight.toFloat()
        val thumbHeightPx = with(density) { PvotFastScrollerDefaults.ThumbHeight.toPx() }
        val bubblePx = with(density) { PvotFastScrollerDefaults.BubbleSize.toPx() }
        val fraction = if (dragging) dragFraction else listFraction

        fun pick(y: Float) {
            val list = currentSections
            dragFraction = (y / heightPx).coerceIn(0f, 1f)
            val section = list[(dragFraction * list.size).toInt().coerceAtMost(list.lastIndex)]
            if (section != picked) {
                picked = section
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                scope.launch { state.scrollToItem(section.index) }
            }
        }

        if (dragging && picked != null) {
            val centerY = (dragFraction * heightPx).coerceIn(bubblePx / 2, heightPx - bubblePx / 2)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset { IntOffset(0, (centerY - bubblePx / 2).roundToInt()) }
                    .padding(end = PvotFastScrollerDefaults.TouchWidth + PvotSpacing.S)
                    .size(PvotFastScrollerDefaults.BubbleSize)
                    .background(colors.bubbleContainerColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = picked?.label.orEmpty(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.bubbleContentColor,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight()
                .width(PvotFastScrollerDefaults.TouchWidth)
                .pointerInput(state, heightPx) {
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        dragging = true
                        picked = null
                        pick(down.position.y)
                        down.consume()
                        drag(down.id) { change ->
                            pick(change.position.y)
                            change.consume()
                        }
                        dragging = false
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset { IntOffset(0, (fraction * (heightPx - thumbHeightPx)).roundToInt()) }
                    .padding(end = PvotSpacing.XS)
                    .alpha(alpha)
                    .size(width = thumbWidth, height = PvotFastScrollerDefaults.ThumbHeight)
                    .background(thumbColor, PvotShapes.Pill)
            )
        }
    }
}
