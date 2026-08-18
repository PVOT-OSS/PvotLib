// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.prauga.coreui.PvotEmptyContent
import com.prauga.coreui.PvotErrorContent
import com.prauga.coreui.PvotLoadingContent
import com.prauga.coreui.PvotSectionHeader
import com.prauga.pvot.R
import com.prauga.pvot.components.CatalogCard
import com.prauga.pvot.designsystem.components.PvotCard
import com.prauga.pvot.designsystem.components.PvotScreen
import com.prauga.pvot.designsystem.components.PvotSkeleton
import com.prauga.pvot.designsystem.components.navigation.PvotNavBar
import com.prauga.pvot.designsystem.components.navigation.PvotNavBarItem
import com.prauga.pvot.designsystem.components.navigation.PvotTabItem
import com.prauga.pvot.designsystem.components.picker.PvotClockPicker
import com.prauga.pvot.designsystem.components.picker.PvotDurationPicker
import com.prauga.pvot.designsystem.modifier.pvotReveal
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Duration.Companion.minutes

@Composable
fun CatalogScreen(
    label: String,
    modifier: Modifier = Modifier
) {
    var selectedTime by remember { mutableStateOf(LocalTime.of(0, 0)) }
    var selectedDuration by remember { mutableStateOf(30.minutes) }
    var demoNavTab by remember { mutableIntStateOf(0) }
    var slotNavTab by remember { mutableIntStateOf(0) }
    var cardTaps by remember { mutableIntStateOf(0) }
    var retries by remember { mutableIntStateOf(0) }
    var revealRun by remember { mutableIntStateOf(0) }

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

    PvotScreen(
        modifier = modifier,
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
            CatalogCard(title = stringResource(R.string.catalog_navbar)) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
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
            CatalogCard(title = stringResource(R.string.catalog_navbar_slots)) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    PvotNavBar {
                        SlotTabs.forEachIndexed { index, tab ->
                            PvotNavBarItem(
                                selected = index == slotNavTab,
                                onClick = { slotNavTab = index },
                                icon = {
                                    Icon(
                                        painter = painterResource(tab.iconRes),
                                        contentDescription = stringResource(
                                            tab.contentDescriptionRes
                                        ),
                                        modifier = Modifier.size(22.dp),
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                },
                                label = {
                                    Text(
                                        text = stringResource(tab.labelRes),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        item {
            CatalogCard(title = stringResource(R.string.catalog_clock_picker)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PvotClockPicker(
                        time = selectedTime,
                        onTimeChange = { selectedTime = it }
                    )
                    Text(
                        text = stringResource(
                            R.string.catalog_selected_time,
                            selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }

        item {
            CatalogCard(title = stringResource(R.string.catalog_duration_picker)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PvotDurationPicker(
                        duration = selectedDuration,
                        onDurationChange = { selectedDuration = it }
                    )
                    Text(
                        text = stringResource(
                            R.string.catalog_selected_duration,
                            selectedDuration.toComponents { h, m, s, _ ->
                                String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
                            }
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }

        item {
            CatalogCard(title = stringResource(R.string.catalog_card)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PvotCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.catalog_card_static),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    PvotCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { cardTaps++ }
                    ) {
                        Text(
                            text = stringResource(R.string.catalog_card_clickable),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.catalog_card_taps, cardTaps),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        item {
            CatalogCard(title = stringResource(R.string.catalog_skeleton)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PvotSkeleton(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PvotSkeleton(Modifier.fillMaxWidth(0.6f).height(16.dp))
                        PvotSkeleton(Modifier.fillMaxWidth().height(12.dp))
                        PvotSkeleton(Modifier.fillMaxWidth(0.8f).height(12.dp))
                    }
                }
            }
        }

        item {
            CatalogCard(title = stringResource(R.string.catalog_section_header)) {
                PvotSectionHeader(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(R.string.catalog_section_header_sample)
                )
            }
        }

        item {
            CatalogCard(title = stringResource(R.string.catalog_loading)) {
                PvotLoadingContent()
            }
        }

        item {
            CatalogCard(title = stringResource(R.string.catalog_empty)) {
                PvotEmptyContent(message = stringResource(R.string.catalog_empty_sample))
            }
        }

        item {
            CatalogCard(title = stringResource(R.string.catalog_error)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PvotErrorContent(
                        title = stringResource(R.string.catalog_error_sample_title),
                        message = stringResource(R.string.catalog_error_sample_message),
                        onRetry = { retries++ }
                    )
                    Text(
                        text = stringResource(R.string.catalog_error_retries, retries),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }

        item {
            CatalogCard(title = stringResource(R.string.catalog_reveal)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    key(revealRun) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(RevealSampleCount) { index ->
                                PvotSkeleton(
                                    modifier = Modifier
                                        .pvotReveal(index)
                                        .fillMaxWidth()
                                        .height(16.dp)
                                )
                            }
                        }
                    }
                    TextButton(onClick = { revealRun++ }) {
                        Text(stringResource(R.string.catalog_reveal_replay))
                    }
                }
            }
        }
    }
}

private const val RevealSampleCount = 4

private val SlotTabs = listOf(
    PvotTabItem(
        iconRes = R.drawable.ic_home,
        labelRes = R.string.catalog_tab_one,
        contentDescriptionRes = R.string.cd_catalog_tab_one
    ),
    PvotTabItem(
        iconRes = R.drawable.ic_apps,
        labelRes = R.string.catalog_tab_two,
        contentDescriptionRes = R.string.cd_catalog_tab_two
    ),
    PvotTabItem(
        iconRes = R.drawable.ic_catalog,
        labelRes = R.string.catalog_tab_three,
        contentDescriptionRes = R.string.cd_catalog_tab_three
    )
)
