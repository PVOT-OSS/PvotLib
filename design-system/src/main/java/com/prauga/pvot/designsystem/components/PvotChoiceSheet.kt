// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import com.prauga.pvot.designsystem.R
import com.prauga.pvot.designsystem.components.list.PvotListRow

/** Defaults for [PvotChoiceSheet]. */
public object PvotChoiceSheetDefaults {

    /** Share of the window's height the list may take before it scrolls. */
    public const val MaxHeightFraction: Float = 0.6f
}

/**
 * One of [entries] picked from a [PvotBottomSheet], such as a setting's value: the current one is
 * checked and scrolled into view when the sheet opens, and a long list scrolls inside the sheet.
 *
 * @param visible Whether the sheet is shown
 * @param title The sheet's title, usually the setting's name
 * @param entries The choices, in order
 * @param selected Index of the current choice in [entries], or -1 for none
 * @param onDismiss Called when the sheet is dismissed without a choice
 * @param onSelect Called with the index of the chosen entry
 * @param modifier Modifier for the sheet
 */
@Composable
public fun PvotChoiceSheet(
    visible: Boolean,
    title: String,
    entries: List<String>,
    selected: Int,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    PvotBottomSheet(
        visible = visible,
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = title,
    ) {
        val state = rememberLazyListState()
        LaunchedEffect(visible) { if (visible && selected > 0) state.scrollToItem(selected) }
        val maxHeight =
            with(LocalDensity.current) { LocalWindowInfo.current.containerSize.height.toDp() } *
                PvotChoiceSheetDefaults.MaxHeightFraction
        LazyColumn(state = state, modifier = Modifier.heightIn(max = maxHeight)) {
            itemsIndexed(entries) { index, entry ->
                PvotListRow(
                    title = entry,
                    selected = index == selected,
                    trailing =
                        if (index == selected) {
                            {
                                Icon(
                                    painterResource(R.drawable.pvot_ic_check),
                                    contentDescription = null,
                                )
                            }
                        } else {
                            null
                        },
                    onClick = { onSelect(index) },
                )
            }
        }
    }
}
