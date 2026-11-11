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
 * Only the styles Pvot overrides are set; the rest fall back to the Material defaults.
 */
public val PvotTypography: Typography =
    Typography(
        bodyLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp,
            )
    )
