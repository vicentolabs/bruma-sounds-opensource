package com.sonidosfondo.bruma.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val BrumaAccent = Color(0xFFD5F28B)
val BrumaBackground = Color(0xFF101716)
val BrumaSurface = Color(0xFF18211F)
val BrumaSurfaceRaised = Color(0xFF202B28)
val BrumaText = Color(0xFFF1F4EC)
val BrumaMuted = Color(0xFFA1ADA6)

private val BrumaColors = darkColorScheme(
    primary = BrumaAccent,
    onPrimary = Color(0xFF1D2A1B),
    secondary = Color(0xFF9CCDBA),
    onSecondary = Color(0xFF11251F),
    secondaryContainer = BrumaAccent,
    onSecondaryContainer = Color(0xFF1D2A1B),
    background = BrumaBackground,
    onBackground = BrumaText,
    surface = BrumaSurface,
    onSurface = BrumaText,
    surfaceVariant = BrumaSurfaceRaised,
    onSurfaceVariant = BrumaMuted,
    outline = Color(0xFF34423D),
)

private val BrumaTypography = Typography(
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        lineHeight = 35.sp,
        letterSpacing = (-0.6).sp,
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 25.sp,
        letterSpacing = (-0.2).sp,
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        letterSpacing = 1.2.sp,
    ),
)

@Composable
fun BrumaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BrumaColors,
        typography = BrumaTypography,
        content = content,
    )
}
