// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.pvot.designsystem.components.picker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.prauga.pvot.designsystem.theme.PvotAppTheme

@Composable
fun PvotTimerPicker(
    onDurationChanged: (hours: Int, minutes: Int, seconds: Int) -> Unit
) {
    var hours by remember { mutableStateOf(0) }
    var minutes by remember { mutableStateOf(30) }
    var seconds by remember { mutableStateOf(0) }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        WheelPicker(
            items = (0..23).toList(),
            suffix = "hours"
        ) {
            hours = it
            onDurationChanged(hours, minutes, seconds)
        }

        WheelPicker(
            items = (0..59).toList(),
            suffix = "min"
        ) {
            minutes = it
            onDurationChanged(hours, minutes, seconds)
        }

        WheelPicker(
            items = (0..59).toList(),
            suffix = "sec"
        ) {
            seconds = it
            onDurationChanged(hours, minutes, seconds)
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 390,
    heightDp = 240
)
@Composable
fun PvotTimerPickerPreview() {
    PvotAppTheme {
        Surface {
            PvotTimerPicker { _, _, _ -> }
        }
    }
}