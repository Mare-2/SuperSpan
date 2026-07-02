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
    var isFilterOpen by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        if (!isFilterOpen) {
            // Intestazione persistente (stessa dell'app): il titolo cambia in base alla sezione
            if (selectedTabIndex == 0) {
                PrimaryHeader("Lavora con noi!", "Gestisci le posizioni aperte")
            } else {
                PrimaryHeader("Gestione Candidature", "Pannello di Amministrazione")
            }

            // Barra tab PERSISTENTE: restando montata attraverso il cambio di sezione,
            // l'indicatore può scorrere animato invece di "saltare".
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp)
            ) {
                AnimatedSegmentedControl(
                    options = listOf("Posizioni Aperte", "Candidature"),
                    selectedIndex = selectedTabIndex
                ) { selectedTabIndex = it }
            }
        }

        // Sotto la barra cambia solo il contenuto. Le pagine non mostrano il proprio
        // titolo/selettore (il tab qui sopra fa già da intestazione).
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (selectedTabIndex) {
                0 -> WorkSearchPageComplete(
                    padding = paddingValues,
                    navController = navController,
                    hideHeader = true,
                    sliderContent = null,
                    onFilterOpenChange = { isFilterOpen = it }
                )
                1 -> AdminCandidaciesPage(
                    navController = navController,
                    paddingValues = paddingValues,
                    hideHeader = true,
                    sliderContent = null,
                    onFilterOpenChange = { isFilterOpen = it }
                )
            }
        }
    }
}
