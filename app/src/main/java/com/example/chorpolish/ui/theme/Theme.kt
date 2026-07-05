package com.rakibul.chorpolish.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.rakibul.chorpolish.ui.menu.DecorativeShapes

private val DarkColorScheme = darkColorScheme(
    primary = GamePrimary,
    onPrimary = Color.Black,
    primaryContainer = GameBackgroundMid,
    onPrimaryContainer = OnSurfaceVibrant,
    secondary = GameSecondary,
    onSecondary = Color.Black,
    secondaryContainer = GameBackgroundEnd,
    onSecondaryContainer = OnSurfaceVibrant,
    tertiary = GameTertiary,
    onTertiary = Color.Black,
    tertiaryContainer = GameBackgroundStart,
    onTertiaryContainer = OnSurfaceVibrant,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = GameBackgroundStart,
    onBackground = Color.White,
    surface = GameBackgroundMid.copy(alpha = 0.85f),
    onSurface = Color.White,
    onSurfaceVariant = Color.White.copy(alpha = 0.7f),
    outline = Color.White.copy(alpha = 0.3f),
    outlineVariant = Color.White.copy(alpha = 0.1f)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight
)

@Composable
fun ChorPolishTheme(
    darkTheme: Boolean = true, // Default to true for game feel
    dynamicColor: Boolean = false, // Disable dynamic for branding consistency
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun GameScreenWrapper(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        GameBackgroundStart,
                        GameBackgroundMid,
                        GameBackgroundEnd
                    )
                )
            )
    ) {
        DecorativeShapes()
        content()
    }
}
