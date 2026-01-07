// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.pvot.designsystem.components.picker.internal

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.prauga.pvot.designsystem.components.picker.LocalPvotPickerColors
import org.prauga.pvot.designsystem.components.picker.PvotPickerColors

private val DefaultItemHeight = 40.dp
private val DefaultVisibleItems = 5
private val DefaultWheelWidth = 110.dp

/**
 * Wheel engine that handles scrolling, snapping, and rendering.
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
    val centerIndex = visibleItemsCount / 2
    val hapticFeedback = LocalHapticFeedback.current
    var lastSelectedIndex by remember { mutableIntStateOf(config.initialIndex) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                val selected = (index + centerIndex).coerceIn(config.values.indices)
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
            contentPadding = PaddingValues(vertical = itemHeight * centerIndex),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(config.values) { index, value ->
                WheelItem(
                    text = config.label(value),
                    suffix = config.suffix,
                    colors = colors,
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
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
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
