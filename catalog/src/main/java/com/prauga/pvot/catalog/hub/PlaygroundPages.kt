// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.catalog.hub

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.prauga.pvot.catalog.R
import com.prauga.pvot.designsystem.components.PvotGlassSurface
import com.prauga.pvot.designsystem.components.PvotMenu
import com.prauga.pvot.designsystem.components.PvotMenuAlign
import com.prauga.pvot.designsystem.components.PvotMenuItem
import com.prauga.pvot.designsystem.components.PvotMenuSeparator
import com.prauga.pvot.designsystem.components.PvotSwitch
import com.prauga.pvot.designsystem.components.background.PvotLiveBackground
import com.prauga.pvot.designsystem.effects.PvotGlassDefaults
import com.prauga.pvot.designsystem.effects.PvotGlassStyle
import com.prauga.pvot.designsystem.effects.pvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.pvotGlassPress
import com.prauga.pvot.designsystem.effects.rememberPvotGlassBackdrop
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing
import kotlin.math.roundToInt

private enum class GlassPreset(val label: String, val style: PvotGlassStyle) {
    Clear("Clear", PvotGlassDefaults.ClearStyle),
    Regular("Regular", PvotGlassDefaults.RegularStyle),
    Thick("Thick", PvotGlassDefaults.ThickStyle),
    Lens("Lens", PvotGlassDefaults.LensStyle),
}

@Composable
internal fun GlassPlaygroundPage(onBack: () -> Unit) {
    var bend by remember { mutableFloatStateOf(PvotGlassDefaults.Style.strength.value) }
    var dispersion by remember { mutableFloatStateOf(PvotGlassDefaults.Style.aberration.value) }
    var frost by remember { mutableFloatStateOf(PvotGlassDefaults.Style.blur.value) }
    var rim by remember { mutableFloatStateOf(PvotGlassDefaults.Style.glow) }
    var dome by remember { mutableFloatStateOf(PvotGlassDefaults.Style.dome.value) }
    var saturation by remember { mutableFloatStateOf(PvotGlassDefaults.Style.saturation) }
    var rimLine by remember { mutableStateOf(true) }
    var preset by remember { mutableStateOf(GlassPreset.Clear) }
    val style =
        PvotGlassDefaults.Style.copy(
            strength = bend.dp,
            aberration = dispersion.dp,
            blur = frost.dp,
            glow = rim,
            dome = dome.dp,
            saturation = saturation,
        )
    DetailPage(
        title = "Glass playground",
        summary =
            "The knobs of a glass: how far the edge bends what is behind it, how far its colors " +
                "split, how frosted it is, how bright its rim is, how much the middle swells, and " +
                "how vivid what shows through is. Drag the pane over the background.",
        onBack = onBack,
        code =
            "PvotGlassStyle(\n    strength = ${bend.roundToInt()}.dp,\n" +
                "    aberration = ${dispersion.roundToInt()}.dp,\n" +
                "    blur = ${frost.roundToInt()}.dp,\n" +
                "    glow = ${"%.2f".format(rim)}f,\n" +
                "    dome = ${dome.roundToInt()}.dp,\n" +
                "    saturation = ${"%.1f".format(saturation)}f,\n)",
    ) {
        DemoSection(title = "Pane", caption = "Press it, drag it over the labels", framed = false) {
            val backdrop = rememberPvotGlassBackdrop()
            var offset by remember { mutableStateOf(Offset.Zero) }
            Box(modifier = Modifier.fillMaxWidth().height(280.dp).clip(PvotShapes.Card)) {
                Box(modifier = Modifier.fillMaxSize().pvotGlassBackdrop(backdrop)) {
                    PvotLiveBackground(Modifier.fillMaxSize())
                    Column(
                        modifier = Modifier.fillMaxSize().padding(PvotSpacing.L),
                        verticalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        repeat(4) { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                repeat(3) { column ->
                                    val icon =
                                        listOf(
                                            R.drawable.ic_t_heart,
                                            R.drawable.ic_star,
                                            R.drawable.ic_t_bell,
                                        )[(row + column) % 3]
                                    Icon(
                                        painter = painterResource(icon),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = listOf("Bend", "Split", "Frost", "Rim")[row],
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            }
                        }
                    }
                }
                PvotGlassSurface(
                    backdrop = backdrop,
                    modifier =
                        Modifier.align(Alignment.Center)
                            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
                            .size(170.dp, 110.dp)
                            .pvotGlassPress(cornerRadius = 28.dp)
                            .pointerInput(Unit) {
                                detectDragGestures { change, drag ->
                                    change.consume()
                                    offset += drag
                                }
                            },
                    cornerRadius = 28.dp,
                    style = style,
                    rim = rimLine,
                ) {
                    Text(
                        text = "Glass",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
        DemoSection(title = "Presets", framed = false) {
            ChoiceChips(
                options = GlassPreset.entries,
                selected = preset,
                onSelect = {
                    preset = it
                    bend = it.style.strength.value
                    dispersion = it.style.aberration.value
                    frost = it.style.blur.value
                    rim = it.style.glow
                    dome = it.style.dome.value
                    saturation = it.style.saturation
                },
                label = { it.label },
            )
        }
        DemoSection(title = "Knobs") {
            OptionSlider("Bend", bend, { bend = it }, 0f..24f, "${bend.roundToInt()} dp")
            OptionSlider(
                "Dispersion",
                dispersion,
                { dispersion = it },
                0f..8f,
                "${dispersion.roundToInt()} dp",
            )
            OptionSlider("Frost", frost, { frost = it }, 0f..18f, "${frost.roundToInt()} dp")
            OptionSlider("Rim", rim, { rim = it }, 0f..0.6f, "%.2f".format(rim))
            OptionSlider("Dome", dome, { dome = it }, 0f..12f, "${dome.roundToInt()} dp")
            OptionSlider(
                "Saturation",
                saturation,
                { saturation = it },
                1f..2f,
                "%.1f".format(saturation),
            )
            OptionSwitch(label = "Rim line", checked = rimLine, onCheckedChange = { rimLine = it })
        }
    }
}

@Composable
internal fun MenuPage(onBack: () -> Unit) {
    val send = rememberSnackbarSender()
    val entries =
        listOf(
            PvotMenuItem("Copy", onClick = { send("Copied") }, icon = R.drawable.ic_t_copy),
            PvotMenuItem(
                "Add to favorites",
                onClick = { send("Added") },
                icon = R.drawable.ic_t_heart,
            ),
            PvotMenuItem("Share", onClick = { send("Shared") }, icon = R.drawable.ic_link),
            PvotMenuSeparator,
            PvotMenuItem(
                "Delete",
                onClick = { send("Deleted") },
                icon = R.drawable.ic_t_trash,
                destructive = true,
            ),
        )
    DetailPage(
        title = "Menu",
        summary =
            "A round button that opens a menu grown out of it. Tap an item, or press the button " +
                "and slide onto an item without lifting the finger to pick it in one move.",
        onBack = onBack,
        code =
            "PvotMenu(\n    contentDescription = \"More options\",\n    entries = listOf(\n" +
                "        PvotMenuItem(\"Copy\", onClick = { copy() }, icon = R.drawable.ic_t_copy),\n" +
                "        PvotMenuSeparator,\n" +
                "        PvotMenuItem(\"Delete\", onClick = { delete() }, destructive = true),\n" +
                "    ),\n)",
    ) {
        DemoSection(
            title = "On glass",
            caption = "Opens along the end edge of the button; press and slide to pick",
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                PvotMenu(entries = entries, contentDescription = "More options")
            }
        }
        DemoSection(title = "Tonal, from the start edge", caption = "Without a backdrop") {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                PvotMenu(
                    entries = entries,
                    contentDescription = "More options",
                    align = PvotMenuAlign.Start,
                    backdrop = null,
                )
            }
        }
        DemoSection(title = "Disabled") {
            PvotMenu(entries = entries, contentDescription = "More options", enabled = false)
        }
    }
}

@Composable
internal fun SwitchDemo() {
    var wifi by remember { mutableStateOf(true) }
    var bluetooth by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PvotSwitch(checked = wifi, onCheckedChange = { wifi = it })
        PvotSwitch(checked = bluetooth, onCheckedChange = { bluetooth = it })
        PvotSwitch(checked = true, onCheckedChange = {}, enabled = false)
    }
}
