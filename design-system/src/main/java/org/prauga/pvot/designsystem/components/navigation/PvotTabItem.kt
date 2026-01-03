// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.pvot.designsystem.components.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Represents a navigation tab item.
 *
 * @param iconRes Icon for collapsed state.
 * @param labelRes Label text.
 * @param contentDescriptionRes Accessibility description.
 * @param expandedIconRes Icon for expanded state (defaults to [iconRes]).
 * @param expandedLabelRes Label for expanded state (defaults to [labelRes]).
 */
data class PvotTabItem(
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int,
    @StringRes val contentDescriptionRes: Int,
    @DrawableRes val expandedIconRes: Int? = null,
    @StringRes val expandedLabelRes: Int? = null
)
