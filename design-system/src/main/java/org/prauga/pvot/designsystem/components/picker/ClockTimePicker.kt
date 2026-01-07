// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.pvot.designsystem.components.picker

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.prauga.pvot.designsystem.components.picker.internal.MultiWheelEngine
import org.prauga.pvot.designsystem.components.picker.internal.toWheelConfigs
import org.prauga.pvot.designsystem.components.picker.internal.wheelValuesToLocalTime
import org.prauga.pvot.designsystem.theme.PvotAppTheme
import java.time.LocalTime

/**
 * A time picker for selecting a time of day (hours and minutes).
 *
 * @param time The currently selected time
 * @param onTimeChange Called when the user selects a new time
 * @param modifier Modifier for the picker container
 */
@Composable
fun ClockTimePicker(
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
    name = "Clock Time Picker",
    showBackground = true,
    backgroundColor = 0xFF121212,
    widthDp = 360,
    heightDp = 200
)
@Composable
private fun ClockTimePickerPreview() {
    PvotAppTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            ClockTimePicker(
                time = LocalTime.of(0, 0),
                onTimeChange = {}
            )
        }
    }
}
