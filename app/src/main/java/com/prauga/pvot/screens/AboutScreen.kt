// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.prauga.coreui.PvotSectionHeader
import com.prauga.pvot.BuildConfig
import com.prauga.pvot.R
import com.prauga.pvot.components.DeveloperCard
import com.prauga.pvot.data.model.GithubUser
import com.prauga.pvot.data.repository.GithubRepository
import com.prauga.pvot.designsystem.components.PvotCard
import com.prauga.pvot.designsystem.components.PvotScreen
import com.prauga.pvot.utils.Constants
import com.prauga.pvot.utils.PreferencesManager

data class TeamMember(
    val username: String,
    val role: String
)

private val teamMembers = listOf(
    TeamMember("danascape", "Lead Developer"),
    TeamMember("sxivansx", "Lead Designer")
)

data class ProjectLink(
    val titleRes: Int,
    val url: String,
    val iconRes: Int
)

private val projectLinks = listOf(
    ProjectLink(R.string.about_link_github_repo, Constants.GITHUB_REPO_URL, R.drawable.ic_github),
    ProjectLink(R.string.about_link_github_org, Constants.GITHUB_ORG_URL, R.drawable.ic_github),
    ProjectLink(R.string.about_link_website, Constants.WEBSITE_URL, R.drawable.ic_link)
)

@Composable
fun AboutScreen(
    label: String,
    modifier: Modifier = Modifier
) {
    var teamUsers by remember { mutableStateOf<Map<String, GithubUser>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val users = mutableMapOf<String, GithubUser>()
        teamMembers.forEach { member ->
            GithubRepository.getUser(member.username).onSuccess { user ->
                users[member.username] = user
            }
        }
        teamUsers = users
        isLoading = false
    }

    PvotScreen(modifier = modifier) {
        item {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // App Info Section
        item {
            PvotCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.about_app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.about_version, BuildConfig.VERSION_NAME),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = stringResource(R.string.about_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }

        // Team Section
        item {
            PvotSectionHeader(title = stringResource(R.string.about_section_team))
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else {
            items(teamMembers) { member ->
                teamUsers[member.username]?.let { user ->
                    DeveloperCard(
                        user = user,
                        role = member.role,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.htmlUrl))
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }

        // Project Links Section
        item {
            PvotSectionHeader(title = stringResource(R.string.about_section_links))
        }

        items(projectLinks) { link ->
            PvotCard(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link.url))
                    context.startActivity(intent)
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = link.iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(link.titleRes),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        // Settings Section
        item {
            PvotSectionHeader(title = stringResource(R.string.about_section_settings))
        }

        item {
            val dynamicColorEnabled by PreferencesManager.dynamicColorEnabled.collectAsState()

            PvotCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.about_dynamic_colors_title),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.about_dynamic_colors_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Switch(
                        checked = dynamicColorEnabled,
                        onCheckedChange = { PreferencesManager.setDynamicColorEnabled(it) }
                    )
                }
            }
        }
    }
}
