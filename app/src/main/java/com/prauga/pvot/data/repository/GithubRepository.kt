// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.data.repository

import com.prauga.pvot.data.model.GithubRepo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object GithubRepository {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun getAppsRepos(): Result<List<GithubRepo>> {
        return try {
            val repos: List<GithubRepo> = client.get("https://api.github.com/orgs/PVOT-OSS/repos").body()
            val appsRepos = repos.filter { "apps" in it.topics }
            Result.success(appsRepos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
