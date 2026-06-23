package com.example.superspan

import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AdminWorkMainPage(paddingValues: PaddingValues, navController: NavController?, initialTab: Int = 0) {
    var selectedTabIndex by remember { mutableStateOf(initialTab) }
    val tabs = listOf("Posizioni Aperte", "Candidature")

    Scaffold(
        modifier = Modifier.padding(paddingValues),
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFF8F9FA)),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .background(Color(0xFFE0E0E0), CircleShape)
                        .padding(4.dp)
                ) {
                    TabButton("Posizioni Aperte", selectedTabIndex == 0, Modifier.weight(1f)) { selectedTabIndex = 0 }
                    TabButton("Candidature", selectedTabIndex == 1, Modifier.weight(1f)) { selectedTabIndex = 1 }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FA))
        ) {
            when (selectedTabIndex) {
                0 -> {
                    // Posizioni Aperte (Stessa vista degli utenti, con il tasto + se admin)
                    WorkSearchPageComplete(padding = PaddingValues(0.dp), navController = navController)
                }
                1 -> {
                    // Candidature
                    AdminCandidaciesPage(navController = navController, paddingValues = PaddingValues(0.dp))
                }
            }
        }
    }
}
