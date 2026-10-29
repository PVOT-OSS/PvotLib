// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.catalog.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.prauga.pvot.catalog.data.model.GithubRepo
import com.prauga.pvot.catalog.data.model.GithubUser
import com.prauga.pvot.catalog.utils.Constants
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import java.text.DateFormat
import java.util.Date

/** A GitHub request that failed, with a message that is fit to show the user. */
class GithubException(message: String) : Exception(message)

/**
 * Reads GitHub's public API, which allows only 60 requests an hour per IP address without a
 * token. To stay well under that, every response is cached on the device: a cached answer is
 * reused for [CacheTtlMillis] without asking GitHub, then revalidated with its ETag (a 304 does
 * not count against the limit), and an expired answer is still served when GitHub can't be
 * reached or has run out of requests.
 */
object GithubRepository {
    private const val PREFS_NAME = "github_cache"
    private const val CacheTtlMillis = 10 * 60 * 1000L

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = HttpClient(Android)

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    suspend fun getAppsRepos(): Result<List<GithubRepo>> =
        fetch(Constants.GITHUB_ORG_REPOS_URL) { body ->
            json.decodeFromString<List<GithubRepo>>(body).filter { "apps" in it.topics }
        }

    suspend fun getUser(username: String): Result<GithubUser> =
        fetch(Constants.githubUserUrl(username)) { body ->
            json.decodeFromString<GithubUser>(body)
        }

    private suspend fun <T> fetch(url: String, decode: (String) -> T): Result<T> {
        val cached = readCache(url)
        val now = System.currentTimeMillis()
        if (cached != null && now - cached.savedAt < CacheTtlMillis) {
            runCatching { decode(cached.body) }.onSuccess { return Result.success(it) }
        }

        return try {
            val response = client.get(url) {
                header(HttpHeaders.Accept, "application/vnd.github+json")
                cached?.etag?.let { header(HttpHeaders.IfNoneMatch, it) }
            }
            when {
                response.status == HttpStatusCode.NotModified && cached != null -> {
                    writeCache(url, cached.body, cached.etag)
                    Result.success(decode(cached.body))
                }
                response.status.isSuccess() -> {
                    val body = response.bodyAsText()
                    val value = decode(body)
                    writeCache(url, body, response.headers[HttpHeaders.ETag])
                    Result.success(value)
                }
                else -> staleOrFailure(cached, decode, failureMessage(response))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            staleOrFailure(cached, decode, "Couldn't reach GitHub. Check your connection.")
        }
    }

    private fun <T> staleOrFailure(
        cached: CachedResponse?,
        decode: (String) -> T,
        message: String
    ): Result<T> {
        val stale = cached?.let { runCatching { decode(it.body) }.getOrNull() }
        return if (stale != null) Result.success(stale) else Result.failure(GithubException(message))
    }

    private fun failureMessage(response: HttpResponse): String {
        val outOfRequests = response.headers["x-ratelimit-remaining"] == "0" ||
            response.status == HttpStatusCode.TooManyRequests
        if (!outOfRequests) return "GitHub answered with an error (${response.status.value})."
        val resetAt = response.headers["x-ratelimit-reset"]?.toLongOrNull()
            ?.let { DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(it * 1000)) }
        return if (resetAt != null) {
            "GitHub's hourly limit for this network was reached. Try again after $resetAt."
        } else {
            "GitHub's hourly limit for this network was reached. Try again later."
        }
    }

    private class CachedResponse(val body: String, val etag: String?, val savedAt: Long)

    private fun readCache(url: String): CachedResponse? {
        if (!::prefs.isInitialized) return null
        val body = prefs.getString("$url#body", null) ?: return null
        return CachedResponse(
            body = body,
            etag = prefs.getString("$url#etag", null),
            savedAt = prefs.getLong("$url#time", 0L)
        )
    }

    private fun writeCache(url: String, body: String, etag: String?) {
        if (!::prefs.isInitialized) return
        prefs.edit()
            .putString("$url#body", body)
            .putString("$url#etag", etag)
            .putLong("$url#time", System.currentTimeMillis())
            .apply()
    }
}
