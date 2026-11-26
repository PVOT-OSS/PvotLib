// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.R
import com.prauga.pvot.designsystem.theme.PvotSpacing

/** Sizes for [PvotSearchField]. */
public object PvotSearchFieldDefaults {
    /** Height of the field, matching a [PvotGlassIconButton] beside it. */
    public val Height: Dp = PvotGlassIconButtonDefaults.Size

    /** Size of the search and clear icons. */
    public val IconSize: Dp = 20.dp

    /** Touch target of the clear button. */
    public val ClearTouchSize: Dp = 44.dp
}

/**
 * A search field on a glass capsule: a search icon, the query, and a clear button once there is
 * text. It has no focus ring or filled box, so it sits cleanly in a floating bar or sheet.
 *
 * @param query The current query
 * @param onQueryChange Called as the query is edited, and with an empty query when cleared
 * @param modifier Modifier for the capsule; use it for width and focus handling
 * @param placeholder What shows while the query is empty
 * @param onSearch Called when the keyboard's search action is pressed
 */
@Composable
public fun PvotSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(R.string.pvot_search_placeholder),
    onSearch: () -> Unit = {},
) {
    val colors = MaterialTheme.colorScheme
    PvotGlassSurface(
        modifier = modifier.height(PvotSearchFieldDefaults.Height),
        contentAlignment = Alignment.CenterStart,
        contentPadding = PaddingValues(start = PvotSpacing.L, end = PvotSpacing.XS),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.pvot_ic_search),
                contentDescription = null,
                tint = colors.onSurfaceVariant,
                modifier = Modifier.size(PvotSearchFieldDefaults.IconSize),
            )
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.onSurface),
                cursorBrush = SolidColor(colors.primary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                modifier = Modifier.weight(1f).padding(horizontal = PvotSpacing.S),
                decorationBox = { field ->
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                        if (query.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyLarge,
                                color = colors.onSurfaceVariant,
                            )
                        }
                        field()
                    }
                },
            )
            if (query.isNotEmpty()) {
                ClearButton(onClick = { onQueryChange("") })
            }
        }
    }
}

@Composable
private fun ClearButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(PvotSearchFieldDefaults.ClearTouchSize)
            .clip(CircleShape)
            .clickable(onClickLabel = stringResource(R.string.pvot_search_clear), onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.pvot_ic_circle_x),
            contentDescription = stringResource(R.string.pvot_search_clear),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(PvotSearchFieldDefaults.IconSize),
        )
    }
}
