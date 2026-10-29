// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.prauga.pvot.designsystem.effects.pvotPressScale
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.motion.PvotMotion
import com.prauga.pvot.designsystem.theme.PvotSpacing

/** Defaults for [PvotDialog]. */
public object PvotDialogDefaults {

    /** Corner shape of the dialog surface. */
    public val Shape: Shape = RoundedCornerShape(28.dp)

    /** The widest the dialog gets, so it stays a dialog on tablets. */
    public val MaxWidth: Dp = 360.dp

    /** Space kept between the dialog and the screen edges. */
    public val ScreenMargin: Dp = PvotSpacing.L * 1.5f

    /** Opacity of the scrim behind the dialog when fully shown. */
    public const val ScrimAlpha: Float = 0.6f

    /** Diameter of the circle that holds the dialog's icon. */
    public val IconCircleSize: Dp = 52.dp

    /** Fill of the dialog surface. */
    public val containerColor: Color
        @Composable get() = MaterialTheme.colorScheme.surfaceVariant
}

/** How prominent a [PvotDialogAction] is. */
public enum class PvotDialogActionStyle {
    /** The action the dialog is asking for, a filled button in the brand color. */
    Primary,

    /** A quiet alternative such as Cancel. */
    Neutral,

    /** An action that destroys something, a filled button in the error color. */
    Destructive
}

/**
 * A centered dialog that eases in over a dimmed screen and eases back out.
 *
 * The dialog stays on screen while [visible] is true. Setting it to false plays the exit before
 * the dialog leaves composition, which a conditionally composed dialog could not do. The icon
 * (when given) sits in a tinted circle above a centered title and message, and the actions stack
 * as full-width buttons. Tapping outside, or the system back, calls [onDismissRequest].
 *
 * @param visible Whether the dialog is showing
 * @param onDismissRequest Called when the user taps outside the dialog or goes back
 * @param title Short summary of the decision
 * @param modifier Modifier for the dialog surface
 * @param message Supporting text under the title
 * @param icon Content of the circle above the title, typically an `Icon`; it is tinted for you
 * @param destructive Whether the dialog is about something destructive, which tints the icon circle
 * in the error color
 * @param dismissOnOutsideTap Whether tapping outside, or going back, dismisses the dialog
 * @param content Extra content between the message and the actions, such as a text field
 * @param actions The buttons, typically [PvotDialogAction]s, most important first
 */
@Composable
public fun PvotDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
    icon: (@Composable () -> Unit)? = null,
    destructive: Boolean = false,
    dismissOnOutsideTap: Boolean = true,
    content: (@Composable ColumnScope.() -> Unit)? = null,
    actions: @Composable ColumnScope.() -> Unit
) {
    val state = remember { MutableTransitionState(false) }
    state.targetState = visible
    if (!state.currentState && !state.targetState) return

    Dialog(
        onDismissRequest = { if (dismissOnOutsideTap) onDismissRequest() },
        properties = DialogProperties(
            dismissOnBackPress = dismissOnOutsideTap,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        // The dialog draws its own scrim so it can animate it with the card.
        val window = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect {
            window?.setDimAmount(0f)
            window?.setWindowAnimations(-1)
        }

        val transition = rememberTransition(state, label = "pvotDialog")
        val scrim = transition.animateFloat(
            transitionSpec = { tween(if (targetState) PvotDurations.Medium else PvotDurations.Short) },
            label = "scrim"
        ) { if (it) PvotDialogDefaults.ScrimAlpha else 0f }
        val alpha = transition.animateFloat(
            transitionSpec = {
                if (targetState) tween(PvotDurations.Medium) else tween(PvotDurations.Short)
            },
            label = "alpha"
        ) { if (it) 1f else 0f }
        val scale = transition.animateFloat(
            transitionSpec = {
                if (targetState) tween(PvotDurations.Long, easing = PvotMotion.Settle)
                else tween(PvotDurations.Short)
            },
            label = "scale"
        ) { if (it) 1f else EnterScale }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = scrim.value))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = dismissOnOutsideTap,
                    onClick = onDismissRequest
                )
                .safeDrawingPadding()
                .imePadding()
                .padding(PvotDialogDefaults.ScreenMargin),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = modifier
                    .widthIn(max = PvotDialogDefaults.MaxWidth)
                    .fillMaxWidth()
                    .heightIn(max = 560.dp)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                        this.alpha = alpha.value
                    }
                    .clip(PvotDialogDefaults.Shape)
                    .background(PvotDialogDefaults.containerColor)
                    // Taps on the dialog itself must not reach the scrim behind it.
                    .pointerInput(Unit) { detectTapGestures { } }
                    .padding(PvotSpacing.L * 1.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(PvotSpacing.M)
            ) {
                if (icon != null) {
                    val circle = if (destructive) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    }
                    val tint = if (destructive) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    }
                    Box(
                        modifier = Modifier
                            .size(PvotDialogDefaults.IconCircleSize)
                            .background(circle, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        CompositionLocalProvider(LocalContentColor provides tint, content = { icon() })
                    }
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                if (message != null) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
                if (content != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(PvotSpacing.S),
                        content = content
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = PvotSpacing.S),
                    verticalArrangement = Arrangement.spacedBy(PvotSpacing.S),
                    content = actions
                )
            }
        }
    }
}

/**
 * A full-width, pill-shaped button for the actions of a [PvotDialog].
 *
 * @param label Text of the button
 * @param onClick Called when the button is tapped
 * @param modifier Modifier for the button
 * @param style How prominent the action is
 * @param enabled Whether the button responds to taps
 */
@Composable
public fun PvotDialogAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: PvotDialogActionStyle = PvotDialogActionStyle.Neutral,
    enabled: Boolean = true
) {
    val colors = MaterialTheme.colorScheme
    val (container, content) = when (style) {
        PvotDialogActionStyle.Primary -> colors.primary to colors.onPrimary
        PvotDialogActionStyle.Destructive -> colors.error to colors.onError
        PvotDialogActionStyle.Neutral -> colors.onSurface.copy(alpha = 0.08f) to colors.onSurface
    }
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .pvotPressScale(interactionSource)
            .clip(CircleShape)
            .background(if (enabled) container else container.copy(alpha = container.alpha * 0.4f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = PvotSpacing.L, vertical = PvotSpacing.M),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) content else content.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
    }
}

private const val EnterScale: Float = 0.88f

