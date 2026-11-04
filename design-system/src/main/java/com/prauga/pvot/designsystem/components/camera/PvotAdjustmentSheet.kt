// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.camera

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prauga.pvot.designsystem.components.pvotGlassSurface
import com.prauga.pvot.designsystem.effects.LocalPvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassStyle
import kotlin.math.roundToInt

/**
 * Color configuration for [PvotRuler] and [PvotChoiceRow].
 *
 * @param accentColor The ruler's index and the selected choice's label
 * @param contentColor The ruler's ticks and the other choices' labels
 * @param choiceColor Fill behind a choice
 * @param selectedChoiceColor Fill behind the selected choice
 */
@Immutable
public data class PvotAdjustmentColors(
    val accentColor: Color,
    val contentColor: Color,
    val choiceColor: Color,
    val selectedChoiceColor: Color,
)

/** Defaults for [PvotAdjustmentSheet], [PvotRuler] and [PvotChoiceRow]. */
public object PvotAdjustmentSheetDefaults {
    /** The sheet's height. */
    public val Height: Dp = 191.dp

    /** The sheet's corner radius. */
    public val CornerRadius: Dp = 48.dp

    /** A choice's height. */
    public val ChoiceHeight: Dp = 44.dp

    /** Ticks on the ruler, from one end to the other. */
    public const val Ticks: Int = 40

    /** Colors from [MaterialTheme]: the primary color for the index and the selected choice. */
    @Composable
    public fun colors(
        accentColor: Color = MaterialTheme.colorScheme.primary,
        contentColor: Color = MaterialTheme.colorScheme.onSurface,
        choiceColor: Color = contentColor.copy(alpha = 0.08f),
        selectedChoiceColor: Color = contentColor.copy(alpha = 0.2f),
    ): PvotAdjustmentColors =
        PvotAdjustmentColors(accentColor, contentColor, choiceColor, selectedChoiceColor)
}

/**
 * A glass sheet for one camera setting, such as the timer or the exposure: its [title] between an
 * optional [icon] and a close button, over [content].
 *
 * @param title What is being set, such as EXPOSURE
 * @param onClose Called when the close button is tapped
 * @param closeContentDescription What the close button does, for TalkBack
 * @param modifier Modifier for the sheet
 * @param backdrop The content behind the sheet, or null for the tint alone
 * @param icon Icon before the title, tinted through [LocalContentColor], or null for none
 * @param closeIcon The close button's icon
 * @param colors The accent and content colors
 * @param style How the sheet's glass looks
 * @param content The setting's control, such as a [PvotRuler] or a [PvotChoiceRow]
 */
@Composable
public fun PvotAdjustmentSheet(
    title: String,
    onClose: () -> Unit,
    closeContentDescription: String,
    modifier: Modifier = Modifier,
    backdrop: PvotGlassBackdrop? = LocalPvotGlassBackdrop.current,
    icon: (@Composable () -> Unit)? = null,
    closeIcon: @Composable () -> Unit,
    colors: PvotAdjustmentColors = PvotAdjustmentSheetDefaults.colors(),
    style: PvotGlassStyle = PvotCameraGlassDefaults.PanelStyle,
    content: @Composable () -> Unit,
) {
    Box(
        modifier =
            modifier
                .height(PvotAdjustmentSheetDefaults.Height)
                .pvotGlassSurface(
                    backdrop = backdrop,
                    cornerRadius = PvotAdjustmentSheetDefaults.CornerRadius,
                    style = style,
                    role = null,
                ),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp, start = 16.dp, end = 16.dp)) {
            if (icon != null) {
                Box(modifier = Modifier.align(Alignment.CenterStart).padding(start = 9.dp)) {
                    CompositionLocalProvider(LocalContentColor provides colors.accentColor) {
                        icon()
                    }
                }
            }
            Text(
                text = title,
                color = colors.contentColor,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.Center),
            )
            Box(
                modifier =
                    Modifier.align(Alignment.CenterEnd)
                        .size(48.dp)
                        .clip(CircleShape)
                        .semantics { contentDescription = closeContentDescription }
                        .clickable(role = Role.Button, onClick = onClose),
                contentAlignment = Alignment.Center,
            ) {
                CompositionLocalProvider(LocalContentColor provides colors.contentColor) {
                    closeIcon()
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize().padding(top = 56.dp, bottom = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            CompositionLocalProvider(LocalContentColor provides colors.contentColor) { content() }
        }
    }
}

/**
 * A tick ruler with an index, for a value such as exposure compensation: drag along it or tap a
 * point to set the value, snapped to [steps] when there are any.
 *
 * @param value The value, from 0 at the start of the ruler to 1 at its end
 * @param onValueChange Called with the new value as the finger moves
 * @param modifier Modifier for the ruler
 * @param steps How many steps the value snaps to, or 0 for none
 * @param startLabel Text under the start, such as −2
 * @param endLabel Text under the end, such as +2
 * @param colors The index and tick colors
 */
@Composable
public fun PvotRuler(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    startLabel: String? = null,
    endLabel: String? = null,
    colors: PvotAdjustmentColors = PvotAdjustmentSheetDefaults.colors(),
) {
    val haptics = LocalHapticFeedback.current
    val currentValue by rememberUpdatedState(value)
    val currentOnValueChange by rememberUpdatedState(onValueChange)

    val snap = { fraction: Float ->
        val clamped = fraction.coerceIn(0f, 1f)
        if (steps > 0) (clamped * steps).roundToInt().toFloat() / steps else clamped
    }
    val update = { fraction: Float ->
        val next = snap(fraction)
        if (next != currentValue) {
            if (steps > 0) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            currentOnValueChange(next)
        }
    }

    Column(modifier = modifier) {
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .height(48.dp)
                    .semantics {
                        progressBarRangeInfo = ProgressBarRangeInfo(value, 0f..1f, steps)
                        setProgress {
                            update(it)
                            true
                        }
                    }
                    .pointerInput(steps) {
                        detectHorizontalDragGestures { change, _ ->
                            update(change.position.x / size.width)
                        }
                    }
                    .pointerInput(steps) {
                        detectTapGestures { offset -> update(offset.x / size.width) }
                    }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val ticks = PvotAdjustmentSheetDefaults.Ticks
                for (i in 0..ticks) {
                    val x = size.width * i / ticks
                    val height =
                        when {
                            i % 10 == 0 -> 24.dp.toPx()
                            i % 5 == 0 -> 17.dp.toPx()
                            else -> 10.dp.toPx()
                        }
                    drawLine(
                        color = colors.contentColor.copy(alpha = if (i % 5 == 0) 0.9f else 0.5f),
                        start = Offset(x, 6.dp.toPx()),
                        end = Offset(x, 6.dp.toPx() + height),
                        strokeWidth = 1.dp.toPx(),
                    )
                }
                val indexX = size.width * value.coerceIn(0f, 1f)
                drawLine(
                    color = colors.accentColor,
                    start = Offset(indexX, 0f),
                    end = Offset(indexX, 37.dp.toPx()),
                    strokeWidth = 2.dp.toPx(),
                )
            }
        }

        if (startLabel != null || endLabel != null) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = startLabel.orEmpty(), color = colors.contentColor, fontSize = 11.sp)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = endLabel.orEmpty(), color = colors.contentColor, fontSize = 11.sp)
            }
        }
    }
}

/**
 * A row of choices for a camera setting, such as Off, 3s and 10s, the selected one lit; it scrolls
 * sideways when the choices do not fit.
 *
 * @param options Label of each choice, in order
 * @param selectedIndex Index of the selected choice in [options], or -1 for none
 * @param onSelect Called with the index of the choice tapped
 * @param modifier Modifier for the row
 * @param colors The label and fill colors
 */
@Composable
public fun PvotChoiceRow(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: PvotAdjustmentColors = PvotAdjustmentSheetDefaults.colors(),
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Box(
                modifier =
                    Modifier.height(PvotAdjustmentSheetDefaults.ChoiceHeight)
                        .widthIn(min = 64.dp)
                        .clip(RoundedCornerShape(PvotAdjustmentSheetDefaults.ChoiceHeight / 2))
                        .background(
                            if (selected) colors.selectedChoiceColor else colors.choiceColor
                        )
                        .clickable(role = Role.RadioButton) { onSelect(index) }
                        .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (selected) colors.accentColor else colors.contentColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }
        }
    }
}
