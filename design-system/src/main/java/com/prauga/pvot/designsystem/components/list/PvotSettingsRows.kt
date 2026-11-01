// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.R
import com.prauga.pvot.designsystem.effects.pvotPressScale
import com.prauga.pvot.designsystem.theme.PvotSpacing

/** Defaults for [PvotSwitchRow] and [PvotPreferenceRow]. */
public object PvotSettingsRowDefaults {

    /** The widest the current value of a [PvotPreferenceRow] gets before it is cut short. */
    public val ValueMaxWidth: Dp = 160.dp

    /** Size of the chevron at the end of a [PvotPreferenceRow]. */
    public val ChevronSize: Dp = 20.dp

    /** Colors of the switch in a [PvotSwitchRow], the theme's own switch. */
    @Composable
    public fun switchColors(): SwitchColors = SwitchDefaults.colors()
}

/**
 * A settings row that turns something on or off: an optional icon, a title, a summary and a
 * switch at the end.
 *
 * The whole row is the touch target, not just the switch, and TalkBack reads it as one switch.
 *
 * @param title What the setting is
 * @param checked Whether the setting is on
 * @param onCheckedChange Called with the new value when the row is tapped
 * @param modifier Modifier for the row
 * @param summary What the setting does, under the title
 * @param icon Content of the tinted circle at the start, typically an `Icon`; it is tinted for you
 * @param enabled Whether the row responds to touch; a disabled row is dimmed
 * @param colors The row's colors
 * @param switchColors The switch's colors
 */
@Composable
public fun PvotSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    icon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: PvotListRowColors = PvotListRowDefaults.themeColors(),
    switchColors: SwitchColors = PvotSettingsRowDefaults.switchColors()
) {
    val interactionSource = remember { MutableInteractionSource() }
    PvotRowLayout(
        modifier = modifier
            .pvotPressScale(interactionSource)
            .clip(PvotListRowDefaults.Shape)
            .toggleable(
                value = checked,
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange
            ),
        title = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) },
        supportingText = summary?.let { { Text(text = it) } },
        leading = icon?.let { { PvotRowIcon(colors, it) } },
        trailing = {
            // The row toggles; the switch only shows the state, so it takes no clicks of its own.
            Switch(
                checked = checked,
                onCheckedChange = null,
                enabled = enabled,
                colors = switchColors
            )
        },
        selected = false,
        enabled = enabled,
        colors = colors
    )
}

/**
 * A settings row that opens something: an optional icon, a title, a summary, the current value
 * and a chevron.
 *
 * Use it for a setting chosen on another screen, in a dialog or in a sheet. The current value
 * sits at the end in the brand color, cut short if it is long; put long values in [summary]
 * instead.
 *
 * @param title What the setting is
 * @param onClick Called when the row is tapped
 * @param modifier Modifier for the row
 * @param summary What the setting does, under the title
 * @param value The current value, such as "Vibrate"
 * @param icon Content of the tinted circle at the start, typically an `Icon`; it is tinted for you
 * @param showChevron Whether to end with a chevron, which says the row opens a new screen
 * @param enabled Whether the row responds to touch; a disabled row is dimmed
 * @param colors The row's colors
 */
@Composable
public fun PvotPreferenceRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    value: String? = null,
    icon: (@Composable () -> Unit)? = null,
    showChevron: Boolean = true,
    enabled: Boolean = true,
    colors: PvotListRowColors = PvotListRowDefaults.themeColors()
) {
    val interactionSource = remember { MutableInteractionSource() }
    val trailing: (@Composable () -> Unit)? = if (value != null || showChevron) {
        {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PvotSpacing.XS)
            ) {
                if (value != null) {
                    Text(
                        text = value,
                        color = colors.valueColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = PvotSettingsRowDefaults.ValueMaxWidth)
                    )
                }
                if (showChevron) {
                    Icon(
                        painter = painterResource(R.drawable.pvot_ic_chevron_right),
                        contentDescription = null,
                        modifier = Modifier.size(PvotSettingsRowDefaults.ChevronSize)
                    )
                }
            }
        }
    } else {
        null
    }
    PvotRowLayout(
        modifier = modifier
            .pvotPressScale(interactionSource)
            .clip(PvotListRowDefaults.Shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            ),
        title = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) },
        supportingText = summary?.let { { Text(text = it) } },
        leading = icon?.let { { PvotRowIcon(colors, it) } },
        trailing = trailing,
        selected = false,
        enabled = enabled,
        colors = colors
    )
}
