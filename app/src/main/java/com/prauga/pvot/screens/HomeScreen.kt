// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import com.prauga.pvot.components.FeedCard
import com.prauga.pvot.data.model.FeedEntry
import com.prauga.pvot.data.repository.FeedRepository

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(val entries: List<FeedEntry>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@Composable
fun HomeScreen(
    label: String,
    modifier: Modifier = Modifier
) {
    var uiState by remember { mutableStateOf<HomeUiState>(HomeUiState.Loading) }
    val context = LocalContext.current

    fun loadFeed() {
        uiState = HomeUiState.Loading
    }

    LaunchedEffect(uiState) {
        if (uiState is HomeUiState.Loading) {
            val result = FeedRepository.getFeedEntries()
            uiState = result.fold(
                onSuccess = { HomeUiState.Success(it) },
                onFailure = { HomeUiState.Error(it.message ?: "Unknown error") }
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is HomeUiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Failed to load feed",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(onClick = { loadFeed() }) {
                        Text("Retry")
                    }
                }
            }

            is HomeUiState.Success -> {
                if (state.entries.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No posts found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
                        items(state.entries) { entry ->
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
