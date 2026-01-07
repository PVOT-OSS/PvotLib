// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.pvot.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.prauga.pvot.R
import org.prauga.pvot.components.CatalogCard
import org.prauga.pvot.designsystem.components.navigation.PvotNavBar
import org.prauga.pvot.designsystem.components.navigation.PvotTabItem
import org.prauga.pvot.designsystem.components.picker.PvotClockPicker
import org.prauga.pvot.designsystem.components.picker.PvotDurationPicker
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.minutes

@Composable
fun CatalogScreen(
    label: String,
    modifier: Modifier
) {
    var selectedTime by remember { mutableStateOf(LocalTime.of(0, 0)) }
    var selectedDuration by remember { mutableStateOf(30.minutes) }
    var demoNavTab by remember { mutableIntStateOf(0) }

    val previewTabs = listOf(
        PvotTabItem(
            iconRes = R.drawable.ic_home,
            labelRes = R.string.tab_home,
            contentDescriptionRes = R.string.cd_home
        ),
        PvotTabItem(
            iconRes = R.drawable.ic_apps,
            labelRes = R.string.tab_apps,
            contentDescriptionRes = R.string.cd_apps
        ),
        PvotTabItem(
            iconRes = R.drawable.ic_catalog,
            labelRes = R.string.tab_catalog,
            contentDescriptionRes = R.string.cd_catalog
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            CatalogCard(title = "PvotNavBar") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    PvotNavBar(
                        selectedTab = demoNavTab,
                        onTabClick = { demoNavTab = it },
                        tabs = previewTabs
                    )
                }
            }
        }

        item {
            CatalogCard(title = "PvotClockPicker") {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PvotClockPicker(
                        time = selectedTime,
                        onTimeChange = { selectedTime = it }
                    )
                    Text(
                        text = "Selected: ${selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }

        item {
            CatalogCard(title = "PvotDurationPicker") {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PvotDurationPicker(
                        duration = selectedDuration,
                        onDurationChange = { selectedDuration = it }
                    )
                    Text(
                        text = "Selected: ${
                            selectedDuration.toComponents { h, m, s, _ ->
                                "%02d:%02d:%02d".format(
                                    h,
                                    m,
                                    s
                                )
                            }
                        }",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }
    }
}