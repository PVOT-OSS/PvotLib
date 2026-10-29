// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.catalog.hub

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.prauga.pvot.catalog.R
import com.prauga.pvot.coreui.PvotEmptyContent
import com.prauga.pvot.coreui.PvotErrorContent
import com.prauga.pvot.coreui.PvotLoadingContent
import com.prauga.pvot.coreui.PvotSectionHeader
import com.prauga.pvot.designsystem.components.PvotCard
import com.prauga.pvot.designsystem.components.PvotSkeleton
import com.prauga.pvot.designsystem.components.picker.PvotClockPicker
import com.prauga.pvot.designsystem.components.picker.PvotDurationPicker
import com.prauga.pvot.designsystem.components.slider.PvotRangeSlider
import com.prauga.pvot.designsystem.components.slider.PvotSlider
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.motion.pvotReveal
import com.prauga.pvot.designsystem.navigation.PvotNavBar
import com.prauga.pvot.designsystem.navigation.PvotNavBarItem
import com.prauga.pvot.designsystem.navigation.PvotTabItem
import com.prauga.pvot.designsystem.theme.PvotSpacing
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Duration.Companion.minutes

// ───────────────────────────── Navigation bar ─────────────────────────────

private val NavTabs = listOf(
    PvotTabItem(R.drawable.ic_home, R.string.tab_home, R.string.cd_home),
    PvotTabItem(R.drawable.ic_apps, R.string.tab_apps, R.string.cd_apps),
    PvotTabItem(R.drawable.ic_catalog, R.string.tab_catalog, R.string.cd_catalog),
    PvotTabItem(R.drawable.ic_about, R.string.tab_about, R.string.cd_about)
)

@Composable
internal fun NavBarPage(onBack: () -> Unit) {
    var compact by remember { mutableStateOf(false) }
    var tab by remember { mutableIntStateOf(0) }
    var slotTab by remember { mutableIntStateOf(0) }
    DetailPage(
        title = "Navigation bar",
        summary = "A floating bar of bare icons; the selected one opens into a pill. Drag a finger " +
            "along the bar to preview items and lift to select. Compact mode shrinks it to icons.",
        onBack = onBack,
        code = "PvotNavBar(\n    selectedTab = selectedTab,\n    onTabClick = { selectedTab = it },\n" +
            "    tabs = tabs,\n    compact = scrolledDown\n)"
    ) {
        DemoSection(title = "Tab list", caption = "Selected: ${stringTab(tab)}") {
            PvotNavBar(
                selectedTab = tab,
                onTabClick = { tab = it },
                tabs = NavTabs,
                compact = compact
            )
            OptionSwitch(label = "Compact", checked = compact, onCheckedChange = { compact = it })
        }
        DemoSection(title = "Slots", caption = "Your own icon and label content in PvotNavBarItem") {
            PvotNavBar(compact = compact) {
                NavTabs.forEachIndexed { index, item ->
                    PvotNavBarItem(
                        selected = index == slotTab,
                        onClick = { slotTab = index },
                        icon = {
                            Icon(
                                painter = painterResource(item.iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text(text = stringTab(index), maxLines = 1) }
                    )
                }
            }
        }
    }
}

private fun stringTab(index: Int) = listOf("Home", "Apps", "Catalog", "About")[index]

// ───────────────────────────── Cards ─────────────────────────────

@Composable
internal fun CardsPage(onBack: () -> Unit) {
    var taps by remember { mutableIntStateOf(0) }
    var expanded by remember { mutableStateOf(false) }
    DetailPage(
        title = "Card",
        summary = "A rounded, translucent surface. Give it onClick and it becomes a button with " +
            "press feedback; leave it out and it is just a container.",
        onBack = onBack,
        code = "PvotCard(onClick = { open() }) {\n    Text(\"Tap me\", Modifier.padding(PvotSpacing.L))\n}"
    ) {
        DemoSection(title = "Static", framed = false) {
            PvotCard(modifier = Modifier.fillMaxWidth()) {
                CardBody("A static card", "No onClick, so no press feedback.")
            }
        }
        DemoSection(title = "Clickable", caption = "Tapped $taps times", framed = false) {
            PvotCard(modifier = Modifier.fillMaxWidth(), onClick = { taps++ }) {
                CardBody("A clickable card", "Press and hold: it squashes on a spring.")
            }
        }
        DemoSection(title = "Expandable", caption = "Content changes size with animateContentSize", framed = false) {
            PvotCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                onClick = { expanded = !expanded }
            ) {
                CardBody(
                    title = if (expanded) "Tap to collapse" else "Tap to expand",
                    body = if (expanded) {
                        "Cards can grow to show more. Keep the motion short and let the neighbours " +
                            "slide with it so the page never jumps."
                    } else {
                        "Hidden detail inside."
                    }
                )
            }
        }
        DemoSection(title = "Outlined", framed = false) {
            PvotCard(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                CardBody("An outlined card", "Pass border for emphasis without a heavier fill.")
            }
        }
        DemoSection(title = "Disabled", framed = false) {
            PvotCard(modifier = Modifier.fillMaxWidth(), onClick = {}, enabled = false) {
                CardBody("A disabled card", "enabled = false mutes it and ignores taps.")
            }
        }
    }
}

@Composable
private fun CardBody(title: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PvotSpacing.L),
        verticalArrangement = Arrangement.spacedBy(PvotSpacing.XS)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        Text(text = body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ───────────────────────────── Skeleton ─────────────────────────────

@Composable
internal fun SkeletonPage(onBack: () -> Unit) {
    var loading by remember { mutableStateOf(true) }
    DetailPage(
        title = "Skeleton",
        summary = "A shimmering placeholder for content that is on its way. Match the shape of what " +
            "will replace it and crossfade, so the page doesn't jump when data lands.",
        onBack = onBack,
        code = "if (loading) {\n    PvotSkeleton(Modifier.fillMaxWidth().height(16.dp))\n} else {\n    Text(title)\n}"
    ) {
        DemoSection(title = "Loading to loaded", caption = "Flip the switch to see the handoff") {
            Crossfade(
                targetState = loading,
                animationSpec = tween(PvotDurations.Medium),
                label = "skeletonHandoff"
            ) { isLoading ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(PvotSpacing.M),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isLoading) {
                        PvotSkeleton(modifier = Modifier.size(48.dp), shape = CircleShape)
                        Column(verticalArrangement = Arrangement.spacedBy(PvotSpacing.S)) {
                            PvotSkeleton(Modifier.fillMaxWidth(0.6f).height(16.dp))
                            PvotSkeleton(Modifier.fillMaxWidth().height(12.dp))
                            PvotSkeleton(Modifier.fillMaxWidth(0.8f).height(12.dp))
                        }
                    } else {
                        IconBadge(icon = R.drawable.ic_t_check, size = 48)
                        Column(verticalArrangement = Arrangement.spacedBy(PvotSpacing.XS)) {
                            Text("Loaded", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                            Text("The real content takes the placeholder's place.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            OptionSwitch(label = "Loading", checked = loading, onCheckedChange = { loading = it })
        }
        DemoSection(title = "Shapes", caption = "The default has 8dp corners; pass shape for avatars and pills") {
            Row(horizontalArrangement = Arrangement.spacedBy(PvotSpacing.M), verticalAlignment = Alignment.CenterVertically) {
                PvotSkeleton(Modifier.size(56.dp), shape = CircleShape)
                PvotSkeleton(Modifier.size(width = 120.dp, height = 56.dp))
                PvotSkeleton(Modifier.size(width = 80.dp, height = 32.dp), shape = CircleShape)
            }
        }
    }
}

// ───────────────────────────── Pickers ─────────────────────────────

@Composable
internal fun PickersPage(onBack: () -> Unit) {
    var time by remember { mutableStateOf(LocalTime.of(7, 30)) }
    var duration by remember { mutableStateOf(25.minutes) }
    DetailPage(
        title = "Pickers",
        summary = "Wheel pickers for a time of day and for a duration. They speak java.time and " +
            "kotlin.time, snap to values and follow the theme.",
        onBack = onBack,
        code = "var time by remember { mutableStateOf(LocalTime.of(7, 30)) }\n" +
            "PvotClockPicker(time = time, onTimeChange = { time = it })"
    ) {
        DemoSection(title = "Clock picker", caption = "Selected ${time.format(DateTimeFormatter.ofPattern("HH:mm"))}") {
            PvotClockPicker(time = time, onTimeChange = { time = it })
            TextButton(onClick = { time = LocalTime.now().withSecond(0).withNano(0) }) { Text("Set to now") }
        }
        DemoSection(
            title = "Duration picker",
            caption = "Selected " + duration.toComponents { h, m, s, _ ->
                String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
            }
        ) {
            PvotDurationPicker(duration = duration, onDurationChange = { duration = it })
            Row(horizontalArrangement = Arrangement.spacedBy(PvotSpacing.S)) {
                listOf(5, 25, 60).forEach { m ->
                    TextButton(onClick = { duration = m.minutes }) { Text("$m min") }
                }
            }
        }
    }
}

// ───────────────────────────── State content ─────────────────────────────

private enum class DemoState(val label: String) { Loading("Loading"), Empty("Empty"), Error("Error"), Success("Loaded") }

@Composable
internal fun StatesPage(onBack: () -> Unit) {
    var state by remember { mutableStateOf(DemoState.Loading) }
    var retries by remember { mutableIntStateOf(0) }
    DetailPage(
        title = "State content",
        summary = "What a screen shows while it loads, when there is nothing to list and when " +
            "something fails. Model it as UiState and switch between the matching components.",
        onBack = onBack,
        code = "when (val s = uiState) {\n    UiState.Loading -> PvotLoadingContent()\n" +
            "    is UiState.Error -> PvotErrorContent(title, s.message, onRetry)\n    is UiState.Success -> Content(s.data)\n}"
    ) {
        DemoSection(title = "UiState", caption = "Pick a state; the content cross-fades") {
            ChoiceChips(DemoState.entries, state, { state = it }, { it.label })
            AnimatedContent(
                targetState = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                transitionSpec = {
                    fadeIn(tween(PvotDurations.Medium, delayMillis = PvotDurations.Short / 2)) togetherWith
                        fadeOut(tween(PvotDurations.Short))
                },
                label = "stateContent"
            ) { shown ->
                Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                    when (shown) {
                        DemoState.Loading -> PvotLoadingContent()
                        DemoState.Empty -> PvotEmptyContent(message = "Nothing here yet")
                        DemoState.Error -> PvotErrorContent(
                            title = "Couldn't load",
                            message = "Check your connection and try again. Retried $retries times.",
                            onRetry = { retries++ }
                        )
                        DemoState.Success -> Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(PvotSpacing.S)
                        ) {
                            repeat(3) { index ->
                                PvotCard(modifier = Modifier.fillMaxWidth().pvotReveal(index)) {
                                    Text(
                                        text = "Item ${index + 1}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(PvotSpacing.L)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        DemoSection(title = "Section header", caption = "Titles a group of items") {
            PvotSectionHeader(modifier = Modifier.fillMaxWidth(), title = "Recent apps")
        }
    }
}

// ───────────────────────────── Slider ─────────────────────────────

@Composable
internal fun SliderPage(onBack: () -> Unit) {
    var volume by remember { mutableFloatStateOf(0.4f) }
    var level by remember { mutableFloatStateOf(3f) }
    var range by remember { mutableStateOf(20f..70f) }
    var brightness by remember { mutableFloatStateOf(0.7f) }
    var enabled by remember { mutableStateOf(true) }
    DetailPage(
        title = "Slider",
        summary = "Pvot's own slider: a slim track and one round thumb. Touch anywhere to jump, " +
            "drag to scrub. It replaces the Material slider everywhere in Pvot.",
        onBack = onBack,
        code = "var value by remember { mutableFloatStateOf(0.4f) }\n" +
            "PvotSlider(value = value, onValueChange = { value = it })\n\n" +
            "PvotSlider(\n    value = level,\n    onValueChange = { level = it },\n" +
            "    valueRange = 0f..6f,\n    steps = 5\n)"
    ) {
        DemoSection(title = "Continuous", caption = "Volume ${(volume * 100).toInt()}%") {
            PvotSlider(value = volume, onValueChange = { volume = it }, enabled = enabled)
            OptionSwitch(label = "Enabled", checked = enabled, onCheckedChange = { enabled = it })
        }
        DemoSection(title = "Stepped", caption = "Level ${level.toInt()} of 6; snaps and ticks on each stop") {
            PvotSlider(
                value = level,
                onValueChange = { level = it },
                valueRange = 0f..6f,
                steps = 5,
                enabled = enabled
            )
        }
        DemoSection(title = "Range", caption = "${range.start.toInt()} to ${range.endInclusive.toInt()}; the thumbs never cross") {
            PvotRangeSlider(
                value = range,
                onValueChange = { range = it },
                valueRange = 0f..100f,
                enabled = enabled
            )
        }
        DemoSection(title = "With icons", caption = "Brightness ${(brightness * 100).toInt()}%") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PvotSpacing.S)
            ) {
                Icon(painterResource(R.drawable.ic_t_eye_off), null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                PvotSlider(
                    value = brightness,
                    onValueChange = { brightness = it },
                    modifier = Modifier.weight(1f),
                    enabled = enabled
                )
                Icon(painterResource(R.drawable.ic_t_eye), null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
