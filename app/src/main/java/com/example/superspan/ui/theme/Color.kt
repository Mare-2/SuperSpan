package com.example.superspan.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

val LogoLeft = Color(0xFF559CA7)
val LogoCenter = Color(0xFF72BA8C)
val LogoRight = Color(0xFF8CD06A)

val AppBackgroundBrush = Brush.verticalGradient(
    colors = listOf(
        LogoLeft.copy(alpha = 0.20f),
        LogoCenter.copy(alpha = 0.15f),
        LogoRight.copy(alpha = 0.20f)
    )
)

val BackgroundLight = Color(0xFFF8F9FA)
val SurfaceWhite = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF1E1E1E)