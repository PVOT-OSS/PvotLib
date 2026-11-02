// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.platform

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable

/**
 * Sets up the window the way every Pvot screen expects: drawn edge to edge, behind transparent
 * system bars whose icons follow the theme.
 *
 * [PvotBaseActivity] does this for you. Call it from `onCreate`, before [setPvotContent], in an
 * activity that has to extend something else, such as an `AppCompatActivity` or a dialer's in-call
 * activity.
 */
public fun ComponentActivity.enablePvotEdgeToEdge() {
    enableEdgeToEdge()
}

/**
 * Sets this activity's Compose content, for an activity that cannot extend [PvotBaseActivity].
 *
 * Call [enablePvotEdgeToEdge] first, so the content lays out the same as in a [PvotBaseActivity].
 *
 * ```kotlin
 * override fun onCreate(savedInstanceState: Bundle?) {
 *     super.onCreate(savedInstanceState)
 *     enablePvotEdgeToEdge()
 *     setPvotContent { PvotAppTheme { Screen() } }
 * }
 * ```
 *
 * @param content The composable hierarchy, typically wrapped in PvotAppTheme
 */
public fun ComponentActivity.setPvotContent(content: @Composable () -> Unit) {
    setContent { content() }
}
