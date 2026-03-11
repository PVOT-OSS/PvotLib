// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.prauga.coreui.PvotEmptyContent
import com.prauga.coreui.PvotErrorContent
import com.prauga.coreui.PvotLoadingContent
import com.prauga.coreui.UiState
import com.prauga.pvot.R
import com.prauga.pvot.components.AppCard
import com.prauga.pvot.data.model.GithubRepo
import com.prauga.pvot.data.repository.GithubRepository
import com.prauga.pvot.designsystem.components.PvotScreen

@Composable
fun AppsScreen(
    label: String,
    modifier: Modifier = Modifier
) {
    var uiState by remember { mutableStateOf<UiState<List<GithubRepo>>>(UiState.Loading) }
    val context = LocalContext.current

    fun loadRepos() {
        uiState = UiState.Loading
    }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Loading) {
            val result = GithubRepository.getAppsRepos()
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
                    title = stringResource(R.string.apps_error_title),
                    message = state.message,
                    onRetry = { loadRepos() },
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PvotEmptyContent(message = stringResource(R.string.apps_empty_title))
                        Text(
                            text = stringResource(R.string.apps_empty_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                } else {
                    PvotScreen {
                        item {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(state.data) { repo ->
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
}
