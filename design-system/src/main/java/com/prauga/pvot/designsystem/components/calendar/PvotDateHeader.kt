// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.prauga.pvot.designsystem.theme.PvotSpacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/** Defaults for [PvotDateHeader]. */
public object PvotDateHeaderDefaults {

    /** The day of the month, the largest text on the screen. */
    public val dayStyle: TextStyle
        @Composable
        get() =
            MaterialTheme.typography.displayLarge.copy(
                fontSize = 64.sp,
                lineHeight = 68.sp,
                fontWeight = FontWeight.Bold,
            )

    /** The month, year and weekday beside the day. */
    public val labelStyle: TextStyle
        @Composable get() = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
}

/**
 * A date as a screen's hero: the day of the month large at the start ("08"), the month and year
 * ("Nov'25") over the weekday at the end, and an optional [secondary] line under them, such as the
 * same date in another calendar system. It is read out as one heading with the full date.
 *
 * @param date The date to show
 * @param modifier Modifier for the header
 * @param secondary An extra line under the weekday, or null for none
 * @param locale Locale for the month and weekday names
 */
@Composable
public fun PvotDateHeader(
    date: LocalDate,
    modifier: Modifier = Modifier,
    secondary: String? = null,
    locale: Locale = Locale.getDefault(),
) {
    val spoken = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale))
    val colors = MaterialTheme.colorScheme
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = PvotSpacing.L + PvotSpacing.S)
                .clearAndSetSemantics {
                    contentDescription = listOfNotNull(spoken, secondary).joinToString(", ")
                    heading()
                },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "%02d".format(locale, date.dayOfMonth),
            style = PvotDateHeaderDefaults.dayStyle,
            color = colors.onBackground,
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = date.format(DateTimeFormatter.ofPattern("MMM''yy", locale)),
                style = PvotDateHeaderDefaults.labelStyle,
                textAlign = TextAlign.End,
                color = colors.onBackground,
            )
            Text(
                text = date.format(DateTimeFormatter.ofPattern("EEEE", locale)),
                style = PvotDateHeaderDefaults.labelStyle,
                textAlign = TextAlign.End,
                color = colors.onBackground,
            )
            if (secondary != null) {
                Text(
                    text = secondary,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.End,
                    color = colors.onSurfaceVariant,
                )
            }
        }
    }
}
