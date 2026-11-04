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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.prauga.pvot.catalog.R
import com.prauga.pvot.designsystem.components.camera.PvotAdjustmentSheet
import com.prauga.pvot.designsystem.components.camera.PvotChoiceRow
import com.prauga.pvot.designsystem.components.camera.PvotControlPanel
import com.prauga.pvot.designsystem.components.camera.PvotControlTile
import com.prauga.pvot.designsystem.components.camera.PvotGlassChip
import com.prauga.pvot.designsystem.components.camera.PvotModeSelector
import com.prauga.pvot.designsystem.components.camera.PvotRuler
import com.prauga.pvot.designsystem.components.camera.PvotShutterButton
import com.prauga.pvot.designsystem.components.camera.PvotShutterState
import com.prauga.pvot.designsystem.components.camera.PvotZoomDial
import com.prauga.pvot.designsystem.components.camera.PvotZoomSelector
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.pvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.rememberPvotGlassBackdrop
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing

@Composable
internal fun CameraPage(onBack: () -> Unit) {
    val send = rememberSnackbarSender()
    var mode by remember { mutableIntStateOf(1) }
    var lens by remember { mutableIntStateOf(1) }
    var zoom by remember { mutableFloatStateOf(1f) }
    var adjusting by remember { mutableStateOf(false) }
    var recording by remember { mutableStateOf(false) }
    var flash by remember { mutableStateOf(false) }
    var timer by remember { mutableIntStateOf(0) }
    var exposure by remember { mutableFloatStateOf(0.5f) }

    DetailPage(
        title = "Camera",
        summary =
            "Controls that float over a live camera preview: glass chips, a shutter with hold to " +
                "record, a lens row with a zoom dial, a mode selector, a control panel and " +
                "adjustment sheets.",
        onBack = onBack,
        code =
            "PvotShutterButton(PvotShutterState.Photo, onClick = { takePhoto() }, " +
                "contentDescription = \"Shutter\", onHold = { startVideo() })\n" +
                "PvotZoomSelector(listOf(\".5\", \"1\", \"2\"), selected, \"1x\", onSelect = { pick(it) })\n" +
                "PvotModeSelector(listOf(\"VIDEO\", \"PHOTO\"), selected, onSelect = { setMode(it) })",
    ) {
        DemoSection(title = "Chips", caption = "Format, status and timer", framed = false) {
            Scene { backdrop ->
                Row(
                    modifier = Modifier.padding(PvotSpacing.L),
                    horizontalArrangement = Arrangement.spacedBy(PvotSpacing.S),
                ) {
                    PvotGlassChip("ULTRA HDR", backdrop = backdrop, onClick = { send("Format") })
                    PvotGlassChip(
                        "00:00:10",
                        backdrop = backdrop,
                        leading = {
                            Box(Modifier.size(7.dp).clip(CircleShape).background(Color(0xFFFF3B30)))
                        },
                    )
                }
            }
        }

        DemoSection(
            title = "Shutter",
            caption = "Tap, or hold to record and slide right to lock",
            framed = false,
        ) {
            Scene { backdrop ->
                Box(
                    Modifier.fillMaxWidth().padding(PvotSpacing.L),
                    contentAlignment = Alignment.Center,
                ) {
                    PvotShutterButton(
                        state =
                            if (recording) PvotShutterState.Recording else PvotShutterState.Photo,
                        onClick = { if (recording) recording = false else send("Photo taken") },
                        contentDescription = "Shutter",
                        backdrop = backdrop,
                        holding = false,
                        onHold = { recording = true },
                        onHoldRelease = { recording = false },
                        onSlideStart = { send("Burst") },
                    )
                }
            }
        }

        DemoSection(
            title = "Lenses and zoom",
            caption = "Hold or drag the row for the dial",
            framed = false,
        ) {
            Scene { backdrop ->
                Column(
                    modifier = Modifier.fillMaxWidth().padding(PvotSpacing.L),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (adjusting) {
                        PvotZoomDial(
                            value = zoom,
                            range = 0.5f..25f,
                            label = "%.1f×".format(zoom),
                            backdrop = backdrop,
                            markLabel = { if (it < 1f) ".5" else "%.0f".format(it) },
                        )
                        Spacer(Modifier.height(PvotSpacing.M))
                    }
                    PvotZoomSelector(
                        labels = listOf(".5", "1", "2", "5"),
                        selectedIndex = lens,
                        selectedLabel = "%.1fx".format(zoom).replace(".0x", "x"),
                        onSelect = {
                            lens = it
                            zoom = listOf(0.5f, 1f, 2f, 5f)[it]
                        },
                        backdrop = backdrop,
                        onAdjustStart = { adjusting = true },
                        onAdjust = { zoom = (zoom * it).coerceIn(0.5f, 25f) },
                        onAdjustEnd = { adjusting = false },
                    )
                }
            }
        }

        DemoSection(title = "Modes", framed = false) {
            Scene { backdrop ->
                Box(
                    Modifier.fillMaxWidth().padding(PvotSpacing.L),
                    contentAlignment = Alignment.Center,
                ) {
                    PvotModeSelector(
                        modes = listOf("VIDEO", "PHOTO", "SCAN"),
                        selectedIndex = mode,
                        onSelect = { mode = it },
                        backdrop = backdrop,
                    )
                }
            }
        }

        DemoSection(title = "Control panel", framed = false) {
            Scene { backdrop ->
                PvotControlPanel(modifier = Modifier.padding(PvotSpacing.S), backdrop = backdrop) {
                    PvotControlTile("FLASH", onClick = { flash = !flash }, active = flash) {
                        Icon(painterResource(R.drawable.ic_t_bell), contentDescription = null)
                    }
                    PvotControlTile(
                        "TIMER",
                        onClick = { timer = (timer + 1) % 3 },
                        active = timer != 0,
                        badge = listOf(null, "3s", "10s")[timer],
                    ) {
                        Icon(painterResource(R.drawable.ic_t_history), contentDescription = null)
                    }
                    PvotControlTile("EXPOSURE", onClick = { exposure = 0.5f }) {
                        Icon(
                            painterResource(R.drawable.ic_t_adjustments),
                            contentDescription = null,
                        )
                    }
                }
            }
        }

        DemoSection(title = "Adjustment sheets", framed = false) {
            Scene { backdrop ->
                Column(
                    modifier = Modifier.padding(PvotSpacing.S),
                    verticalArrangement = Arrangement.spacedBy(PvotSpacing.S),
                ) {
                    PvotAdjustmentSheet(
                        title = "EXPOSURE",
                        onClose = { exposure = 0.5f },
                        closeContentDescription = "Close",
                        modifier = Modifier.fillMaxWidth(),
                        backdrop = backdrop,
                        closeIcon = {
                            Icon(painterResource(R.drawable.ic_t_x), contentDescription = null)
                        },
                    ) {
                        PvotRuler(
                            value = exposure,
                            onValueChange = { exposure = it },
                            modifier = Modifier.padding(horizontal = 36.dp),
                            steps = 12,
                            startLabel = "−2",
                            endLabel = "+2",
                        )
                    }
                    PvotAdjustmentSheet(
                        title = "TIMER",
                        onClose = {},
                        closeContentDescription = "Close",
                        modifier = Modifier.fillMaxWidth(),
                        backdrop = backdrop,
                        closeIcon = {
                            Icon(painterResource(R.drawable.ic_t_x), contentDescription = null)
                        },
                    ) {
                        PvotChoiceRow(listOf("Off", "3s", "10s"), timer, onSelect = { timer = it })
                    }
                }
            }
        }
    }
}

@Composable
private fun Scene(content: @Composable (PvotGlassBackdrop) -> Unit) {
    val backdrop = rememberPvotGlassBackdrop()
    Box(modifier = Modifier.fillMaxWidth().clip(PvotShapes.Card)) {
        Box(
            modifier =
                Modifier.matchParentSize()
                    .pvotGlassBackdrop(backdrop)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF2F5D3A), Color(0xFF8DB255), Color(0xFFE2C35A))
                        )
                    )
        )
        content(backdrop)
    }
}
