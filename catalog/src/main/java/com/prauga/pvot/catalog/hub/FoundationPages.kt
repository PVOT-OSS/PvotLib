// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.catalog.hub

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.catalog.R
import com.prauga.pvot.designsystem.components.PvotCard
import com.prauga.pvot.designsystem.components.PvotSkeleton
import com.prauga.pvot.designsystem.components.textfield.PvotTextField
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.motion.PvotMotion
import com.prauga.pvot.designsystem.motion.pvotReveal
import com.prauga.pvot.designsystem.theme.PvotDarkColorScheme
import com.prauga.pvot.designsystem.theme.PvotLightColorScheme
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

// ───────────────────────────── Colors ─────────────────────────────

private enum class SchemeMode(val label: String) { System("Current"), Light("Light"), Dark("Dark") }

private class Swatch(val name: String, val color: Color, val on: Color)

private fun swatches(cs: ColorScheme): List<Pair<String, List<Swatch>>> = listOf(
    "Primary" to listOf(
        Swatch("primary", cs.primary, cs.onPrimary),
        Swatch("onPrimary", cs.onPrimary, cs.primary),
        Swatch("primaryContainer", cs.primaryContainer, cs.onPrimaryContainer),
        Swatch("onPrimaryContainer", cs.onPrimaryContainer, cs.primaryContainer)
    ),
    "Secondary" to listOf(
        Swatch("secondary", cs.secondary, cs.onSecondary),
        Swatch("onSecondary", cs.onSecondary, cs.secondary),
        Swatch("secondaryContainer", cs.secondaryContainer, cs.onSecondaryContainer),
        Swatch("onSecondaryContainer", cs.onSecondaryContainer, cs.secondaryContainer)
    ),
    "Tertiary" to listOf(
        Swatch("tertiary", cs.tertiary, cs.onTertiary),
        Swatch("onTertiary", cs.onTertiary, cs.tertiary),
        Swatch("tertiaryContainer", cs.tertiaryContainer, cs.onTertiaryContainer),
        Swatch("onTertiaryContainer", cs.onTertiaryContainer, cs.tertiaryContainer)
    ),
    "Error" to listOf(
        Swatch("error", cs.error, cs.onError),
        Swatch("onError", cs.onError, cs.error),
        Swatch("errorContainer", cs.errorContainer, cs.onErrorContainer),
        Swatch("onErrorContainer", cs.onErrorContainer, cs.errorContainer)
    ),
    "Surfaces" to listOf(
        Swatch("background", cs.background, cs.onBackground),
        Swatch("surface", cs.surface, cs.onSurface),
        Swatch("surfaceVariant", cs.surfaceVariant, cs.onSurfaceVariant),
        Swatch("onSurfaceVariant", cs.onSurfaceVariant, cs.surfaceVariant),
        Swatch("outline", cs.outline, cs.background),
        Swatch("outlineVariant", cs.outlineVariant, cs.onSurface),
        Swatch("inverseSurface", cs.inverseSurface, cs.inverseOnSurface),
        Swatch("inversePrimary", cs.inversePrimary, cs.onSurface)
    )
)

private fun Color.hex(): String = "#%06X".format(toArgb() and 0xFFFFFF)

@Composable
internal fun ColorsPage(onBack: () -> Unit) {
    var mode by remember { mutableStateOf(SchemeMode.System) }
    val copy = rememberCopier()
    val scheme = when (mode) {
        SchemeMode.System -> MaterialTheme.colorScheme
        SchemeMode.Light -> PvotLightColorScheme
        SchemeMode.Dark -> PvotDarkColorScheme
    }
    DetailPage(
        title = "Color",
        summary = "The Pvot palette fills every Material role in a light and a dark arrangement. " +
            "Read colors from MaterialTheme.colorScheme; tap a swatch to copy its hex.",
        onBack = onBack,
        code = "Text(\n    text = \"Hello\",\n    color = MaterialTheme.colorScheme.primary\n)"
    ) {
        DemoSection(title = "Arrangement", framed = false) {
            ChoiceChips(SchemeMode.entries, mode, { mode = it }, { it.label })
        }
        swatches(scheme).forEach { (group, items) ->
            DemoSection(title = group, framed = false) {
                items.chunked(2).forEach { pair ->
                    Row(horizontalArrangement = Arrangement.spacedBy(ItemGap)) {
                        pair.forEach { swatch ->
                            SwatchTile(
                                swatch = swatch,
                                modifier = Modifier.weight(1f),
                                onClick = { copy(swatch.color.hex(), "${swatch.name} ${swatch.color.hex()} copied") }
                            )
                        }
                        if (pair.size == 1) Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun SwatchTile(swatch: Swatch, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .height(76.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(swatch.color)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(PvotSpacing.M),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = swatch.name, style = MaterialTheme.typography.labelMedium, color = swatch.on)
        Text(text = swatch.color.hex(), style = MaterialTheme.typography.labelSmall, color = swatch.on)
    }
}

// ───────────────────────────── Typography ─────────────────────────────

@Composable
internal fun TypographyPage(onBack: () -> Unit) {
    var sample by remember { mutableStateOf("Pvot makes apps feel crafted") }
    val copy = rememberCopier()
    val t = MaterialTheme.typography
    val styles: List<Pair<String, TextStyle>> = listOf(
        "displayLarge" to t.displayLarge, "displayMedium" to t.displayMedium,
        "displaySmall" to t.displaySmall, "headlineLarge" to t.headlineLarge,
        "headlineMedium" to t.headlineMedium, "headlineSmall" to t.headlineSmall,
        "titleLarge" to t.titleLarge, "titleMedium" to t.titleMedium, "titleSmall" to t.titleSmall,
        "bodyLarge" to t.bodyLarge, "bodyMedium" to t.bodyMedium, "bodySmall" to t.bodySmall,
        "labelLarge" to t.labelLarge, "labelMedium" to t.labelMedium, "labelSmall" to t.labelSmall
    )
    DetailPage(
        title = "Typography",
        summary = "PvotTypography changes bodyLarge (16/24sp, 0.5sp tracking); every other style is " +
            "the Material default. Type your own text to preview, tap a style to copy its name.",
        onBack = onBack,
        code = "Text(\n    text = \"Title\",\n    style = MaterialTheme.typography.titleLarge\n)"
    ) {
        PvotTextField(
            value = sample,
            onValueChange = { sample = it },
            modifier = Modifier.fillMaxWidth(),
            label = "Preview text",
            singleLine = true
        )
        DemoSection(title = "Type scale", framed = false) {
            styles.forEach { (name, style) ->
                PvotCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { copy("MaterialTheme.typography.$name", "$name copied") }
                ) {
                    Column(
                        modifier = Modifier.padding(PvotSpacing.L),
                        verticalArrangement = Arrangement.spacedBy(PvotSpacing.XS)
                    ) {
                        Text(
                            text = "$name · ${style.fontSize.value.toInt()}/${style.lineHeight.value.toInt()}sp",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = sample.ifBlank { name },
                            style = style,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

// ───────────────────────────── Shape & spacing ─────────────────────────────

@Composable
internal fun ShapeSpacingPage(onBack: () -> Unit) {
    var radius by remember { mutableFloatStateOf(16f) }
    DetailPage(
        title = "Shape & spacing",
        summary = "Four shape tokens and four spacing steps cover the library. Use PvotSpacing for " +
            "gaps and padding, PvotShapes for corners.",
        onBack = onBack,
        code = "Box(\n    Modifier\n        .padding(PvotSpacing.L)\n        .clip(PvotShapes.Card)\n)"
    ) {
        DemoSection(title = "Shapes", caption = "PvotShapes") {
            val shapes = listOf(
                Triple("Small", PvotShapes.Small, "8dp"),
                Triple("Card", PvotShapes.Card, "16dp"),
                Triple("Selection", PvotShapes.Selection, "20dp"),
                Triple("Pill", PvotShapes.Pill, "circle")
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                shapes.forEach { (name, shape, dp) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(shape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = PvotSpacing.S)
                        )
                        Text(
                            text = dp,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        DemoSection(title = "Corner playground", caption = "Drag to see how a radius feels") {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(radius.dp))
                    .background(MaterialTheme.colorScheme.tertiary)
            )
            OptionSlider(
                label = "Corner radius",
                value = radius,
                onValueChange = { radius = it },
                valueRange = 0f..60f,
                valueText = "${radius.toInt()}dp"
            )
        }
        DemoSection(title = "Spacing", caption = "PvotSpacing") {
            listOf("XS" to PvotSpacing.XS, "S" to PvotSpacing.S, "M" to PvotSpacing.M, "L" to PvotSpacing.L)
                .forEach { (name, size) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.width(32.dp)
                        )
                        Box(
                            modifier = Modifier
                                .width(size * 6)
                                .height(size.coerceAtLeast(8.dp))
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary)
                        )
                        Text(
                            text = "${size.value.toInt()}dp",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = PvotSpacing.M)
                        )
                    }
                }
        }
    }
}

// ───────────────────────────── Motion ─────────────────────────────

@Composable
internal fun MotionPage(onBack: () -> Unit) {
    var durationRun by remember { mutableIntStateOf(0) }
    var curveRun by remember { mutableIntStateOf(0) }
    var springRight by remember { mutableStateOf(false) }
    var revealRun by remember { mutableIntStateOf(0) }
    var taps by remember { mutableIntStateOf(0) }
    DetailPage(
        title = "Motion",
        summary = "Short, eased and a little springy. Replay anything to feel it; the same tokens " +
            "drive this catalog's own page transitions.",
        onBack = onBack,
        code = "Modifier.pvotReveal(index)\nModifier.pvotPressScale(interactionSource)\n" +
            "tween(PvotDurations.Medium, easing = PvotMotion.Settle)"
    ) {
        DemoSection(title = "Durations", caption = "PvotDurations") {
            val items = listOf("Short" to PvotDurations.Short, "Medium" to PvotDurations.Medium, "Long" to PvotDurations.Long)
            items.forEach { (name, ms) ->
                DurationBar(name = name, millis = ms, run = durationRun)
            }
            TextButton(onClick = { durationRun++ }) { Text("Play") }
        }
        DemoSection(title = "Settle easing", caption = "PvotMotion.Settle, expo-out: fast start, long soft landing") {
            EasingCurve(run = curveRun)
            TextButton(onClick = { curveRun++ }) { Text("Replay") }
        }
        DemoSection(title = "Springs", caption = "pressSpring for presses, bounceSpring for playful moves") {
            SpringTrack(label = "pressSpring", toRight = springRight, spec = PvotMotion.pressSpring())
            SpringTrack(label = "bounceSpring", toRight = springRight, spec = PvotMotion.bounceSpring())
            TextButton(onClick = { springRight = !springRight }) { Text("Move") }
        }
        DemoSection(title = "Press feedback", caption = "pvotPressScale: cards squash to 97% on a spring", framed = false) {
            PvotCard(modifier = Modifier.fillMaxWidth(), onClick = { taps++ }) {
                Text(
                    text = "Press and hold me · tapped $taps times",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(PvotSpacing.L)
                )
            }
        }
        DemoSection(title = "Staggered reveal", caption = "pvotReveal: 20ms steps, capped at three") {
            key(revealRun) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(PvotSpacing.S)
                ) {
                    repeat(5) { index ->
                        PvotSkeleton(
                            modifier = Modifier
                                .pvotReveal(index)
                                .fillMaxWidth()
                                .height(16.dp)
                        )
                    }
                }
            }
            TextButton(onClick = { revealRun++ }) { Text("Replay") }
        }
    }
}

@Composable
private fun DurationBar(name: String, millis: Int, run: Int) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(run) {
        if (run > 0) {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(millis, easing = PvotMotion.Settle))
        } else {
            progress.snapTo(1f)
        }
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row {
            Text(
                text = name,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${millis}ms",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .padding(top = PvotSpacing.XS)
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.value)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun EasingCurve(run: Int) {
    val t = remember { Animatable(1f) }
    LaunchedEffect(run) {
        if (run > 0) {
            t.snapTo(0f)
            t.animateTo(1f, tween(900, easing = androidx.compose.animation.core.LinearEasing))
        }
    }
    val line = MaterialTheme.colorScheme.primary
    val guide = MaterialTheme.colorScheme.outlineVariant
    val dot = MaterialTheme.colorScheme.tertiary
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        val pad = 8.dp.toPx()
        val w = size.width - pad * 2
        val h = size.height - pad * 2
        drawLine(guide, Offset(pad, pad + h), Offset(pad + w, pad + h), strokeWidth = 1.dp.toPx())
        drawLine(guide, Offset(pad, pad), Offset(pad, pad + h), strokeWidth = 1.dp.toPx())
        val path = Path()
        val steps = 60
        for (i in 0..steps) {
            val x = i / steps.toFloat()
            val y = PvotMotion.Settle.transform(x)
            val p = Offset(pad + x * w, pad + h - y * h)
            if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
        }
        drawPath(path, line, style = Stroke(width = 3.dp.toPx()))
        val x = t.value
        val y = PvotMotion.Settle.transform(x)
        drawCircle(dot, radius = 7.dp.toPx(), center = Offset(pad + x * w, pad + h - y * h))
    }
}

@Composable
private fun SpringTrack(
    label: String,
    toRight: Boolean,
    spec: androidx.compose.animation.core.SpringSpec<Dp>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        androidx.compose.foundation.layout.BoxWithConstraints(
            modifier = Modifier
                .padding(top = PvotSpacing.XS)
                .fillMaxWidth()
        ) {
            val travel = maxWidth - 32.dp
            val x by animateDpAsState(
                targetValue = if (toRight) travel else 0.dp,
                animationSpec = spec,
                label = "${label}Offset"
            )
            Box(
                modifier = Modifier
                    .offset(x = x)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

// ───────────────────────────── Icons ─────────────────────────────

private class IconEntry(val name: String, @DrawableRes val res: Int)

private val CatalogIcons = listOf(
    IconEntry("home", R.drawable.ic_home), IconEntry("layout-grid", R.drawable.ic_apps),
    IconEntry("components", R.drawable.ic_catalog), IconEntry("info-circle", R.drawable.ic_about),
    IconEntry("brand-github", R.drawable.ic_github), IconEntry("link", R.drawable.ic_link),
    IconEntry("star", R.drawable.ic_star), IconEntry("arrow-left", R.drawable.ic_t_arrow_left),
    IconEntry("chevron-right", R.drawable.ic_t_chevron_right), IconEntry("copy", R.drawable.ic_t_copy),
    IconEntry("search", R.drawable.ic_t_search), IconEntry("check", R.drawable.ic_t_check),
    IconEntry("x", R.drawable.ic_t_x), IconEntry("plus", R.drawable.ic_t_plus),
    IconEntry("heart", R.drawable.ic_t_heart), IconEntry("settings", R.drawable.ic_t_settings),
    IconEntry("palette", R.drawable.ic_t_palette), IconEntry("typography", R.drawable.ic_t_typography),
    IconEntry("shape", R.drawable.ic_t_shape), IconEntry("wave-sine", R.drawable.ic_t_wave_sine),
    IconEntry("eye", R.drawable.ic_t_eye), IconEntry("eye-off", R.drawable.ic_t_eye_off),
    IconEntry("trash", R.drawable.ic_t_trash), IconEntry("bell", R.drawable.ic_t_bell),
    IconEntry("forms", R.drawable.ic_t_forms), IconEntry("click", R.drawable.ic_t_click),
    IconEntry("toggle-left", R.drawable.ic_t_toggle_left), IconEntry("list", R.drawable.ic_t_list),
    IconEntry("layout-navbar", R.drawable.ic_t_layout_navbar), IconEntry("loader-2", R.drawable.ic_t_loader_2),
    IconEntry("message-circle", R.drawable.ic_t_message_circle), IconEntry("icons", R.drawable.ic_t_icons),
    IconEntry("cards", R.drawable.ic_t_cards), IconEntry("rectangle", R.drawable.ic_t_rectangle),
    IconEntry("player-play", R.drawable.ic_t_player_play), IconEntry("arrow-back-up", R.drawable.ic_t_arrow_back_up)
)

@Composable
internal fun IconsPage(onBack: () -> Unit) {
    var size by remember { mutableFloatStateOf(24f) }
    var query by remember { mutableStateOf("") }
    val copy = rememberCopier()
    val shown = CatalogIcons.filter { it.name.contains(query.trim(), ignoreCase = true) }
    DetailPage(
        title = "Icons",
        summary = "The catalog uses Tabler Icons (MIT): a 24dp outline set with a 2dp stroke and " +
            "round caps. Tap one to copy its name.",
        onBack = onBack,
        code = "Icon(\n    painter = painterResource(R.drawable.ic_home),\n    contentDescription = null,\n" +
            "    modifier = Modifier.size(24.dp)\n)"
    ) {
        PvotTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            label = "Filter icons",
            singleLine = true
        )
        DemoSection(title = "Size", framed = false) {
            OptionSlider(
                label = "Icon size",
                value = size,
                onValueChange = { size = it },
                valueRange = 16f..48f,
                valueText = "${size.toInt()}dp"
            )
        }
        DemoSection(title = "${shown.size} icons", framed = false) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PvotSpacing.S),
                verticalArrangement = Arrangement.spacedBy(PvotSpacing.S)
            ) {
                shown.forEach { icon ->
                    Column(
                        modifier = Modifier
                            .width(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable { copy(icon.name, "${icon.name} copied") }
                            .padding(vertical = PvotSpacing.M, horizontal = PvotSpacing.XS),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(PvotSpacing.S)
                    ) {
                        Box(modifier = Modifier.height(48.dp), contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(icon.res),
                                contentDescription = icon.name,
                                modifier = Modifier.size(size.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = icon.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
