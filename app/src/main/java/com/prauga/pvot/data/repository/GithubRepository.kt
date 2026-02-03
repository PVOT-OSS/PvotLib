// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.data.repository

import com.prauga.pvot.data.model.GithubRepo
import com.prauga.pvot.data.model.GithubUser
import com.prauga.pvot.utils.Constants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.IOException
import java.nio.channels.UnresolvedAddressException

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
            val repos: List<GithubRepo> = client.get(Constants.GITHUB_ORG_REPOS_URL).body()
            val appsRepos = repos.filter { "apps" in it.topics }
            Result.success(appsRepos)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: UnresolvedAddressException) {
            Result.failure(e)
        } catch (e: HttpRequestTimeoutException) {
            Result.failure(e)
        } catch (e: ClientRequestException) {
            Result.failure(e)
        } catch (e: ServerResponseException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    }

    suspend fun getUser(username: String): Result<GithubUser> {
        return try {
            val user: GithubUser = client.get(Constants.githubUserUrl(username)).body()
            Result.success(user)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: UnresolvedAddressException) {
            Result.failure(e)
        } catch (e: HttpRequestTimeoutException) {
            Result.failure(e)
        } catch (e: ClientRequestException) {
            Result.failure(e)
        } catch (e: ServerResponseException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    }
}
