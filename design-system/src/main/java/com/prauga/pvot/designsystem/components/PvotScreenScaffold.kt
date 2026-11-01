// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.Dp
import com.prauga.pvot.designsystem.effects.LocalPvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.pvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.rememberPvotGlassBackdrop
import com.prauga.pvot.designsystem.theme.PvotSpacing

/** Defaults for [PvotScreenScaffold]. */
public object PvotScreenScaffoldDefaults {

    /** The gap between the floating action and the edges of the screen. */
    public val FloatingActionMargin: Dp = PvotSpacing.L
}

/**
 * The frame of a screen with glass over it: the background and the content inside one glass
 * backdrop, and the bars, floating action, snackbars and overlays over it, each drawn as glass
 * over that backdrop by default.
 *
 * It wires what every glass screen otherwise repeats: the backdrop, the opaque background inside
 * it, [LocalPvotGlassBackdrop] for the chrome, and the padding that keeps the content clear of the
 * top bar. Content scrolls under the bars, which is what makes the glass worth drawing; the
 * padding is for the first item, not a frame. Inside [content] the local is null, since glass
 * inside its own backdrop shows only its fill; glass that belongs with the content goes in
 * [overlay].
 *
 * ```
 * PvotScreenScaffold(
 *     topBar = { PvotTopBar(title, compact = compact) },
 *     bottomBar = { PvotNavBar(selected, onSelect, tabs) },
 * ) { padding ->
 *     PvotScreen(contentPadding = PvotScreenDefaults.contentPadding(top = padding.calculateTopPadding())) { … }
 * }
 * ```
 *
 * The bottom padding is 0: [PvotScreen] already clears the nav bar through
 * [LocalPvotNavBarSizes][com.prauga.pvot.designsystem.navigation.LocalPvotNavBarSizes].
 *
 * @param modifier Modifier for the scaffold, which fills the window edge to edge
 * @param backdrop The backdrop the chrome's glass shows; one per scaffold
 * @param background What draws over the theme background under the content, inside the backdrop.
 * Nothing by default: the design is glass over the plain background, refracting the content that
 * scrolls under it. Pass a `PvotPhotoBackground` for a screen about a photo, such as a call
 * @param topBar The bar at the top, over the content; typically a `PvotTopBar`
 * @param bottomBar The bar at the bottom, over the content; typically a `PvotNavBar`
 * @param floatingAction The floating action above the bottom bar's end; typically a `PvotFab`
 * @param snackbarHost The snackbar host above the bottom bar
 * @param overlay Sheets, dialogs and anything else drawn last, over everything
 * @param content The screen, given the padding that clears the top bar, or the status bar when
 *     there is no top bar
 */
@Composable
public fun PvotScreenScaffold(
    modifier: Modifier = Modifier,
    backdrop: PvotGlassBackdrop = rememberPvotGlassBackdrop(),
    background: @Composable BoxScope.() -> Unit = {},
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingAction: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    overlay: @Composable BoxScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val density = LocalDensity.current
    var topBarHeight by remember { mutableIntStateOf(0) }
    var bottomBarHeight by remember { mutableIntStateOf(0) }
    // A screen with no top bar still clears the status bar; a top bar already pads itself by it.
    val statusBar = WindowInsets.statusBars.getTop(density)
    val contentPadding = with(density) { PaddingValues(top = maxOf(topBarHeight, statusBar).toDp()) }
    val aboveBottomBar = with(density) { bottomBarHeight.toDp() }

    Box(modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                // The background goes inside the backdrop: glass frosts what is recorded there,
                // and a backdrop without an opaque background lets the content show through sharp.
                .pvotGlassBackdrop(backdrop)
                .background(MaterialTheme.colorScheme.background)
        ) {
            background()
            CompositionLocalProvider(LocalPvotGlassBackdrop provides null) {
                content(contentPadding)
            }
        }
        CompositionLocalProvider(LocalPvotGlassBackdrop provides backdrop) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .onSizeChanged { topBarHeight = it.height }
                    // TalkBack reads the top bar before the content it floats over.
                    .semantics { isTraversalGroup = true; traversalIndex = -1f }
            ) {
                topBar()
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .onSizeChanged { bottomBarHeight = it.height }
                    .semantics { isTraversalGroup = true }
            ) {
                bottomBar()
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = aboveBottomBar)
                    .then(if (bottomBarHeight == 0) Modifier.navigationBarsPadding() else Modifier)
                    .padding(PvotScreenScaffoldDefaults.FloatingActionMargin)
            ) {
                floatingAction()
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = aboveBottomBar)
                    .then(if (bottomBarHeight == 0) Modifier.navigationBarsPadding() else Modifier)
            ) {
                snackbarHost()
            }
            overlay()
        }
    }
}
