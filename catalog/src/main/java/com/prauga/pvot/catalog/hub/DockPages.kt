// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.catalog.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.prauga.pvot.catalog.R
import com.prauga.pvot.designsystem.components.PvotCapsule
import com.prauga.pvot.designsystem.components.PvotTabRow
import com.prauga.pvot.designsystem.components.button.PvotIconButton
import com.prauga.pvot.designsystem.components.button.PvotIconButtonDefaults
import com.prauga.pvot.designsystem.components.call.PvotCallAction
import com.prauga.pvot.designsystem.components.call.PvotCallActionButton
import com.prauga.pvot.designsystem.components.list.PvotListGroup
import com.prauga.pvot.designsystem.components.list.PvotPreferenceRow
import com.prauga.pvot.designsystem.components.list.PvotRowChevron
import com.prauga.pvot.designsystem.components.list.PvotRowIconStyle
import com.prauga.pvot.designsystem.components.list.PvotSwitchRow
import com.prauga.pvot.designsystem.components.progress.PvotLinearProgress
import com.prauga.pvot.designsystem.components.progress.PvotProportionBar
import com.prauga.pvot.designsystem.components.progress.PvotProportionBarDefaults
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing

// ───────────────────────────── Tab row ─────────────────────────────

@Composable
internal fun TabRowPage(onBack: () -> Unit) {
    var filter by remember { mutableIntStateOf(0) }
    var many by remember { mutableIntStateOf(0) }
    DetailPage(
        title = "Tab row",
        summary =
            "Text tabs with a line under the selected one that slides to the tab tapped. A light " +
                "way to switch what a list shows.",
        onBack = onBack,
        code =
            "PvotTabRow(\n    tabs = listOf(\"All\", \"Missed\", \"Contacts\", \"Blocked\"),\n" +
                "    selectedIndex = filter,\n    onSelect = { filter = it }\n)",
    ) {
        DemoSection(title = "Shared width") {
            PvotTabRow(listOf("All", "Missed", "Contacts", "Blocked"), filter, { filter = it })
        }
        DemoSection(title = "Scrollable", caption = "Each tab as wide as its label") {
            PvotTabRow(
                listOf("All voicemail", "New", "Archived", "From contacts", "Unknown"),
                many,
                { many = it },
                scrollable = true,
            )
        }
    }
}

// ───────────────────────────── List group ─────────────────────────────

@Composable
internal fun ListGroupPage(onBack: () -> Unit) {
    val send = rememberSnackbarSender()
    var vibrate by remember { mutableStateOf(true) }
    DetailPage(
        title = "List group",
        summary =
            "Rows that belong together on one card, with a hairline between each and an optional " +
                "title. Rows in a group take plain icons and an arrow.",
        onBack = onBack,
        code =
            "PvotListGroup(title = \"General\") {\n    item {\n        PvotPreferenceRow(\"Appearance\", onClick = ::open,\n" +
                "            icon = { Icon(palette, null) }, iconStyle = PvotRowIconStyle.Plain,\n" +
                "            chevron = PvotRowChevron.Arrow)\n    }\n}",
    ) {
        DemoSection(title = "Settings", framed = false) {
            Column(verticalArrangement = Arrangement.spacedBy(SectionGap)) {
                PvotListGroup(title = "Call assist") {
                    item {
                        GroupRow("Caller ID and spam", R.drawable.ic_t_phone) { send("Caller ID") }
                    }
                }
                PvotListGroup(title = "General") {
                    item { GroupRow("Appearance", R.drawable.ic_t_palette) { send("Appearance") } }
                    item {
                        GroupRow("Sounds and vibration", R.drawable.ic_t_volume) { send("Sounds") }
                    }
                    item {
                        GroupRow("Notifications", R.drawable.ic_t_bell) { send("Notifications") }
                    }
                    item {
                        PvotSwitchRow(
                            title = "Vibrate when ringing",
                            checked = vibrate,
                            onCheckedChange = { vibrate = it },
                            icon = {
                                Icon(
                                    painterResource(R.drawable.ic_t_wave_sine),
                                    contentDescription = null,
                                )
                            },
                            iconStyle = PvotRowIconStyle.Plain,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupRow(title: String, icon: Int, onClick: () -> Unit) {
    PvotPreferenceRow(
        title = title,
        onClick = onClick,
        icon = { Icon(painterResource(icon), contentDescription = null) },
        iconStyle = PvotRowIconStyle.Plain,
        chevron = PvotRowChevron.Arrow,
    )
}

// ───────────────────────────── Progress ─────────────────────────────

@Composable
internal fun PvotProgressPage(onBack: () -> Unit) {
    var position by remember { mutableFloatStateOf(0.35f) }
    var playing by remember { mutableStateOf(false) }
    LaunchedEffect(playing) {
        var last = 0L
        while (playing) {
            withFrameNanos { now ->
                if (last != 0L)
                    position =
                        (position + (now - last) / 20_000_000_000f).let { if (it > 1f) 0f else it }
                last = now
            }
        }
    }
    DetailPage(
        title = "Progress bars",
        summary =
            "A thin bar for how far along something is, which can seek, and a bar split into " +
                "shares whose parts are tonal steps of one hue.",
        onBack = onBack,
        code =
            "PvotLinearProgress(progress = { position }, onSeek = player::seekTo)\n" +
                "PvotProportionBar(segments = listOf(incoming, outgoing, missed))",
    ) {
        DemoSection(
            title = "Seekable",
            caption = "Tap or drag along the bar · ${"%.0f".format(position * 100)}%",
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PvotSpacing.M),
            ) {
                PvotIconButton(
                    icon =
                        painterResource(
                            if (playing) R.drawable.ic_t_player_pause
                            else R.drawable.ic_t_player_play
                        ),
                    contentDescription = if (playing) "Pause" else "Play",
                    onClick = { playing = !playing },
                    size = PvotIconButtonDefaults.SmallSize,
                )
                PvotLinearProgress(
                    progress = { position },
                    onSeek = { position = it },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        DemoSection(title = "Shares", caption = "One hue, four steps") {
            val shares = listOf(0.42f, 0.28f, 0.12f, 0.06f)
            val labels = listOf("Incoming", "Outgoing", "Missed", "Blocked")
            val colors = PvotProportionBarDefaults.tonalColors()
            Column(verticalArrangement = Arrangement.spacedBy(PvotSpacing.M)) {
                PvotProportionBar(segments = shares, colors = colors)
                labels.forEachIndexed { i, label ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PvotProportionBarDefaults.LegendDot(colors.segmentColors[i])
                        Text(
                            text = "$label · ${(shares[i] * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = PvotSpacing.S),
                        )
                    }
                }
            }
        }
    }
}

// ───────────────────────────── Icon buttons and capsule ─────────────────────────────

@Composable
internal fun CapsulePage(onBack: () -> Unit) {
    val send = rememberSnackbarSender()
    var speaker by remember { mutableStateOf(false) }
    var muted by remember { mutableStateOf(true) }
    DetailPage(
        title = "Icon buttons and capsule",
        summary =
            "Round buttons with an icon, grouped on a capsule; as a call's controls with the end " +
                "pill under them, its label inside.",
        onBack = onBack,
        code =
            "PvotCapsule {\n    PvotIconButton(mic, \"Mute\", ::toggleMute, size = PvotIconButtonDefaults.LargeSize,\n" +
                "        checked = muted, colors = PvotIconButtonDefaults.tonalColors(checkedContainerColor = error))\n}\n" +
                "PvotCallActionButton(PvotCallAction.End, phoneOff, \"End\", ::hangUp, labelInside = true)",
    ) {
        DemoSection(title = "Call controls", caption = "Mute turns red while on", framed = false) {
            Box(
                Modifier.fillMaxWidth()
                    .clip(PvotShapes.Card)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(vertical = PvotSpacing.L * 2),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PvotCapsule {
                        val size = PvotIconButtonDefaults.LargeSize
                        PvotIconButton(
                            painterResource(R.drawable.ic_t_volume),
                            "Speaker",
                            { speaker = !speaker },
                            size = size,
                            checked = speaker,
                        )
                        PvotIconButton(
                            painterResource(R.drawable.ic_t_microphone_off),
                            "Mute",
                            { muted = !muted },
                            size = size,
                            checked = muted,
                            colors =
                                PvotIconButtonDefaults.tonalColors(
                                    checkedContainerColor = MaterialTheme.colorScheme.error,
                                    checkedContentColor = MaterialTheme.colorScheme.onError,
                                ),
                        )
                        PvotIconButton(
                            painterResource(R.drawable.ic_t_dialpad),
                            "Keypad",
                            { send("Keypad") },
                            size = size,
                        )
                        PvotIconButton(
                            painterResource(R.drawable.ic_t_dots_vertical),
                            "More",
                            { send("More") },
                            size = size,
                        )
                    }
                    Spacer(Modifier.height(PvotSpacing.L * 3))
                    PvotCallActionButton(
                        PvotCallAction.End,
                        painterResource(R.drawable.ic_t_phone_off),
                        "End",
                        { send("End") },
                        labelInside = true,
                    )
                }
            }
        }
        DemoSection(title = "Sizes and colors") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PvotSpacing.M),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PvotIconButton(
                    painterResource(R.drawable.ic_t_phone),
                    "Call",
                    { send("Call") },
                    colors = PvotIconButtonDefaults.filledColors(),
                )
                PvotIconButton(
                    painterResource(R.drawable.ic_t_user),
                    "Contact",
                    { send("Contact") },
                )
                PvotIconButton(
                    painterResource(R.drawable.ic_t_phone),
                    "Call",
                    { send("Call") },
                    size = PvotIconButtonDefaults.SmallSize,
                )
                PvotIconButton(
                    painterResource(R.drawable.ic_t_phone),
                    "Call",
                    { send("Call") },
                    colors = PvotIconButtonDefaults.plainColors(),
                )
                Spacer(Modifier.width(PvotSpacing.S))
            }
        }
    }
}
