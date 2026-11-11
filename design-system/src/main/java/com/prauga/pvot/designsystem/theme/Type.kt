// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.prauga.pvot.designsystem.R

/**
 * Lenia Sans, the Pvot typeface, in the four weights the type scale and the components use:
 * regular, medium, semibold and bold.
 */
public val PvotFontFamily: FontFamily =
    FontFamily(
        Font(R.font.pvot_lenia_sans_regular, FontWeight.Normal),
        Font(R.font.pvot_lenia_sans_medium, FontWeight.Medium),
        Font(R.font.pvot_lenia_sans_semibold, FontWeight.SemiBold),
        Font(R.font.pvot_lenia_sans_bold, FontWeight.Bold),
    )

/**
 * The Pvot type scale.
 *
 * Every style is set in Lenia Sans, see [PvotFontFamily]. Only `bodyLarge` changes its metrics; the
 * other sizes and weights are the Material defaults.
 */
public val PvotTypography: Typography =
    with(Typography()) {
        Typography(
            displayLarge = displayLarge.copy(fontFamily = PvotFontFamily),
            displayMedium = displayMedium.copy(fontFamily = PvotFontFamily),
            displaySmall = displaySmall.copy(fontFamily = PvotFontFamily),
            headlineLarge = headlineLarge.copy(fontFamily = PvotFontFamily),
            headlineMedium = headlineMedium.copy(fontFamily = PvotFontFamily),
            headlineSmall = headlineSmall.copy(fontFamily = PvotFontFamily),
            titleLarge = titleLarge.copy(fontFamily = PvotFontFamily),
            titleMedium = titleMedium.copy(fontFamily = PvotFontFamily),
            titleSmall = titleSmall.copy(fontFamily = PvotFontFamily),
            bodyLarge =
                TextStyle(
                    fontFamily = PvotFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    letterSpacing = 0.5.sp,
                ),
            bodyMedium = bodyMedium.copy(fontFamily = PvotFontFamily),
            bodySmall = bodySmall.copy(fontFamily = PvotFontFamily),
            labelLarge = labelLarge.copy(fontFamily = PvotFontFamily),
            labelMedium = labelMedium.copy(fontFamily = PvotFontFamily),
            labelSmall = labelSmall.copy(fontFamily = PvotFontFamily),
        )
    }
