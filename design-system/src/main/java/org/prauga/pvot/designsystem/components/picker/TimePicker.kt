// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.pvot.designsystem.components.picker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.prauga.pvot.designsystem.theme.PvotAppTheme

@Composable
fun TimePicker(
    onTimeChanged: (hour: Int, minute: Int) -> Unit
) {
    var selectedHour by remember { mutableStateOf(0) }
    var selectedMinute by remember { mutableStateOf(0) }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        WheelPicker(
            items = (0..23).toList(),
            suffix = "hours"
        ) {
            selectedHour = it
            onTimeChanged(selectedHour, selectedMinute)
        }

        Spacer(modifier = Modifier.width(16.dp))

        WheelPicker(
            items = (0..59).toList(),
            suffix = "min"
        ) {
            selectedMinute = it
            onTimeChanged(selectedHour, selectedMinute)
        }
    }
}

@Preview(
    name = "Wheel Time Picker",
    showBackground = true,
    backgroundColor = 0xFF121212,
    widthDp = 360,
    heightDp = 200
)
@Composable
fun TimePickerPreview() {
    PvotAppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            TimePicker { _, _ -> }
        }
    }
}
