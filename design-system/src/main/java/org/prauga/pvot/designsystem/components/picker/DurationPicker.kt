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
import org.prauga.pvot.designsystem.components.picker.internal.wheelValuesToDuration
import org.prauga.pvot.designsystem.theme.PvotAppTheme
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * A duration picker for selecting a time duration (hours, minutes, and seconds).
 *
 * @param duration The currently selected duration
 * @param onDurationChange Called when the user selects a new duration
 * @param modifier Modifier for the picker container
 */
@Composable
fun DurationPicker(
    duration: Duration,
    onDurationChange: (Duration) -> Unit,
    modifier: Modifier = Modifier
) {
    MultiWheelEngine(
        configs = duration.toWheelConfigs(),
        onValuesSelected = { values ->
            onDurationChange(wheelValuesToDuration(values))
        },
        modifier = modifier
    )
}

@Preview(
    name = "Duration Picker",
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 390,
    heightDp = 240
)
@Composable
private fun DurationPickerPreview() {
    PvotAppTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            DurationPicker(
                duration = 30.minutes,
                onDurationChange = {}
            )
        }
    }
}
