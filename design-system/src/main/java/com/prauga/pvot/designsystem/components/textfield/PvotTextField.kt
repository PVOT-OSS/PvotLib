// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.textfield

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.motion.PvotMotion
import com.prauga.pvot.designsystem.theme.PvotSpacing

/**
 * A text field with a soft filled surface, a label that floats up as you type, a ring that fades
 * in on focus and a shake when it turns invalid.
 *
 * The whole surface is the tap target, not just the line of text. Supporting text sits underneath
 * and cross-fades when it changes, so a validation message arrives smoothly. With [maxLength] the
 * field stops accepting input at the limit and shows a counter.
 *
 * @param value The text to show
 * @param onValueChange Called with the new text as the user types
 * @param modifier Modifier for the whole field, including its supporting text
 * @param label Name of the field; it rests inside the field and floats above the text once the
 * field is focused or filled
 * @param placeholder Hint shown in an empty field while it is focused (or always, without a label)
 * @param supportingText Help or error message under the field
 * @param isError Whether the value is invalid; turns the ring, label and supporting text red
 * @param enabled Whether the field accepts input
 * @param readOnly Whether the text can be selected and copied but not edited
 * @param leadingIcon Content before the text, typically an `Icon`
 * @param trailingIcon Content after the text, such as a clear or reveal button
 * @param singleLine Whether the text stays on one line
 * @param minLines Fewest lines the field shows when not [singleLine]
 * @param maxLines Most lines the field grows to
 * @param maxLength Longest the text may be, or null for no limit; shows a counter when set
 * @param keyboardOptions Keyboard type and IME action
 * @param keyboardActions What the IME action does
 * @param visualTransformation Transformation applied to the shown text, such as a password mask
 * @param shape Corner shape of the field
 * @param colors Color configuration
 * @param interactionSource Source of the field's focus and press interactions
 */
@Composable
public fun PvotTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    maxLength: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    shape: Shape = PvotTextFieldDefaults.Shape,
    colors: PvotTextFieldColors = PvotTextFieldDefaults.themeColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val focused by interactionSource.collectIsFocusedAsState()
    val focusRequester = remember { FocusRequester() }
    val floated = label != null && (focused || value.isNotEmpty())

    val floatProgress by animateFloatAsState(
        targetValue = if (floated) 1f else 0f,
        animationSpec = tween(PvotDurations.Medium, easing = PvotMotion.Settle),
        label = "pvotFieldLabel"
    )
    val borderAlpha by animateFloatAsState(
        targetValue = if (focused || isError) 1f else 0f,
        animationSpec = tween(PvotDurations.Short),
        label = "pvotFieldRing"
    )
    val ringColor by animateColorAsState(
        targetValue = if (isError) colors.errorColor else colors.focusedBorderColor,
        animationSpec = tween(PvotDurations.Short),
        label = "pvotFieldRingColor"
    )
    val labelColor by animateColorAsState(
        targetValue = when {
            isError -> colors.errorColor
            focused -> colors.focusedLabelColor
            else -> colors.labelColor
        },
        animationSpec = tween(PvotDurations.Short),
        label = "pvotFieldLabelColor"
    )

    // A short sideways shake the moment the value turns invalid.
    val shake = remember { Animatable(0f) }
    LaunchedEffect(isError) {
        if (isError) {
            shake.snapTo(0f)
            shake.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = ShakeMillis
                    -8f at 50
                    8f at 110
                    -5f at 170
                    5f at 220
                    0f at ShakeMillis
                }
            )
        }
    }

    val textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.textColor)
    val labelTopPadding: Dp = lerp(0.dp, LabelRoom, floatProgress)

    Column(modifier = modifier.alpha(if (enabled) 1f else DisabledAlpha)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = PvotTextFieldDefaults.MinHeight)
                .offset(x = shake.value.dp)
                .clip(shape)
                .drawBehind { drawRect(colors.containerColor) }
                .border(
                    border = BorderStroke(
                        PvotTextFieldDefaults.BorderWidth,
                        ringColor.copy(alpha = ringColor.alpha * borderAlpha)
                    ),
                    shape = shape
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = enabled
                ) { focusRequester.requestFocus() }
                .semantics(mergeDescendants = true) {
                    if (isError && supportingText != null) error(supportingText)
                }
                .padding(horizontal = PvotSpacing.L),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PvotSpacing.M)
        ) {
            if (leadingIcon != null) {
                CompositionLocalProvider(LocalContentColor provides colors.iconColor, content = { leadingIcon() })
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = PvotSpacing.L)
            ) {
                if (label != null) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = labelColor,
                        maxLines = 1,
                        modifier = Modifier
                            .offset(y = lerp(0.dp, -LabelRise, floatProgress))
                            .graphicsLayer {
                                val scale = 1f - (1f - FloatedLabelScale) * floatProgress
                                scaleX = scale
                                scaleY = scale
                                transformOrigin = TransformOrigin(0f, 0f)
                            }
                    )
                }
                Box(modifier = Modifier.padding(top = labelTopPadding)) {
                    val showPlaceholder = placeholder != null && value.isEmpty() &&
                        (label == null || floated)
                    if (showPlaceholder) {
                        Text(
                            text = placeholder.orEmpty(),
                            style = textStyle,
                            color = colors.placeholderColor,
                            maxLines = 1
                        )
                    }
                    BasicTextField(
                        value = value,
                        onValueChange = { next ->
                            if (maxLength == null || next.length <= maxLength) onValueChange(next)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        enabled = enabled,
                        readOnly = readOnly,
                        textStyle = textStyle,
                        cursorBrush = SolidColor(colors.cursorColor),
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        singleLine = singleLine,
                        minLines = minLines,
                        maxLines = maxLines,
                        visualTransformation = visualTransformation,
                        interactionSource = interactionSource
                    )
                }
            }
            if (trailingIcon != null) {
                CompositionLocalProvider(LocalContentColor provides colors.iconColor, content = { trailingIcon() })
            }
        }

        if (supportingText != null || maxLength != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = PvotSpacing.L, end = PvotSpacing.L, top = PvotSpacing.XS),
                horizontalArrangement = Arrangement.spacedBy(PvotSpacing.M)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = supportingText to isError,
                        transitionSpec = {
                            (fadeIn(tween(PvotDurations.Medium)) +
                                slideInVertically(tween(PvotDurations.Medium, easing = PvotMotion.Settle)) { -it / 2 }) togetherWith
                                (fadeOut(tween(PvotDurations.Short)) +
                                    slideOutVertically(tween(PvotDurations.Short)) { it / 2 })
                        },
                        label = "pvotFieldSupporting"
                    ) { (text, error) ->
                        if (text != null) {
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (error) colors.errorColor else colors.supportingTextColor
                            )
                        }
                    }
                }
                if (maxLength != null) {
                    Text(
                        text = "${value.length}/$maxLength",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (value.length >= maxLength) colors.errorColor else colors.supportingTextColor
                    )
                }
            }
        }
    }
}

private const val ShakeMillis: Int = 300
private const val DisabledAlpha: Float = 0.5f
private const val FloatedLabelScale: Float = 0.75f
private val LabelRoom: Dp = 12.dp
private val LabelRise: Dp = 8.dp
