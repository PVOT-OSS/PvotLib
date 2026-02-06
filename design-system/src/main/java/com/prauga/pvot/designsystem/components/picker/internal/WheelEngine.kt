// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
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
import com.prauga.pvot.designsystem.domain.monitoring.IPerformanceMonitor
import com.prauga.pvot.designsystem.domain.scroll.IScrollCalculator
import com.prauga.pvot.designsystem.domain.scroll.ScrollCalculator
import com.prauga.pvot.designsystem.domain.transform.ITransformEngine
import com.prauga.pvot.designsystem.domain.transform.ItemTransform
import com.prauga.pvot.designsystem.domain.transform.TransformEngine

private val DefaultItemHeight = 40.dp
private val DefaultVisibleItems = 5
private val DefaultWheelWidth = 110.dp

// wheel effect constants
private const val MAX_ROTATION_DEGREES = 60f
private const val MIN_SCALE = 0.7f
private const val MIN_ALPHA = 0.3f

/**
 * Wheel engine that handles scrolling, snapping, and rendering with 3D cylindrical effect.
 * Optimized version using domain layer components for better performance and testability.
 */
@Composable
internal fun WheelEngine(
    config: WheelConfig,
    onValueSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: PvotPickerColors = LocalPvotPickerColors.current,
    itemHeight: Dp = DefaultItemHeight,
    visibleItemsCount: Int = DefaultVisibleItems,
    transformEngine: ITransformEngine = remember { TransformEngine() },
    scrollCalculator: IScrollCalculator = remember { ScrollCalculator() },
    performanceMonitor: IPerformanceMonitor? = null
) {
    performanceMonitor?.recordRecomposition("WheelEngine")
    
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = config.initialIndex)
    val flingBehavior = rememberSnapFlingBehavior(listState)
    val hapticFeedback = LocalHapticFeedback.current
    var lastSelectedIndex by remember { mutableIntStateOf(config.initialIndex) }

    val density = LocalDensity.current
    
    // Pre-calculate constants using remember
    val itemHeightPx = remember(itemHeight, density) {
        with(density) { itemHeight.toPx() }
    }
    val halfVisibleItems = remember(visibleItemsCount) {
        visibleItemsCount / 2f
    }

    // Use derivedStateOf for scroll position
    val firstVisibleIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex }
    }
    val scrollOffset by remember {
        derivedStateOf { listState.firstVisibleItemScrollOffset.toFloat() }
    }
    
    // Calculate visible range once per scroll
    val visibleRange by remember {
        derivedStateOf {
            scrollCalculator.calculateVisibleRange(
                firstVisibleIndex,
                scrollOffset,
                itemHeightPx,
                visibleItemsCount
            )
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                val selected = index.coerceIn(config.values.indices)
                if (selected != lastSelectedIndex) {
                    if (config.behavior.enableHapticFeedback) {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    lastSelectedIndex = selected
                }
                onValueSelected(config.values[selected])
            }
    }

    Box(
        modifier = modifier
            .height(itemHeight * visibleItemsCount)
            .width(config.appearance.wheelWidth)
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(vertical = itemHeight * (visibleItemsCount / 2)),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                count = config.values.size,
                key = { index -> config.values[index] }  // Stable keys for efficiency
            ) { index ->
                // Only calculate transforms for visible items
                if (scrollCalculator.isItemVisible(index, visibleRange)) {
                    val transform = remember(
                        index,
                        firstVisibleIndex,
                        scrollOffset,
                        itemHeightPx,
                        halfVisibleItems
                    ) {
                        transformEngine.calculateTransform(
                            index,
                            firstVisibleIndex,
                            scrollOffset,
                            itemHeightPx,
                            halfVisibleItems
                        )
                    }

                    WheelItem(
                        text = config.label(config.values[index]),
                        suffix = config.suffix,
                        colors = colors,
                        transform = transform,
                        enable3D = config.behavior.enable3DEffect,
                        modifier = Modifier.height(itemHeight)
                    )
                } else {
                    // Placeholder for off-screen items
                    Spacer(modifier = Modifier.height(itemHeight))
                }
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
    transform: ItemTransform,
    enable3D: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (enable3D) {
                    Modifier.graphicsLayer {
                        rotationX = transform.rotationX
                        scaleX = transform.scale
                        scaleY = transform.scale
                        alpha = transform.alpha
                        transformOrigin = TransformOrigin.Center
                        cameraDistance = 12f * density
                    }
                } else {
                    Modifier.graphicsLayer {
                        alpha = transform.alpha
                    }
                }
            ),
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
 * Updated to use new WheelEngine signature with backward compatibility.
 */
@Composable
internal fun MultiWheelEngine(
    configs: List<WheelConfig>,
    onValuesSelected: (List<Int>) -> Unit,
    modifier: Modifier = Modifier,
    colors: PvotPickerColors = LocalPvotPickerColors.current,
    transformEngine: ITransformEngine = remember { TransformEngine() },
    scrollCalculator: IScrollCalculator = remember { ScrollCalculator() },
    performanceMonitor: IPerformanceMonitor? = null
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
                colors = colors,
                transformEngine = transformEngine,
                scrollCalculator = scrollCalculator,
                performanceMonitor = performanceMonitor
            )
        }
    }
}
