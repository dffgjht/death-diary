package com.memoamber.desktop.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ===== 琥珀暖色系 — 亮色 =====
private val AmberPrimary = Color(0xFFBF8A30)
private val AmberOnPrimary = Color(0xFFFFFFFF)
private val AmberPrimaryContainer = Color(0xFFFFDEA6)
private val AmberOnPrimaryContainer = Color(0xFF2A1D00)

private val AmberSecondary = Color(0xFF765A47)
private val AmberOnSecondary = Color(0xFFFFFFFF)
private val AmberSecondaryContainer = Color(0xFFFFDEA6)
private val AmberOnSecondaryContainer = Color(0xFF2C1E0F)

private val AmberTertiary = Color(0xFF6B5F7E)
private val AmberOnTertiary = Color(0xFFFFFFFF)
private val AmberTertiaryContainer = Color(0xFFF2DAFF)
private val AmberOnTertiaryContainer = Color(0xFF261437)

private val AmberBackground = Color(0xFFFFF8F0)
private val AmberOnBackground = Color(0xFF1F1B16)
private val AmberSurface = Color(0xFFFFFBF5)
private val AmberOnSurface = Color(0xFF1F1B16)
private val AmberSurfaceVariant = Color(0xFFF0E6D8)
private val AmberOnSurfaceVariant = Color(0xFF50453A)

private val AmberOutline = Color(0xFF827568)
private val AmberOutlineVariant = Color(0xFFD4C4B4)

// 侧边栏专用深色
val SidebarBackground = Color(0xFF2A1F14)
val SidebarOnBackground = Color(0xFFF5E6D0)
val SidebarSelectedItem = Color(0xFFBF8A30)
val SidebarHoverItem = Color(0xFF3D2E1E)

// ===== 琥珀暖色系 — 暗色 =====
private val DarkAmberPrimary = Color(0xFFFFB840)
private val DarkAmberOnPrimary = Color(0xFF432C00)
private val DarkAmberPrimaryContainer = Color(0xFF614000)
private val DarkAmberOnPrimaryContainer = Color(0xFFFFDEA6)

private val DarkAmberSecondary = Color(0xFFE5C1A4)
private val DarkAmberOnSecondary = Color(0xFF432C1A)
private val DarkAmberSecondaryContainer = Color(0xFF5C422F)
private val DarkAmberOnSecondaryContainer = Color(0xFFFFDEA6)

private val DarkAmberTertiary = Color(0xFFD6BEF0)
private val DarkAmberOnTertiary = Color(0xFF3C2D4D)
private val DarkAmberTertiaryContainer = Color(0xFF534465)
private val DarkAmberOnTertiaryContainer = Color(0xFFF2DAFF)

private val DarkAmberBackground = Color(0xFF17130E)
private val DarkAmberOnBackground = Color(0xFFECE0D4)
private val DarkAmberSurface = Color(0xFF1F1B16)
private val DarkAmberOnSurface = Color(0xFFECE0D4)
private val DarkAmberSurfaceVariant = Color(0xFF50453A)
private val DarkAmberOnSurfaceVariant = Color(0xFFD4C4B4)

private val DarkAmberOutline = Color(0xFF9C8F80)
private val DarkAmberOutlineVariant = Color(0xFF50453A)

val DarkSidebarBackground = Color(0xFF0F0C08)
val DarkSidebarOnBackground = Color(0xFFECE0D4)
val DarkSidebarSelectedItem = Color(0xFFFFB840)
val DarkSidebarHoverItem = Color(0xFF2A2018)

@Composable
fun MemoAmberTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme(
            primary = DarkAmberPrimary,
            onPrimary = DarkAmberOnPrimary,
            primaryContainer = DarkAmberPrimaryContainer,
            onPrimaryContainer = DarkAmberOnPrimaryContainer,
            secondary = DarkAmberSecondary,
            onSecondary = DarkAmberOnSecondary,
            secondaryContainer = DarkAmberSecondaryContainer,
            onSecondaryContainer = DarkAmberOnSecondaryContainer,
            tertiary = DarkAmberTertiary,
            onTertiary = DarkAmberOnTertiary,
            tertiaryContainer = DarkAmberTertiaryContainer,
            onTertiaryContainer = DarkAmberOnTertiaryContainer,
            background = DarkAmberBackground,
            onBackground = DarkAmberOnBackground,
            surface = DarkAmberSurface,
            onSurface = DarkAmberOnSurface,
            surfaceVariant = DarkAmberSurfaceVariant,
            onSurfaceVariant = DarkAmberOnSurfaceVariant,
            outline = DarkAmberOutline,
            outlineVariant = DarkAmberOutlineVariant,
        ) else lightColorScheme(
            primary = AmberPrimary,
            onPrimary = AmberOnPrimary,
            primaryContainer = AmberPrimaryContainer,
            onPrimaryContainer = AmberOnPrimaryContainer,
            secondary = AmberSecondary,
            onSecondary = AmberOnSecondary,
            secondaryContainer = AmberSecondaryContainer,
            onSecondaryContainer = AmberOnSecondaryContainer,
            tertiary = AmberTertiary,
            onTertiary = AmberOnTertiary,
            tertiaryContainer = AmberTertiaryContainer,
            onTertiaryContainer = AmberOnTertiaryContainer,
            background = AmberBackground,
            onBackground = AmberOnBackground,
            surface = AmberSurface,
            onSurface = AmberOnSurface,
            surfaceVariant = AmberSurfaceVariant,
            onSurfaceVariant = AmberOnSurfaceVariant,
            outline = AmberOutline,
            outlineVariant = AmberOutlineVariant,
        ),
        content = content
    )
}
