// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.picker.internal

import com.prauga.pvot.designsystem.R
import java.time.LocalTime
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private fun twoDigits(value: Int): String = String.format(Locale.getDefault(), "%02d", value)

private fun plain(value: Int): String = value.toString(10)

/**
 * Converts a [LocalTime] to wheel configurations for hour and minute selection.
 */
internal fun LocalTime.toWheelConfigs(): List<WheelConfig> = listOf(
    WheelConfig(
        values = (0..23).toList(),
        label = ::twoDigits,
        suffixRes = R.string.pvot_picker_suffix_hours,
        descriptionRes = R.string.pvot_picker_label_hours,
        initialIndex = hour
    ),
    WheelConfig(
        values = (0..59).toList(),
        label = ::twoDigits,
        suffixRes = R.string.pvot_picker_suffix_minutes,
        descriptionRes = R.string.pvot_picker_label_minutes,
        initialIndex = minute
    )
)

/**
 * Converts wheel values back to a [LocalTime].
 */
internal fun wheelValuesToLocalTime(values: List<Int>): LocalTime {
    require(values.size >= 2) { "Need at least hour and minute values" }
    return LocalTime.of(values[0], values[1])
}

/**
 * Converts a [Duration] to wheel configurations for hours, minutes, and seconds.
 */
internal fun Duration.toWheelConfigs(): List<WheelConfig> {
    require(this >= Duration.ZERO && this < 24.hours) {
        "Duration must be in [0h, 24h), was $this"
    }
    val totalSeconds = inWholeSeconds
    val hours = (totalSeconds / 3600).toInt()
    val minutes = ((totalSeconds % 3600) / 60).toInt()
    val seconds = (totalSeconds % 60).toInt()

    return listOf(
        WheelConfig(
            values = (0..23).toList(),
            label = ::plain,
            suffixRes = R.string.pvot_picker_suffix_hours,
            descriptionRes = R.string.pvot_picker_label_hours,
            initialIndex = hours
        ),
        WheelConfig(
            values = (0..59).toList(),
            label = ::plain,
            suffixRes = R.string.pvot_picker_suffix_minutes,
            descriptionRes = R.string.pvot_picker_label_minutes,
            initialIndex = minutes
        ),
        WheelConfig(
            values = (0..59).toList(),
            label = ::plain,
            suffixRes = R.string.pvot_picker_suffix_seconds,
            descriptionRes = R.string.pvot_picker_label_seconds,
            initialIndex = seconds
        )
    )
}

/**
 * Converts wheel values back to a [Duration].
 */
internal fun wheelValuesToDuration(values: List<Int>): Duration {
    require(values.size >= 3) { "Need hours, minutes, and seconds values" }
    return values[0].hours + values[1].minutes + values[2].seconds
}
