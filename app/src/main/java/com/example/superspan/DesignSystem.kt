package com.example.superspan

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superspan.ui.theme.BrandDark
import com.example.superspan.ui.theme.LogoLeft
import com.example.superspan.ui.theme.Neutral600
import com.example.superspan.ui.theme.Neutral900

/**
 * Stato "vuoto" coerente per le liste: icona tenue + titolo + sottotitolo + eventuale azione.
 * Sostituisce i semplici testi grigi centrati, dando una finitura "da app vera".
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(84.dp)
                .background(LogoLeft.copy(alpha = 0.08f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = LogoLeft.copy(alpha = 0.7f), modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Neutral900, textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text(subtitle, fontSize = 14.sp, color = Neutral600, textAlign = TextAlign.Center)
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onAction,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = LogoLeft)
            ) {
                Text(actionLabel, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

/** Piccola "pillola" informativa con icona, per riepiloghi rapidi (es. dettaglio offerta di lavoro). */
@Composable
fun InfoChip(icon: ImageVector, text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = LogoLeft.copy(alpha = 0.10f),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = BrandDark, modifier = Modifier.size(15.dp))
            Spacer(Modifier.width(5.dp))
            Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BrandDark)
        }
    }
}

/**
 * Codice a barre "finto" ma deterministico (stesso codice -> stesso disegno).
 * Puramente decorativo, per rendere credibile la schermata del coupon alla cassa.
 */
@Composable
fun FakeBarcode(code: String, modifier: Modifier = Modifier) {
    val bars = remember(code) {
        val rnd = java.util.Random(code.hashCode().toLong())
        List(56) { 1f + rnd.nextInt(3) } // larghezze relative delle barre
    }
    Canvas(modifier = modifier.fillMaxWidth().height(60.dp)) {
        val total = bars.sum()
        val unit = size.width / total
        var x = 0f
        bars.forEachIndexed { i, w ->
            if (i % 2 == 0) {
                drawRect(
                    color = Color.Black,
                    topLeft = Offset(x, 0f),
                    size = Size(w * unit, size.height)
                )
            }
            x += w * unit
        }
    }
}

/**
 * Selettore a segmenti con indicatore che SCORRE (stessa animazione "a molla" della navbar).
 * Stile: track teal tenue, pillola selezionata piena (BrandDark) con testo bianco.
 * Riusato per Coupon/Offerte, Bozze/Inviate e Posizioni Aperte/Candidature.
 */
@Composable
fun AnimatedSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    onSelect: (Int) -> Unit
) {
    val count = options.size.coerceAtLeast(1)
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(CircleShape)
            .background(LogoLeft.copy(alpha = 0.12f))
            .padding(4.dp)
    ) {
        val segmentWidth = maxWidth / count
        val indicatorOffset by animateDpAsState(
            targetValue = segmentWidth * selectedIndex,
            animationSpec = spring(dampingRatio = 0.85f, stiffness = 380f),
            label = "segmentIndicator"
        )
        // Indicatore animato (la "pillola" selezionata)
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(segmentWidth)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(BrandDark)
        )
        // Etichette cliccabili sopra l'indicatore
        Row(Modifier.fillMaxSize()) {
            options.forEachIndexed { i, label ->
                val selected = i == selectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onSelect(i) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                        color = if (selected) Color.White else BrandDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
