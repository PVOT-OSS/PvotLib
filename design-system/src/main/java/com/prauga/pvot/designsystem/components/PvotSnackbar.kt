// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.R
import com.prauga.pvot.designsystem.effects.pvotPressScale
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing

/**
 * Color configuration for [PvotSnackbar].
 *
 * @param containerColor Fill of the snackbar
 * @param contentColor Color of the message and the close button
 * @param actionColor Color of the action, such as Undo
 */
@Immutable
public data class PvotSnackbarColors(
    val containerColor: Color,
    val contentColor: Color,
    val actionColor: Color,
)

/** Defaults for [PvotSnackbar] and [PvotSnackbarHost]. */
public object PvotSnackbarDefaults {

    /** Shape of the snackbar. */
    public val Shape: Shape = PvotShapes.Card

    /** The widest the snackbar gets, so it stays a snackbar on tablets. */
    public val MaxWidth: Dp = 480.dp

    /** The shortest the snackbar gets. */
    public val MinHeight: Dp = 52.dp

    /** Height of the soft shadow under the snackbar. */
    public val Elevation: Dp = 6.dp

    /** Snackbar colors derived from [colorScheme], each overridable. */
    public fun colors(
        colorScheme: ColorScheme,
        containerColor: Color = colorScheme.inverseSurface,
        contentColor: Color = colorScheme.inverseOnSurface,
        actionColor: Color = colorScheme.inversePrimary,
    ): PvotSnackbarColors = PvotSnackbarColors(containerColor, contentColor, actionColor)

    /** Snackbar colors from the theme's color scheme. */
    @Composable public fun themeColors(): PvotSnackbarColors = colors(MaterialTheme.colorScheme)
}

/**
 * Shows the snackbars of [hostState] as [PvotSnackbar]s, one at a time, easing each in and out.
 *
 * Show one with `hostState.showSnackbar(message, actionLabel = "Undo")`, which suspends until the
 * snackbar goes and says whether its action was taken. Place the host above floating bars, such as
 * `PvotNavBar`, so it does not cover them.
 *
 * @param hostState The state that queues the snackbars
 * @param modifier Modifier for the host
 * @param snackbar How each snackbar draws
 */
@Composable
public fun PvotSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    snackbar: @Composable (SnackbarData) -> Unit = { PvotSnackbar(it) },
) {
    SnackbarHost(hostState = hostState, modifier = modifier, snackbar = snackbar)
}

/**
 * A brief message at the bottom of the screen, with an optional action such as Undo and an optional
 * close button. Normally drawn by [PvotSnackbarHost] rather than called directly.
 *
 * @param data The message, its action and its callbacks, from [SnackbarHostState]
 * @param modifier Modifier for the snackbar
 * @param colors The snackbar's colors
 */
@Composable
public fun PvotSnackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
    colors: PvotSnackbarColors = PvotSnackbarDefaults.themeColors(),
) {
    val visuals = data.visuals
    Row(
        modifier =
            modifier
                .widthIn(max = PvotSnackbarDefaults.MaxWidth)
                .fillMaxWidth()
                .heightIn(min = PvotSnackbarDefaults.MinHeight)
                .shadow(PvotSnackbarDefaults.Elevation, PvotSnackbarDefaults.Shape)
                .clip(PvotSnackbarDefaults.Shape)
                .background(colors.containerColor)
                .padding(start = PvotSpacing.L, end = PvotSpacing.XS),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PvotSpacing.XS),
    ) {
        Text(
            text = visuals.message,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.contentColor,
            maxLines = 2,
            modifier = Modifier.weight(1f).padding(vertical = PvotSpacing.M),
        )
        val actionLabel = visuals.actionLabel
        if (actionLabel != null) {
            SnackbarButton(onClick = data::performAction) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.actionColor,
                    modifier = Modifier.padding(horizontal = PvotSpacing.M),
                )
            }
        }
        if (visuals.withDismissAction) {
            SnackbarButton(onClick = data::dismiss) {
                Icon(
                    painter = painterResource(R.drawable.pvot_ic_x),
                    contentDescription = stringResource(R.string.pvot_snackbar_dismiss),
                    tint = colors.contentColor,
                    modifier = Modifier.padding(horizontal = PvotSpacing.M).size(20.dp),
                )
            }
        }
    }
}

/** A 48dp tall, pill-clipped tap target with press feedback, for the snackbar's controls. */
@Composable
private fun SnackbarButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier =
            Modifier.heightIn(min = 48.dp)
                .widthIn(min = 48.dp)
                .pvotPressScale(interactionSource)
                .clip(PvotShapes.Pill)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
