// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

@Composable
internal fun RowScope.PillNavItem(
    tab: PvotTabItem,
    selected: Boolean,
    onClick: () -> Unit,
    @DrawableRes expandedIconRes: Int,
    @StringRes expandedLabelRes: Int,
    @StringRes expandedContentDescriptionRes: Int,
    sizes: PvotNavBarSizes,
    colors: PvotNavBarColors,
    modifier: Modifier = Modifier
) {
    val iconRes = if (selected) expandedIconRes else tab.iconRes
    val descriptionRes =
        if (selected) expandedContentDescriptionRes else tab.contentDescriptionRes

    PvotNavBarItem(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        sizes = sizes,
        colors = colors,
        icon = {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = stringResource(id = descriptionRes),
                modifier = Modifier.size(
                    if (selected) sizes.expandedIconSize else sizes.collapsedIconSize
                )
            )
        },
        label = { Text(text = stringResource(id = expandedLabelRes), maxLines = 1) }
    )
}
