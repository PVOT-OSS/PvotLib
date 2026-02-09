// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.picker.internal

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.ui.hapticfeedback.HapticFeedback
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
import com.prauga.pvot.designsystem.components.picker.internal.WheelConstants.CAMERA_DISTANCE_MULTIPLIER
import com.prauga.pvot.designsystem.components.picker.internal.WheelConstants.ITEM_TEXT_SIZE
import com.prauga.pvot.designsystem.components.picker.internal.WheelConstants.SELECTION_INDICATOR_CORNER_RADIUS
import com.prauga.pvot.designsystem.components.picker.internal.WheelConstants.SUFFIX_TEXT_SIZE
import com.prauga.pvot.designsystem.components.picker.internal.WheelConstants.TEXT_SUFFIX_SPACING

private val DefaultItemHeight = 40.dp
private val DefaultVisibleItems = 5
private val DefaultWheelWidth = 110.dp

// wheel effect constants
private const val MAX_ROTATION_DEGREES = 60f
private const val MIN_SCALE = 0.7f
private const val MIN_ALPHA = 0.3f

/**
 * Wheel engine that handles scrolling, snapping, and rendering with 3D cylindrical effect.
 *
 * This is the optimized version using domain layer components for better performance
 * and testability. The wheel displays a vertical list of values with a 3D perspective
 * effect, where items closer to the center are larger and more opaque.
 *
 * ## Performance Optimizations
 * - Only visible items are rendered with 3D transformations
 * - Off-screen items use simple Spacer placeholders
 * - Constants are pre-calculated using remember
 * - Scroll position uses derivedStateOf for efficient updates
 * - Visible range is calculated once per scroll
 * - Stable keys prevent unnecessary recompositions
 *
 * ## State Management
 * - Scroll state is managed by LazyListState
 * - Selected value is tracked and reported via onValueSelected callback
 * - Haptic feedback is triggered on value changes
 * - Performance monitoring is optional via performanceMonitor parameter
 *
 * ## Error Handling
 * - Gracefully handles haptic feedback failures
 * - Gracefully handles performance monitoring failures
 * - Continues with basic functionality if optional features fail
 *
 * @param config Wheel configuration including values, labels, and appearance
 * @param onValueSelected Callback invoked when the selected value changes
 * @param modifier Modifier to be applied to the wheel container
 * @param colors Color configuration for the wheel items
 * @param itemHeight Height of each item in the wheel
 * @param visibleItemsCount Number of items visible at once (should be odd for centered selection)
 * @param transformEngine Engine for calculating 3D transformations (injectable for testing)
 * @param scrollCalculator Calculator for determining visible item range (injectable for testing)
 * @param performanceMonitor Optional monitor for tracking performance metrics
 *
 * @see WheelConfig
 * @see TransformEngine
 * @see ScrollCalculator
 * @see PerformanceMonitor
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
    // Gracefully handle performance monitoring errors
    try {
        performanceMonitor?.recordRecomposition("WheelEngine")
    } catch (e: Exception) {
        Log.e("DesignSystem", "Failed to record recomposition for WheelEngine", e)
        // Continue with basic functionality
    }
    
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
        handleWheelSelection(
            listState = listState,
            config = config,
            hapticFeedback = hapticFeedback,
            lastSelectedIndex = lastSelectedIndex,
            onIndexChanged = { newIndex -> lastSelectedIndex = newIndex },
            onValueSelected = onValueSelected
        )
    }
    
    // Log performance warnings when thresholds are exceeded
    LaunchedEffect(performanceMonitor) {
        performanceMonitor?.logWarnings()
    }

    Box(
        modifier = modifier
            .height(itemHeight * visibleItemsCount)
            .width(config.appearance.wheelWidth)
    ) {
        WheelScrollList(
            config = config,
            listState = listState,
            flingBehavior = flingBehavior,
            itemHeight = itemHeight,
            visibleItemsCount = visibleItemsCount,
            colors = colors,
            scrollCalculator = scrollCalculator,
            transformEngine = transformEngine,
            visibleRange = visibleRange,
            firstVisibleIndex = firstVisibleIndex,
            scrollOffset = scrollOffset,
            itemHeightPx = itemHeightPx,
            halfVisibleItems = halfVisibleItems
        )

        WheelSelectionIndicator(
            itemHeight = itemHeight,
            colors = colors
        )
    }
}

/**
 * Handles wheel selection changes with haptic feedback.
 */
private suspend fun handleWheelSelection(
    listState: LazyListState,
    config: WheelConfig,
    hapticFeedback: HapticFeedback,
    lastSelectedIndex: Int,
    onIndexChanged: (Int) -> Unit,
    onValueSelected: (Int) -> Unit
) {
    var currentLastIndex = lastSelectedIndex
    snapshotFlow { listState.firstVisibleItemIndex }
        .collect { index ->
            val selected = index.coerceIn(config.values.indices)
            if (selected != currentLastIndex) {
                // Gracefully handle haptic feedback errors
                if (config.behavior.enableHapticFeedback) {
                    try {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    } catch (e: Exception) {
                        Log.e("DesignSystem", "Failed to perform haptic feedback", e)
                        // Continue without haptic feedback
                    }
                }
                currentLastIndex = selected
                onIndexChanged(selected)
            }
            onValueSelected(config.values[selected])
        }
}

/**
 * Renders the scrollable list of wheel items.
 */
@Composable
private fun WheelScrollList(
    config: WheelConfig,
    listState: LazyListState,
    flingBehavior: FlingBehavior,
    itemHeight: Dp,
    visibleItemsCount: Int,
    colors: PvotPickerColors,
    scrollCalculator: IScrollCalculator,
    transformEngine: ITransformEngine,
    visibleRange: IntRange,
    firstVisibleIndex: Int,
    scrollOffset: Float,
    itemHeightPx: Float,
    halfVisibleItems: Float
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
}

/**
 * Renders the selection indicator overlay.
 */
@Composable
private fun BoxScope.WheelSelectionIndicator(
    itemHeight: Dp,
    colors: PvotPickerColors
) {
    Box(
        modifier = Modifier
            .align(Alignment.Center)
            .height(itemHeight)
            .fillMaxWidth()
            .background(
                colors.selectionBackgroundColor,
                RoundedCornerShape(SELECTION_INDICATOR_CORNER_RADIUS)
            )
    )
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
                        cameraDistance = CAMERA_DISTANCE_MULTIPLIER * density
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
                fontSize = ITEM_TEXT_SIZE,
                color = colors.textColor
            )
            if (suffix.isNotEmpty()) {
                Spacer(Modifier.width(TEXT_SUFFIX_SPACING))
                Text(
                    text = suffix,
                    fontSize = SUFFIX_TEXT_SIZE,
                    color = colors.textSecondaryColor
                )
            }
        }
    }
}

/**
 * Legacy version of WheelEngine for backward compatibility.
 * 
 * @deprecated Use the version that accepts WheelConfig instead.
 * This overload will be removed in a future version.
 */
@Deprecated(
    message = "Use WheelEngine(config: WheelConfig, ...) instead",
    replaceWith = ReplaceWith(
        "WheelEngine(config = WheelConfig(values = values, label = label, suffix = suffix, " +
            "initialIndex = initialIndex, appearance = WheelAppearance(itemHeight = itemHeight, " +
            "visibleItemsCount = visibleItemsCount, wheelWidth = wheelWidth, colors = colors), " +
            "behavior = WheelBehavior(enableHapticFeedback = enableHapticFeedback, enable3DEffect = enable3DEffect)), " +
            "onValueSelected = onValueSelected, modifier = modifier)"
    )
)
@Composable
internal fun WheelEngine(
    values: List<Int>,
    onValueSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: (Int) -> String = { it.toString() },
    suffix: String = "",
    initialIndex: Int = 0,
    colors: PvotPickerColors = LocalPvotPickerColors.current,
    itemHeight: Dp = DefaultItemHeight,
    visibleItemsCount: Int = DefaultVisibleItems,
    wheelWidth: Dp = DefaultWheelWidth,
    enableHapticFeedback: Boolean = true,
    enable3DEffect: Boolean = true
) {
    // Convert old parameters to WheelConfig and delegate to new implementation
    val config = WheelConfig(
        values = values,
        label = label,
        suffix = suffix,
        initialIndex = initialIndex,
        appearance = WheelAppearance(
            itemHeight = itemHeight,
            visibleItemsCount = visibleItemsCount,
            wheelWidth = wheelWidth,
            colors = colors
        ),
        behavior = WheelBehavior(
            enableHapticFeedback = enableHapticFeedback,
            enable3DEffect = enable3DEffect
        )
    )
    
    // Call the new optimized version
    WheelEngine(
        config = config,
        onValueSelected = onValueSelected,
        modifier = modifier,
        colors = colors,
        itemHeight = itemHeight,
        visibleItemsCount = visibleItemsCount
    )
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
