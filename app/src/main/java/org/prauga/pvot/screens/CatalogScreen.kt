// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.pvot.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.prauga.pvot.designsystem.components.picker.ClockTimePicker
import org.prauga.pvot.designsystem.components.picker.DurationPicker
import java.time.LocalTime
import kotlin.time.Duration.Companion.minutes

@Composable
fun CatalogScreen(
    label: String,
    modifier: Modifier
) {
    var selectedTime by remember { mutableStateOf(LocalTime.of(0, 0)) }
    var selectedDuration by remember { mutableStateOf(30.minutes) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Clock Time Picker",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        ClockTimePicker(
            time = selectedTime,
            onTimeChange = { selectedTime = it }
        )

        Text(
            text = "Duration Picker",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        DurationPicker(
            duration = selectedDuration,
            onDurationChange = { selectedDuration = it }
        )
    }
}