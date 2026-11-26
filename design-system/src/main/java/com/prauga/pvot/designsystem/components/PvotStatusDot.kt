// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.theme.PvotTheme

/** The state a [PvotStatusDot] marks. */
public enum class PvotStatus {
    /** Done or healthy, such as a finished backup. */
    Good,

    /** Needs attention soon, such as a backup that is getting old. */
    Warning,

    /** Failed or broken. */
    Error,

    /** No state yet, such as something that has never run. */
    Neutral,
}

/** Defaults for [PvotStatusDot]. */
public object PvotStatusDotDefaults {

    /** Diameter of the dot. */
    public val Size: Dp = 10.dp

    /** The theme color for [status]: success, warning, error or a muted outline. */
    @Composable
    public fun color(status: PvotStatus): Color =
        when (status) {
            PvotStatus.Good -> PvotTheme.extendedColors.success
            PvotStatus.Warning -> PvotTheme.extendedColors.warning
            PvotStatus.Error -> MaterialTheme.colorScheme.error
            PvotStatus.Neutral -> MaterialTheme.colorScheme.outline
        }
}

/**
 * A small round dot that marks a status beside a label, such as "Backed up".
 *
 * The dot is decorative: the text next to it must say the status too, so the state never depends on
 * color alone.
 *
 * @param status The state to show
 * @param modifier Modifier for the dot
 * @param size Diameter of the dot
 */
@Composable
public fun PvotStatusDot(
    status: PvotStatus,
    modifier: Modifier = Modifier,
    size: Dp = PvotStatusDotDefaults.Size,
) {
    Box(modifier.size(size).background(PvotStatusDotDefaults.color(status), CircleShape))
}
