// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.prauga.pvot.catalog.hub

import com.prauga.pvot.catalog.components.GlassPanel
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ListItemDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.prauga.pvot.catalog.R
import com.prauga.pvot.designsystem.components.PvotCard
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.theme.PvotSpacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val MaterialNote = "Material 3, styled by PvotAppTheme. A Pvot version is on the roadmap."

// ───────────────────────────── Buttons ─────────────────────────────

@Composable
internal fun ButtonsPage(onBack: () -> Unit) {
    var enabled by remember { mutableStateOf(true) }
    var loading by remember { mutableStateOf(false) }
    var likes by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    DetailPage(
        title = "Buttons",
        summary = MaterialNote,
        onBack = onBack,
        code = "Button(onClick = { save() }) { Text(\"Save\") }\nOutlinedButton(onClick = { cancel() }) { Text(\"Cancel\") }"
    ) {
        DemoSection(title = "Emphasis", caption = "From most to least prominent") {
            Button(onClick = {}, enabled = enabled, modifier = Modifier.fillMaxWidth()) { Text("Filled") }
            FilledTonalButton(onClick = {}, enabled = enabled, modifier = Modifier.fillMaxWidth()) { Text("Tonal") }
            ElevatedButton(onClick = {}, enabled = enabled, modifier = Modifier.fillMaxWidth()) { Text("Elevated") }
            OutlinedButton(onClick = {}, enabled = enabled, modifier = Modifier.fillMaxWidth()) { Text("Outlined") }
            TextButton(onClick = {}, enabled = enabled, modifier = Modifier.fillMaxWidth()) { Text("Text") }
            OptionSwitch(label = "Enabled", checked = enabled, onCheckedChange = { enabled = it })
        }
        DemoSection(title = "Loading", caption = "The label swaps for a spinner and the button holds its size") {
            Button(
                onClick = {
                    loading = true
                    scope.launch { delay(1600); loading = false }
                },
                enabled = !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(PvotSpacing.S))
                    Text("Saving")
                } else {
                    Text("Save changes")
                }
            }
        }
        DemoSection(title = "Icon buttons", caption = "Liked $likes times") {
            Row(horizontalArrangement = Arrangement.spacedBy(PvotSpacing.M), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { likes++ }) { Icon(painterResource(R.drawable.ic_t_heart), "Like") }
                FilledIconButton(onClick = { likes++ }) { Icon(painterResource(R.drawable.ic_t_heart), "Like") }
                OutlinedIconButton(onClick = { likes++ }) { Icon(painterResource(R.drawable.ic_t_heart), "Like") }
            }
        }
        DemoSection(title = "Floating action buttons") {
            Row(horizontalArrangement = Arrangement.spacedBy(PvotSpacing.L), verticalAlignment = Alignment.CenterVertically) {
                FloatingActionButton(onClick = {}) { Icon(painterResource(R.drawable.ic_t_plus), "Add") }
                ExtendedFloatingActionButton(
                    onClick = {},
                    icon = { Icon(painterResource(R.drawable.ic_t_plus), null) },
                    text = { Text("New") }
                )
            }
        }
    }
}

// ───────────────────────────── Selection controls ─────────────────────────────

@Composable
internal fun SelectionPage(onBack: () -> Unit) {
    var wifi by remember { mutableStateOf(true) }
    var bluetooth by remember { mutableStateOf(false) }
    var terms by remember { mutableStateOf(false) }
    var plan by remember { mutableIntStateOf(1) }
    val plans = listOf("Free", "Plus", "Pro")
    DetailPage(
        title = "Selection controls",
        summary = MaterialNote + " Sliders have their own page: Pvot ships its own.",
        onBack = onBack,
        code = "Switch(checked = on, onCheckedChange = { on = it })\nCheckbox(checked = ok, onCheckedChange = { ok = it })"
    ) {
        DemoSection(title = "Switches") {
            OptionSwitch(label = "Wi-Fi", checked = wifi, onCheckedChange = { wifi = it })
            OptionSwitch(label = "Bluetooth", checked = bluetooth, onCheckedChange = { bluetooth = it })
            OptionSwitch(label = "Disabled", checked = true, onCheckedChange = {}, enabled = false)
        }
        DemoSection(title = "Checkbox", caption = "The whole row is the target") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(selected = terms, role = Role.Checkbox, onClick = { terms = !terms }),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = terms, onCheckedChange = null)
                Text(
                    text = "I accept the terms",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = PvotSpacing.M)
                )
            }
        }
        DemoSection(title = "Radio buttons") {
            Column(modifier = Modifier.fillMaxWidth().selectableGroup()) {
                plans.forEachIndexed { index, name ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .selectable(selected = plan == index, role = Role.RadioButton, onClick = { plan = index }),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = plan == index, onClick = null)
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = PvotSpacing.M)
                        )
                    }
                }
            }
        }
    }
}

// ───────────────────────────── Chips ─────────────────────────────

@Composable
internal fun ChipsPage(onBack: () -> Unit) {
    val topics = listOf("Design", "Android", "Compose", "Motion", "Icons")
    var picked by remember { mutableStateOf(setOf("Design")) }
    val tags = remember { mutableStateListOf("kotlin", "material", "pvot") }
    val send = rememberSnackbarSender()
    DetailPage(
        title = "Chips",
        summary = MaterialNote,
        onBack = onBack,
        code = "FilterChip(\n    selected = selected,\n    onClick = { selected = !selected },\n    label = { Text(\"Design\") }\n)"
    ) {
        DemoSection(title = "Filter", caption = "Pick any number") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(PvotSpacing.S), verticalArrangement = Arrangement.spacedBy(PvotSpacing.S)) {
                topics.forEach { topic ->
                    FilterChip(
                        selected = topic in picked,
                        onClick = { picked = if (topic in picked) picked - topic else picked + topic },
                        label = { Text(topic) },
                        leadingIcon = if (topic in picked) {
                            { Icon(painterResource(R.drawable.ic_t_check), null, Modifier.size(18.dp)) }
                        } else null,
                        shape = CircleShape
                    )
                }
            }
        }
        DemoSection(title = "Input", caption = "Tap the x to remove; they animate out") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(PvotSpacing.S), verticalArrangement = Arrangement.spacedBy(PvotSpacing.S)) {
                tags.toList().forEach { tag ->
                    InputChip(
                        selected = false,
                        onClick = { tags.remove(tag) },
                        label = { Text(tag) },
                        trailingIcon = { Icon(painterResource(R.drawable.ic_t_x), "Remove $tag", Modifier.size(18.dp)) },
                        shape = CircleShape
                    )
                }
                if (tags.isEmpty()) {
                    TextButton(onClick = { tags.addAll(listOf("kotlin", "material", "pvot")) }) { Text("Reset") }
                }
            }
        }
        DemoSection(title = "Assist and suggestion") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(PvotSpacing.S), verticalArrangement = Arrangement.spacedBy(PvotSpacing.S)) {
                AssistChip(
                    onClick = { send("Added to favorites") },
                    label = { Text("Favorite") },
                    leadingIcon = { Icon(painterResource(R.drawable.ic_t_heart), null, Modifier.size(18.dp)) },
                    shape = CircleShape
                )
                SuggestionChip(onClick = { send("Searching Compose") }, label = { Text("Compose") }, shape = CircleShape)
                SuggestionChip(onClick = { send("Searching Motion") }, label = { Text("Motion") }, shape = CircleShape)
            }
        }
    }
}

// ───────────────────────────── Progress ─────────────────────────────

@Composable
internal fun ProgressPage(onBack: () -> Unit) {
    var progress by remember { mutableFloatStateOf(0.35f) }
    val animated by animateFloatAsState(progress, tween(PvotDurations.Long), label = "progress")
    DetailPage(
        title = "Progress",
        summary = MaterialNote,
        onBack = onBack,
        code = "LinearProgressIndicator(progress = { value })\nCircularProgressIndicator()"
    ) {
        DemoSection(title = "Determinate", caption = "${(progress * 100).toInt()}%, eased between steps") {
            LinearProgressIndicator(progress = { animated }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(PvotSpacing.S)) {
                TextButton(onClick = { progress = (progress - 0.2f).coerceAtLeast(0f) }) { Text("Less") }
                TextButton(onClick = { progress = (progress + 0.2f).coerceAtMost(1f) }) { Text("More") }
                TextButton(onClick = { progress = 0f }) { Text("Reset") }
            }
        }
        DemoSection(title = "Indeterminate", caption = "When the wait has no known length") {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            CircularProgressIndicator()
        }
        DemoSection(title = "Badges", caption = "A count or a dot on any icon") {
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp), verticalAlignment = Alignment.CenterVertically) {
                BadgedBox(badge = { Badge { Text("3") } }) {
                    Icon(painterResource(R.drawable.ic_t_bell), "Notifications")
                }
                BadgedBox(badge = { Badge() }) {
                    Icon(painterResource(R.drawable.ic_t_message_circle), "Messages")
                }
            }
        }
    }
}

// ───────────────────────────── Lists ─────────────────────────────

@Composable
internal fun ListsPage(onBack: () -> Unit) {
    val items = remember { mutableStateListOf("Alarm", "Timer", "Stopwatch", "World clock") }
    var notify by remember { mutableStateOf(true) }
    val snackbar = LocalCatalogSnackbar.current
    val scope = rememberCoroutineScope()
    DetailPage(
        title = "Lists",
        summary = MaterialNote,
        onBack = onBack,
        code = "ListItem(\n    headlineContent = { Text(\"Title\") },\n    supportingContent = { Text(\"Detail\") },\n    trailingContent = { Switch(...) }\n)"
    ) {
        DemoSection(title = "List items", framed = false) {
            GlassPanel(modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text("Notifications") },
                    supportingContent = { Text("Banners and sounds") },
                    leadingContent = { Icon(painterResource(R.drawable.ic_t_bell), null) },
                    trailingContent = { Switch(checked = notify, onCheckedChange = { notify = it }) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = PvotSpacing.L))
                ListItem(
                    headlineContent = { Text("Settings") },
                    leadingContent = { Icon(painterResource(R.drawable.ic_t_settings), null) },
                    trailingContent = { Icon(painterResource(R.drawable.ic_t_chevron_right), null) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
        DemoSection(title = "Swipe to remove", caption = "Swipe a row sideways; Undo brings it back", framed = false) {
            items.toList().forEach { item ->
                key(item) {
                    val state = rememberSwipeToDismissBoxState()
                    LaunchedEffect(state.currentValue) {
                        if (state.currentValue != SwipeToDismissBoxValue.Settled) {
                            val index = items.indexOf(item)
                            items.remove(item)
                            scope.launch {
                                snackbar.currentSnackbarData?.dismiss()
                                val result = snackbar.showSnackbar(
                                    message = "$item removed",
                                    actionLabel = "Undo"
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    items.add(index.coerceAtMost(items.size), item)
                                }
                            }
                        }
                    }
                    SwipeToDismissBox(
                        state = state,
                        modifier = Modifier.animateContentSize(),
                        backgroundContent = {
                            // Only while swiping: the card in front is translucent.
                            if (state.dismissDirection != SwipeToDismissBoxValue.Settled) Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(16.dp))
                                    .padding(horizontal = PvotSpacing.L),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(painterResource(R.drawable.ic_t_trash), "Remove", tint = MaterialTheme.colorScheme.onErrorContainer)
                            }
                        }
                    ) {
                        GlassPanel(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .padding(horizontal = PvotSpacing.L),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
            if (items.isEmpty()) {
                TextButton(onClick = { items.addAll(listOf("Alarm", "Timer", "Stopwatch", "World clock")) }) { Text("Restore all") }
            }
        }
    }
}

// ───────────────────────────── Tabs & segmented ─────────────────────────────

@Composable
internal fun TabsPage(onBack: () -> Unit) {
    var tab by remember { mutableIntStateOf(0) }
    var segment by remember { mutableIntStateOf(1) }
    val tabs = listOf("Overview", "Specs", "Reviews")
    val segments = listOf("Day", "Week", "Month")
    DetailPage(
        title = "Tabs & segmented",
        summary = MaterialNote,
        onBack = onBack,
        code = "PrimaryTabRow(selectedTabIndex = tab) {\n    tabs.forEachIndexed { i, t ->\n        Tab(selected = tab == i, onClick = { tab = i }, text = { Text(t) })\n    }\n}"
    ) {
        DemoSection(title = "Tabs", caption = "The indicator slides and the content cross-fades") {
            PrimaryTabRow(selectedTabIndex = tab, containerColor = Color.Transparent) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = tab == index, onClick = { tab = index }, text = { Text(title) })
                }
            }
            AnimatedContent(
                targetState = tab,
                modifier = Modifier.fillMaxWidth().height(72.dp),
                transitionSpec = { fadeIn(tween(PvotDurations.Medium)) togetherWith fadeOut(tween(PvotDurations.Short)) },
                label = "tabContent"
            ) { shown ->
                Text(
                    text = "${tabs[shown]} content",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = PvotSpacing.L)
                )
            }
        }
        DemoSection(title = "Segmented buttons", caption = "Choose exactly one, ${segments[segment]} selected") {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                segments.forEachIndexed { index, label ->
                    SegmentedButton(
                        selected = segment == index,
                        onClick = { segment = index },
                        shape = SegmentedButtonDefaults.itemShape(index, segments.size),
                        label = { Text(label) }
                    )
                }
            }
        }
    }
}
