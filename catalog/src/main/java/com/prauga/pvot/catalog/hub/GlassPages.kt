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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.components.PvotGlassSurface
import com.prauga.pvot.designsystem.components.background.PvotBackgroundMotion
import com.prauga.pvot.designsystem.components.background.PvotGradientBackground
import com.prauga.pvot.designsystem.components.background.PvotLiveBackground
import com.prauga.pvot.designsystem.components.background.PvotPhotoBackground
import com.prauga.pvot.designsystem.components.background.preparePvotPhotoBackground
import com.prauga.pvot.designsystem.effects.PvotGlassMaterial
import com.prauga.pvot.designsystem.effects.pvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.rememberPvotGlassBackdrop
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing

@Composable
internal fun BackgroundsPage(onBack: () -> Unit) {
    var motion by remember { mutableStateOf(PvotBackgroundMotion.Slow) }
    DetailPage(
        title = "Backgrounds",
        summary =
            "What glass refracts: the screen's background with a slow drift of color, a still " +
                "gradient, or a photo blurred once and dimmed.",
        onBack = onBack,
        code =
            "PvotScreenScaffold(\n    background = { PvotLiveBackground(Modifier.fillMaxSize()) },\n" +
                "    bottomBar = { PvotNavBar(selected, onSelect, tabs) },\n) { padding -> … }",
    ) {
        DemoSection(
            title = "Live",
            caption = "Five blobs of the scheme's container colors drift across the background",
            framed = false,
        ) {
            ChoiceChips(
                options = PvotBackgroundMotion.entries,
                selected = motion,
                onSelect = { motion = it },
                label = { it.name },
            )
            BackgroundFrame { backdropModifier ->
                PvotLiveBackground(backdropModifier, motion = motion)
            }
        }
        DemoSection(
            title = "Gradient",
            caption = "The still version, for a screen that must not move",
            framed = false,
        ) {
            BackgroundFrame { backdropModifier -> PvotGradientBackground(backdropModifier) }
        }
        DemoSection(
            title = "Photo",
            caption = "Blurred once off the main thread, then drawn under a scrim",
            framed = false,
        ) {
            val density = LocalDensity.current
            val source = remember { samplePhoto() }
            var blurred by remember { mutableStateOf<ImageBitmap?>(null) }
            LaunchedEffect(source) {
                blurred = preparePvotPhotoBackground(source, density, downscale = 1)
            }
            BackgroundFrame { backdropModifier -> PvotPhotoBackground(blurred, backdropModifier) }
        }
    }
}

/**
 * A clipped frame with a glass card over [background], so each background is seen through glass.
 */
@Composable
private fun BackgroundFrame(background: @Composable (Modifier) -> Unit) {
    val backdrop = rememberPvotGlassBackdrop()
    Box(modifier = Modifier.fillMaxWidth().height(220.dp).clip(PvotShapes.Card)) {
        background(Modifier.fillMaxSize().pvotGlassBackdrop(backdrop))
        PvotGlassSurface(
            backdrop = backdrop,
            modifier = Modifier.align(Alignment.Center).fillMaxWidth(0.7f),
            cornerRadius = 16.dp,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(PvotSpacing.L),
        ) {
            Text(
                text = "Glass over it",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

/** A small stand-in photo: four blocks of color, so the blur and the scrim show. */
private fun samplePhoto(): Bitmap {
    val size = 96
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint()
    val colors = listOf(0xFFE5287A, 0xFF1F6FFF, 0xFFF5C451, 0xFF5FD48A).map { it.toInt() }
    val half = size / 2f
    colors.forEachIndexed { index, color ->
        paint.color = color
        val left = if (index % 2 == 0) 0f else half
        val top = if (index < 2) 0f else half
        canvas.drawRect(left, top, left + half, top + half, paint)
    }
    return bitmap
}

/** The three materials side by side over a live background, each with body text on it. */
@Composable
internal fun GlassMaterialsDemo(modifier: Modifier = Modifier) {
    val backdrop = rememberPvotGlassBackdrop()
    Box(modifier = modifier.fillMaxWidth().height(200.dp).clip(PvotShapes.Card)) {
        PvotLiveBackground(Modifier.fillMaxSize().pvotGlassBackdrop(backdrop))
        Row(
            modifier = Modifier.fillMaxSize().padding(PvotSpacing.M),
            horizontalArrangement = Arrangement.spacedBy(PvotSpacing.S),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PvotGlassMaterial.entries.forEach { material ->
                PvotGlassSurface(
                    backdrop = backdrop,
                    modifier = Modifier.weight(1f).height(120.dp),
                    cornerRadius = 16.dp,
                    style =
                        com.prauga.pvot.designsystem.effects.PvotGlassDefaults.styleOf(material)
                            .copy(
                                fillColor =
                                    com.prauga.pvot.designsystem.effects.PvotGlassDefaults
                                        .materialFill(material, MaterialTheme.colorScheme)
                            ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(PvotSpacing.S),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = material.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Body text",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

/** A glass chip built from the public recipe alone. */
@Composable
internal fun BuildYourOwnGlassDemo(modifier: Modifier = Modifier) {
    val backdrop = rememberPvotGlassBackdrop()
    var picked by remember { mutableStateOf(0) }
    Box(modifier = modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(16.dp))) {
        PvotGradientBackground(Modifier.fillMaxSize().pvotGlassBackdrop(backdrop))
        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(PvotSpacing.S),
        ) {
            listOf("All", "Missed", "Voicemail").forEachIndexed { index, label ->
                PvotGlassSurface(
                    backdrop = backdrop,
                    onClick = { picked = index },
                    contentPadding =
                        androidx.compose.foundation.layout.PaddingValues(
                            horizontal = PvotSpacing.L,
                            vertical = PvotSpacing.S,
                        ),
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        color =
                            if (picked == index) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}
