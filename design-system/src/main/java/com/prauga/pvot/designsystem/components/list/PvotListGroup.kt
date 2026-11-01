// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.components.PvotCard
import com.prauga.pvot.designsystem.theme.PvotSpacing

/**
 * The colors of a [PvotListGroup].
 *
 * @param titleColor Color of the title over the card
 * @param dividerColor Color of the hairline between rows
 */
@Immutable
public data class PvotListGroupColors(
    val titleColor: Color,
    val dividerColor: Color
)

/** Defaults for [PvotListGroup]. */
public object PvotListGroupDefaults {

    /** Thickness of the line between rows. */
    public val DividerThickness: Dp = 1.dp

    /** How far the line between rows stays from each edge of the card. */
    public val DividerInset: Dp = PvotSpacing.L

    /** Padding inside the card, round its rows. */
    public val ContentPadding: PaddingValues = PaddingValues(horizontal = PvotSpacing.XS, vertical = PvotSpacing.XS)

    /** Opacity of the divider color over the card. */
    public const val DividerAlpha: Float = 0.6f

    /** Colors from the theme: the title in `onSurfaceVariant`, dividers in a faint `outlineVariant`. */
    @Composable
    public fun colors(): PvotListGroupColors = PvotListGroupColors(
        titleColor = MaterialTheme.colorScheme.onSurfaceVariant,
        dividerColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = DividerAlpha)
    )
}

/** Where the rows of a [PvotListGroup] are added. */
public interface PvotListGroupScope {

    /** Adds a row to the group; a divider goes between it and the row before. */
    public fun item(content: @Composable () -> Unit)
}

private class PvotListGroupScopeImpl : PvotListGroupScope {
    val items = mutableListOf<@Composable () -> Unit>()
    override fun item(content: @Composable () -> Unit) {
        items += content
    }
}

/**
 * Rows that belong together on one card, with a hairline between each, under an optional title.
 *
 * A settings screen, the actions on a number, the calls on a day: each is a group. Rows inside are
 * typically [PvotPreferenceRow]s and [PvotSwitchRow]s with
 * [plain icons][PvotRowIconStyle.Plain], or [PvotListRow]s.
 *
 * ```
 * PvotListGroup(title = "General") {
 *     item { PvotPreferenceRow("Display", onClick = ::openDisplay, icon = { Icon(display, null) },
 *         iconStyle = PvotRowIconStyle.Plain, chevron = PvotRowChevron.Arrow) }
 *     item { PvotSwitchRow("Vibrate", vibrate, onVibrateChange) }
 * }
 * ```
 *
 * @param modifier Modifier for the group
 * @param title The group's title over the card, read as a heading; null for none
 * @param dividerInset How far each divider stays from the card's edges
 * @param colors The group's colors
 * @param content The rows, each added with [PvotListGroupScope.item]
 */
@Composable
public fun PvotListGroup(
    modifier: Modifier = Modifier,
    title: String? = null,
    dividerInset: Dp = PvotListGroupDefaults.DividerInset,
    colors: PvotListGroupColors = PvotListGroupDefaults.colors(),
    content: PvotListGroupScope.() -> Unit
) {
    val items = PvotListGroupScopeImpl().apply(content).items
    Column(modifier) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = colors.titleColor,
                modifier = Modifier
                    .padding(start = PvotSpacing.L, end = PvotSpacing.L, bottom = PvotSpacing.S)
                    .semantics { heading() }
            )
        }
        PvotCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(PvotListGroupDefaults.ContentPadding)) {
                items.forEachIndexed { index, item ->
                    if (index > 0) {
                        Box(
                            Modifier
                                .padding(horizontal = dividerInset)
                                .fillMaxWidth()
                                .height(PvotListGroupDefaults.DividerThickness)
                                .background(colors.dividerColor)
                        )
                    }
                    item()
                }
            }
        }
    }
}
