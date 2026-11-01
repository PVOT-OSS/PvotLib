// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.keypad

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.R

/** Defaults for [PvotKeypadDisplay]. */
public object PvotKeypadDisplayDefaults {

    /** Height of the display. */
    public val Height: Dp = 64.dp

    /** Diameter of the backspace button, and of the empty space that balances it. */
    public val ButtonSize: Dp = 48.dp

    /** Opacity of the backspace icon while there is nothing to delete. */
    public const val DisabledAlpha: Float = 0.38f
}

/**
 * The line above a [PvotKeypad]: the number being typed, centered, with a backspace button at its
 * end.
 *
 * Tapping backspace calls [onBackspace]; holding it calls [onBackspaceLongPress] once, which
 * usually clears the number. [content] draws the number, so an app can keep its own text field
 * there; the overload taking `digits` draws plain text.
 *
 * @param onBackspace Called when backspace is tapped
 * @param onBackspaceLongPress Called when backspace is held
 * @param modifier Modifier for the display
 * @param backspaceEnabled Whether there is anything to delete
 * @param backspaceModifier Extra modifier for the backspace button, such as a test tag
 * @param content The number being typed
 */
@Composable
public fun PvotKeypadDisplay(
    onBackspace: () -> Unit,
    onBackspaceLongPress: () -> Unit,
    modifier: Modifier = Modifier,
    backspaceEnabled: Boolean = true,
    backspaceModifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(PvotKeypadDisplayDefaults.Height),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(PvotKeypadDisplayDefaults.ButtonSize))
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center, content = content)
        BackspaceButton(backspaceEnabled, onBackspace, onBackspaceLongPress, backspaceModifier)
    }
}

/**
 * A [PvotKeypadDisplay] showing [digits] as text, shrinking to one line with the start cut off
 * when the number is too long.
 *
 * @param digits The number being typed
 * @param onBackspace Called when backspace is tapped
 * @param onBackspaceLongPress Called when backspace is held
 * @param modifier Modifier for the display
 * @param textStyle Style of the number
 */
@Composable
public fun PvotKeypadDisplay(
    digits: String,
    onBackspace: () -> Unit,
    onBackspaceLongPress: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.headlineMedium
) {
    PvotKeypadDisplay(
        onBackspace = onBackspace,
        onBackspaceLongPress = onBackspaceLongPress,
        modifier = modifier,
        backspaceEnabled = digits.isNotEmpty()
    ) {
        Text(
            text = digits,
            style = textStyle,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.StartEllipsis
        )
    }
}

@Composable
private fun BackspaceButton(
    enabled: Boolean,
    onBackspace: () -> Unit,
    onBackspaceLongPress: () -> Unit,
    modifier: Modifier
) {
    val description = stringResource(R.string.pvot_keypad_backspace)
    val haptics = LocalHapticFeedback.current
    val tap by rememberUpdatedState(onBackspace)
    val hold by rememberUpdatedState(onBackspaceLongPress)
    Box(
        modifier = modifier
            .size(PvotKeypadDisplayDefaults.ButtonSize)
            .clip(CircleShape)
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                awaitEachGesture {
                    awaitFirstDown()
                    try {
                        withTimeout(viewConfiguration.longPressTimeoutMillis) {
                            if (waitForUpOrCancellation() != null) tap()
                        }
                    } catch (_: PointerEventTimeoutCancellationException) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        hold()
                        waitForUpOrCancellation()
                    }
                }
            }
            .semantics {
                role = Role.Button
                contentDescription = description
                if (enabled) {
                    onClick { tap(); true }
                    onLongClick { hold(); true }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        val tint = MaterialTheme.colorScheme.onSurfaceVariant
        Icon(
            painter = painterResource(R.drawable.pvot_ic_backspace),
            contentDescription = null,
            tint = if (enabled) tint else tint.copy(alpha = tint.alpha * PvotKeypadDisplayDefaults.DisabledAlpha)
        )
    }
}
