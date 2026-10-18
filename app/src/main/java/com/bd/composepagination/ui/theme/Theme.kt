package com.bd.composepagination.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = InboxPurple,
    onPrimary = LightInboxBackground,
    background = LightInboxBackground,
    surface = LightInboxBackground,
    surfaceVariant = LightInboxSearchBackground,
    onSurface = LightInboxText,
    onSurfaceVariant = LightInboxSecondaryText,
    outlineVariant = LightInboxDivider
)

private val DarkColorScheme = darkColorScheme(
    primary = InboxPurple,
    onPrimary = DarkInboxText,
    background = DarkInboxBackground,
    surface = DarkInboxBackground,
    surfaceVariant = DarkInboxSearchBackground,
    onSurface = DarkInboxText,
    onSurfaceVariant = DarkInboxSecondaryText,
    outlineVariant = DarkInboxDivider
)

@Composable
fun ComposePaginationTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
