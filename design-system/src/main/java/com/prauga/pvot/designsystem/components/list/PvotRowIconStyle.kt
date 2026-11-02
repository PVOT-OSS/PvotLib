// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.list

/** How a settings row draws its leading icon. */
public enum class PvotRowIconStyle {
    /** In a tinted circle, for rows standing on their own. */
    Contained,

    /**
     * Bare and tinted, for rows grouped in a [PvotListGroup], where circles would crowd the card.
     */
    Plain,
}

/** What ends a [PvotPreferenceRow] that opens something. */
public enum class PvotRowChevron {
    /** A chevron, the usual sign of a row that opens a screen. */
    Chevron,

    /** An arrow, for rows grouped in a [PvotListGroup]. */
    Arrow,
}
