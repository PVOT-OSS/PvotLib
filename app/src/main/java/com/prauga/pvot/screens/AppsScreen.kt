// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prauga.pvot.components.AppCard
import com.prauga.pvot.data.model.GithubRepo
import com.prauga.pvot.data.repository.GithubRepository

sealed class AppsUiState {
    data object Loading : AppsUiState()
    data class Success(val repos: List<GithubRepo>) : AppsUiState()
    data class Error(val message: String) : AppsUiState()
}

@Composable
fun AppsScreen(
    label: String,
    modifier: Modifier = Modifier
) {
    var uiState by remember { mutableStateOf<AppsUiState>(AppsUiState.Loading) }
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        if (uiState is AppsUiState.Loading) {
            val result = GithubRepository.getAppsRepos()
            uiState = result.fold(
                onSuccess = { AppsUiState.Success(it) },
                onFailure = { AppsUiState.Error(it.message ?: "Unknown error") }
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.background
                    ),
                    startY = 0f,
                    endY = 800f
                )
            )
    ) {
        when (val state = uiState) {
            is AppsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is AppsUiState.Error -> {
                // Error implementation...
            }

            is AppsUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 140.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Modern Header
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Explore our",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Ecosystem",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = (-0.5).sp
                            )
                        }
                    }

                    // Unified Card Layout
                    items(state.repos) { repo ->
                        AppCard(
                            repo = repo,
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.htmlUrl))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}
