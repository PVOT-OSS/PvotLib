// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.coreui

/**
 * The loading state of a screen's content.
 *
 * Pair each state with the matching component: [PvotLoadingContent] for [Loading],
 * [PvotErrorContent] for [Error], and your own content for [Success].
 *
 * @param T The type of the loaded content
 */
sealed interface UiState<out T> {

    /** The content has been requested but has not arrived yet. */
    data object Loading : UiState<Nothing>

    /** The content arrived. */
    data class Success<T>(val data: T) : UiState<T>

    /**
     * The content could not be loaded.
     *
     * @param message Text to show the user
     * @param cause The underlying failure, kept so callers can tell a network error
     * from a parse error without matching on [message]
     */
    data class Error(
        val message: String,
        val cause: Throwable? = null
    ) : UiState<Nothing>
}
