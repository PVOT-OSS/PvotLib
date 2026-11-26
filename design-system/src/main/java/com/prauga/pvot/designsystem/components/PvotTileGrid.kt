// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.R
import com.prauga.pvot.designsystem.theme.PvotSpacing

/** Colors of a [PvotTileGrid] and its tiles. */
@Immutable
public data class PvotTileColors(
    val tileColor: Color,
    val iconContainerColor: Color,
    val titleColor: Color,
    val valueColor: Color,
    val headerColor: Color,
    val actionColor: Color,
)

/** Defaults for [PvotTileGrid]. */
public object PvotTileGridDefaults {

    /** Corner shape of a tile. */
    public val TileShape: Shape = RoundedCornerShape(20.dp)

    /** Size of the rounded square behind a tile's icon. */
    public val IconSize: Dp = 40.dp

    /** The shortest a tile gets, so a row of tiles lines up. */
    public val MinTileHeight: Dp = 72.dp

    /** Space between tiles. */
    public val Spacing: Dp = PvotSpacing.S

    /** Colors from the theme: tiles one tone above the card, values in `onSurfaceVariant`. */
    @Composable
    public fun colors(): PvotTileColors =
        PvotTileColors(
            tileColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            iconContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            titleColor = MaterialTheme.colorScheme.onSurface,
            valueColor = MaterialTheme.colorScheme.onSurfaceVariant,
            headerColor = MaterialTheme.colorScheme.onSurface,
            actionColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
}

/** Builds the tiles of a [PvotTileGrid]. */
public interface PvotTileGridScope {

    /**
     * Adds a tile.
     *
     * @param title What the tile counts, such as "Photos"
     * @param value Its amount, such as "9,013 items"
     * @param icon The tile's icon, drawn on a rounded square
     * @param onClick Called when the tile is tapped; null makes it static
     */
    public fun tile(
        title: String,
        value: String,
        icon: (@Composable () -> Unit)? = null,
        onClick: (() -> Unit)? = null,
    )
}

private class Tile(
    val title: String,
    val value: String,
    val icon: (@Composable () -> Unit)?,
    val onClick: (() -> Unit)?,
)

private class PvotTileGridScopeImpl : PvotTileGridScope {
    val tiles = mutableListOf<Tile>()

    override fun tile(
        title: String,
        value: String,
        icon: (@Composable () -> Unit)?,
        onClick: (() -> Unit)?,
    ) {
        tiles += Tile(title, value, icon, onClick)
    }
}

/**
 * A card of tiles laid out in equal columns, each with an icon, a title and a value, such as what a
 * backup holds: "Photos · 9,013 items", "Contacts · 512".
 *
 * An optional header row sits above the tiles: a status dot, a title and an action such as "See
 * all".
 *
 * @param modifier Modifier for the card
 * @param title Heading in the header row
 * @param status Dot before [title]; null shows none
 * @param actionLabel Text of the header's action, such as "See all"
 * @param onAction Called when the header's action is tapped
 * @param columns How many tiles share a row
 * @param colors Colors of the tiles and header
 * @param content Adds the tiles with [PvotTileGridScope.tile]
 */
@Composable
public fun PvotTileGrid(
    modifier: Modifier = Modifier,
    title: String? = null,
    status: PvotStatus? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    columns: Int = 2,
    colors: PvotTileColors = PvotTileGridDefaults.colors(),
    content: PvotTileGridScope.() -> Unit,
) {
    require(columns > 0) { "columns must be positive" }
    val tiles = PvotTileGridScopeImpl().apply(content).tiles
    PvotCard(modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(PvotSpacing.M),
            verticalArrangement = Arrangement.spacedBy(PvotTileGridDefaults.Spacing),
        ) {
            if (title != null || actionLabel != null) {
                Header(title, status, actionLabel, onAction, colors)
            }
            tiles.chunked(columns).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(PvotTileGridDefaults.Spacing)) {
                    row.forEach { tile -> TileContent(tile, colors, Modifier.weight(1f)) }
                    repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun Header(
    title: String?,
    status: PvotStatus?,
    actionLabel: String?,
    onAction: (() -> Unit)?,
    colors: PvotTileColors,
) {
    Row(
        modifier =
            Modifier.fillMaxWidth().padding(horizontal = PvotSpacing.XS, vertical = PvotSpacing.XS),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PvotSpacing.S),
    ) {
        if (status != null) PvotStatusDot(status)
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.headerColor,
                modifier = Modifier.weight(1f).semantics { heading() },
            )
        } else {
            Spacer(Modifier.weight(1f))
        }
        if (actionLabel != null) {
            Row(
                modifier =
                    if (onAction != null) {
                        Modifier.clip(RoundedCornerShape(8.dp))
                            .clickable(role = Role.Button, onClick = onAction)
                            .padding(PvotSpacing.XS)
                    } else {
                        Modifier
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.actionColor,
                )
                Icon(
                    painter = painterResource(R.drawable.pvot_ic_chevron_right),
                    contentDescription = null,
                    tint = colors.actionColor,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun TileContent(tile: Tile, colors: PvotTileColors, modifier: Modifier) {
    val clickable =
        tile.onClick?.let { onClick -> Modifier.clickable(role = Role.Button, onClick = onClick) }
            ?: Modifier
    Row(
        modifier =
            modifier
                .heightIn(min = PvotTileGridDefaults.MinTileHeight)
                .clip(PvotTileGridDefaults.TileShape)
                .background(colors.tileColor)
                .then(clickable)
                .padding(PvotSpacing.M),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PvotSpacing.M),
    ) {
        if (tile.icon != null) {
            Box(
                modifier =
                    Modifier.size(PvotTileGridDefaults.IconSize)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.iconContainerColor),
                contentAlignment = Alignment.Center,
            ) {
                tile.icon.invoke()
            }
        }
        Column(Modifier.weight(1f)) {
            Text(
                text = tile.title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = tile.value,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.valueColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
