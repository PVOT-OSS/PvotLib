// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

import java.io.File

internal object PvotTokens {
    private val roleAttributes =
        linkedMapOf(
            "Primary" to "colorPrimary",
            "OnPrimary" to "colorOnPrimary",
            "PrimaryContainer" to "colorPrimaryContainer",
            "OnPrimaryContainer" to "colorOnPrimaryContainer",
            "Secondary" to "colorSecondary",
            "OnSecondary" to "colorOnSecondary",
            "SecondaryContainer" to "colorSecondaryContainer",
            "OnSecondaryContainer" to "colorOnSecondaryContainer",
            "Tertiary" to "colorTertiary",
            "OnTertiary" to "colorOnTertiary",
            "TertiaryContainer" to "colorTertiaryContainer",
            "OnTertiaryContainer" to "colorOnTertiaryContainer",
            "Error" to "colorError",
            "OnError" to "colorOnError",
            "ErrorContainer" to "colorErrorContainer",
            "OnErrorContainer" to "colorOnErrorContainer",
            "Background" to "android:colorBackground",
            "OnBackground" to "colorOnBackground",
            "Surface" to "colorSurface",
            "OnSurface" to "colorOnSurface",
            "SurfaceVariant" to "colorSurfaceVariant",
            "OnSurfaceVariant" to "colorOnSurfaceVariant",
            "Outline" to "colorOutline",
            "OutlineVariant" to "colorOutlineVariant",
            "InverseSurface" to "colorSurfaceInverse",
            "InverseOnSurface" to "colorOnSurfaceInverse",
            "InversePrimary" to "colorPrimaryInverse",
        )

    private val baseThemes =
        listOf(
            Triple("PvotTheme.Base.Light", "Theme.Material3.Light.NoActionBar", "Light"),
            Triple("PvotTheme.Base.Dark", "Theme.Material3.Dark.NoActionBar", "Dark"),
            Triple("PvotTheme.Base.Light.Dialog", "Theme.Material3.Light.Dialog", "Light"),
            Triple("PvotTheme.Base.Dark.Dialog", "Theme.Material3.Dark.Dialog", "Dark"),
        )

    fun generate(themeDir: File, motionDir: File): String {
        val colors = parseColors(themeDir.resolve("Color.kt"))
        val corners =
            parse(
                themeDir.resolve("Shape.kt"),
                """public val (\w+): Shape = RoundedCornerShape\((\d+)\.dp\)""",
            )
        val spacing = parse(themeDir.resolve("Spacing.kt"), """public val (\w+): Dp = (\d+)\.dp""")
        val durations =
            parse(motionDir.resolve("PvotDurations.kt"), """public const val (\w+): Int = (\d+)""")
        return buildString {
            append(
                """
                |<?xml version="1.0" encoding="utf-8"?>
                |<!--
                |     SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
                |     SPDX-License-Identifier: Apache-2.0
                |-->
                |<!-- Generated from design-system by tokensDump. Do not edit. -->
                |<resources>
                |
                """
                    .trimMargin()
            )
            for ((name, value) in colors) appendLine(
                """    <color name="pvot_$name">#$value</color>"""
            )
            appendLine()
            for ((name, value) in corners) appendLine(
                """    <dimen name="pvot_corner_${snake(name)}">${value}dp</dimen>"""
            )
            appendLine()
            for ((name, value) in spacing) appendLine(
                """    <dimen name="pvot_spacing_${snake(name)}">${value}dp</dimen>"""
            )
            appendLine()
            for ((name, value) in durations) appendLine(
                """    <integer name="pvot_duration_${snake(name)}">$value</integer>"""
            )
            for ((style, parent, variant) in baseThemes) {
                appendLine()
                appendLine("""    <style name="$style" parent="$parent">""")
                for ((role, attribute) in roleAttributes) {
                    val color = "${snake(role)}_${variant.lowercase()}"
                    if (colors.containsKey(color))
                        appendLine("""        <item name="$attribute">@color/pvot_$color</item>""")
                }
                appendLine("    </style>")
            }
            appendLine("</resources>")
        }
    }

    private fun parseColors(file: File): Map<String, String> {
        val regex =
            Regex("""public val Pvot(\w+?)(Light|Dark)?: Color = Color\(0x([0-9A-Fa-f]{8})\)""")
        return regex.findAll(file.readText()).associate { match ->
            val (role, variant, hex) = match.destructured
            val name =
                if (variant.isEmpty()) snake(role) else "${snake(role)}_${variant.lowercase()}"
            name to hex.uppercase()
        }
    }

    private fun parse(file: File, pattern: String): Map<String, String> =
        Regex(pattern).findAll(file.readText()).associate { it.groupValues[1] to it.groupValues[2] }

    private fun snake(name: String): String =
        name.replace(Regex("(?<=[a-z0-9])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])"), "_").lowercase()
}
