// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

public object PvotShapes {
    public val Small: Shape = RoundedCornerShape(8.dp)
    public val Card: Shape = RoundedCornerShape(16.dp)
    public val Selection: Shape = RoundedCornerShape(20.dp)
    public val Pill: Shape = CircleShape
}
