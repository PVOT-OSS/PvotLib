// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.catalog.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.adaptive.PvotCenteredContent
import com.prauga.pvot.designsystem.adaptive.PvotTabletopSplit
import com.prauga.pvot.designsystem.adaptive.PvotTwoPane
import com.prauga.pvot.designsystem.adaptive.rememberPvotWindowLayout
import com.prauga.pvot.designsystem.theme.PvotShapes

private val DemoHeight = 200.dp
private val NarrowColumn = 180.dp

@Composable
internal fun AdaptivePage(onBack: () -> Unit) {
    val layout = rememberPvotWindowLayout()
    val posture =
        when {
            layout.tabletop -> "tabletop"
            layout.book -> "book"
            layout.hinge != null -> "flat"
            else -> "no fold"
        }
    DetailPage(
        title = "Adaptive layouts",
        summary =
            "The window's size class and fold, and layouts that use them: two panes that meet " +
                "at a book fold, a tabletop split at the hinge, and a column that stays readable " +
                "on a tablet. Resize the window or fold the device to see them change.",
        onBack = onBack,
        code =
            "val layout = rememberPvotWindowLayout()\nwhen {\n" +
                "    layout.tabletop -> PvotTabletopSplit(top = { Player() }, bottom = { Controls() })\n" +
                "    layout.twoPane -> PvotTwoPane(first = { List() }, second = { Detail() })\n" +
                "    else -> PvotCenteredContent { List() }\n}",
    ) {
        DemoSection(
            title = "This window",
            caption =
                "${layout.width} width · $posture · " +
                    if (layout.twoPane) "two panes" else "one column",
        ) {
            Text(
                text = "Unfold, rotate or resize to update",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        DemoSection(
            title = "Two panes",
            caption = "Split at the hinge when folded like a book, otherwise by weight",
        ) {
            PvotTwoPane(
                first = { DemoPane("List", MaterialTheme.colorScheme.primaryContainer) },
                second = { DemoPane("Detail", MaterialTheme.colorScheme.secondaryContainer) },
                modifier = Modifier.fillMaxWidth().height(DemoHeight),
            )
        }
        DemoSection(
            title = "Tabletop split",
            caption = "What to look at above the hinge, controls below",
        ) {
            PvotTabletopSplit(
                top = { DemoPane("Player", MaterialTheme.colorScheme.primaryContainer) },
                bottom = { DemoPane("Controls", MaterialTheme.colorScheme.tertiaryContainer) },
                modifier = Modifier.fillMaxWidth().height(DemoHeight),
            )
        }
        DemoSection(title = "Centred column", caption = "Capped at a width, centred") {
            PvotCenteredContent(maxWidth = NarrowColumn) {
                Box(Modifier.height(DemoHeight / 2)) {
                    DemoPane("Content", MaterialTheme.colorScheme.secondaryContainer)
                }
            }
        }
    }
}

@Composable
private fun DemoPane(label: String, color: Color) {
    Box(
        Modifier.fillMaxSize().clip(PvotShapes.Card).background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = MaterialTheme.typography.titleSmall)
    }
}
