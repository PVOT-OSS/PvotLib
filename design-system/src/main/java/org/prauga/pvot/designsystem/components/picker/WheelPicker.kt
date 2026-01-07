package org.prauga.pvot.designsystem.components.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WheelPicker(
    items: List<String>,
    visibleItemsCount: Int = 5,
    itemHeight: Dp = 40.dp,
    onItemSelected: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(listState)

    val centerIndex = visibleItemsCount / 2

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                onItemSelected(index + centerIndex)
            }
    }

    Box(
        modifier = Modifier
            .height(itemHeight * visibleItemsCount)
            .width(80.dp)
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
                    Text(
                        text = item,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .height(itemHeight)
                .fillMaxWidth()
                .background(
                    Color.White.copy(alpha = 0.08f),
                    RoundedCornerShape(12.dp)
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
    MaterialTheme {
        Surface(color = Color(0xFF121212)) {
            WheelPicker(
                items = (0..23).map { "%02d".format(it) }
            ) {}
        }
    }
}
