// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.data.model

data class FeedEntry(
    val title: String,
    val link: String,
    val published: String,
    val summary: String,
    val category: String?,
    val author: String?
)
