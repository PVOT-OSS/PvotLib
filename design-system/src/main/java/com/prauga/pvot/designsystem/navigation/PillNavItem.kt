// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.prauga.pvot.designsystem.R

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
            val description = stringResource(id = descriptionRes)
            val badgeDescription = pluralStringResource(
                R.plurals.pvot_nav_badge_description,
                tab.badgeCount,
                tab.badgeCount
            )
            BadgedBox(
                badge = {
                    if (tab.badgeCount > 0) {
                        Badge {
                            Text(
                                text = if (tab.badgeCount > MaxBadgeCount) {
                                    stringResource(R.string.pvot_nav_badge_overflow, MaxBadgeCount)
                                } else {
                                    tab.badgeCount.toString()
                                }
                            )
                        }
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = if (tab.badgeCount > 0) {
                        "$description, $badgeDescription"
                    } else {
                        description
                    },
                    modifier = Modifier.size(
                        if (selected) sizes.expandedIconSize else sizes.collapsedIconSize
                    )
                )
            }
        },
        label = { Text(text = stringResource(id = expandedLabelRes), maxLines = 1) }
    )
}

private const val MaxBadgeCount = 999
