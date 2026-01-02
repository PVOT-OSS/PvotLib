package org.prauga.pvot.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        primary = Purple80,
        secondary = PurpleGrey80,
        tertiary = Pink80,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Purple40,
        secondary = PurpleGrey40,
        tertiary = Pink40,
    )

@Immutable
data class PvotNavBarColors(
    val gradient: Brush,
    val collapsedChipColor: Color,
    val containerColor: Color,
    val iconSelectedColor: Color,
    val iconUnselectedColor: Color,
)

val LocalPvotNavBarColors =
    staticCompositionLocalOf {
        PvotNavBarColors(
            gradient = Brush.horizontalGradient(listOf(NavBarGradientStart, NavBarGradientEnd)),
            collapsedChipColor = NavBarCollapsedChip,
            containerColor = NavBarContainer,
            iconSelectedColor = NavBarIconSelected,
            iconUnselectedColor = NavBarIconUnselected,
        )
    }

object PvotTheme {
    val navBarColors: PvotNavBarColors
        @Composable
        get() = LocalPvotNavBarColors.current
}

@Composable
fun PvotAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    navBarColors: PvotNavBarColors =
        PvotNavBarColors(
            gradient = Brush.horizontalGradient(listOf(NavBarGradientStart, NavBarGradientEnd)),
            collapsedChipColor = NavBarCollapsedChip,
            containerColor = NavBarContainer,
            iconSelectedColor = NavBarIconSelected,
            iconUnselectedColor = NavBarIconUnselected,
        ),
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    CompositionLocalProvider(
        LocalPvotNavBarColors provides navBarColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = PvotTypography,
            content = content,
        )
    }
}
