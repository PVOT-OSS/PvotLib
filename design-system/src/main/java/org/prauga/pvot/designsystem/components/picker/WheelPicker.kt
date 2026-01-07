// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.pvot.designsystem.components.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.prauga.pvot.designsystem.theme.PvotAppTheme

@Composable
fun WheelPicker(
    items: List<Int>,
    suffix: String,
    visibleItemsCount: Int = 5,
    itemHeight: Dp = 40.dp,
    colors: PvotPickerColors = LocalPvotPickerColors.current,
    onItemSelected: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(listState)
    val centerIndex = visibleItemsCount / 2

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                val selected = (index + centerIndex)
                    .coerceIn(items.indices)
                onItemSelected(items[selected])
            }
    }

    Box(
        modifier = Modifier
            .height(itemHeight * visibleItemsCount)
            .width(110.dp)
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(vertical = itemHeight * centerIndex),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(items) { index, item ->
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = items[index].toString(),
                            fontSize = 28.sp,
                            color = colors.textColor
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = suffix,
                            fontSize = 16.sp,
                            color = colors.textSecondaryColor
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .height(itemHeight)
                .fillMaxWidth()
                .background(
                    colors.selectionBackgroundColor,
                    RoundedCornerShape(20.dp)
                )
        )
    }
}

@Preview(
    name = "Wheel Picker – Hours",
    showBackground = true,
    backgroundColor = 0xFF121212,
    widthDp = 120,
    heightDp = 200
)
@Composable
fun WheelPickerPreview() {
    PvotAppTheme {
        Surface {
            WheelPicker(
                items = (0..23).toList(),
                suffix = "hours",
                colors = pvotPickerColors()
            ) {}
        }
    }
}
