// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.picker.internal

import java.time.LocalTime
import kotlin.time.Duration

/**
 * Converts a [LocalTime] to wheel configurations for hour and minute selection.
 */
internal fun LocalTime.toWheelConfigs(): List<WheelConfig> = listOf(
    WheelConfig(
        values = (0..23).toList(),
        label = { "%02d".format(it) },
        suffix = "hours",
        initialIndex = hour
    ),
    WheelConfig(
        values = (0..59).toList(),
        label = { "%02d".format(it) },
        suffix = "min",
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
    val totalSeconds = inWholeSeconds
    val hours = (totalSeconds / 3600).toInt().coerceIn(0, 23)
    val minutes = ((totalSeconds % 3600) / 60).toInt()
    val seconds = (totalSeconds % 60).toInt()

    return listOf(
        WheelConfig(
            values = (0..23).toList(),
            label = { it.toString() },
            suffix = "hours",
            initialIndex = hours
        ),
        WheelConfig(
            values = (0..59).toList(),
            label = { it.toString() },
            suffix = "min",
            initialIndex = minutes
        ),
        WheelConfig(
            values = (0..59).toList(),
            label = { it.toString() },
            suffix = "sec",
            initialIndex = seconds
        )
    )
}

/**
 * Converts wheel values back to a [Duration].
 */
internal fun wheelValuesToDuration(values: List<Int>): Duration {
    require(values.size >= 3) { "Need hours, minutes, and seconds values" }
    val hours = values[0]
    val minutes = values[1]
    val seconds = values[2]
    return Duration.parse("${hours}h ${minutes}m ${seconds}s")
}
