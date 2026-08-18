// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.picker.internal

import androidx.annotation.StringRes

/**
 * Configuration for a single wheel in a picker.
 *
 * @param values The list of integer values to display
 * @param label Function to convert a value to its display string
 * @param suffixRes Suffix shown after the value, or null for none
 * @param descriptionRes Spoken name for this wheel, or null to leave it unnamed
 * @param initialIndex The index to initially select
 */
internal data class WheelConfig(
    val values: List<Int>,
    val label: (Int) -> String = { it.toString() },
    @StringRes val suffixRes: Int? = null,
    @StringRes val descriptionRes: Int? = null,
    val initialIndex: Int = 0
)
