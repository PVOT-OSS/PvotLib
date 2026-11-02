// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.effects

/**
 * How much a glass hides what is behind it, from the clearest to the most frosted.
 *
 * Pick by what sits on the glass, not by taste: text is only readable on [Regular] or [Thick],
 * whose surface-colored tint keeps the contrast up whatever scrolls underneath. [Clear] is for
 * controls that carry an icon alone, and for controls over a photo that the screen already dims.
 * - [Clear]: a faint white haze and a light blur; what is behind reads through. Icon-only controls,
 *   call buttons over a dimmed photo.
 * - [Regular]: a surface tint at about half strength and a deeper blur. Bars, search fields, cards
 *   with a title.
 * - [Thick]: a nearly opaque surface tint and the deepest blur. Sheets and dialogs, where body text
 *   sits on the glass.
 */
public enum class PvotGlassMaterial {
    /** A faint haze; for icon-only controls. */
    Clear,

    /** A half-strength surface tint; for bars and titled cards. */
    Regular,

    /** A nearly opaque surface tint; for sheets and dialogs with body text. */
    Thick,
}
