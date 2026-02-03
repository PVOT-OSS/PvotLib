// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.utils

object Constants {
    const val GITHUB_API_BASE_URL = "https://api.github.com"
    const val GITHUB_ORG_REPOS_URL = "$GITHUB_API_BASE_URL/orgs/PVOT-OSS/repos"
    fun githubUserUrl(username: String) = "$GITHUB_API_BASE_URL/users/$username"

    private const val GITHUB_OPENGRAPH_BASE_URL = "https://opengraph.githubassets.com/1"
    fun githubSocialPreviewUrl(owner: String, repo: String) = "$GITHUB_OPENGRAPH_BASE_URL/$owner/$repo"

    // Project Links
    const val GITHUB_REPO_URL = "https://github.com/PVOT-OSS/PvotLib"
    const val GITHUB_ORG_URL = "https://github.com/PVOT-OSS"
    const val WEBSITE_URL = "https://squadri.me"
    // Feed
    const val FEED_URL = "$WEBSITE_URL/feed.xml"
}
