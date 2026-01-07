// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.prauga.pvot.designsystem.components.navigation.LocalPvotNavBarColors
import com.prauga.pvot.designsystem.components.navigation.LocalPvotNavBarSizes
import com.prauga.pvot.designsystem.components.navigation.PvotNavBarColors
import com.prauga.pvot.designsystem.components.navigation.PvotNavBarSizes
import com.prauga.pvot.designsystem.components.picker.LocalPvotPickerColors
import com.prauga.pvot.designsystem.components.picker.PvotPickerColors

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

object PvotTheme {
    val navBarColors: PvotNavBarColors
        @Composable get() = LocalPvotNavBarColors.current

    val navBarSizes: PvotNavBarSizes
        @Composable get() = LocalPvotNavBarSizes.current

    val pickerColors: PvotPickerColors
        @Composable get() = LocalPvotPickerColors.current
}

@Composable
fun PvotAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    navBarColors: PvotNavBarColors = LocalPvotNavBarColors.current,
    navBarSizes: PvotNavBarSizes = PvotNavBarSizes(),
    pickerColors: PvotPickerColors? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val resolvedPickerColors = pickerColors ?: PvotPickerColors(
        textColor = colorScheme.onBackground,
        textSecondaryColor = colorScheme.onBackground.copy(alpha = 0.7f),
        selectionBackgroundColor = colorScheme.onBackground.copy(alpha = 0.1f)
    )

    CompositionLocalProvider(
        LocalPvotNavBarColors provides navBarColors,
        LocalPvotNavBarSizes provides navBarSizes,
        LocalPvotPickerColors provides resolvedPickerColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = PvotTypography,
            content = content
        )
    }
}
