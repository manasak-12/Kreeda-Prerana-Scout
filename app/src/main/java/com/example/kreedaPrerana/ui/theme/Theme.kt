package com.example.myapplication.ui.theme

import android.app.Activity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ScoutNavyLight,
    onPrimary = ScoutWhite,
    primaryContainer = ScoutBlue,
    onPrimaryContainer = ScoutWhite,
    secondary = ScoutOrangeLight,
    onSecondary = ScoutNavy,
    background = ScoutNavy,
    surface = ScoutBlue,
    onBackground = ScoutWhite,
    onSurface = ScoutWhite,
)

private val LightColorScheme = lightColorScheme(
    primary = ScoutNavy,
    onPrimary = ScoutWhite,
    primaryContainer = ScoutBlue,
    onPrimaryContainer = ScoutWhite,
    secondary = ScoutOrange,
    onSecondary = ScoutWhite,
    background = ScoutWhite,
    surface = ScoutGray,
    onBackground = ScoutNavy,
    onSurface = ScoutNavy,
    tertiary = ScoutGreen
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoutScaffold(
    title: String,
    actions: @Composable RowScope.() -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val currentLocale = AppCompatDelegate.getApplicationLocales().get(0)?.language ?: "en"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    actions()
                    TextButton(
                        onClick = {
                            val newLocale = if (currentLocale == "en") "kn" else "en"
                            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(newLocale)
                            AppCompatDelegate.setApplicationLocales(appLocale)
                        }
                    ) {
                        Text(
                            text = if (currentLocale == "en") "ಕನ್ನಡ" else "EN",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        },
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        containerColor = MaterialTheme.colorScheme.background,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
