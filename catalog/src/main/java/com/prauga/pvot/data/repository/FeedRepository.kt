// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.data.repository

import com.prauga.pvot.data.model.FeedEntry
import com.prauga.pvot.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException

object FeedRepository {
    suspend fun getFeedEntries(): Result<List<FeedEntry>> = withContext(Dispatchers.IO) {
        try {
            val url = URL(Constants.FEED_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val entries = mutableListOf<FeedEntry>()

            connection.inputStream.use { inputStream ->
                val factory = XmlPullParserFactory.newInstance()
                factory.isNamespaceAware = true
                val parser = factory.newPullParser()
                parser.setInput(inputStream, null)

                var eventType = parser.eventType
                var currentEntry: MutableMap<String, String>? = null
                var currentTag: String? = null
                var inAuthor = false

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            currentTag = parser.name
                            when (currentTag) {
                                "entry" -> currentEntry = mutableMapOf()
                                "author" -> inAuthor = true
                                "link" -> {
                                    if (currentEntry != null && !inAuthor) {
                                        val href = parser.getAttributeValue(null, "href")
                                        if (href != null) {
                                            currentEntry["link"] = href
                                        }
                                    }
                                }
                                "category" -> {
                                    if (currentEntry != null) {
                                        val term = parser.getAttributeValue(null, "term")
                                        if (term != null) {
                                            currentEntry["category"] = term
                                        }
                                    }
                                }
                            }
                        }
                        XmlPullParser.TEXT -> {
                            val text = parser.text?.trim()
                            if (currentEntry != null && !text.isNullOrEmpty()) {
                                when {
                                    currentTag == "name" && inAuthor -> currentEntry["author"] = text
                                    currentTag == "title" && !inAuthor -> currentEntry["title"] = text
                                    currentTag == "published" -> currentEntry["published"] = text
                                    currentTag == "summary" -> currentEntry["summary"] = text
                                }
                            }
                        }
                        XmlPullParser.END_TAG -> {
                            when (parser.name) {
                                "entry" -> {
                                    currentEntry?.let { entry ->
                                        entries.add(
                                            FeedEntry(
                                                title = entry["title"] ?: "",
                                                link = entry["link"] ?: "",
                                                published = entry["published"] ?: "",
                                                summary = entry["summary"] ?: "",
                                                category = entry["category"],
                                                author = entry["author"]
                                            )
                                        )
                                    }
                                    currentEntry = null
                                }
                                "author" -> inAuthor = false
                            }
                            currentTag = null
                        }
                    }
                    eventType = parser.next()
                }
            }

            Result.success(entries)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: SocketTimeoutException) {
            Result.failure(e)
        } catch (e: UnknownHostException) {
            Result.failure(e)
        } catch (e: XmlPullParserException) {
            Result.failure(e)
        }
    }
}
