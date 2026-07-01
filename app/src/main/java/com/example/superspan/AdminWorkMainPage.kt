package com.example.superspan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AdminWorkMainPage(paddingValues: PaddingValues, navController: NavController?, initialTab: Int = 0) {
    var selectedTabIndex by remember { mutableStateOf(initialTab) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Intestazione persistente: il titolo cambia in base alla sezione selezionata
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = paddingValues.calculateTopPadding() + 24.dp, bottom = 8.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (selectedTabIndex == 0) {
                Text("Lavora con noi!", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A1A1A))
                Text("Gestisci le posizioni aperte", fontSize = 15.sp, color = Color.Gray)
            } else {
                Text("Gestione Candidature", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A1A1A))
                Text("Pannello di Amministrazione", fontSize = 15.sp, color = Color.Gray)
            }
        }

        // Barra tab PERSISTENTE: restando montata attraverso il cambio di sezione,
        // l'indicatore può scorrere animato invece di "saltare".
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
        ) {
            AnimatedSegmentedControl(
                options = listOf("Posizioni Aperte", "Candidature"),
                selectedIndex = selectedTabIndex
            ) { selectedTabIndex = it }
        }

        // Sotto la barra cambia solo il contenuto. Le pagine non mostrano il proprio
        // titolo/selettore (il tab qui sopra fa già da intestazione).
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (selectedTabIndex) {
                0 -> WorkSearchPageComplete(
                    padding = paddingValues,
                    navController = navController,
                    hideHeader = true,
                    sliderContent = null
                )
                1 -> AdminCandidaciesPage(
                    navController = navController,
                    paddingValues = paddingValues,
                    hideHeader = true,
                    sliderContent = null
                )
            }
        }
    }
}
