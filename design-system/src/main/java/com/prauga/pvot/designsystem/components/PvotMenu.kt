// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.prauga.pvot.designsystem.R
import com.prauga.pvot.designsystem.effects.LocalPvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.PvotGlassDefaults
import com.prauga.pvot.designsystem.effects.PvotGlassMaterial
import com.prauga.pvot.designsystem.effects.pvotGlassPress
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing

/** One entry of a [PvotMenu]: a [PvotMenuItem], or [PvotMenuSeparator] between groups of them. */
public sealed interface PvotMenuEntry

/**
 * An action in a [PvotMenu].
 *
 * @param label What the action is called
 * @param onClick Called when the user picks it
 * @param icon The action's icon at the start of the row, or null for none
 * @param destructive Whether the action removes something, shown in the error color
 * @param enabled Whether the action can be picked; a disabled one is dimmed
 */
@Immutable
public data class PvotMenuItem(
    val label: String,
    val onClick: () -> Unit,
    @DrawableRes val icon: Int? = null,
    val destructive: Boolean = false,
    val enabled: Boolean = true,
) : PvotMenuEntry

/** A hairline between two groups of [PvotMenuItem]s. */
public object PvotMenuSeparator : PvotMenuEntry

/** Which edge of its trigger a [PvotMenu] opens along. */
public enum class PvotMenuAlign {
    /** The menu's start edge lines up with the trigger's: it opens toward the end. */
    Start,
    /** The menu's end edge lines up with the trigger's: it opens toward the start. */
    End,
}

/**
 * Color configuration for [PvotMenu].
 *
 * @param containerColor Fill of the open menu
 * @param contentColor Label and icon color of an item
 * @param destructiveColor Label and icon color of a destructive item
 * @param separatorColor The hairline between groups
 * @param highlightColor Fill behind the item under the finger
 * @param triggerColor Fill of the trigger button when it is not glass
 * @param triggerContentColor Icon color of the trigger button
 */
@Immutable
public data class PvotMenuColors(
    val containerColor: Color,
    val contentColor: Color,
    val destructiveColor: Color,
    val separatorColor: Color,
    val highlightColor: Color,
    val triggerColor: Color,
    val triggerContentColor: Color,
)

/** Defaults for [PvotMenu]. */
public object PvotMenuDefaults {

    /** Width of the open menu. */
    public val Width: Dp = 240.dp

    /** Height of one item. */
    public val ItemHeight: Dp = 48.dp

    /** Corner radius of the open menu. */
    public val CornerRadius: Dp = 20.dp

    /** Diameter of the trigger button. */
    public val TriggerSize: Dp = 48.dp

    /** Size of an item's icon. */
    public val IconSize: Dp = 22.dp

    /** Elevation of the shadow under the open menu. */
    public val ShadowElevation: Dp = 16.dp

    /** How much the items are blurred before they come into focus as the menu opens. */
    public val OpeningBlur: Dp = 6.dp

    /** How much the glass hides what is behind the open menu: enough for its labels to read. */
    public val Material: PvotGlassMaterial = PvotGlassMaterial.Thick

    /** Opacity of a disabled item. */
    public const val DisabledAlpha: Float = 0.38f

    /** Menu colors derived from [colorScheme], each overridable. */
    public fun colors(
        colorScheme: ColorScheme,
        containerColor: Color = colorScheme.surfaceContainerHigh,
        contentColor: Color = colorScheme.onSurface,
        destructiveColor: Color = colorScheme.error,
        separatorColor: Color = colorScheme.outlineVariant.copy(alpha = 0.6f),
        highlightColor: Color = colorScheme.onSurface.copy(alpha = 0.08f),
        triggerColor: Color = colorScheme.surfaceContainerHighest,
        triggerContentColor: Color = colorScheme.onSurface,
    ): PvotMenuColors =
        PvotMenuColors(
            containerColor = containerColor,
            contentColor = contentColor,
            destructiveColor = destructiveColor,
            separatorColor = separatorColor,
            highlightColor = highlightColor,
            triggerColor = triggerColor,
            triggerContentColor = triggerContentColor,
        )

    /** Menu colors from the theme's color scheme. */
    @Composable public fun themeColors(): PvotMenuColors = colors(MaterialTheme.colorScheme)
}

private enum class MenuPhase {
    Closed,
    Open,
    Leaving,
}

/**
 * A round button that opens a menu of actions over the screen.
 *
 * Tap the button and the menu grows out of it, its items scaling up from the button's centre and
 * coming into focus; tap outside, or press back, to close it. Or press the button and, without
 * lifting the finger, slide onto an item and let go to pick it in one move. Items with
 * [PvotMenuItem.destructive] show in the error color; [PvotMenuSeparator] draws a hairline between
 * groups.
 *
 * The button is glass over [backdrop] when there is one, and a tonal circle otherwise. The open
 * menu is a thick, nearly opaque surface, so its labels read over anything.
 *
 * @param entries The menu's items and separators, in order
 * @param contentDescription What the button opens, for TalkBack, such as "More options"
 * @param modifier Modifier for the button
 * @param icon The button's icon; three dots by default
 * @param align Which edge of the button the menu opens along
 * @param enabled Whether the button responds to touch
 * @param backdrop The content behind the button, or null for a tonal button
 * @param colors The menu's colors
 */
@Composable
public fun PvotMenu(
    entries: List<PvotMenuEntry>,
    contentDescription: String,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int = R.drawable.pvot_ic_dots,
    align: PvotMenuAlign = PvotMenuAlign.End,
    enabled: Boolean = true,
    backdrop: PvotGlassBackdrop? = LocalPvotGlassBackdrop.current,
    colors: PvotMenuColors = PvotMenuDefaults.themeColors(),
) {
    val haptics = LocalHapticFeedback.current
    var phase by remember { mutableStateOf(MenuPhase.Closed) }
    var hovered by remember { mutableIntStateOf(-1) }
    var scrubbing by remember { mutableStateOf(false) }
    val itemBounds = remember { mutableStateMapOf<Int, Rect>() }
    var popupOffset by remember { mutableStateOf(IntOffset.Zero) }
    var triggerSize by remember { mutableStateOf(IntSize.Zero) }
    val currentEntries by rememberUpdatedState(entries)
    val morph = remember { Animatable(0f) }
    val open = phase == MenuPhase.Open

    LaunchedEffect(phase) {
        when (phase) {
            MenuPhase.Open -> morph.animateTo(1f, spring(dampingRatio = 0.85f, stiffness = 420f))
            MenuPhase.Leaving -> {
                morph.animateTo(0f, tween(PvotDurations.Short))
                phase = MenuPhase.Closed
            }
            MenuPhase.Closed -> morph.snapTo(0f)
        }
    }

    fun close() {
        if (phase == MenuPhase.Open) phase = MenuPhase.Leaving
        hovered = -1
        scrubbing = false
    }

    fun pick(index: Int) {
        val item = currentEntries.getOrNull(index) as? PvotMenuItem ?: return
        if (!item.enabled) return
        close()
        item.onClick()
    }

    fun itemAt(inWindow: Offset): Int =
        itemBounds.entries
            .firstOrNull { (_, rect) -> rect.translate(popupOffset.toOffset()).contains(inWindow) }
            ?.key ?: -1

    val shape = PvotShapes.Pill
    val triggerSurface =
        if (backdrop != null) {
            Modifier.pvotGlassSurface(
                backdrop = backdrop,
                cornerRadius = PvotGlassDefaults.CapsuleRadius,
                role = null,
            )
        } else {
            Modifier.background(colors.triggerColor, shape)
        }
    var triggerOrigin by remember { mutableStateOf(Offset.Zero) }
    Box(
        modifier =
            modifier
                .size(PvotMenuDefaults.TriggerSize)
                .semantics {
                    role = Role.Button
                    this.contentDescription = contentDescription
                    if (enabled) {
                        onClick {
                            if (open) close() else phase = MenuPhase.Open
                            true
                        }
                    } else {
                        disabled()
                    }
                }
                .onGloballyPositioned {
                    triggerOrigin = it.boundsInWindow().topLeft
                    triggerSize = it.size
                }
                .pvotGlassPress(enabled = enabled)
                .then(triggerSurface)
                .clip(shape)
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val wasOpen = phase == MenuPhase.Open
                        if (!wasOpen) phase = MenuPhase.Open
                        var moved = false
                        val slop = viewConfiguration.touchSlop
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Main)
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (change.changedToUpIgnoreConsumed()) {
                                val at = triggerOrigin + change.position
                                val index = if (moved) itemAt(at) else -1
                                when {
                                    index >= 0 -> pick(index)
                                    moved -> close()
                                    wasOpen -> close()
                                }
                                break
                            }
                            if (change.isConsumed && change.positionChanged()) {
                                close()
                                break
                            }
                            change.consume()
                            if ((change.position - down.position).getDistance() > slop) {
                                moved = true
                                scrubbing = true
                                val under = itemAt(triggerOrigin + change.position)
                                if (under != hovered) {
                                    hovered = under
                                    if (under >= 0) {
                                        haptics.performHapticFeedback(
                                            HapticFeedbackType.TextHandleMove
                                        )
                                    }
                                }
                            }
                        }
                        if (phase == MenuPhase.Open) scrubbing = false
                        hovered = -1
                    }
                },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = colors.triggerContentColor,
            modifier = Modifier.size(PvotIconButtonDefaultsIconSize),
        )
    }

    if (phase != MenuPhase.Closed) {
        val end = align == PvotMenuAlign.End
        val provider =
            remember(end) {
                object : PopupPositionProvider {
                    override fun calculatePosition(
                        anchorBounds: IntRect,
                        windowSize: IntSize,
                        layoutDirection: LayoutDirection,
                        popupContentSize: IntSize,
                    ): IntOffset {
                        val trailing = end != (layoutDirection == LayoutDirection.Rtl)
                        val x =
                            if (trailing) anchorBounds.right - popupContentSize.width
                            else anchorBounds.left
                        val below = anchorBounds.top
                        val y =
                            if (below + popupContentSize.height <= windowSize.height) below
                            else (anchorBounds.bottom - popupContentSize.height)
                        val offset =
                            IntOffset(
                                x.coerceIn(
                                    0,
                                    (windowSize.width - popupContentSize.width).coerceAtLeast(0),
                                ),
                                y.coerceAtLeast(0),
                            )
                        popupOffset = offset
                        return offset
                    }
                }
            }
        Popup(
            popupPositionProvider = provider,
            onDismissRequest = { close() },
            properties = PopupProperties(focusable = !scrubbing),
        ) {
            MenuCard(
                entries = entries,
                colors = colors,
                morph = morph.value,
                fromSize = triggerSize,
                trailing = end,
                hovered = hovered,
                onBounds = { index, rect -> itemBounds[index] = rect },
                onPick = ::pick,
            )
        }
    }
}

private val PvotIconButtonDefaultsIconSize: Dp = 24.dp

@Composable
private fun MenuCard(
    entries: List<PvotMenuEntry>,
    colors: PvotMenuColors,
    morph: Float,
    fromSize: IntSize,
    trailing: Boolean,
    hovered: Int,
    onBounds: (Int, Rect) -> Unit,
    onPick: (Int) -> Unit,
) {
    val shape = RoundedCornerShape(PvotMenuDefaults.CornerRadius)
    val glassIntensity = PvotGlassDefaults.currentIntensity()
    Box(
        modifier =
            Modifier.graphicsLayer { alpha = (morph * 5f).coerceIn(0f, 1f) }
                .shadow(
                    PvotMenuDefaults.ShadowElevation * morph,
                    shape,
                    clip = false,
                    ambientColor = Color.Black.copy(alpha = PvotGlassDefaults.ShadowAlpha),
                    spotColor = Color.Black.copy(alpha = PvotGlassDefaults.ShadowAlpha),
                )
                .clip(shape)
                .background(colors.containerColor)
                .then(
                    if (glassIntensity > 0f) {
                        with(PvotGlassDefaults) { Modifier.edge(glassIntensity, shape) }
                    } else {
                        Modifier
                    }
                )
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    val fromWidth = minOf(fromSize.width, placeable.width)
                    val fromHeight = minOf(fromSize.height, placeable.height)
                    val width = lerp(fromWidth, placeable.width, morph)
                    val height = lerp(fromHeight, placeable.height, morph)
                    layout(width, height) {
                        placeable.place(if (trailing) width - placeable.width else 0, 0)
                    }
                }
    ) {
        Column(
            modifier =
                Modifier.width(PvotMenuDefaults.Width)
                    .padding(vertical = PvotSpacing.XS)
                    .graphicsLayer {
                        val focus = morph.coerceIn(0f, 1f)
                        val scale = 0.6f + 0.4f * focus
                        scaleX = scale
                        scaleY = scale
                        transformOrigin =
                            TransformOrigin(
                                if (trailing) 1f - (fromSize.width / 2f) / size.width
                                else (fromSize.width / 2f) / size.width,
                                (fromSize.height / 2f) / size.height,
                            )
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && focus < 1f) {
                            val blur = PvotMenuDefaults.OpeningBlur.toPx() * (1f - focus)
                            renderEffect = if (blur > 0.5f) BlurEffect(blur, blur) else null
                        } else {
                            renderEffect = null
                        }
                        alpha = focus
                    }
        ) {
            entries.forEachIndexed { index, entry ->
                when (entry) {
                    PvotMenuSeparator ->
                        Box(
                            modifier =
                                Modifier.fillMaxWidth()
                                    .padding(vertical = PvotSpacing.XS)
                                    .height(1.dp)
                                    .background(colors.separatorColor)
                        )
                    is PvotMenuItem -> {
                        val tint =
                            if (entry.destructive) colors.destructiveColor else colors.contentColor
                        Row(
                            modifier =
                                Modifier.fillMaxWidth()
                                    .height(PvotMenuDefaults.ItemHeight)
                                    .onGloballyPositioned { onBounds(index, it.boundsInWindow()) }
                                    .background(
                                        if (hovered == index) colors.highlightColor
                                        else Color.Transparent
                                    )
                                    .clickable(enabled = entry.enabled, role = Role.Button) {
                                        onPick(index)
                                    }
                                    .graphicsLayer {
                                        alpha =
                                            if (entry.enabled) 1f
                                            else PvotMenuDefaults.DisabledAlpha
                                    }
                                    .padding(horizontal = PvotSpacing.L),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            CompositionLocalProvider(LocalContentColor provides tint) {
                                Text(
                                    text = entry.label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = tint,
                                    modifier = Modifier.weight(1f),
                                )
                                if (entry.icon != null) {
                                    Spacer(Modifier.width(PvotSpacing.M))
                                    Icon(
                                        painter = painterResource(entry.icon),
                                        contentDescription = null,
                                        tint = tint,
                                        modifier = Modifier.size(PvotMenuDefaults.IconSize),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun IntOffset.toOffset(): Offset = Offset(x.toFloat(), y.toFloat())
