// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.keypad

import android.view.KeyEvent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.effects.pvotPressScale
import com.prauga.pvot.designsystem.motion.PvotDurations

/**
 * One key of a [PvotKeypad].
 *
 * @param id What the key reports, such as a [KeyEvent] key code; the keypad only passes it on
 * @param digit The large character on the key, "1" to "9", "*", "0" or "#"
 * @param letters The small line under the digit, such as "ABC", or null for none
 * @param secondaryLetters A second small line, such as another alphabet's letters, or null
 * @param longPressLabel What holding the key does, read by TalkBack, or null for no long press
 */
@Immutable
public data class PvotKeypadKey(
    val id: Int,
    val digit: String,
    val letters: String? = null,
    val secondaryLetters: String? = null,
    val longPressLabel: String? = null,
) {
    /** What TalkBack reads: the digit and its letters, such as "2, ABC". */
    val contentDescription: String
        get() = listOfNotNull(digit, letters?.takeIf { it.isNotBlank() }).joinToString(", ")
}

/**
 * The colors of a [PvotKeypad]'s keys.
 *
 * @param keyColor Fill of a key at rest
 * @param pressedKeyColor Fill of a key while it is held
 * @param digitColor Color of the digit
 * @param lettersColor Color of the letters under the digit
 */
@Immutable
public data class PvotKeypadColors(
    val keyColor: Color,
    val pressedKeyColor: Color,
    val digitColor: Color,
    val lettersColor: Color,
)

/** Defaults for [PvotKeypad]. */
public object PvotKeypadDefaults {

    /** Diameter of a key. */
    public val KeySize: Dp = 72.dp

    /** Keys to a row. */
    public const val Columns: Int = 3

    /**
     * Keys with no fill of their own, for a keypad on a sheet or glass; a faint fill while held.
     */
    @Composable
    public fun flatColors(): PvotKeypadColors =
        PvotKeypadColors(
            keyColor = Color.Transparent,
            pressedKeyColor = MaterialTheme.colorScheme.onSurface.copy(alpha = PressedFillAlpha),
            digitColor = MaterialTheme.colorScheme.onSurface,
            lettersColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )

    /** Filled round keys, for a keypad on the plain background; brighter while held. */
    @Composable
    public fun filledColors(): PvotKeypadColors =
        PvotKeypadColors(
            keyColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            pressedKeyColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            digitColor = MaterialTheme.colorScheme.onSurface,
            lettersColor = MaterialTheme.colorScheme.onSurface,
        )

    /**
     * The twelve keys of a phone keypad, with the Latin letters and [KeyEvent] key codes as ids.
     * Pass [digits] to show another script's digits, in the order 0 to 9.
     *
     * @param digits The digits 0 to 9 to show
     * @param zeroLongPressLabel What holding 0 does, such as "Plus", or null for no long press
     * @param oneLongPressLabel What holding 1 does, such as "Voicemail", or null for no long press
     */
    public fun keys(
        digits: List<String> = (0..9).map { it.toString() },
        zeroLongPressLabel: String? = null,
        oneLongPressLabel: String? = null,
    ): List<PvotKeypadKey> {
        require(digits.size == 10) { "keys needs the ten digits 0 to 9" }
        val letters = listOf(null, null, "ABC", "DEF", "GHI", "JKL", "MNO", "PQRS", "TUV", "WXYZ")
        val codes =
            listOf(
                KeyEvent.KEYCODE_0,
                KeyEvent.KEYCODE_1,
                KeyEvent.KEYCODE_2,
                KeyEvent.KEYCODE_3,
                KeyEvent.KEYCODE_4,
                KeyEvent.KEYCODE_5,
                KeyEvent.KEYCODE_6,
                KeyEvent.KEYCODE_7,
                KeyEvent.KEYCODE_8,
                KeyEvent.KEYCODE_9,
            )
        val numbered =
            (1..9).map { n ->
                PvotKeypadKey(
                    id = codes[n],
                    digit = digits[n],
                    letters = letters[n],
                    longPressLabel = if (n == 1) oneLongPressLabel else null,
                )
            }
        return numbered +
            listOf(
                PvotKeypadKey(KeyEvent.KEYCODE_STAR, "*"),
                PvotKeypadKey(
                    id = KeyEvent.KEYCODE_0,
                    digit = digits[0],
                    // The "+" is the long press, so it shows only where the long press works.
                    letters = if (zeroLongPressLabel != null) "+" else null,
                    longPressLabel = zeroLongPressLabel,
                ),
                PvotKeypadKey(KeyEvent.KEYCODE_POUND, "#"),
            )
    }
}

/**
 * A phone keypad: twelve round keys, three to a row.
 *
 * A key reports going down and coming up through [onKeyPress], so a tone can play for as long as it
 * is held, and ticks the keyboard haptic when it goes down. A key with a long press label reports a
 * hold through [onKeyLongPress]; if that returns true the key's up is still reported, but nothing
 * else happens. TalkBack reads each key as its digit and letters and taps it with a down and an up.
 *
 * By default the keys have no fill of their own, for a keypad on a sheet or glass; use
 * [PvotKeypadDefaults.filledColors] for round filled keys on a plain background.
 *
 * @param onKeyPress Called with a key and true when it goes down, false when it comes up
 * @param modifier Modifier for the keypad
 * @param keys The keys, in rows of [PvotKeypadDefaults.Columns]
 * @param onKeyLongPress Called when a key with a long press is held; returns whether it was handled
 * @param haptics Whether a key ticks when it goes down
 * @param keySize Diameter of a key
 * @param keyModifier Extra modifier for each key, such as a test tag
 * @param colors The keys' colors
 */
@Composable
public fun PvotKeypad(
    onKeyPress: (key: PvotKeypadKey, pressed: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    keys: List<PvotKeypadKey> = PvotKeypadDefaults.keys(),
    onKeyLongPress: (key: PvotKeypadKey) -> Boolean = { false },
    haptics: Boolean = true,
    keySize: Dp = PvotKeypadDefaults.KeySize,
    keyModifier: (PvotKeypadKey) -> Modifier = { Modifier },
    colors: PvotKeypadColors = PvotKeypadDefaults.flatColors(),
) {
    Column(modifier) {
        for (row in keys.chunked(PvotKeypadDefaults.Columns)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (key in row) {
                    KeypadKey(
                        key,
                        onKeyPress,
                        onKeyLongPress,
                        haptics,
                        keySize,
                        keyModifier(key),
                        colors,
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadKey(
    key: PvotKeypadKey,
    onKeyPress: (PvotKeypadKey, Boolean) -> Unit,
    onKeyLongPress: (PvotKeypadKey) -> Boolean,
    haptics: Boolean,
    size: Dp,
    modifier: Modifier,
    colors: PvotKeypadColors,
) {
    val source = remember { MutableInteractionSource() }
    val hapticFeedback = LocalHapticFeedback.current
    val press by rememberUpdatedState(onKeyPress)
    val longPress by rememberUpdatedState(onKeyLongPress)
    val pressed by source.collectIsPressedAsState()
    val fill by
        animateColorAsState(
            if (pressed) colors.pressedKeyColor else colors.keyColor,
            tween(PvotDurations.Short),
            label = "keypadKeyFill",
        )
    Column(
        modifier =
            modifier
                .size(size)
                .pvotPressScale(source)
                .clip(CircleShape)
                .background(fill, CircleShape)
                .pointerInput(key) {
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        val interaction = PressInteraction.Press(down.position)
                        source.tryEmit(interaction)
                        if (haptics)
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                        press(key, true)
                        var released = false
                        try {
                            withTimeout(viewConfiguration.longPressTimeoutMillis) {
                                waitForUpOrCancellation()
                                released = true
                            }
                        } catch (_: PointerEventTimeoutCancellationException) {
                            if (key.longPressLabel != null && longPress(key)) {
                                if (haptics)
                                    hapticFeedback.performHapticFeedback(
                                        HapticFeedbackType.LongPress
                                    )
                            }
                        }
                        if (!released) waitForUpOrCancellation()
                        source.tryEmit(PressInteraction.Release(interaction))
                        press(key, false)
                    }
                }
                .semantics(mergeDescendants = true) {
                    role = Role.Button
                    contentDescription = key.contentDescription
                    onClick {
                        press(key, true)
                        press(key, false)
                        true
                    }
                    key.longPressLabel?.let { label -> onLongClick(label) { longPress(key) } }
                },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = key.digit,
            style = MaterialTheme.typography.headlineMedium,
            color = colors.digitColor,
        )
        // 1 keeps an empty letters line so its digit sits level with 2 and 3.
        val letters =
            key.letters
                ?: if (key.secondaryLetters == null && key.id == KeyEvent.KEYCODE_1) " " else null
        letters?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.lettersColor,
            )
        }
        key.secondaryLetters?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.lettersColor,
            )
        }
    }
}

/** Opacity of the onSurface fill under a held key. */
private const val PressedFillAlpha = 0.08f
