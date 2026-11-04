// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.catalog.hub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.prauga.pvot.designsystem.components.button.PvotButton
import com.prauga.pvot.designsystem.components.button.PvotButtonStyle
import com.prauga.pvot.designsystem.components.calendar.PvotDateHeader
import com.prauga.pvot.designsystem.components.calendar.PvotDayStrip
import com.prauga.pvot.designsystem.components.calendar.PvotEventCard
import com.prauga.pvot.designsystem.components.list.PvotAvatarStack
import com.prauga.pvot.designsystem.theme.PvotSpacing
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@Composable
internal fun CalendarPage(onBack: () -> Unit) {
    val today = remember { LocalDate.now() }
    var selected by remember { mutableStateOf(today) }
    var weekStart by remember {
        mutableStateOf(today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
    }
    var opened by remember { mutableIntStateOf(0) }
    val sendSnackbar = rememberSnackbarSender()
    DetailPage(
        title = "Calendar",
        summary =
            "Pieces for agenda screens: the date as a hero, a strip of days to pick from, event " +
                "cards with the event's color and its guests, and an avatar stack for any group of people.",
        onBack = onBack,
        code =
            "PvotDateHeader(date)\n" +
                "PvotDayStrip(week, selected, today, onSelect = { selected = it }, marked = { it in busy })\n" +
                "PvotEventCard(\"Design sync\", onClick = { open() }, accent = color, supportingText = \"15:00 - 16:00\")",
    ) {
        DemoSection(
            title = "Date header",
            caption = "The selected day below drives it",
            framed = false,
        ) {
            PvotDateHeader(selected)
        }
        DemoSection(
            title = "Day strip",
            caption = "Tap a day, or swipe for the next or previous week",
            framed = false,
        ) {
            PvotDayStrip(
                days = (0L until 7L).map { weekStart.plusDays(it) },
                selected = selected,
                today = today,
                onSelect = { selected = it },
                marked = { it.dayOfMonth % 3 == 0 },
                onSwipe = { next -> weekStart = weekStart.plusWeeks(if (next) 1 else -1) },
            )
        }
        DemoSection(title = "Event cards", caption = "Opened $opened times", framed = false) {
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(PvotSpacing.M),
            ) {
                PvotEventCard(
                    title = "Design sync",
                    onClick = { opened++ },
                    accent = MaterialTheme.colorScheme.primary,
                    supportingText = "15:00 - 16:00",
                    guests = listOf("Alice", "Bob", "Carol", "Dave"),
                    action = {
                        PvotButton(
                            text = "Join",
                            onClick = { sendSnackbar("Joining the call") },
                            style = PvotButtonStyle.Tonal,
                        )
                    },
                )
                PvotEventCard(
                    title = "Read a book",
                    onClick = { opened++ },
                    accent = MaterialTheme.colorScheme.tertiary,
                    supportingText = "21:00 - 21:45",
                )
                PvotEventCard(
                    title = "Pay rent",
                    onClick = { opened++ },
                    accent = MaterialTheme.colorScheme.secondary,
                    supportingText = "Done",
                    done = true,
                    dense = true,
                )
            }
        }
        DemoSection(
            title = "Avatar stack",
            caption = "Three avatars, then a count",
            framed = false,
        ) {
            PvotAvatarStack(listOf("Alice", "Bob", "Carol", "Dave", "Erin"))
        }
    }
}
