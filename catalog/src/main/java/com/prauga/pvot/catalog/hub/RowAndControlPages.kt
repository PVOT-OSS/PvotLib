// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.catalog.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.prauga.pvot.catalog.R
import com.prauga.pvot.catalog.components.GlassPanel
import com.prauga.pvot.designsystem.components.PvotMenuItem
import com.prauga.pvot.designsystem.components.PvotSegmentedControl
import com.prauga.pvot.designsystem.components.button.PvotButton
import com.prauga.pvot.designsystem.components.button.PvotButtonStyle
import com.prauga.pvot.designsystem.components.button.PvotFab
import com.prauga.pvot.designsystem.components.list.PvotAvatar
import com.prauga.pvot.designsystem.components.list.PvotAvatarDefaults
import com.prauga.pvot.designsystem.components.list.PvotFastScrollSection
import com.prauga.pvot.designsystem.components.list.PvotFastScroller
import com.prauga.pvot.designsystem.components.list.PvotLetterTile
import com.prauga.pvot.designsystem.components.list.PvotListRow
import com.prauga.pvot.designsystem.components.list.PvotPreferenceRow
import com.prauga.pvot.designsystem.components.list.PvotSwipeRow
import com.prauga.pvot.designsystem.components.list.PvotSwipeRowDefaults
import com.prauga.pvot.designsystem.components.list.PvotSwitchRow
import com.prauga.pvot.designsystem.navigation.PvotTopBar
import com.prauga.pvot.designsystem.navigation.PvotTopBarAction
import com.prauga.pvot.designsystem.navigation.rememberPvotTopBarCompact
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing
import com.prauga.pvot.designsystem.theme.PvotTheme
import kotlinx.coroutines.launch

private val People =
    listOf("Ada Lovelace", "Grace Hopper", "Alan Turing", "Katherine Johnson", "Linus Torvalds")

// ───────────────────────────── List row ─────────────────────────────

@Composable
internal fun ListRowPage(onBack: () -> Unit) {
    val send = rememberSnackbarSender()
    val selected = remember { mutableStateListOf<String>() }
    DetailPage(
        title = "List row",
        summary =
            "A row with a leading avatar or letter tile, a title, supporting text and a trailing " +
                "slot. The whole row is the target; long-press is there for selection.",
        onBack = onBack,
        code =
            "PvotListRow(\n    title = contact.name,\n    supportingText = \"Mobile · 2 min ago\",\n" +
                "    leading = { PvotLetterTile(contact.name, colorKey = contact.id) },\n" +
                "    trailing = { Text(\"10:42\") },\n    onClick = { call(contact) },\n    onLongClick = { select(contact) }\n)",
    ) {
        DemoSection(
            title = "Contacts",
            caption = "Tap to open, long-press to select (${selected.size} selected)",
            framed = false,
        ) {
            GlassPanelColumn {
                People.take(3).forEachIndexed { index, name ->
                    val isSelected = name in selected
                    PvotListRow(
                        title = name,
                        supportingText =
                            listOf("Mobile · 2 min ago", "Work · Yesterday", "Home · Monday")[
                                index],
                        leading = {
                            if (isSelected) {
                                Box(
                                    modifier =
                                        Modifier.clip(PvotShapes.Pill)
                                            .background(MaterialTheme.colorScheme.primary)
                                            .padding(PvotSpacing.S)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_t_check),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                    )
                                }
                            } else {
                                PvotLetterTile(name)
                            }
                        },
                        trailing = { Text(listOf("10:42", "Tue", "Mon")[index]) },
                        selected = isSelected,
                        onClick = {
                            if (selected.isNotEmpty()) toggle(selected, name)
                            else send("Open $name")
                        },
                        onLongClick = { toggle(selected, name) },
                    )
                }
            }
        }
        DemoSection(
            title = "Letter tiles",
            caption = "The color follows the name; no letter shows a person",
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(PvotSpacing.S)) {
                listOf("Ada", "Grace", "Émile", "7 Eleven", "+1 555", "").forEach {
                    PvotLetterTile(it)
                }
            }
        }
        DemoSection(
            title = "Avatars",
            caption =
                "A photo or the letter tile, from list row size to the call screen's, with an optional ring and badge",
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PvotSpacing.M),
                verticalAlignment = Alignment.Bottom,
            ) {
                PvotAvatar(name = "Ada Lovelace", size = PvotAvatarDefaults.Small)
                PvotAvatar(
                    name = "Grace Hopper",
                    size = PvotAvatarDefaults.Medium,
                    ring = MaterialTheme.colorScheme.primary,
                )
                PvotAvatar(
                    name = "Alan Turing",
                    size = PvotAvatarDefaults.Large,
                    badge = {
                        Box(
                            modifier =
                                Modifier.clip(PvotShapes.Pill)
                                    .background(MaterialTheme.colorScheme.error)
                                    .padding(PvotSpacing.XS)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_t_phone),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onError,
                                modifier = Modifier.size(PvotSpacing.L),
                            )
                        }
                    },
                )
                PvotAvatar(
                    name = "Katherine Johnson",
                    size = PvotAvatarDefaults.ExtraLarge,
                    ring = PvotTheme.extendedColors.success,
                )
            }
        }
        DemoSection(title = "Static and disabled", framed = false) {
            GlassPanelColumn {
                PvotListRow(
                    title = "Voicemail",
                    supportingText = "No onClick: a static row",
                    leading = { Icon(painterResource(R.drawable.ic_t_message_circle), null) },
                )
                PvotListRow(
                    title = "Blocked numbers",
                    supportingText = "enabled = false dims it and ignores taps",
                    leading = { Icon(painterResource(R.drawable.ic_t_x), null) },
                    onClick = {},
                    enabled = false,
                )
            }
        }
    }
}

private fun toggle(list: MutableList<String>, item: String) {
    if (!list.remove(item)) list.add(item)
}

/** A glass panel holding rows edge to edge, with a little room around them. */
@Composable
private fun GlassPanelColumn(content: @Composable () -> Unit) {
    GlassPanel(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(PvotSpacing.XS)) { content() }
    }
}

// ───────────────────────────── Swipe row ─────────────────────────────

@Composable
internal fun SwipeRowPage(onBack: () -> Unit) {
    val people = remember { mutableStateListOf(*People.toTypedArray()) }
    val recents = remember { mutableStateListOf(*People.toTypedArray()) }
    val snackbar = LocalCatalogSnackbar.current
    val send = rememberSnackbarSender()
    val scope = rememberCoroutineScope()
    fun removeWithUndo(list: MutableList<String>, name: String) {
        val index = list.indexOf(name)
        list.remove(name)
        scope.launch {
            snackbar.currentSnackbarData?.dismiss()
            val result =
                snackbar.showSnackbar(
                    message = "$name deleted",
                    actionLabel = "Undo",
                    duration = SnackbarDuration.Short,
                )
            if (result == SnackbarResult.ActionPerformed && name !in list) {
                list.add(index.coerceAtMost(list.size), name)
            }
        }
    }
    DetailPage(
        title = "Swipe row",
        summary =
            "Swipe toward the end to call, toward the start to delete. Past the threshold the icon " +
                "swells and the phone ticks. Delete slides the row away and offers Undo; Call springs back. " +
                "With end buttons, a swipe toward the start stops open on round buttons instead.",
        onBack = onBack,
        code =
            "key(entry.id) {\n    PvotSwipeRow(\n        startAction = PvotSwipeRowDefaults.action(\"Call\", phone, { call(entry) }),\n" +
                "        endAction = PvotSwipeRowDefaults.destructiveAction(\"Delete\", trash, {\n" +
                "            remove(entry); showUndo(entry)\n        })\n    ) { PvotListRow(entry.name, ...) }\n}\n\n" +
                "PvotSwipeRow(\n    endButtons = listOf(\n        PvotSwipeRowDefaults.action(\"Callback reminder\", clock, {}, menu = reminders),\n" +
                "        PvotSwipeRowDefaults.destructiveAction(\"Delete\", trash, { remove(entry) }),\n    )\n) { ... }",
    ) {
        DemoSection(
            title = "Call log",
            caption = "TalkBack offers Call and Delete as actions on each row",
            framed = false,
        ) {
            people.toList().forEach { name ->
                key(name) {
                    PvotSwipeRow(
                        startAction =
                            PvotSwipeRowDefaults.action(
                                label = "Call",
                                icon = painterResource(R.drawable.ic_t_phone),
                                onSwipe = { send("Calling $name") },
                            ),
                        endAction =
                            PvotSwipeRowDefaults.destructiveAction(
                                label = "Delete",
                                icon = painterResource(R.drawable.ic_t_trash),
                                onSwipe = { removeWithUndo(people, name) },
                            ),
                    ) {
                        PvotListRow(
                            title = name,
                            supportingText = "Mobile · Today",
                            leading = { PvotLetterTile(name) },
                            onClick = { send("Open $name") },
                        )
                    }
                }
            }
            if (people.isEmpty()) {
                PvotButton(
                    text = "Restore all",
                    onClick = { people.addAll(People) },
                    style = PvotButtonStyle.Text,
                )
            }
        }
        DemoSection(
            title = "Recents",
            caption =
                "Swipe toward the start to stand a row open on a callback reminder and Delete; swipe all the way to delete",
            framed = false,
        ) {
            recents.toList().forEach { name ->
                key(name) {
                    PvotSwipeRow(
                        startAction =
                            PvotSwipeRowDefaults.action(
                                label = "Call",
                                icon = painterResource(R.drawable.ic_t_phone),
                                onSwipe = { send("Calling $name") },
                            ),
                        endButtons =
                            listOf(
                                PvotSwipeRowDefaults.action(
                                    label = "Callback reminder",
                                    icon = painterResource(R.drawable.ic_t_history),
                                    onSwipe = {},
                                    menu =
                                        listOf(
                                            PvotMenuItem(
                                                "Remind me in 1 hour",
                                                { send("Reminder for $name in 1 hour") },
                                            ),
                                            PvotMenuItem(
                                                "Remind me tonight",
                                                { send("Reminder for $name tonight") },
                                            ),
                                            PvotMenuItem(
                                                "Remind me tomorrow",
                                                { send("Reminder for $name tomorrow") },
                                            ),
                                            PvotMenuItem(
                                                "Remind me later…",
                                                { send("Pick a time for $name") },
                                            ),
                                        ),
                                ),
                                PvotSwipeRowDefaults.destructiveAction(
                                    label = "Delete",
                                    icon = painterResource(R.drawable.ic_t_trash),
                                    onSwipe = { removeWithUndo(recents, name) },
                                ),
                            ),
                    ) {
                        PvotListRow(
                            title = name,
                            supportingText = "Mobile · Today",
                            leading = { PvotLetterTile(name) },
                            onClick = { send("Calling $name") },
                        )
                    }
                }
            }
            if (recents.isEmpty()) {
                PvotButton(
                    text = "Restore all",
                    onClick = { recents.addAll(People) },
                    style = PvotButtonStyle.Text,
                )
            }
        }
    }
}

// ───────────────────────────── Settings rows ─────────────────────────────

@Composable
internal fun SettingsRowsPage(onBack: () -> Unit) {
    var vibrate by remember { mutableStateOf(true) }
    var dtmf by remember { mutableStateOf(false) }
    val tones = listOf("Default", "Chime", "Pulse", "A much longer ringtone name")
    var tone by remember { mutableIntStateOf(0) }
    DetailPage(
        title = "Settings rows",
        summary =
            "PvotSwitch toggles on a tap, or by holding its thumb, which lifts into a lens, and " +
                "sliding it. PvotSwitchRow puts one at the end of a row that toggles as a whole. " +
                "PvotPreferenceRow opens something, with the current value and a chevron.",
        onBack = onBack,
        code =
            "PvotSwitch(checked = on, onCheckedChange = { on = it })\n" +
                "PvotSwitchRow(\n    title = \"Vibrate for calls\",\n    checked = vibrate,\n    onCheckedChange = { vibrate = it }\n)\n" +
                "PvotPreferenceRow(\n    title = \"Ringtone\",\n    value = tone,\n    onClick = { openPicker() }\n)",
    ) {
        DemoSection(title = "Switch", caption = "Tap, or hold the thumb and slide it") {
            SwitchDemo()
        }
        DemoSection(title = "Switch rows", framed = false) {
            GlassPanelColumn {
                PvotSwitchRow(
                    title = "Vibrate for calls",
                    summary = "Also vibrate when the phone rings",
                    checked = vibrate,
                    onCheckedChange = { vibrate = it },
                    icon = { Icon(painterResource(R.drawable.ic_t_bell), null) },
                )
                PvotSwitchRow(
                    title = "Dialpad tones",
                    checked = dtmf,
                    onCheckedChange = { dtmf = it },
                )
                PvotSwitchRow(
                    title = "Disabled",
                    summary = "enabled = false",
                    checked = true,
                    onCheckedChange = {},
                    enabled = false,
                )
            }
        }
        DemoSection(
            title = "Preferences",
            caption = "Tap Ringtone to cycle its value",
            framed = false,
        ) {
            GlassPanelColumn {
                PvotPreferenceRow(
                    title = "Ringtone",
                    value = tones[tone],
                    onClick = { tone = (tone + 1) % tones.size },
                    icon = { Icon(painterResource(R.drawable.ic_t_wave_sine), null) },
                )
                PvotPreferenceRow(
                    title = "Blocked numbers",
                    summary = "Calls and texts from these numbers are silenced",
                    onClick = {},
                    icon = { Icon(painterResource(R.drawable.ic_t_x), null) },
                )
                PvotPreferenceRow(
                    title = "Without a chevron",
                    value = "Off",
                    showChevron = false,
                    onClick = {},
                )
            }
        }
    }
}

// ───────────────────────────── Button ─────────────────────────────

@Composable
internal fun PvotButtonPage(onBack: () -> Unit) {
    var enabled by remember { mutableStateOf(true) }
    var taps by remember { mutableIntStateOf(0) }
    DetailPage(
        title = "Button",
        summary =
            "A pill button in four styles: one Filled for the main action, Tonal and Text for the " +
                "rest, Destructive for what destroys. At least 48dp tall, squashes under the finger.",
        onBack = onBack,
        code =
            "PvotButton(\n    text = \"Save\",\n    onClick = { save() },\n    style = PvotButtonStyle.Filled\n)",
    ) {
        DemoSection(title = "Styles", caption = "Tapped $taps times") {
            OptionSwitch(label = "Enabled", checked = enabled, onCheckedChange = { enabled = it })
            PvotButtonStyle.entries.forEach { style ->
                PvotButton(
                    text = style.name,
                    onClick = { taps++ },
                    style = style,
                    enabled = enabled,
                )
            }
        }
        DemoSection(title = "With an icon, full width") {
            PvotButton(
                text = "Add contact",
                onClick = { taps++ },
                icon = painterResource(R.drawable.ic_t_plus),
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
            )
            PvotButton(
                text = "Delete call log",
                onClick = { taps++ },
                style = PvotButtonStyle.Destructive,
                icon = painterResource(R.drawable.ic_t_trash),
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ───────────────────────────── Top bar ─────────────────────────────

@Composable
internal fun TopBarPage(onBack: () -> Unit) {
    var showBack by remember { mutableStateOf(true) }
    val send = rememberSnackbarSender()
    DetailPage(
        title = "Top bar",
        summary =
            "Back, title and actions. Expanded, the title sits large under the bar; scroll the " +
                "list and it folds into the bar, which takes a fill of its own, until the list " +
                "is back at the top. In a scaffold the content keeps the expanded bar's padding.",
        onBack = onBack,
        code =
            "val listState = rememberLazyListState()\nPvotTopBar(\n    title = \"Settings\",\n    onBack = ::finish,\n" +
                "    compact = rememberPvotTopBarCompact(listState),\n    actions = {\n" +
                "        PvotTopBarAction(searchIcon, \"Search\", onClick = { search() })\n    }\n)",
    ) {
        DemoSection(title = "Scroll to compact", caption = "Scroll the list inside the frame") {
            OptionSwitch(
                label = "Back button",
                checked = showBack,
                onCheckedChange = { showBack = it },
            )
            val listState = rememberLazyListState()
            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .height(360.dp)
                        .clip(PvotShapes.Card)
                        .background(MaterialTheme.colorScheme.background)
            ) {
                PvotTopBar(
                    title = "Call settings",
                    onBack = if (showBack) ({ send("Back") }) else null,
                    compact = rememberPvotTopBarCompact(listState),
                    windowInsets = WindowInsets(0),
                    actions = {
                        PvotTopBarAction(
                            icon = painterResource(R.drawable.ic_t_search),
                            contentDescription = "Search",
                            onClick = { send("Search") },
                        )
                    },
                )
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    items((1..20).toList()) { index ->
                        PvotListRow(
                            title = "Setting $index",
                            supportingText = "Detail",
                            onClick = {},
                        )
                    }
                }
            }
        }
    }
}

// ───────────────────────────── Segmented control ─────────────────────────────

@Composable
internal fun SegmentedControlPage(onBack: () -> Unit) {
    var filter by remember { mutableIntStateOf(0) }
    var range by remember { mutableIntStateOf(1) }
    DetailPage(
        title = "Segmented control",
        summary =
            "Mutually exclusive choices with a pill that slides to the selected one. Hold it and " +
                "the pill lifts into a lens that follows the finger; let go to pick. Segments " +
                "share the width; picking ticks the haptics.",
        onBack = onBack,
        code =
            "PvotSegmentedControl(\n    options = listOf(\"All\", \"Missed\"),\n    selectedIndex = filter,\n" +
                "    onSelect = { filter = it }\n)",
    ) {
        DemoSection(
            title = "Two segments",
            caption = "Showing ${listOf("all calls", "missed calls")[filter]}",
        ) {
            PvotSegmentedControl(
                options = listOf("All", "Missed"),
                selectedIndex = filter,
                onSelect = { filter = it },
            )
        }
        DemoSection(title = "Three segments") {
            PvotSegmentedControl(
                options = listOf("Day", "Week", "Month"),
                selectedIndex = range,
                onSelect = { range = it },
            )
        }
        DemoSection(title = "Disabled") {
            PvotSegmentedControl(
                options = listOf("Day", "Week", "Month"),
                selectedIndex = range,
                onSelect = {},
                enabled = false,
            )
        }
    }
}

// ───────────────────────────── Fast scroller ─────────────────────────────

private val Directory =
    listOf(
        "Aanya",
        "Abel",
        "Ada",
        "Bea",
        "Bilal",
        "Bruno",
        "Chen",
        "Clara",
        "Dara",
        "Dev",
        "Diego",
        "Elif",
        "Emeka",
        "Farah",
        "Felix",
        "Gita",
        "Hana",
        "Hugo",
        "Ines",
        "Ivan",
        "Jae",
        "Juno",
        "Kai",
        "Kiran",
        "Lena",
        "Luca",
        "Maya",
        "Milo",
        "Nadia",
        "Noor",
        "Omar",
        "Priya",
        "Quinn",
        "Ravi",
        "Rosa",
        "Sana",
        "Sven",
        "Tara",
        "Theo",
        "Uma",
        "Vera",
        "Wen",
        "Xavi",
        "Yara",
        "Zane",
        "Zoe",
    )

@Composable
internal fun FastScrollerPage(onBack: () -> Unit) {
    val send = rememberSnackbarSender()
    DetailPage(
        title = "Fast scroller",
        summary =
            "Jumps through a long list by section. Drag along the end edge: a bubble shows the " +
                "section under the finger and the phone ticks on each new one. It hides when the list fits.",
        onBack = onBack,
        code =
            "Box {\n    LazyColumn(state = listState) { ... }\n    PvotFastScroller(\n" +
                "        state = listState,\n        sections = sections, // PvotFastScrollSection(\"A\", index)\n" +
                "        modifier = Modifier.align(Alignment.CenterEnd)\n    )\n}",
    ) {
        DemoSection(title = "Contacts", caption = "Drag along the right edge of the frame") {
            val listState = rememberLazyListState()
            val names = remember { (Directory + Directory.map { "$it Jr." }).sorted() }
            val sections =
                remember(names) {
                    names
                        .withIndex()
                        .distinctBy { it.value.first() }
                        .map { PvotFastScrollSection(it.value.first().toString(), it.index) }
                }
            Box(
                modifier =
                    Modifier.fillMaxWidth()
                        .height(360.dp)
                        .clip(PvotShapes.Card)
                        .background(MaterialTheme.colorScheme.background)
            ) {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    items(names) { name ->
                        PvotListRow(
                            title = name,
                            leading = { PvotLetterTile(name) },
                            onClick = { send(name) },
                        )
                    }
                }
                PvotFastScroller(
                    state = listState,
                    sections = sections,
                    modifier = Modifier.align(Alignment.CenterEnd),
                )
            }
        }
    }
}

// ───────────────────────────── FAB ─────────────────────────────

@Composable
internal fun FabPage(onBack: () -> Unit) {
    val send = rememberSnackbarSender()
    DetailPage(
        title = "Floating action button",
        summary =
            "The one main action of a screen. With a label it is a pill; set expanded to false, " +
                "for example while the list scrolls, and the label folds into a round button.",
        onBack = onBack,
        code =
            "PvotFab(\n    icon = painterResource(R.drawable.ic_dialpad),\n    contentDescription = \"Dialpad\",\n" +
                "    text = \"Dialpad\",\n    expanded = !listState.isScrollInProgress,\n    onClick = { openDialpad() }\n)",
    ) {
        DemoSection(title = "Round and extended") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PvotSpacing.L),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PvotFab(
                    icon = painterResource(R.drawable.ic_t_plus),
                    contentDescription = "Add",
                    onClick = { send("Add") },
                )
                PvotFab(
                    icon = painterResource(R.drawable.ic_t_phone),
                    contentDescription = null,
                    text = "Call",
                    onClick = { send("Call") },
                )
            }
        }
        DemoSection(title = "Folds while scrolling", caption = "Scroll the list inside the frame") {
            val listState = rememberLazyListState()
            Box(
                modifier =
                    Modifier.fillMaxWidth()
                        .height(320.dp)
                        .clip(PvotShapes.Card)
                        .background(MaterialTheme.colorScheme.background)
            ) {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    items(People + People + People) { name ->
                        PvotListRow(title = name, leading = { PvotLetterTile(name) })
                    }
                }
                PvotFab(
                    icon = painterResource(R.drawable.ic_t_phone),
                    contentDescription = null,
                    text = "Dialpad",
                    expanded = !listState.isScrollInProgress,
                    onClick = { send("Dialpad") },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(PvotSpacing.L),
                )
            }
        }
    }
}

// ───────────────────────────── Snackbar ─────────────────────────────

@Composable
internal fun SnackbarPage(onBack: () -> Unit) {
    val snackbar = LocalCatalogSnackbar.current
    val scope = rememberCoroutineScope()
    var undone by remember { mutableIntStateOf(0) }
    fun show(message: String, action: String? = null, dismiss: Boolean = false) {
        scope.launch {
            snackbar.currentSnackbarData?.dismiss()
            val result =
                snackbar.showSnackbar(
                    message = message,
                    actionLabel = action,
                    withDismissAction = dismiss,
                    duration = if (dismiss) SnackbarDuration.Long else SnackbarDuration.Short,
                )
            if (result == SnackbarResult.ActionPerformed) undone++
        }
    }
    DetailPage(
        title = "Snackbar",
        summary =
            "A brief message at the bottom, with an optional action and close button. The catalog " +
                "shows every message through PvotSnackbarHost.",
        onBack = onBack,
        code =
            "val host = remember { SnackbarHostState() }\nPvotSnackbarHost(host)\n\n" +
                "scope.launch {\n    if (host.showSnackbar(\"Deleted\", actionLabel = \"Undo\") ==\n" +
                "        SnackbarResult.ActionPerformed) restore()\n}",
    ) {
        DemoSection(title = "Show one", caption = "Undo tapped $undone times") {
            PvotButton(
                text = "Message",
                onClick = { show("Call log cleared") },
                style = PvotButtonStyle.Tonal,
            )
            PvotButton(
                text = "With Undo",
                onClick = { show("Contact deleted", action = "Undo") },
                style = PvotButtonStyle.Tonal,
            )
            PvotButton(
                text = "With close button",
                onClick = {
                    show("Blocked numbers are silenced on this phone only", dismiss = true)
                },
                style = PvotButtonStyle.Tonal,
            )
        }
    }
}
