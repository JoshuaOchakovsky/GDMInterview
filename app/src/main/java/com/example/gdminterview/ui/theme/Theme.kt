package com.example.gdminterview.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

//private val DarkColorScheme = darkColorScheme()

private val LightColorScheme = lightColorScheme(
    primary = DarkBlue,
    onPrimary = White,

    surface = DarkBlueGray,
    onSurface = White,

    surfaceVariant = MediumGray,

    outlineVariant = DarkGray,
)

@Composable
fun GDMInterviewTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // TODO: implement dynamic colors
        //        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        //            val context = LocalContext.current
        //            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        //        }
        // TODO: implement dark theme
        // darkTheme -> LightColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = AppShapes,
        typography = Typography,
        content = content
    )
}