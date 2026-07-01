package com.example.superspan.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

// --- Brand (trio del logo) ---
val LogoLeft = Color(0xFF559CA7)
val LogoCenter = Color(0xFF72BA8C)
val LogoRight = Color(0xFF8CD06A)

// Variante scura del brand: per testo/icone su bianco (passa il contrasto, a differenza dei verdi chiari)
val BrandDark = Color(0xFF2A6E78)

// --- Colori di stato (semantici) ---
// Distinti dal brand: indicano stato, non decorazione. Utili a Daniela (coupon attivi/in scadenza/scaduti)
// e a Paolo (conferme/errori inequivocabili).
val AppError = Color(0xFFE63946)   // errore / scartato
val AppSuccess = Color(0xFF2E7D32) // successo / attivo
val AppWarning = Color(0xFFF59E0B) // attenzione / in scadenza
val AppExpired = Color(0xFF9CA3AF) // scaduto / disabilitato

// --- Neutri (la base del 90% dell'interfaccia) ---
val Neutral50 = Color(0xFFF8F9FA)  // sfondo pagina
val Neutral100 = Color(0xFFF1F3F5)
val Neutral200 = Color(0xFFE9ECEF) // bordi / divisori
val Neutral400 = Color(0xFFADB5BD)
val Neutral600 = Color(0xFF6B7280) // testo secondario
val Neutral900 = Color(0xFF1E1E1E) // testo primario

val AppBackgroundBrush = Brush.verticalGradient(
    colors = listOf(
        LogoLeft.copy(alpha = 0.49f),
        LogoCenter.copy(alpha = 0.39f),
        LogoRight.copy(alpha = 0.49f)
    )
)

val BackgroundLight = Color(0xFFF8F9FA)
val SurfaceWhite = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF1E1E1E)

val AppHeaderFadeBrush = Brush.verticalGradient(
    colors = listOf(
        LogoLeft.copy(alpha = 0.30f),
        LogoCenter.copy(alpha = 0.10f),
        Color.Transparent
    )
)