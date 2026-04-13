package com.cisco.quizapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Focus Theme: Neutral grays + color only for feedback ─────────────────────
// Primary blue is used sparingly: progress bar + action buttons only.
// Green/Red are reserved exclusively for correct/wrong feedback.
// Gold/Amber is reserved exclusively for timer urgency.
val CiscoTeal = Color(0xFF4B9EFF)        // Accent blue — progress bar, CTAs
val CiscoDarkBlue = Color(0xFF1E3560)    // Dark container (rarely used)
val CiscoNavyBg = Color(0xFF1A1A1A)      // Background: dark gray
val CiscoNavySurface = Color(0xFF2A2A2A) // Cards: #2A2A2A
val CiscoNavyVariant = Color(0xFF333333) // Surface variant: slightly lighter gray
val CiscoGreen = Color(0xFF4CAF50)       // Feedback only: correct answer
val CiscoGold = Color(0xFFFFA726)        // Warning only: timer urgency
val CiscoRed = Color(0xFFEF5350)         // Feedback only: wrong answer

private val CiscoColorScheme = darkColorScheme(
    primary = CiscoTeal,
    onPrimary = Color(0xFF0A1F3D),
    primaryContainer = Color(0xFF1A3050),
    onPrimaryContainer = Color(0xFFB8D4FF),
    secondary = Color(0xFF7AABCC),
    onSecondary = Color(0xFF0D2030),
    secondaryContainer = Color(0xFF1A3040),
    onSecondaryContainer = Color(0xFFB0D4E8),
    tertiary = CiscoGreen,
    onTertiary = Color(0xFF0A2010),
    tertiaryContainer = Color(0xFF1A3520),
    onTertiaryContainer = Color(0xFFB8E8C0),
    background = CiscoNavyBg,
    onBackground = Color(0xFFE0E0E0),
    surface = CiscoNavySurface,
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = CiscoNavyVariant,
    onSurfaceVariant = Color(0xFF9E9E9E),
    outline = Color(0xFF4A4A4A),
    error = CiscoRed,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF4A1010),
    onErrorContainer = Color(0xFFFFDAD6),
)

@Composable
fun CiscoQuizAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CiscoColorScheme,
        typography = Typography,
        content = content
    )
}
