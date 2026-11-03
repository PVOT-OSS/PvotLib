// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.R
import com.prauga.pvot.designsystem.effects.LocalPvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassDefaults
import com.prauga.pvot.designsystem.effects.PvotGlassMaterial
import com.prauga.pvot.designsystem.effects.pvotGlass
import com.prauga.pvot.designsystem.effects.pvotPressScale
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.motion.PvotMotion
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing
import kotlin.math.roundToInt

/**
 * Color configuration for [PvotTopBar].
 *
 * @param containerColor Fill of the bar while expanded, usually the screen's background
 * @param compactContainerColor Fill of the bar once compact, so it separates from the content
 *   scrolling under it
 * @param titleColor Color of the title
 * @param contentColor Color of the back button and the actions
 */
@Immutable
public data class PvotTopBarColors(
    val containerColor: Color,
    val compactContainerColor: Color,
    val titleColor: Color,
    val contentColor: Color,
)

/** Defaults for [PvotTopBar]. */
public object PvotTopBarDefaults {

    /** Height of the row that holds the back button, the compact title and the actions. */
    public val BarHeight: Dp = 64.dp

    /** Size of the back button and of each [PvotTopBarAction], the minimum touch target. */
    public val ActionSize: Dp = 48.dp

    /** How far a list scrolls before [rememberPvotTopBarCompact] reports it compact. */
    public val CompactThreshold: Dp = 8.dp

    /** The insets the bar pads itself by, the status bar by default. */
    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.statusBars

    /** Bar colors derived from [colorScheme], each overridable. */
    public fun colors(
        colorScheme: ColorScheme,
        containerColor: Color = colorScheme.background,
        compactContainerColor: Color = colorScheme.surfaceContainer,
        titleColor: Color = colorScheme.onBackground,
        contentColor: Color = colorScheme.onSurfaceVariant,
    ): PvotTopBarColors =
        PvotTopBarColors(
            containerColor = containerColor,
            compactContainerColor = compactContainerColor,
            titleColor = titleColor,
            contentColor = contentColor,
        )

    /** Bar colors from the theme's color scheme. */
    @Composable public fun themeColors(): PvotTopBarColors = colors(MaterialTheme.colorScheme)

    /**
     * Bar colors for a bar drawn as glass over a backdrop: no fill of its own, the glass is the
     * fill, with the title and content in the surface's colors.
     */
    @Composable
    public fun glassColors(): PvotTopBarColors =
        colors(
            MaterialTheme.colorScheme,
            containerColor = Color.Transparent,
            compactContainerColor = Color.Transparent,
            titleColor = MaterialTheme.colorScheme.onSurface,
        )

    /** How much the glass is frosted: enough for the title to read over anything. */
    public val Material: PvotGlassMaterial = PvotGlassMaterial.Regular

    internal val HairlineAlpha: Float = 0.3f
}

/**
 * How much of a [PvotTopBar] is folded away, in pixels: 0 while expanded, the large title's height
 * once compact, in between while it animates. The scaffold provides one to its top bar slot and
 * adds it to the bar's measured height, so the content's padding stays the bar's expanded height
 * whatever the fold does. A bar outside a scaffold reports to nobody.
 */
@Stable
internal class PvotTopBarFold {
    /** Pixels of the bar folded away right now. */
    var foldedHeight: Int by mutableIntStateOf(0)
}

/** The fold the enclosing scaffold listens to, or null outside one. */
internal val LocalPvotTopBarFold: ProvidableCompositionLocal<PvotTopBarFold?> = compositionLocalOf {
    null
}

/** The large title's laid-out height at [progress] (0 expanded, 1 compact) of its [fullHeight]. */
internal fun pvotTopBarTitleHeight(fullHeight: Int, progress: Float): Int =
    (fullHeight * (1f - progress)).roundToInt()

/**
 * Whether the bar should be compact given where the list is, with hysteresis: a bar that is
 * expanded folds once the list scrolls past [thresholdPx]; a bar that is compact unfolds only once
 * the list is back at the very top. Scrolling to and fro around the threshold never flaps the bar.
 */
internal fun pvotTopBarCompact(
    compact: Boolean,
    firstVisibleItemIndex: Int,
    firstVisibleItemScrollOffset: Int,
    thresholdPx: Float,
): Boolean =
    if (compact) firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0
    else firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > thresholdPx

/**
 * Whether [state] has scrolled past [threshold], for driving [PvotTopBar]'s `compact`. It only
 * changes when the answer does, so reading it does not recompose on every scrolled pixel. Once
 * compact, the bar stays so until the list is back at the top, so a list that stops just past the
 * threshold does not fold and unfold under the finger.
 *
 * @param state The scroll state of the list under the bar
 * @param threshold How far the list scrolls before the bar compacts
 */
@Composable
public fun rememberPvotTopBarCompact(
    state: LazyListState,
    threshold: Dp = PvotTopBarDefaults.CompactThreshold,
): Boolean {
    val thresholdPx = with(LocalDensity.current) { threshold.toPx() }
    var compact by
        remember(state, thresholdPx) {
            mutableStateOf(
                pvotTopBarCompact(
                    compact = false,
                    firstVisibleItemIndex = state.firstVisibleItemIndex,
                    firstVisibleItemScrollOffset = state.firstVisibleItemScrollOffset,
                    thresholdPx = thresholdPx,
                )
            )
        }
    LaunchedEffect(state, thresholdPx) {
        snapshotFlow { state.firstVisibleItemIndex to state.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                compact = pvotTopBarCompact(compact, index, offset, thresholdPx)
            }
    }
    return compact
}

/**
 * The bar at the top of a screen: a back button, the title and the screen's actions.
 *
 * Expanded, the title sits large under the bar, on the screen's own background. Compact, the large
 * title folds away into the bar and the bar takes a fill of its own, so the content scrolling under
 * it stays apart. Drive [compact] from the list with [rememberPvotTopBarCompact]. The bar pads
 * itself below the status bar. Inside a `PvotScreenScaffold` the content's padding stays the bar's
 * expanded height while it folds, so the fold never moves the content.
 *
 * Over a [backdrop] the compact bar is glass: the content scrolling under it shows through,
 * frosted, with a hairline along its bottom edge. While expanded the title sits straight on the
 * background with no glass to pay for; the glass fades in as the bar folds. The glass runs up under
 * the status bar too.
 *
 * @param title Title of the screen
 * @param modifier Modifier for the bar
 * @param onBack Called when the back button is tapped, or null for a top-level screen without one
 * @param compact Whether the bar is folded to a single row
 * @param backdrop The content behind the bar, marked with
 *   [pvotGlassBackdrop][com.prauga.pvot.designsystem.effects.pvotGlassBackdrop], to draw the
 *   compact bar as glass over it; null for a filled bar. The scaffold's backdrop by default
 * @param material How much the glass is frosted, only meaningful with [backdrop]
 * @param colors The bar's colors
 * @param windowInsets The insets the bar pads itself by
 * @param actions The screen's actions at the end of the bar, typically [PvotTopBarAction]s
 */
@Composable
public fun PvotTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    compact: Boolean = false,
    backdrop: PvotGlassBackdrop? = LocalPvotGlassBackdrop.current,
    material: PvotGlassMaterial = PvotTopBarDefaults.Material,
    colors: PvotTopBarColors =
        if (backdrop != null) PvotTopBarDefaults.glassColors()
        else PvotTopBarDefaults.themeColors(),
    windowInsets: WindowInsets = PvotTopBarDefaults.windowInsets,
    actions: @Composable RowScope.() -> Unit = {},
) {
    // 0 is expanded, 1 compact. Read only in layout and draw, so the fold costs no recomposition.
    val progress =
        animateFloatAsState(
            targetValue = if (compact) 1f else 0f,
            animationSpec = tween(PvotDurations.Medium, easing = PvotMotion.Settle),
            label = "pvotTopBarCompact",
        )
    CompositionLocalProvider(LocalContentColor provides colors.contentColor) {
        Box(modifier.fillMaxWidth()) {
            if (backdrop != null) {
                // Composed only while folding or folded, so an expanded bar records no glass.
                val folding by remember { derivedStateOf { progress.value > 0f } }
                if (folding) {
                    val hairline = MaterialTheme.colorScheme.outlineVariant
                    Box(
                        Modifier.matchParentSize()
                            .graphicsLayer { alpha = progress.value }
                            .pvotGlass(
                                backdrop,
                                cornerRadius = 0.dp,
                                style =
                                    PvotGlassDefaults.style(
                                        PvotGlassDefaults.currentIntensity(),
                                        material,
                                    ),
                            )
                            .drawBehind {
                                val y = size.height - 0.5.dp.toPx()
                                drawLine(
                                    hairline.copy(alpha = PvotTopBarDefaults.HairlineAlpha),
                                    Offset(0f, y),
                                    Offset(size.width, y),
                                    strokeWidth = 1.dp.toPx(),
                                )
                            }
                    )
                }
            }
            TopBarColumn(title, onBack, compact, colors, windowInsets, progress, actions)
        }
    }
}

@Composable
private fun TopBarColumn(
    title: String,
    onBack: (() -> Unit)?,
    compact: Boolean,
    colors: PvotTopBarColors,
    windowInsets: WindowInsets,
    progress: State<Float>,
    actions: @Composable RowScope.() -> Unit,
) {
    val fold = LocalPvotTopBarFold.current
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .drawBehind {
                    drawRect(
                        lerp(colors.containerColor, colors.compactContainerColor, progress.value)
                    )
                }
                .windowInsetsPadding(windowInsets)
    ) {
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .height(PvotTopBarDefaults.BarHeight)
                    .padding(horizontal = PvotSpacing.XS),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                PvotTopBarAction(
                    icon = painterResource(R.drawable.pvot_ic_arrow_left),
                    contentDescription = stringResource(R.string.pvot_top_bar_back),
                    onClick = onBack,
                )
            }
            Box(
                modifier =
                    Modifier.weight(1f)
                        .padding(horizontal = if (onBack != null) PvotSpacing.XS else PvotSpacing.M)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier =
                        Modifier.graphicsLayer { alpha = progress.value }
                            // One title for TalkBack: this one only once the large one has gone.
                            .then(
                                if (compact) Modifier.semantics { heading() }
                                else Modifier.clearAndSetSemantics {}
                            ),
                )
            }
            actions()
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = colors.titleColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier =
                Modifier.fillMaxWidth()
                    .clipToBounds()
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        val height = pvotTopBarTitleHeight(placeable.height, progress.value)
                        // Tell the scaffold what folded away, so its padding does not follow.
                        fold?.foldedHeight = placeable.height - height
                        layout(placeable.width, height) { placeable.place(0, 0) }
                    }
                    .graphicsLayer { alpha = 1f - progress.value }
                    .padding(start = PvotSpacing.L, end = PvotSpacing.L, bottom = PvotSpacing.M)
                    .then(
                        if (compact) Modifier.clearAndSetSemantics {}
                        else Modifier.semantics { heading() }
                    ),
        )
    }
}

/**
 * An icon button for the actions of a [PvotTopBar], 48dp across, tinted with the bar's content
 * color and squashing slightly under the finger.
 *
 * @param icon The action's icon
 * @param contentDescription What the action does, read by TalkBack
 * @param onClick Called when the action is tapped
 * @param modifier Modifier for the button
 * @param enabled Whether the action responds to taps
 */
@Composable
public fun PvotTopBarAction(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier =
            modifier
                .size(PvotTopBarDefaults.ActionSize)
                .pvotPressScale(interactionSource)
                .clip(PvotShapes.Pill)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                )
                .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = LocalContentColor.current.copy(alpha = if (enabled) 1f else 0.38f),
        )
    }
}
