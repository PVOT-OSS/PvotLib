// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.catalog.hub

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import com.prauga.pvot.catalog.R
import com.prauga.pvot.designsystem.components.PvotGlassCard
import com.prauga.pvot.designsystem.components.PvotSlideTarget
import com.prauga.pvot.designsystem.components.PvotSlideToAct
import com.prauga.pvot.designsystem.components.PvotSlideToActDefaults
import com.prauga.pvot.designsystem.components.background.PvotPhotoBackground
import com.prauga.pvot.designsystem.components.background.preparePvotPhotoBackground
import com.prauga.pvot.designsystem.components.call.PvotCallAction
import com.prauga.pvot.designsystem.components.call.PvotCallActionButton
import com.prauga.pvot.designsystem.components.call.PvotCallActionButtonDefaults
import com.prauga.pvot.designsystem.components.call.PvotCallButton
import com.prauga.pvot.designsystem.components.keypad.PvotKeypad
import com.prauga.pvot.designsystem.components.keypad.PvotKeypadDefaults
import com.prauga.pvot.designsystem.components.keypad.PvotKeypadDisplay
import com.prauga.pvot.designsystem.effects.PvotGlassDefaults
import com.prauga.pvot.designsystem.effects.PvotGlassMaterial
import com.prauga.pvot.designsystem.effects.pvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.rememberPvotGlassBackdrop
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing

// ───────────────────────────── Call controls ─────────────────────────────

@Composable
internal fun CallControlsPage(onBack: () -> Unit) {
    val send = rememberSnackbarSender()
    var muted by remember { mutableStateOf(false) }
    var speaker by remember { mutableStateOf(false) }
    var held by remember { mutableStateOf(false) }
    DetailPage(
        title = "Call controls",
        summary = "The in-call buttons: round toggles with labels, grouped on one glass card over the " +
            "caller's photo, and the solid end-call pill that is found at a glance.",
        onBack = onBack,
        code = "PvotCallButton(\n    icon = painterResource(R.drawable.ic_mic_off),\n    label = \"Mute\",\n" +
            "    checked = muted,\n    onClick = { muted = !muted }\n)\n" +
            "PvotCallActionButton(PvotCallAction.End, endIcon, \"End call\", onClick = ::hangUp)"
    ) {
        DemoSection(title = "Over a photo", caption = "Toggles morph to a rounded square while on", framed = false) {
            PhotoFrame { backdrop ->
                Column(
                    modifier = Modifier.fillMaxWidth().padding(PvotSpacing.L),
                    verticalArrangement = Arrangement.spacedBy(PvotSpacing.L * 2),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PvotGlassCard(
                        backdrop = backdrop,
                        style = PvotGlassDefaults.materialStyle(PvotGlassMaterial.Regular),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(PvotSpacing.L)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                PvotCallButton(painterResource(R.drawable.ic_t_microphone_off), "Mute", { muted = !muted }, checked = muted)
                                PvotCallButton(painterResource(R.drawable.ic_t_dialpad), "Keypad", { send("Keypad") })
                                PvotCallButton(painterResource(R.drawable.ic_t_volume), "Speaker", { speaker = !speaker }, checked = speaker, badge = true)
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                PvotCallButton(painterResource(R.drawable.ic_t_user_plus), "Add call", { send("Add call") })
                                PvotCallButton(painterResource(R.drawable.ic_t_player_pause), "Hold", { held = !held }, checked = held)
                                PvotCallButton(painterResource(R.drawable.ic_t_arrows_exchange), "Swap", {}, enabled = false)
                            }
                        }
                    }
                    PvotCallActionButton(
                        action = PvotCallAction.End,
                        icon = painterResource(R.drawable.ic_t_phone_off),
                        label = "End call",
                        onClick = { send("End call") },
                        width = PvotCallActionButtonDefaults.EndWidth
                    )
                }
            }
        }
        DemoSection(title = "Answer and decline", caption = "Solid semantic colors, never glass") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                PvotCallActionButton(PvotCallAction.Decline, painterResource(R.drawable.ic_t_phone_off), "Decline", { send("Decline") }, showLabel = true)
                PvotCallActionButton(PvotCallAction.Answer, painterResource(R.drawable.ic_t_phone), "Answer", { send("Answer") }, showLabel = true)
            }
        }
    }
}

/** A frame over a blurred stand-in photo, the way the call screen draws a caller's photo. */
@Composable
private fun PhotoFrame(content: @Composable (com.prauga.pvot.designsystem.effects.PvotGlassBackdrop) -> Unit) {
    val density = LocalDensity.current
    val backdrop = rememberPvotGlassBackdrop()
    val source = remember { callerPhoto() }
    var blurred by remember { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(source) { blurred = preparePvotPhotoBackground(source, density, downscale = 1) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(PvotShapes.Card)
    ) {
        PvotPhotoBackground(blurred, Modifier.matchParentSize().pvotGlassBackdrop(backdrop))
        Box(Modifier.fillMaxWidth().padding(vertical = PvotSpacing.L)) { content(backdrop) }
    }
}

/** A warm stand-in for a caller's photo: a few soft discs of color. */
private fun callerPhoto(): Bitmap {
    val size = 96
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(0xFF6D4C8F.toInt())
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    listOf(Triple(30f, 28f, 0xFFE07A5F), Triple(70f, 40f, 0xFF3D5A80), Triple(48f, 76f, 0xFFF2CC8F)).forEach { (x, y, color) ->
        paint.color = color.toInt()
        canvas.drawCircle(x, y, 40f, paint)
    }
    return bitmap
}

// ───────────────────────────── Keypad ─────────────────────────────

@Composable
internal fun KeypadPage(onBack: () -> Unit) {
    val send = rememberSnackbarSender()
    var digits by remember { mutableStateOf("") }
    var held by remember { mutableIntStateOf(0) }
    DetailPage(
        title = "Keypad",
        summary = "Twelve round keys that report down and up, so a tone plays while a key is held, " +
            "with the number above and a backspace that clears when held.",
        onBack = onBack,
        code = "PvotKeypadDisplay(digits, onBackspace = { digits = digits.dropLast(1) },\n" +
            "    onBackspaceLongPress = { digits = \"\" })\n" +
            "PvotKeypad(onKeyPress = { key, pressed -> if (pressed) { tone(key); digits += key.digit } })"
    ) {
        DemoSection(title = "Dial", caption = "Hold 0 for +. Keys held: $held") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PvotKeypadDisplay(
                    digits = digits,
                    onBackspace = { digits = digits.dropLast(1) },
                    onBackspaceLongPress = { digits = "" }
                )
                PvotKeypad(
                    onKeyPress = { key, pressed ->
                        if (pressed) {
                            digits += key.digit
                            held++
                        }
                    },
                    keys = PvotKeypadDefaults.keys(zeroLongPressLabel = "Plus"),
                    onKeyLongPress = { key ->
                        // The 0 already went in on the down: swap it for the +.
                        digits = digits.dropLast(1) + "+"
                        key.longPressLabel != null
                    },
                    modifier = Modifier.padding(vertical = PvotSpacing.S)
                )
                PvotCallActionButton(
                    action = PvotCallAction.Answer,
                    icon = painterResource(R.drawable.ic_t_phone),
                    label = "Call",
                    onClick = { send(if (digits.isEmpty()) "Type a number" else "Calling $digits") }
                )
            }
        }
    }
}

// ───────────────────────────── Slide to act ─────────────────────────────

@Composable
internal fun SlideToActPage(onBack: () -> Unit) {
    val context = LocalContext.current
    var progress by remember { mutableFloatStateOf(0f) }
    var result by remember { mutableStateOf("Ringing") }
    var round by remember { mutableIntStateOf(0) }
    DetailPage(
        title = "Slide to act",
        summary = "A puck slid up or down to act, as an incoming call is answered or declined: far " +
            "enough or flung, it acts; let go early, it springs back. TalkBack gets each end as an action.",
        onBack = onBack,
        code = "PvotSlideToAct(\n    icon = painterResource(R.drawable.ic_phone),\n    contentDescription = \"Answer\",\n" +
            "    up = PvotSlideTarget(\"Answer\", ::answer, 120.dp, hint = \"Swipe up to answer\"),\n" +
            "    down = PvotSlideTarget(\"Decline\", ::decline, 56.dp, hint = \"Swipe down to decline\",\n" +
            "        color = MaterialTheme.colorScheme.error)\n)"
    ) {
        val touchExploration = PvotSlideToActDefaults.isTouchExplorationEnabled(context)
        DemoSection(
            title = "Incoming call",
            caption = "$result · progress ${"%.2f".format(progress)}" +
                if (touchExploration) " · TalkBack is on: an app shows two buttons here" else ""
        ) {
            // A new key per round puts the puck back after it acts.
            androidx.compose.runtime.key(round) {
                PvotSlideToAct(
                    icon = painterResource(R.drawable.ic_t_phone),
                    contentDescription = "Answer",
                    up = PvotSlideTarget("Answer", { result = "Answered"; round++ }, PvotSlideToActDefaults.UpDistance, hint = "Swipe up to answer"),
                    down = PvotSlideTarget(
                        "Decline", { result = "Declined"; round++ }, PvotSlideToActDefaults.DownDistance,
                        hint = "Swipe down to decline", color = MaterialTheme.colorScheme.error
                    ),
                    onProgress = { progress = it },
                    modifier = Modifier.fillMaxWidth().padding(vertical = PvotSpacing.L)
                )
            }
            Text(
                text = "Each round resets the puck so you can try again.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = PvotSpacing.XS)
            )
        }
    }
}
