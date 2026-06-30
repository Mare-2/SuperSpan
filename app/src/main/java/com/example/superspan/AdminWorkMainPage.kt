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
    val tabs = listOf("Posizioni Aperte", "Candidature")

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val sliderContent: @Composable () -> Unit = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 12.dp)
                    .shadow(4.dp, CircleShape)
                    .background(Color(0xFFEDF7E7), CircleShape)
                    .padding(4.dp)
            ) {
                TabButton("Posizioni Aperte", selectedTabIndex == 0, Modifier.weight(1f)) { selectedTabIndex = 0 }
                TabButton("Candidature", selectedTabIndex == 1, Modifier.weight(1f)) { selectedTabIndex = 1 }
            }
        }

        when (selectedTabIndex) {
            0 -> {
                // Posizioni Aperte
                WorkSearchPageComplete(
                    padding = paddingValues,
                    navController = navController,
                    hideHeader = false,
                    sliderContent = sliderContent
                )
            }
            1 -> {
                // Candidature
                AdminCandidaciesPage(
                    navController = navController,
                    paddingValues = paddingValues,
                    sliderContent = sliderContent
                )
            }
        }
    }
}
