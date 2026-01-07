// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.picker.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prauga.pvot.designsystem.components.picker.LocalPvotPickerColors
import com.prauga.pvot.designsystem.components.picker.PvotPickerColors
import kotlin.math.abs

private val DefaultItemHeight = 40.dp
private val DefaultVisibleItems = 5
private val DefaultWheelWidth = 110.dp

// wheel effect constants
private const val MAX_ROTATION_DEGREES = 60f
private const val MIN_SCALE = 0.7f
private const val MIN_ALPHA = 0.3f

/**
 * Wheel engine that handles scrolling, snapping, and rendering with 3D cylindrical effect.
 */
@Composable
internal fun WheelEngine(
    config: WheelConfig,
    onValueSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: PvotPickerColors = LocalPvotPickerColors.current,
    itemHeight: Dp = DefaultItemHeight,
    visibleItemsCount: Int = DefaultVisibleItems
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = config.initialIndex)
    val flingBehavior = rememberSnapFlingBehavior(listState)
    val hapticFeedback = LocalHapticFeedback.current
    var lastSelectedIndex by remember { mutableIntStateOf(config.initialIndex) }

    val density = LocalDensity.current
    val itemHeightPx = with(density) { itemHeight.toPx() }
    val halfVisibleItems = visibleItemsCount / 2f

    // Track scroll position
    val firstVisibleIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val scrollOffset by remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                val selected = index.coerceIn(config.values.indices)
                if (selected != lastSelectedIndex) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    lastSelectedIndex = selected
                }
                onValueSelected(config.values[selected])
            }
    }

    Box(
        modifier = modifier
            .height(itemHeight * visibleItemsCount)
            .width(DefaultWheelWidth)
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(vertical = itemHeight * (visibleItemsCount / 2)),
            modifier = Modifier.fillMaxSize()
        ) {
            items(config.values.size) { index ->
                // Calculate distance from center
                val distanceFromCenter = (index - firstVisibleIndex) - (scrollOffset / itemHeightPx)

                // Normalize distance
                val normalizedDistance = (distanceFromCenter / halfVisibleItems).coerceIn(-1f, 1f)

                // Calculate 3D transformations
                val rotationX = normalizedDistance * MAX_ROTATION_DEGREES
                val scale = 1f - (abs(normalizedDistance) * (1f - MIN_SCALE))
                val alpha = 1f - (abs(normalizedDistance) * (1f - MIN_ALPHA))

                WheelItem(
                    text = config.label(config.values[index]),
                    suffix = config.suffix,
                    colors = colors,
                    rotationX = rotationX,
                    scale = scale,
                    alpha = alpha,
                    modifier = Modifier.height(itemHeight)
                )
            }
        }

        // Selection indicator
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .height(itemHeight)
                .fillMaxWidth()
                .background(
                    colors.selectionBackgroundColor,
                    RoundedCornerShape(20.dp)
                )
        )
    }
}

@Composable
private fun WheelItem(
    text: String,
    suffix: String,
    colors: PvotPickerColors,
    rotationX: Float,
    scale: Float,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.rotationX = rotationX
                this.scaleX = scale
                this.scaleY = scale
                this.alpha = alpha
                this.transformOrigin = TransformOrigin.Center
                this.cameraDistance = 12f * density
            },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = text,
                fontSize = 28.sp,
                color = colors.textColor
            )
            if (suffix.isNotEmpty()) {
                Spacer(Modifier.width(4.dp))
                Text(
                    text = suffix,
                    fontSize = 16.sp,
                    color = colors.textSecondaryColor
                )
            }
        }
    }
}

/**
 * Composable that renders multiple wheels in a row.
 */
@Composable
internal fun MultiWheelEngine(
    configs: List<WheelConfig>,
    onValuesSelected: (List<Int>) -> Unit,
    modifier: Modifier = Modifier,
    colors: PvotPickerColors = LocalPvotPickerColors.current
) {
    val selectedValues = configs.map { it.values.getOrElse(it.initialIndex) { 0 } }.toMutableList()

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        configs.forEachIndexed { index, config ->
            WheelEngine(
                config = config,
                onValueSelected = { value ->
                    selectedValues[index] = value
                    onValuesSelected(selectedValues.toList())
                },
                colors = colors
            )
        }
    }
}
