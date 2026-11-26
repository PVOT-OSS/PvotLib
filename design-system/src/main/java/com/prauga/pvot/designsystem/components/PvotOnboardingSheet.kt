// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.components.button.PvotButton
import com.prauga.pvot.designsystem.components.button.PvotButtonStyle
import com.prauga.pvot.designsystem.theme.PvotSpacing

/** Defaults for [PvotOnboardingSheet]. */
public object PvotOnboardingSheetDefaults {

    /** Size of the area that holds the icon above the title. */
    public val IconSize: Dp = 72.dp

    /** The tallest the scrolling part gets before it scrolls. */
    public val MaxContentHeight: Dp = 560.dp
}

/**
 * A full-height modal sheet for one step of a setup flow: an icon, a title, a short message,
 * optional content such as a list of words or switches, and the step's buttons pinned at the
 * bottom.
 *
 * The sheet stays open while the flow changes its title and content, so a flow can move from step
 * to step inside one sheet. Set [dismissible] to false for a step the user has to finish: dragging,
 * tapping the scrim and going back then leave the sheet open, and it shows no drag handle.
 *
 * @param visible Whether the sheet is showing
 * @param title The step's heading
 * @param primaryActionLabel Text of the main button, such as "Continue"
 * @param onPrimaryAction Called when the main button is tapped
 * @param modifier Modifier for the sheet surface
 * @param message A short explanation under the title
 * @param icon Large icon above the title
 * @param primaryActionEnabled Whether the main button can be tapped yet
 * @param secondaryActionLabel Text of a quieter button under the main one, such as "Not now"
 * @param onSecondaryAction Called when the secondary button is tapped
 * @param dismissible Whether the user can close the sheet without finishing the step
 * @param onDismissRequest Called when a dismissible sheet is dragged away, its scrim tapped or back
 *   pressed
 * @param content Extra content between the message and the buttons; it scrolls with the header
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun PvotOnboardingSheet(
    visible: Boolean,
    title: String,
    primaryActionLabel: String,
    onPrimaryAction: () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null,
    icon: (@Composable () -> Unit)? = null,
    primaryActionEnabled: Boolean = true,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    dismissible: Boolean = true,
    onDismissRequest: () -> Unit = {},
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val canDismiss by rememberUpdatedState(dismissible)
    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { it != SheetValue.Hidden || canDismiss },
        )
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
        onDismissRequest = { if (canDismiss) onDismissRequest() },
        modifier = modifier,
        sheetState = sheetState,
        sheetMaxWidth = PvotBottomSheetDefaults.MaxWidth,
        shape = PvotBottomSheetDefaults.Shape,
        containerColor = PvotBottomSheetDefaults.containerColor,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        scrimColor = Color.Black.copy(alpha = PvotBottomSheetDefaults.ScrimAlpha),
        dragHandle = if (dismissible) ({ PvotSheetHandle() }) else null,
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = dismissible),
    ) {
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = PvotSpacing.L)
                    .padding(top = if (dismissible) 0.dp else PvotSpacing.L, bottom = PvotSpacing.L)
        ) {
            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .heightIn(max = PvotOnboardingSheetDefaults.MaxContentHeight)
                        .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(PvotSpacing.M),
            ) {
                if (icon != null) {
                    Box(
                        modifier =
                            Modifier.padding(top = PvotSpacing.M)
                                .size(PvotOnboardingSheetDefaults.IconSize),
                        contentAlignment = Alignment.Center,
                    ) {
                        icon()
                    }
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().semantics { heading() },
                )
                if (message != null) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                content?.invoke(this)
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = PvotSpacing.L),
                verticalArrangement = Arrangement.spacedBy(PvotSpacing.XS),
            ) {
                PvotButton(
                    text = primaryActionLabel,
                    onClick = onPrimaryAction,
                    enabled = primaryActionEnabled,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (secondaryActionLabel != null && onSecondaryAction != null) {
                    PvotButton(
                        text = secondaryActionLabel,
                        onClick = onSecondaryAction,
                        style = PvotButtonStyle.Text,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
