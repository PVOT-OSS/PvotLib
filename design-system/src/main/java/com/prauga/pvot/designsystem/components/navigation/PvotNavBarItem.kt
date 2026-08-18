// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.theme.PvotTheme

/**
 * A pill-style item for the slot-based [PvotNavBar].
 *
 * The item collapses to a circular chip when unselected and expands to a rounded
 * pill revealing [label] when selected. Its width follows the content, so [icon]
 * and [label] may be any composable.
 *
 * @param selected Whether this item is the selected one
 * @param onClick Called when the user taps this item
 * @param icon The item's icon, shown in both the collapsed and expanded state
 * @param modifier Modifier for this item
 * @param label The item's label, revealed while selected, or null for an icon-only item
 * @param sizes Size configuration for this item
 * @param colors Color configuration for this item
 */
@Composable
fun RowScope.PvotNavBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    sizes: PvotNavBarSizes = PvotTheme.navBarSizes,
    colors: PvotNavBarColors = PvotTheme.navBarColors
) {
    val interactionSource = remember { MutableInteractionSource() }

    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.98f,
        animationSpec = tween(200),
        label = "scale"
    )

    Box(
        modifier = modifier
            .height(sizes.collapsedItemSize)
            .widthIn(min = sizes.collapsedItemSize)
            .clip(if (selected) RoundedCornerShape(sizes.itemCornerRadius) else CircleShape)
            .background(
                if (selected) colors.gradient else SolidColor(colors.collapsedChipColor)
            )
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
            .indication(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.25f))
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (selected) PvotNavBarDefaults.ItemHorizontalPadding else 0.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon()

            if (label != null) {
                AnimatedVisibility(
                    visible = selected,
                    enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
                    exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start)
                ) {
                    Box(modifier = Modifier.padding(start = PvotNavBarDefaults.IconTextGap)) {
                        label()
                    }
                }
            }
        }
    }
}
