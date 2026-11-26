// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Sizes for [PvotPageSheet]. */
public object PvotPageSheetDefaults {
    /** Gap between the status bar and the sheet, where the page behind stays in view. */
    public val TopGap: Dp = 12.dp
}

/** Controls a [PvotPageSheet], such as animating it closed before it leaves composition. */
@OptIn(ExperimentalMaterial3Api::class)
@Stable
public class PvotPageSheetState internal constructor(internal val sheetState: SheetState) {
    /** Slides the sheet away. */
    public suspend fun hide() {
        sheetState.hide()
    }
}

/** Remembers the state of a [PvotPageSheet] that opens straight to full height. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun rememberPvotPageSheetState(): PvotPageSheetState {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    return remember(sheetState) { PvotPageSheetState(sheetState) }
}

/**
 * A modal sheet that covers almost the whole screen, like a compose sheet: it slides up over the
 * current page, which stays in view above it, and has a drag handle. Drag it down, tap above it or
 * go back to dismiss it. The content lays itself out; it is given the sheet's full height.
 *
 * @param onDismissRequest Called when the user drags the sheet away, taps above it or goes back
 * @param modifier Modifier for the sheet surface
 * @param state The sheet's state, to animate it closed with [PvotPageSheetState.hide]
 * @param content The sheet's content
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun PvotPageSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    state: PvotPageSheetState = rememberPvotPageSheetState(),
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier.statusBarsPadding().padding(top = PvotPageSheetDefaults.TopGap),
        sheetState = state.sheetState,
        sheetMaxWidth = PvotBottomSheetDefaults.MaxWidth,
        shape = PvotBottomSheetDefaults.Shape,
        containerColor = PvotBottomSheetDefaults.containerColor,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        scrimColor = Color.Black.copy(alpha = PvotBottomSheetDefaults.ScrimAlpha),
        dragHandle = { PvotSheetHandle() },
        contentWindowInsets = { WindowInsets(0) },
    ) {
        Column(modifier = Modifier.fillMaxHeight(), content = content)
    }
}
