// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable

/**
 * A [ComponentActivity] that has already opted into edge-to-edge layout.
 *
 * Extend this instead of [ComponentActivity] to get the window setup every Pvot
 * screen expects, then set content with [setPvotContent].
 */
abstract class PvotBaseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    /**
     * Sets this activity's Compose content.
     *
     * @param content The composable hierarchy, typically wrapped in PvotAppTheme
     */
    fun setPvotContent(content: @Composable () -> Unit) {
        setContent { content() }
    }
}
