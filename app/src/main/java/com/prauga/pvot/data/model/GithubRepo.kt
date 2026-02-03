// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubOwner(
    val login: String
)

@Serializable
data class GithubRepo(
    val name: String,
    val description: String?,
    @SerialName("html_url") val htmlUrl: String,
    val topics: List<String>,
    @SerialName("stargazers_count") val stars: Int,
    val owner: GithubOwner
) {
    val socialPreviewUrl: String
        get() = "https://opengraph.githubassets.com/1/${owner.login}/$name"
}
