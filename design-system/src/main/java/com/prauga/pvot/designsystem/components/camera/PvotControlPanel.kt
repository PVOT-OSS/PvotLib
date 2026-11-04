// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prauga.pvot.designsystem.components.pvotGlassSurface
import com.prauga.pvot.designsystem.effects.LocalPvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassStyle

/**
 * Color configuration for [PvotControlTile].
 *
 * @param contentColor Icon and label of a tile that is off
 * @param activeContentColor Icon of a tile that is on
 * @param containerColor Circle behind the icon of a tile that is off
 * @param activeContainerColor Circle behind the icon of a tile that is on
 */
@Immutable
public data class PvotControlTileColors(
    val contentColor: Color,
    val activeContentColor: Color,
    val containerColor: Color,
    val activeContainerColor: Color,
)

/** Defaults for [PvotControlPanel] and [PvotControlTile]. */
public object PvotControlPanelDefaults {
    /** The panel's corner radius. */
    public val CornerRadius: Dp = 58.dp

    /** Tiles in each row of the panel. */
    public const val Columns: Int = 3

    /** The circle behind a tile's icon. */
    public val IconContainerSize: Dp = 60.dp

    /** A tile's height, its circle and its label. */
    public val TileHeight: Dp = 110.dp

    /** Colors from [MaterialTheme]: the primary color for a tile that is on. */
    @Composable
    public fun tileColors(
        contentColor: Color = MaterialTheme.colorScheme.onSurface,
        activeContentColor: Color = MaterialTheme.colorScheme.primary,
        containerColor: Color = contentColor.copy(alpha = 0.1f),
        activeContainerColor: Color = contentColor.copy(alpha = 0.2f),
    ): PvotControlTileColors =
        PvotControlTileColors(
            contentColor,
            activeContentColor,
            containerColor,
            activeContainerColor,
        )
}

/**
 * A glass panel of camera controls, laid out [columns] to a row, such as flash, timer, exposure and
 * aspect; fill it with [PvotControlTile]s.
 *
 * @param modifier Modifier for the panel
 * @param backdrop The content behind the panel, or null for the tint alone
 * @param columns Tiles in each row
 * @param style How the panel's glass looks
 * @param content The tiles
 */
@Composable
public fun PvotControlPanel(
    modifier: Modifier = Modifier,
    backdrop: PvotGlassBackdrop? = LocalPvotGlassBackdrop.current,
    columns: Int = PvotControlPanelDefaults.Columns,
    style: PvotGlassStyle = PvotCameraGlassDefaults.PanelStyle,
    content: @Composable () -> Unit,
) {
    Layout(
        content = content,
        modifier =
            modifier
                .pvotGlassSurface(
                    backdrop = backdrop,
                    cornerRadius = PvotControlPanelDefaults.CornerRadius,
                    style = style,
                    role = null,
                )
                .padding(vertical = 16.dp, horizontal = 7.dp),
    ) { measurables, constraints ->
        val cellWidth = constraints.maxWidth / columns
        val gap = 3.dp.roundToPx()
        val placeables =
            measurables.map { it.measure(Constraints.fixedWidth(cellWidth).copy(minHeight = 0)) }
        val rows = placeables.chunked(columns)
        val height =
            rows.sumOf { row -> row.maxOf { it.height } } + gap * (rows.size - 1).coerceAtLeast(0)
        layout(constraints.maxWidth, height) {
            var y = 0
            rows.forEach { row ->
                row.forEachIndexed { index, placeable -> placeable.place(index * cellWidth, y) }
                y += row.maxOf { it.height } + gap
            }
        }
    }
}

/**
 * A control in a [PvotControlPanel]: an icon in a circle over its label, lit when [active].
 *
 * Inside the panel the tile takes its column's width.
 *
 * @param label What the control is, such as FLASH
 * @param onClick Called when the tile is tapped
 * @param modifier Modifier for the tile
 * @param active Whether the control is on, which lights its icon
 * @param enabled Whether the tile responds; a disabled tile is dimmed
 * @param badge Short text over the icon, such as 3s or 4:3, or null for none
 * @param colors The icon, label and circle colors
 * @param icon The control's icon, tinted through [LocalContentColor]
 */
@Composable
public fun PvotControlTile(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    enabled: Boolean = true,
    badge: String? = null,
    colors: PvotControlTileColors = PvotControlPanelDefaults.tileColors(),
    icon: @Composable () -> Unit,
) {
    val alpha = if (enabled) 1f else 0.4f
    val iconColor = (if (active) colors.activeContentColor else colors.contentColor).copy(alpha)

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .height(PvotControlPanelDefaults.TileHeight)
                .clip(RoundedCornerShape(24.dp))
                .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(7.dp))
        Box(
            modifier =
                Modifier.size(PvotControlPanelDefaults.IconContainerSize)
                    .clip(CircleShape)
                    .background(if (active) colors.activeContainerColor else colors.containerColor),
            contentAlignment = Alignment.Center,
        ) {
            CompositionLocalProvider(LocalContentColor provides iconColor) {
                icon()
                if (badge != null) {
                    Text(text = badge, color = iconColor, fontSize = 9.sp, maxLines = 1)
                }
            }
        }
        Spacer(modifier = Modifier.height(11.dp))
        Text(text = label, color = colors.contentColor.copy(alpha), fontSize = 12.sp, maxLines = 1)
    }
}
