// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.catalog.legacy

import androidx.annotation.DrawableRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import com.prauga.pvot.catalog.R
import com.prauga.pvot.designsystem.effects.PvotGlassBackdrop
import com.prauga.pvot.designsystem.navigation.PvotNavBar
import com.prauga.pvot.designsystem.navigation.PvotTabItem
import com.prauga.pvot.designsystem.theme.PvotThemeDefaults

/**
 * Whether the redesigned UI is on ("Enable New UI" in Settings). Every redesign change in the
 * catalog hangs off this, so turning it off shows the UI as it was before.
 */
internal val LocalNewUi = staticCompositionLocalOf { true }

/** The catalog's icons; the new UI uses Tabler Icons, the old one the Material set. */
internal class AppIcons(
    @DrawableRes val home: Int,
    @DrawableRes val apps: Int,
    @DrawableRes val catalog: Int,
    @DrawableRes val about: Int,
    @DrawableRes val github: Int,
    @DrawableRes val link: Int,
    @DrawableRes val star: Int
)

internal val NewIcons = AppIcons(
    home = R.drawable.ic_home,
    apps = R.drawable.ic_apps,
    catalog = R.drawable.ic_catalog,
    about = R.drawable.ic_about,
    github = R.drawable.ic_github,
    link = R.drawable.ic_link,
    star = R.drawable.ic_star
)

internal val LegacyIcons = AppIcons(
    home = R.drawable.ic_legacy_home,
    apps = R.drawable.ic_legacy_apps,
    catalog = R.drawable.ic_legacy_catalog,
    about = R.drawable.ic_legacy_about,
    github = R.drawable.ic_legacy_github,
    link = R.drawable.ic_legacy_link,
    star = R.drawable.ic_legacy_star
)

internal val LocalAppIcons = staticCompositionLocalOf { NewIcons }

/** The color scheme for the chosen UI: the library palette, or the old one. */
@Composable
internal fun catalogColorScheme(newUi: Boolean, dynamicColor: Boolean): ColorScheme {
    val darkTheme = isSystemInDarkTheme()
    return when {
        newUi || PvotThemeDefaults.usingDynamicColor(dynamicColor) ->
            PvotThemeDefaults.colorScheme(darkTheme, dynamicColor)
        darkTheme -> LegacyDarkColorScheme
        else -> LegacyLightColorScheme
    }
}

/** The library's nav bar with the new UI on, the pre-redesign one otherwise. */
@Composable
internal fun CatalogNavBar(
    selectedTab: Int,
    onTabClick: (Int) -> Unit,
    tabs: List<PvotTabItem>,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    backdrop: PvotGlassBackdrop? = null
) {
    if (LocalNewUi.current) {
        PvotNavBar(selectedTab, onTabClick, tabs, modifier, backdrop = backdrop, compact = compact)
    } else {
        LegacyNavBar(selectedTab, onTabClick, tabs, modifier)
    }
}
