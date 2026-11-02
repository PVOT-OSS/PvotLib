// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.effects

import androidx.compose.ui.graphics.Color
import com.prauga.pvot.designsystem.theme.PvotDarkColorScheme
import com.prauga.pvot.designsystem.theme.PvotLightColorScheme
import com.prauga.pvot.designsystem.theme.composite
import com.prauga.pvot.designsystem.theme.contrastRatio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** The glass materials: thicker is more frosted, and text stays readable on the tinted ones. */
class PvotGlassMaterialTest {

    @Test
    fun thickerMaterialsBlurMore() {
        val clear = PvotGlassDefaults.styleOf(PvotGlassMaterial.Clear).blur
        val regular = PvotGlassDefaults.styleOf(PvotGlassMaterial.Regular).blur
        val thick = PvotGlassDefaults.styleOf(PvotGlassMaterial.Thick).blur
        assertTrue(clear < regular)
        assertTrue(regular < thick)
    }

    @Test
    fun clearKeepsTheHaze() {
        assertEquals(
            PvotGlassDefaults.FillColor,
            PvotGlassDefaults.materialFill(PvotGlassMaterial.Clear, PvotDarkColorScheme),
        )
    }

    @Test fun textReadsOnRegularAndThickOverTheDarkPalette() = assertReadable(PvotDarkColorScheme)

    @Test fun textReadsOnRegularAndThickOverTheLightPalette() = assertReadable(PvotLightColorScheme)

    /**
     * The worst case for the tint is a vivid blob of the live background right under the text: the
     * fill laid over each of the palette's blob colors, with no blur to average them out.
     */
    private fun assertReadable(scheme: androidx.compose.material3.ColorScheme) {
        val behind =
            listOf(
                scheme.background,
                scheme.primaryContainer,
                scheme.tertiaryContainer,
                scheme.secondaryContainer,
                composite(scheme.primary.copy(alpha = 0.7f), scheme.background),
            )
        for (material in listOf(PvotGlassMaterial.Regular, PvotGlassMaterial.Thick)) {
            val fill = PvotGlassDefaults.materialFill(material, scheme)
            for (color in behind) {
                val surface = composite(fill, color)
                val ratio = contrastRatio(scheme.onSurface, surface)
                val minimum = if (material == PvotGlassMaterial.Thick) 4.5 else 3.0
                assertTrue(
                    "$material over ${color.hex()} gives onSurface contrast $ratio, under $minimum",
                    ratio >= minimum,
                )
            }
        }
    }

    private fun Color.hex(): String = "#%08X".format(toArgbLong())

    private fun Color.toArgbLong(): Long =
        ((alpha * 255).toLong() shl 24) or
            ((red * 255).toLong() shl 16) or
            ((green * 255).toLong() shl 8) or
            (blue * 255).toLong()
}
