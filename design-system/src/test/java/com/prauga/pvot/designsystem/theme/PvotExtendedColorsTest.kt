// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertTrue
import org.junit.Test

/** The success and warning roles keep their content readable, as the Material roles do. */
class PvotExtendedColorsTest {

    @Test
    fun lightRolesMeetBodyTextContrast() = assertPairs(PvotLightExtendedColors)

    @Test
    fun darkRolesMeetBodyTextContrast() = assertPairs(PvotDarkExtendedColors)

    private fun assertPairs(colors: PvotExtendedColors) {
        assertContrast("success", colors.onSuccess, colors.success)
        assertContrast("successContainer", colors.onSuccessContainer, colors.successContainer)
        assertContrast("warning", colors.onWarning, colors.warning)
        assertContrast("warningContainer", colors.onWarningContainer, colors.warningContainer)
    }

    private fun assertContrast(role: String, foreground: Color, background: Color) {
        val ratio = contrastRatio(foreground, background)
        assertTrue("$role contrast is $ratio, under 4.5", ratio >= 4.5)
    }
}

/** The WCAG contrast ratio of two opaque colors. */
internal fun contrastRatio(a: Color, b: Color): Double {
    val la = relativeLuminance(a)
    val lb = relativeLuminance(b)
    val lighter = maxOf(la, lb)
    val darker = minOf(la, lb)
    return (lighter + 0.05) / (darker + 0.05)
}

/** The WCAG relative luminance of an opaque sRGB color. */
internal fun relativeLuminance(color: Color): Double {
    fun channel(c: Float): Double {
        val v = c.toDouble()
        return if (v <= 0.03928) v / 12.92 else Math.pow((v + 0.055) / 1.055, 2.4)
    }
    return 0.2126 * channel(color.red) + 0.7152 * channel(color.green) + 0.0722 * channel(color.blue)
}

/** [top] laid over an opaque [bottom]. */
internal fun composite(top: Color, bottom: Color): Color {
    val a = top.alpha
    return Color(
        red = top.red * a + bottom.red * (1 - a),
        green = top.green * a + bottom.green * (1 - a),
        blue = top.blue * a + bottom.blue * (1 - a),
    )
}
