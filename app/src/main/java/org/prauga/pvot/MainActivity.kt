// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.pvot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.prauga.pvot.designsystem.components.navigation.PvotNavBar
import org.prauga.pvot.designsystem.components.navigation.TabItem
import org.prauga.pvot.designsystem.theme.PvotAppTheme

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
        TabItem(
            iconRes = R.drawable.ic_home,
            labelRes = R.string.tab_home,
            contentDescriptionRes = R.string.cd_home
        ),
        TabItem(
            iconRes = R.drawable.ic_settings,
            labelRes = R.string.tab_settings,
            contentDescriptionRes = R.string.cd_settings
        )
    )

    val screenNames = listOf("Home", "Settings")

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = screenNames[selectedTab],
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
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
