// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.pvot

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.prauga.pvot.designsystem.components.navigation.PvotNavBar
import org.prauga.pvot.designsystem.components.navigation.PvotTabItem
import org.prauga.pvot.designsystem.theme.PvotAppTheme
import org.prauga.pvot.screens.CatalogScreen
import org.prauga.pvot.screens.EmptyScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PvotAppTheme {
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
        bottomBar = {
            PvotNavBar(
                selectedTab = selectedTab,
                onTabClick = { selectedTab = it },
                tabs = tabs
            )
        }
    ) { innerPadding ->
        val containerModifer = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)

        when (selectedTab) {
            0 -> EmptyScreen(
                label = "Home",
                modifier = containerModifer
            )

            1 -> EmptyScreen(
                label = "Apps",
                modifier = containerModifer
            )

            2 -> CatalogScreen(
                label = "Design Catalog",
                modifier = containerModifer
            )

            3 -> EmptyScreen(
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
