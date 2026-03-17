package com.example.superspan

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

enum class Destination (
    route: String,
    label: String
) {
    HOME("home", "Home"),
    COUPON("coupon", "Coupon"),
    OFFERTE("discount", "Offerte"),
    LAVORO("work", "Lavoro"),
    PROFILO("profile", "Profilo")
}
@Composable
fun Navigation() {

}

/*@Composable
@Preview(showBackground = true)
fun Navigation() {

}*/