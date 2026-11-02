// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import com.prauga.pvot.designsystem.R
import kotlin.math.absoluteValue

/**
 * One color pair of a [PvotLetterTile].
 *
 * @param containerColor Fill of the tile
 * @param contentColor Color of the letter or the fallback icon
 */
@Immutable
public data class PvotLetterTileColors(val containerColor: Color, val contentColor: Color)

/** Defaults for [PvotLetterTile]. */
public object PvotLetterTileDefaults {

    /** Diameter of the tile, the same as a list row's leading visual. */
    public val Size: Dp = PvotListRowDefaults.LeadingSize

    /**
     * The color pairs a tile picks from, the container roles of [colorScheme], so tiles follow the
     * theme and dynamic color.
     */
    public fun palette(colorScheme: ColorScheme): List<PvotLetterTileColors> =
        listOf(
            PvotLetterTileColors(colorScheme.primaryContainer, colorScheme.onPrimaryContainer),
            PvotLetterTileColors(colorScheme.secondaryContainer, colorScheme.onSecondaryContainer),
            PvotLetterTileColors(colorScheme.tertiaryContainer, colorScheme.onTertiaryContainer),
        )

    /** The theme's palette for tiles. */
    @Composable
    public fun themePalette(): List<PvotLetterTileColors> = palette(MaterialTheme.colorScheme)

    /** The pair of [palette] for [key]: the same key always gets the same pair. */
    public fun colorsFor(key: Any, palette: List<PvotLetterTileColors>): PvotLetterTileColors =
        palette[key.hashCode().absoluteValue % palette.size]
}

/**
 * A round tile showing the first letter of a name, the leading visual of a contact or caller
 * without a photo.
 *
 * Its colors come from [palette] by [colorKey], so a contact keeps its color wherever it appears. A
 * name with no letter or digit to show, such as an unknown number's "+" or an empty name, gets a
 * person icon instead. The tile is decorative: the row next to it already says the name.
 *
 * @param name The name to take the first letter from
 * @param modifier Modifier for the tile
 * @param colorKey What picks the color, such as a contact's lookup key; defaults to [name]
 * @param size Diameter of the tile
 * @param palette The color pairs the tile picks from
 */
@Composable
public fun PvotLetterTile(
    name: String,
    modifier: Modifier = Modifier,
    colorKey: Any = name,
    size: Dp = PvotLetterTileDefaults.Size,
    palette: List<PvotLetterTileColors> = PvotLetterTileDefaults.themePalette(),
) {
    val colors = PvotLetterTileDefaults.colorsFor(colorKey, palette)
    val initial = initialOf(name)
    Box(
        modifier =
            modifier
                .size(size)
                .background(colors.containerColor, CircleShape)
                .clearAndSetSemantics {},
        contentAlignment = Alignment.Center,
    ) {
        if (initial != null) {
            Text(
                text = initial,
                color = colors.contentColor,
                style = MaterialTheme.typography.titleMedium,
                // In dp, not sp: the tile does not grow with the font scale, so neither may the
                // letter.
                fontSize = with(LocalDensity.current) { (size * LetterFraction).toSp() },
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.pvot_ic_user),
                contentDescription = null,
                tint = colors.contentColor,
                modifier = Modifier.size(size / 2),
            )
        }
    }
}

/**
 * The first letter or digit of [name], upper-cased, whole even outside the Basic Multilingual
 * Plane.
 */
private fun initialOf(name: String): String? {
    var index = 0
    while (index < name.length) {
        val codePoint = name.codePointAt(index)
        if (Character.isLetterOrDigit(codePoint)) {
            return String(Character.toChars(codePoint)).uppercase()
        }
        index += Character.charCount(codePoint)
    }
    return null
}

/** The letter fills a little under half the tile, whatever its size. */
private const val LetterFraction: Float = 0.45f
