// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.coreui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * The message shown in place of a list or screen that has no content, with an optional icon above
 * it and an optional action below, such as "Add a contact".
 *
 * @param message Text explaining why there is nothing to show
 * @param modifier Modifier for the column
 * @param icon Content of the tinted circle above the message, typically an `Icon`; it is tinted for
 *   you
 * @param actionLabel Label of the button under the message; shown only with [onAction]
 * @param onAction Called when the button is tapped
 */
@Composable
public fun PvotEmptyContent(
    message: String,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (icon != null) {
            Box(
                modifier =
                    Modifier.size(64.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                            CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.primary,
                    content = icon,
                )
            }
        }
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (actionLabel != null && onAction != null) {
            FilledTonalButton(onClick = onAction, modifier = Modifier.padding(top = 4.dp)) {
                Text(actionLabel)
            }
        }
    }
}

/**
 * The 0.1.0 signature, kept so code compiled against it still links. New code resolves to the
 * overload above.
 *
 * @param message Text explaining why there is nothing to show
 * @param modifier Modifier for the column
 */
@Deprecated("Kept for binary compatibility.", level = DeprecationLevel.HIDDEN)
@Composable
public fun PvotEmptyContent(message: String, modifier: Modifier = Modifier) {
    PvotEmptyContent(message = message, modifier = modifier, icon = null)
}
