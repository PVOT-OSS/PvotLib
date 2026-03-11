// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.prauga.pvot.R
import com.prauga.pvot.components.FeedCard
import com.prauga.pvot.data.model.FeedEntry
import com.prauga.pvot.data.repository.FeedRepository
import com.prauga.coreui.PvotEmptyContent
import com.prauga.coreui.PvotErrorContent
import com.prauga.coreui.PvotLoadingContent
import com.prauga.coreui.UiState

@Composable
fun HomeScreen(
    label: String,
    modifier: Modifier = Modifier
) {
    var uiState by remember { mutableStateOf<UiState<List<FeedEntry>>>(UiState.Loading) }
    val context = LocalContext.current

    fun loadFeed() {
        uiState = UiState.Loading
    }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Loading) {
            val result = FeedRepository.getFeedEntries()
            uiState = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Unknown error") }
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is UiState.Loading -> {
                PvotLoadingContent(modifier = Modifier.align(Alignment.Center))
            }

            is UiState.Error -> {
                PvotErrorContent(
                    title = stringResource(R.string.home_error_title),
                    message = state.message,
                    onRetry = { loadFeed() },
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    PvotEmptyContent(
                        message = stringResource(R.string.home_empty_title),
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp,
                            bottom = 100.dp
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(state.data) { entry ->
                            FeedCard(
                                entry = entry,
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(entry.link))
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
