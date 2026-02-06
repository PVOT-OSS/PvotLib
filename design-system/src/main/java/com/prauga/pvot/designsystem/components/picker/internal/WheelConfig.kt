// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.picker.internal

/**
 * Configuration for a single wheel in a picker.
 *
 * @param values The list of integer values to display
 * @param label Function to convert a value to its display string
 * @param suffix Optional suffix displayed after the value (e.g., "hours", "min")
 * @param initialIndex The index to initially select
 * @param appearance Visual appearance configuration
 * @param behavior Behavioral configuration
 * @throws IllegalArgumentException if values list is empty or initialIndex is out of bounds
 */
internal data class WheelConfig(
    val values: List<Int>,
    val label: (Int) -> String = { it.toString() },
    val suffix: String = "",
    val initialIndex: Int = 0,
    val appearance: WheelAppearance = WheelAppearance(),
    val behavior: WheelBehavior = WheelBehavior()
) {
    init {
        require(values.isNotEmpty()) { "Values list cannot be empty" }
        require(initialIndex in values.indices) {
            "Initial index $initialIndex out of bounds (0..${values.size - 1})"
        }
    }
}
