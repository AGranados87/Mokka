package com.agranadosruiz.mokka.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MarronOscuro,
    secondary = BeigeRecuadro,
    tertiary = Blanco,
    background = MarronOscuro,
    surface = MarronFondo,
    onPrimary = Blanco,
    onSecondary = MarronOscuro,
    onSurface = Blanco
)

private val LightColorScheme = lightColorScheme(
    primary = MarronFondo,
    secondary = BeigeRecuadro,
    tertiary = MarronOscuro,
    background = MarronFondo,
    surface = BeigeRecuadro,
    onPrimary = Blanco,
    onSecondary = MarronOscuro,
    onSurface = MarronOscuro
)

@Composable
fun MokkaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
