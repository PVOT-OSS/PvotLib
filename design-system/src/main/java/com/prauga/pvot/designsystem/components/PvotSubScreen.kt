// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.prauga.pvot.designsystem.navigation.PvotTopBar
import com.prauga.pvot.designsystem.navigation.rememberPvotTopBarCompact
import com.prauga.pvot.designsystem.theme.PvotSpacing

/**
 * A screen one level down from the app's tabs, such as settings or a picker: a [PvotTopBar] with a
 * back button and a large [title] that folds into the bar once [content] scrolls, over a
 * [PvotScreen] list that clears the system navigation bar. No nav bar.
 *
 * @param title The screen's title
 * @param onBack Called when the back button is pressed
 * @param modifier Modifier for the scaffold
 * @param verticalArrangement Spacing between the list's items
 * @param state The list's state, to scroll it from outside
 * @param actions Icon buttons at the end of the top bar, typically
 *   [com.prauga.pvot.designsystem.navigation.PvotTopBarAction]
 * @param listOverlay Content over the list and below the top bar, such as a
 *   [com.prauga.pvot.designsystem.components.list.PvotFastScroller]; kept below the bar so an edge
 *   control there can't take the bar's taps
 * @param content The list's items
 */
@Composable
public fun PvotSubScreen(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(PvotSpacing.L),
    state: LazyListState = rememberLazyListState(),
    actions: @Composable RowScope.() -> Unit = {},
    listOverlay: @Composable BoxScope.() -> Unit = {},
    content: LazyListScope.() -> Unit,
) {
    val compact = rememberPvotTopBarCompact(state)
    val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    PvotScreenScaffold(
        modifier = modifier,
        topBar = { PvotTopBar(title, onBack = onBack, compact = compact, actions = actions) },
    ) { padding ->
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            PvotScreen(
                state = state,
                contentPadding =
                    PvotScreenDefaults.contentPadding(
                        top = padding.calculateTopPadding() + PvotSpacing.S,
                        bottom = bottom + PvotSpacing.L,
                    ),
                verticalArrangement = verticalArrangement,
                horizontalAlignment = Alignment.Start,
                content = content,
            )
            Box(Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
                listOverlay()
            }
        }
    }
}
