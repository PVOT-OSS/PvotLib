// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.math.abs

/** Colors of a [PvotDayStrip]. */
@Immutable
public data class PvotDayStripColors(
    /** Fill of the selected day. */
    val selectedContainer: Color,
    /** Text and mark on the selected day. */
    val selectedContent: Color,
    /** Text and mark on the other days. */
    val content: Color,
    /** Outline of today when it is not selected. */
    val todayOutline: Color,
    /** Outline of the other days. */
    val outline: Color,
)

/** Defaults for [PvotDayStrip]. */
public object PvotDayStripDefaults {

    /** Height of each day, tall enough for the number, the weekday and the mark. */
    public val DayHeight: Dp = 76.dp

    /** How far a horizontal drag must go to count as a swipe to the next or previous days. */
    public val SwipeThreshold: Dp = 48.dp

    /** Colors from [colorScheme]: the selected day filled in primary. */
    public fun colors(colorScheme: ColorScheme): PvotDayStripColors =
        PvotDayStripColors(
            selectedContainer = colorScheme.primary,
            selectedContent = colorScheme.onPrimary,
            content = colorScheme.onSurface,
            todayOutline = colorScheme.primary,
            outline = colorScheme.outlineVariant,
        )

    /** [colors] from the theme in scope. */
    @Composable public fun themeColors(): PvotDayStripColors = colors(MaterialTheme.colorScheme)
}

/**
 * A row of days as tall pills, such as the week above a day's timeline: the day of the month over
 * the short weekday, the [selected] day filled, [today] outlined, and a dot under the days [marked]
 * says have something on. Tapping a day calls [onSelect]; a horizontal swipe across the row calls
 * [onSwipe] with true for the next days. Each day is read out with its full date.
 *
 * @param days The days to show, usually a week
 * @param selected The selected day
 * @param today Today, outlined when not selected
 * @param onSelect Called with the tapped day
 * @param modifier Modifier for the row
 * @param marked Whether a day gets the dot
 * @param onSwipe Called on a swipe, with true for the next days; null for no swiping
 * @param colors Colors of the days
 * @param locale Locale for the weekday names and the spoken dates
 */
@Composable
public fun PvotDayStrip(
    days: List<LocalDate>,
    selected: LocalDate,
    today: LocalDate,
    onSelect: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    marked: (LocalDate) -> Boolean = { false },
    onSwipe: ((next: Boolean) -> Unit)? = null,
    colors: PvotDayStripColors = PvotDayStripDefaults.themeColors(),
    locale: Locale = Locale.getDefault(),
) {
    val weekday = DateTimeFormatter.ofPattern("EEE", locale)
    val spoken = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale)
    val swipePx = with(LocalDensity.current) { PvotDayStripDefaults.SwipeThreshold.toPx() }
    val haptics = LocalHapticFeedback.current
    Row(
        modifier.fillMaxWidth().let { base ->
            if (onSwipe == null) base
            else
                base.pointerInput(days.firstOrNull()) {
                    var total = 0f
                    detectHorizontalDragGestures(
                        onDragStart = { total = 0f },
                        onDragEnd = { if (abs(total) > swipePx) onSwipe(total < 0) },
                        onHorizontalDrag = { _, delta -> total += delta },
                    )
                }
        },
        horizontalArrangement = Arrangement.spacedBy(PvotSpacing.S - 2.dp),
    ) {
        days.forEach { date ->
            val isSelected = date == selected
            val content = if (isSelected) colors.selectedContent else colors.content
            val border =
                when {
                    isSelected -> null
                    date == today -> BorderStroke(1.5.dp, colors.todayOutline)
                    else -> BorderStroke(1.dp, colors.outline)
                }
            Column(
                Modifier.weight(1f)
                    .height(PvotDayStripDefaults.DayHeight)
                    .clip(PvotShapes.Pill)
                    .background(if (isSelected) colors.selectedContainer else Color.Transparent)
                    .let { if (border != null) it.border(border, PvotShapes.Pill) else it }
                    .clearAndSetSemantics {
                        contentDescription = date.format(spoken)
                        this.selected = isSelected
                        onClick {
                            onSelect(date)
                            true
                        }
                    }
                    .clickable {
                        haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        onSelect(date)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "%02d".format(locale, date.dayOfMonth),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = content,
                )
                Text(
                    text = date.format(weekday),
                    style = MaterialTheme.typography.labelSmall,
                    color = content,
                )
                Box(
                    Modifier.padding(top = PvotSpacing.XS)
                        .size(4.dp)
                        .background(if (marked(date)) content else Color.Transparent, CircleShape)
                )
            }
        }
    }
}
