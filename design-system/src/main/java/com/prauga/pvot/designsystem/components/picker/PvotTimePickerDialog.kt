// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.prauga.pvot.designsystem.components.PvotDialog
import com.prauga.pvot.designsystem.components.PvotDialogAction
import com.prauga.pvot.designsystem.components.PvotDialogActionStyle
import java.time.LocalTime

/**
 * A time of day picked on a [PvotClockPicker] in a [PvotDialog], with OK and Cancel. The wheels
 * start from [initial] each time the dialog opens and keep the picked time while it eases out.
 *
 * @param visible Whether the dialog is shown
 * @param initial The time the wheels show when the dialog opens
 * @param onDismiss Called on Cancel, back or a tap outside
 * @param onConfirm Called with the picked time on OK
 * @param modifier Modifier for the dialog's card
 * @param title The dialog's title, or null for none
 */
@Composable
public fun PvotTimePickerDialog(
    visible: Boolean,
    initial: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
) {
    var time by remember(visible) { mutableStateOf(initial) }
    PvotDialog(
        visible = visible,
        onDismissRequest = onDismiss,
        title = title,
        modifier = modifier,
        content = { PvotClockPicker(time = time, onTimeChange = { time = it }) },
        actions = {
            PvotDialogAction(
                label = stringResource(android.R.string.ok),
                onClick = { onConfirm(time) },
                style = PvotDialogActionStyle.Primary,
            )
            PvotDialogAction(label = stringResource(android.R.string.cancel), onClick = onDismiss)
        },
    )
}
