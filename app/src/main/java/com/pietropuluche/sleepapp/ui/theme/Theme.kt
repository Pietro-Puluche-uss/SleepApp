package com.pietropuluche.sleepapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SleepDarkColors = darkColorScheme(
    primary = DreamPurple,
    onPrimary = TextPrimary,
    secondary = CalmTeal,
    tertiary = MoonCream,
    background = NightBackground,
    surface = NightPanel,
    onSurface = TextPrimary,
    onBackground = TextPrimary,
    error = WarningCoral
)

@Composable
fun SleepAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SleepDarkColors,
        typography = Typography,
        content = content
    )
}
