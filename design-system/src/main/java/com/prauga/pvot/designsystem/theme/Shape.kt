// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/** Shared corner shapes. */
public object PvotShapes {
    /** Small elements, such as skeleton placeholders. */
    public val Small: Shape = RoundedCornerShape(8.dp)
    /** Cards and grouped surfaces. */
    public val Card: Shape = RoundedCornerShape(16.dp)
    /** The selection row of a picker wheel. */
    public val Selection: Shape = RoundedCornerShape(20.dp)
    /** Fully rounded elements, such as an unselected nav item. */
    public val Pill: Shape = CircleShape
}
