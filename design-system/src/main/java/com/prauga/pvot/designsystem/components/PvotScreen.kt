// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.navigation.LocalPvotNavBarSizes
import com.prauga.pvot.designsystem.theme.PvotSpacing

/** Defaults for [PvotScreen]. */
public object PvotScreenDefaults {

    public val HorizontalPadding: Dp = PvotSpacing.L

    public val TopPadding: Dp = PvotSpacing.L

    /** Gap left below the last item, on top of the nav bar's own height. */
    public val BottomSpacing: Dp = 28.dp

    public val VerticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(PvotSpacing.M)

    @Composable
    public fun contentPadding(
        horizontal: Dp = HorizontalPadding,
        top: Dp = TopPadding,
        bottom: Dp = LocalPvotNavBarSizes.current.barHeight + BottomSpacing
    ): PaddingValues = PaddingValues(
        start = horizontal,
        end = horizontal,
        top = top,
        bottom = bottom
    )
}

/**
 * A vertically scrolling screen container that keeps its last item clear of [PvotNavBar].
 *
 * @param modifier Modifier for the scroll container
 * @param state The scroll state, hoist it to observe or restore scroll position
 * @param contentPadding Padding around the content, inside the scroll container
 * @param verticalArrangement Vertical arrangement of the items
 * @param horizontalAlignment Horizontal alignment of the items
 */
@Composable
public fun PvotScreen(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PvotScreenDefaults.contentPadding(),
    verticalArrangement: Arrangement.Vertical = PvotScreenDefaults.VerticalArrangement,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: LazyListScope.() -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
        content = content
    )
}
