// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.coreui

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * The indeterminate progress indicator shown while content loads.
 *
 * @param modifier Modifier for the indicator
 */
@Composable
fun PvotLoadingContent(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier)
}
