// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.catalog

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import com.prauga.pvot.designsystem.effects.LocalPvotGlassIntensity
import com.prauga.pvot.designsystem.effects.pvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.rememberPvotGlassBackdrop
import com.prauga.pvot.designsystem.platform.PvotBaseActivity
import com.prauga.pvot.designsystem.navigation.PvotTabItem
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import com.prauga.pvot.catalog.components.LiveBackground
import com.prauga.pvot.catalog.components.LocalBackgroundBackdrop
import com.prauga.pvot.catalog.data.repository.GithubRepository
import com.prauga.pvot.catalog.hub.CatalogHub
import com.prauga.pvot.catalog.legacy.CatalogNavBar
import com.prauga.pvot.catalog.legacy.LegacyIcons
import com.prauga.pvot.catalog.legacy.LocalAppIcons
import com.prauga.pvot.catalog.legacy.LocalNewUi
import com.prauga.pvot.catalog.legacy.NewIcons
import com.prauga.pvot.catalog.legacy.catalogColorScheme
import com.prauga.pvot.catalog.screens.AboutScreen
import com.prauga.pvot.catalog.screens.AppsScreen
import com.prauga.pvot.catalog.screens.CatalogScreen
import com.prauga.pvot.catalog.screens.EmptyScreen
import com.prauga.pvot.catalog.screens.HomeScreen
import com.prauga.pvot.catalog.utils.PreferencesManager

class MainActivity : PvotBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferencesManager.init(applicationContext)
        GithubRepository.init(applicationContext)
        setPvotContent {
            val dynamicColor by PreferencesManager.dynamicColorEnabled.collectAsState()
            val newUi by PreferencesManager.newUiEnabled.collectAsState()
            val glassIntensity by PreferencesManager.glassIntensity.collectAsState()
            CompositionLocalProvider(
                LocalPvotGlassIntensity provides glassIntensity,
                LocalNewUi provides newUi,
                LocalAppIcons provides if (newUi) NewIcons else LegacyIcons
            ) {
                PvotAppTheme(
                    dynamicColor = dynamicColor,
                    colorScheme = catalogColorScheme(newUi, dynamicColor)
                ) {
                    DesignSystemShowcase()
                }
            }
        }
    }
}

/** Pixels of scroll in one event before the nav bar reacts, so a jitter doesn't flip it. */
private const val ScrollThreshold = 4f

@Composable
fun DesignSystemShowcase() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var navCompact by remember { mutableStateOf(false) }
    // Swiping up through a list shrinks the nav bar out of the way; swiping back down restores it.
    val scrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -ScrollThreshold) navCompact = true
                else if (available.y > ScrollThreshold) navCompact = false
                return Offset.Zero
            }
        }
    }
    // The old UI is the glass demo as it was: a live background behind glass cards and a glass
    // bar. The new UI keeps the glass bar and cards over the plain background the app has always had.
    val newUi = LocalNewUi.current
    val backdrop = rememberPvotGlassBackdrop()
    val backgroundBackdrop = rememberPvotGlassBackdrop()

    val icons = LocalAppIcons.current
    val tabs = listOf(
        PvotTabItem(
            iconRes = icons.home,
            labelRes = R.string.tab_home,
            contentDescriptionRes = R.string.cd_home
        ),
        PvotTabItem(
            iconRes = icons.apps,
            labelRes = R.string.tab_apps,
            contentDescriptionRes = R.string.cd_apps
        ),
        PvotTabItem(
            iconRes = icons.catalog,
            labelRes = R.string.tab_catalog,
            contentDescriptionRes = R.string.cd_catalog
        ),
        PvotTabItem(
            iconRes = icons.about,
            labelRes = R.string.tab_about,
            contentDescriptionRes = R.string.cd_about
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        bottomBar = {
            CatalogNavBar(
                selectedTab = selectedTab,
                onTabClick = {
                    selectedTab = it
                    navCompact = false
                },
                tabs = tabs,
                compact = navCompact,
                backdrop = backdrop
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .pvotGlassBackdrop(backdrop)
        ) {
            if (!newUi) {
                LiveBackground(
                    modifier = Modifier
                        .fillMaxSize()
                        .pvotGlassBackdrop(backgroundBackdrop)
                )
            }
            CompositionLocalProvider(
                LocalBackgroundBackdrop provides if (newUi) null else backgroundBackdrop
            ) {
                val containerModifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollConnection)
                    .padding(top = innerPadding.calculateTopPadding())

                when (selectedTab) {
                    0 -> HomeScreen(label = "Home", modifier = containerModifier)
                    1 -> AppsScreen(label = "Apps", modifier = containerModifier)
                    2 -> if (newUi) {
                        CatalogHub(modifier = containerModifier)
                    } else {
                        CatalogScreen(label = "Design Catalog", modifier = containerModifier)
                    }
                    3 -> AboutScreen(label = "About", modifier = containerModifier)
                    else -> EmptyScreen(label = "None", modifier = containerModifier)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DesignSystemShowcasePreview() {
    PvotAppTheme {
        DesignSystemShowcase()
    }
}
