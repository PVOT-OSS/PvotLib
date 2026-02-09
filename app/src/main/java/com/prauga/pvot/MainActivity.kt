// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.prauga.pvot.utils.PreferencesManager
import com.prauga.pvot.designsystem.components.navigation.NavBarConfig
import com.prauga.pvot.designsystem.components.navigation.PvotNavBar
import com.prauga.pvot.designsystem.components.navigation.PvotTabItem
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import com.prauga.pvot.screens.AboutScreen
import com.prauga.pvot.screens.AppsScreen
import com.prauga.pvot.screens.CatalogScreen
import com.prauga.pvot.screens.EmptyScreen
import com.prauga.pvot.screens.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferencesManager.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            val dynamicColor by PreferencesManager.dynamicColorEnabled.collectAsState()
            PvotAppTheme(dynamicColor = dynamicColor) {
                DesignSystemShowcase()
            }
        }
    }
}

@Composable
fun DesignSystemShowcase() {
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        PvotTabItem(
            iconRes = R.drawable.ic_home,
            labelRes = R.string.tab_home,
            contentDescriptionRes = R.string.cd_home
        ),
        PvotTabItem(
            iconRes = R.drawable.ic_apps,
            labelRes = R.string.tab_apps,
            contentDescriptionRes = R.string.cd_apps
        ),
        PvotTabItem(
            iconRes = R.drawable.ic_catalog,
            labelRes = R.string.tab_catalog,
            contentDescriptionRes = R.string.cd_catalog
        ),
        PvotTabItem(
            iconRes = R.drawable.ic_about,
            labelRes = R.string.tab_about,
            contentDescriptionRes = R.string.cd_about
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        bottomBar = {
            // Create NavBarConfig with tabs and selectedTab grouped together
            // Uses default NavBarAppearance and NavBarBehavior for optimized performance
            val config = NavBarConfig(
                tabs = tabs,
                selectedTab = selectedTab
            )
            
            PvotNavBar(
                config = config,
                onTabClick = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        val containerModifer = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = innerPadding.calculateTopPadding())

        when (selectedTab) {
            0 -> HomeScreen(
                label = "Home",
                modifier = containerModifer
            )

            1 -> AppsScreen(
                label = "Apps",
                modifier = containerModifer
            )

            2 -> CatalogScreen(
                label = "Design Catalog",
                modifier = containerModifer
            )

            3 -> AboutScreen(
                label = "About",
                modifier = containerModifer
            )

            else -> EmptyScreen(
                label = "None",
                modifier = containerModifer
            )
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
