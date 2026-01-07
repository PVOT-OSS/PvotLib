// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.pvot.designsystem.components.picker.internal

/**
 * Configuration for a single wheel in a picker.
 *
 * @param values The list of integer values to display
 * @param label Function to convert a value to its display string
 * @param suffix Optional suffix displayed after the value (e.g., "hours", "min")
 * @param initialIndex The index to initially select
 */
internal data class WheelConfig(
    val values: List<Int>,
    val label: (Int) -> String = { it.toString() },
    val suffix: String = "",
    val initialIndex: Int = 0
)
