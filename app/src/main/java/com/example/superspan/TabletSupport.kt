package com.example.superspan

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

/**
 * true su schermi larghi (tablet, tipicamente in orizzontale): abilita i layout a due pannelli
 * (lista a sinistra, filtri / form a destra). Sotto la soglia l'app resta identica al telefono.
 * 840dp è il breakpoint "expanded" di Material: esclude i telefoni in orizzontale (600-840dp).
 */
@Composable
fun isExpandedScreen(): Boolean = LocalConfiguration.current.screenWidthDp >= 840

/**
 * Numero di colonne per la griglia prodotti in base alla larghezza:
 * telefono = 2, schermi medi = 3, tablet = 4. Così su telefono sono sempre 2 per riga.
 */
@Composable
fun productGridColumns(): Int {
    val w = LocalConfiguration.current.screenWidthDp
    return when {
        w >= 840 -> 4
        w >= 600 -> 3
        else -> 2
    }
}
