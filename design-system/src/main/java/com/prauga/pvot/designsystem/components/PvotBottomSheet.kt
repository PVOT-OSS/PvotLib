// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.effects.pvotPressScale
import com.prauga.pvot.designsystem.theme.PvotSpacing

/** Defaults for [PvotBottomSheet]. */
public object PvotBottomSheetDefaults {

    /** Corner shape of the sheet, rounded at the top only. */
    public val Shape: Shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

    /** Width of the drag handle. */
    public val HandleWidth: Dp = 36.dp

    /** Height of the drag handle. */
    public val HandleHeight: Dp = 4.dp

    /** Opacity of the scrim behind the sheet. */
    public const val ScrimAlpha: Float = 0.5f

    /** The widest the sheet gets, so it doesn't stretch across a tablet. */
    public val MaxWidth: Dp = 640.dp

    /** Fill of the sheet. */
    public val containerColor: Color
        @Composable get() = MaterialTheme.colorScheme.surfaceVariant
}

/**
 * A modal bottom sheet with a slim drag handle, an optional title and content that clears the
 * navigation bar.
 *
 * Drag the sheet down, tap the scrim or go back to dismiss it. The sheet stays composed until it
 * has slid away, so setting [visible] to false animates it out as well. Long content scrolls
 * inside the sheet rather than pushing it off screen.
 *
 * @param visible Whether the sheet is showing
 * @param onDismissRequest Called when the user drags the sheet away, taps the scrim or goes back
 * @param modifier Modifier for the sheet surface
 * @param title Heading above the content
 * @param skipPartiallyExpanded Whether the sheet opens straight to full height instead of stopping
 * at half
 * @param content The sheet's content, typically [PvotSheetItem]s
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun PvotBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    skipPartiallyExpanded: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)
    var shown by remember { mutableStateOf(visible) }

    LaunchedEffect(visible) {
        if (visible) {
            shown = true
        } else if (shown) {
            sheetState.hide()
            shown = false
        }
    }
    if (!shown) return

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        sheetMaxWidth = PvotBottomSheetDefaults.MaxWidth,
        shape = PvotBottomSheetDefaults.Shape,
        containerColor = PvotBottomSheetDefaults.containerColor,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        scrimColor = Color.Black.copy(alpha = PvotBottomSheetDefaults.ScrimAlpha),
        dragHandle = { PvotSheetHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 640.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = PvotSpacing.L)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = PvotSpacing.L),
            verticalArrangement = Arrangement.spacedBy(PvotSpacing.XS)
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                        start = PvotSpacing.S,
                        end = PvotSpacing.S,
                        top = PvotSpacing.XS,
                        bottom = PvotSpacing.S
                    )
                )
            }
            content()
        }
    }
}

@Composable
private fun PvotSheetHandle() {
    Box(
        modifier = Modifier
            .padding(vertical = PvotSpacing.M)
            .size(PvotBottomSheetDefaults.HandleWidth, PvotBottomSheetDefaults.HandleHeight)
            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), CircleShape)
    )
}

/**
 * A tappable row for a [PvotBottomSheet]: an optional icon in a tinted circle, a title and a
 * subtitle. It squashes slightly under the finger.
 *
 * @param title Main text of the row
 * @param onClick Called when the row is tapped
 * @param modifier Modifier for the row
 * @param subtitle Supporting text under the title
 * @param icon Content of the circle at the start, typically an `Icon`; it is tinted for you
 * @param destructive Whether the row destroys something, which tints it in the error color
 * @param enabled Whether the row responds to taps
 */
@Composable
public fun PvotSheetItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: (@Composable () -> Unit)? = null,
    destructive: Boolean = false,
    enabled: Boolean = true
) {
    val colors = MaterialTheme.colorScheme
    val accent = if (destructive) colors.error else colors.primary
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .pvotPressScale(interactionSource)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = PvotSpacing.S, vertical = PvotSpacing.S),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PvotSpacing.L)
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(accent.copy(alpha = 0.14f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CompositionLocalProvider(LocalContentColor provides accent, content = { icon() })
            }
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (destructive) colors.error else colors.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}
