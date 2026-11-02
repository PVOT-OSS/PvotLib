// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * The color roles Material has none for, alongside `MaterialTheme.colorScheme`: success and
 * warning, each with its content color and a container pair, as the Material roles are arranged.
 *
 * Read them through [PvotTheme.extendedColors]. Success is for an answer button or a saved state;
 * warning for a caution that is not an error, such as a call on hold or a weak signal. Errors keep
 * using `colorScheme.error`.
 *
 * @param success A filled success surface, such as an answer button
 * @param onSuccess Content on [success]
 * @param successContainer A soft success surface, such as a banner
 * @param onSuccessContainer Content on [successContainer]
 * @param warning A filled warning surface
 * @param onWarning Content on [warning]
 * @param warningContainer A soft warning surface
 * @param onWarningContainer Content on [warningContainer]
 */
@Immutable
public data class PvotExtendedColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
)

/** The extended roles arranged for a light background. */
public val PvotLightExtendedColors: PvotExtendedColors =
    PvotExtendedColors(
        success = PvotSuccessLight,
        onSuccess = PvotOnSuccessLight,
        successContainer = PvotSuccessContainerLight,
        onSuccessContainer = PvotOnSuccessContainerLight,
        warning = PvotWarningLight,
        onWarning = PvotOnWarningLight,
        warningContainer = PvotWarningContainerLight,
        onWarningContainer = PvotOnWarningContainerLight,
    )

/** The extended roles arranged for a dark background. */
public val PvotDarkExtendedColors: PvotExtendedColors =
    PvotExtendedColors(
        success = PvotSuccessDark,
        onSuccess = PvotOnSuccessDark,
        successContainer = PvotSuccessContainerDark,
        onSuccessContainer = PvotOnSuccessContainerDark,
        warning = PvotWarningDark,
        onWarning = PvotOnWarningDark,
        warningContainer = PvotWarningContainerDark,
        onWarningContainer = PvotOnWarningContainerDark,
    )

internal val LocalPvotExtendedColors = staticCompositionLocalOf { PvotDarkExtendedColors }
