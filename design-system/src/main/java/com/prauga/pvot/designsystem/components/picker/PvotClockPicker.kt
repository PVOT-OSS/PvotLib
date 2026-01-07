// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.picker

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.prauga.pvot.designsystem.components.picker.internal.MultiWheelEngine
import com.prauga.pvot.designsystem.components.picker.internal.toWheelConfigs
import com.prauga.pvot.designsystem.components.picker.internal.wheelValuesToLocalTime
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import java.time.LocalTime

/**
 * A time picker for selecting a time of day (hours and minutes).
 *
 * @param time The currently selected time
 * @param onTimeChange Called when the user selects a new time
 * @param modifier Modifier for the picker container
 */
@Composable
fun PvotClockPicker(
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    MultiWheelEngine(
        configs = time.toWheelConfigs(),
        onValuesSelected = { values ->
            onTimeChange(wheelValuesToLocalTime(values))
        },
        modifier = modifier
    )
}

@Preview(
    name = "Pvot Clock Picker",
    showBackground = true,
    backgroundColor = 0xFF121212,
    widthDp = 360,
    heightDp = 200
)
@Composable
private fun PvotClockPickerPreview() {
    PvotAppTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            PvotClockPicker(
                time = LocalTime.of(0, 0),
                onTimeChange = {}
            )
        }
    }
}
