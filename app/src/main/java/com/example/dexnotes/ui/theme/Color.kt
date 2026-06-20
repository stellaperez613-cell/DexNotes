package com.example.dexnotes.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Seed / primary: a calm indigo-blue suited to a reading/writing app
private val Seed = Color(0xFF3D5AFE)

private val PrimaryLight = Color(0xFF3D5AFE)
private val OnPrimaryLight = Color(0xFFFFFFFF)
private val PrimaryContainerLight = Color(0xFFDDE1FF)
private val OnPrimaryContainerLight = Color(0xFF00116B)

private val SecondaryLight = Color(0xFF5B5D72)
private val OnSecondaryLight = Color(0xFFFFFFFF)
private val SecondaryContainerLight = Color(0xFFE0E0F9)
private val OnSecondaryContainerLight = Color(0xFF181A2C)

private val TertiaryLight = Color(0xFF77536C)
private val OnTertiaryLight = Color(0xFFFFFFFF)
private val TertiaryContainerLight = Color(0xFFFFD7F2)
private val OnTertiaryContainerLight = Color(0xFF2D1228)

private val PrimaryDark = Color(0xFFBAC3FF)
private val OnPrimaryDark = Color(0xFF002094)
private val PrimaryContainerDark = Color(0xFF1534E0)
private val OnPrimaryContainerDark = Color(0xFFDDE1FF)

private val SecondaryDark = Color(0xFFC4C4DD)
private val OnSecondaryDark = Color(0xFF2D2F42)
private val SecondaryContainerDark = Color(0xFF434559)
private val OnSecondaryContainerDark = Color(0xFFE0E0F9)

private val TertiaryDark = Color(0xFFE7B9D8)
private val OnTertiaryDark = Color(0xFF44263D)
private val TertiaryContainerDark = Color(0xFF5D3C54)
private val OnTertiaryContainerDark = Color(0xFFFFD7F2)

val LightColorScheme = lightColorScheme(
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
)

val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
)
