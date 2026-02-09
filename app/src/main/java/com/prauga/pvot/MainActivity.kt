// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prauga.pvot.utils.PreferencesManager
import com.prauga.pvot.designsystem.components.navigation.NavBarConfig
import com.prauga.pvot.designsystem.components.navigation.PvotNavBar
import com.prauga.pvot.designsystem.components.navigation.PvotTabItem
import com.prauga.pvot.designsystem.domain.monitoring.PerformanceMonitor
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import com.prauga.pvot.screens.AboutScreen
import com.prauga.pvot.screens.AppsScreen
import com.prauga.pvot.screens.CatalogScreen
import com.prauga.pvot.screens.EmptyScreen
import com.prauga.pvot.screens.HomeScreen
import kotlinx.coroutines.delay

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
    var configurationError by remember { mutableStateOf<String?>(null) }

    // Create PerformanceMonitor instance with DEBUG flag
    // Monitoring is only enabled in debug builds to avoid production overhead
    val performanceMonitor = remember {
        PerformanceMonitor(
            enabled = BuildConfig.DEBUG,
            warningThreshold = 100
        )
    }

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

    // Log performance metrics after user interactions
    // Waits 5 seconds to allow for tab switching and navigation
    LaunchedEffect(Unit) {
        try {
            delay(5000)
            val metrics = performanceMonitor.getMetrics("PvotNavBar")
            Log.d("Performance", "PvotNavBar Metrics:")
            Log.d("Performance", "  Recompositions: ${metrics.recompositionCount}")
            Log.d("Performance", "  Avg Calculation Time: ${metrics.averageCalculationTime}ms")
            Log.d("Performance", "  Max Calculation Time: ${metrics.maxCalculationTime}ms")
            performanceMonitor.logWarnings()
        } catch (e: Exception) {
            // Error handling for monitoring failures
            // Monitoring failures should not break the app
            Log.e("Performance", "Failed to retrieve performance metrics", e)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        bottomBar = {
            // Validate configuration before creating PvotNavBar
            // This catches validation errors early and displays error UI
            val validationError = remember(tabs, selectedTab) {
                try {
                    // Attempt to create config to trigger validation
                    NavBarConfig(
                        tabs = tabs,
                        selectedTab = selectedTab
                    )
                    null // No error
                } catch (e: IllegalArgumentException) {
                    // Log validation errors with full details
                    Log.e("MainActivity", "NavBarConfig validation failed", e)
                    Log.e("MainActivity", "Configuration details:")
                    Log.e("MainActivity", "  - tabs.size: ${tabs.size}")
                    Log.e("MainActivity", "  - selectedTab: $selectedTab")
                    Log.e("MainActivity", "  - error message: ${e.message}")
                    
                    // Store error message
                    e.message
                }
            }
            
            // Update configuration error state
            if (validationError != null && configurationError == null) {
                configurationError = validationError
            }
            
            if (validationError == null) {
                // Create NavBarConfig with tabs and selectedTab grouped together
                // Uses default NavBarAppearance and NavBarBehavior for optimized performance
                val config = NavBarConfig(
                    tabs = tabs,
                    selectedTab = selectedTab
                )
                
                // Pass PerformanceMonitor to PvotNavBar for recomposition tracking
                // Monitoring is only active in debug builds
                PvotNavBar(
                    config = config,
                    onTabClick = { selectedTab = it },
                    performanceMonitor = performanceMonitor
                )
            } else {
                // Display empty box to prevent crash
                // The error UI will be shown in the main content area
                Box(modifier = Modifier)
            }
        }
    ) { innerPadding ->
        val containerModifer = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = innerPadding.calculateTopPadding())

        // Display error UI if configuration validation failed
        if (configurationError != null) {
            ConfigurationErrorScreen(
                errorMessage = configurationError ?: "Unknown configuration error",
                modifier = containerModifer
            )
        } else {
            // Normal navigation flow
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
}

@Preview(showBackground = true)
@Composable
fun DesignSystemShowcasePreview() {
    PvotAppTheme {
        DesignSystemShowcase()
    }
}

/**
 * Error screen displayed when NavBarConfig validation fails.
 * Shows detailed error information to help developers identify configuration issues.
 *
 * @param errorMessage The validation error message from IllegalArgumentException
 * @param modifier Modifier to be applied to the error screen container
 */
@Composable
fun ConfigurationErrorScreen(
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Configuration Error",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "The navigation bar configuration is invalid:",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Text(
                text = "Please check the logs for detailed information.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
