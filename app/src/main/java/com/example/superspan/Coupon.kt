package com.example.superspan

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource // AGGIUNTO
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Classe per gestire lo stato della scadenza visivamente
data class ExpirationStatus(val label: String, val color: Color)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CouponPageComplete(paddingValues: PaddingValues, navController: NavController?, initialCouponCode: String? = null, initialTab: Int = 0) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    var selectedOffer: Coupon? by remember { mutableStateOf(ListOfCoupon.find { it.code == initialCouponCode }) }
    var searchQuery by remember { mutableStateOf("") }

    // --- GESTIONE RITORNO IN ALTO ---
    val listState = rememberLazyListState()

    val addedCouponCode = navController?.currentBackStackEntry?.savedStateHandle?.getStateFlow("added_coupon_code", "")?.collectAsState()?.value
    val deletedCouponCode = navController?.currentBackStackEntry?.savedStateHandle?.getStateFlow("deleted_coupon_code", "")?.collectAsState()?.value

    var highlightedCouponCode by remember { mutableStateOf<String?>(null) }
    val deletingCouponCodes = remember { mutableStateListOf<String>() }
    var couponToDelete by remember { mutableStateOf<Coupon?>(null) }
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(addedCouponCode) {
        if (!addedCouponCode.isNullOrEmpty()) {
            val newCoupon = ListOfCoupon.find { it.code == addedCouponCode }
            if (newCoupon != null) {
                val correctTab = if (newCoupon.products.size == 3) 0 else 1
                if (selectedTab != correctTab) {
                    selectedTab = correctTab
                    delay(100)
                }
                val currentFilteredList = if (selectedTab == 0) {
                    ListOfCoupon.filter { it.products.size == 3 }
                } else {
                    ListOfCoupon.filter { it.products.size == 1 }
                }
                val index = currentFilteredList.indexOfFirst { it.code == addedCouponCode }
                if (index >= 0) {
                    delay(100)
                    listState.animateScrollToItem(index + 2, scrollOffset = -250) // offset per non nasconderlo sotto l'header
                    highlightedCouponCode = addedCouponCode
                    delay(500)
                    highlightedCouponCode = null
                }
            }
            navController.currentBackStackEntry?.savedStateHandle?.set("added_coupon_code", "")
        }
    }

    LaunchedEffect(deletedCouponCode) {
        if (!deletedCouponCode.isNullOrEmpty()) {
            val deletedCoupon = ListOfCoupon.find { it.code == deletedCouponCode }
            if (deletedCoupon != null) {
                val correctTab = if (deletedCoupon.products.size == 3) 0 else 1
                if (selectedTab != correctTab) {
                    selectedTab = correctTab
                    delay(100)
                }
                val currentFilteredList = if (selectedTab == 0) {
                    ListOfCoupon.filter { it.products.size == 3 }
                } else {
                    ListOfCoupon.filter { it.products.size == 1 }
                }
                val index = currentFilteredList.indexOfFirst { it.code == deletedCouponCode }
                if (index >= 0) {
                    delay(100)
                    listState.animateScrollToItem(index + 2, scrollOffset = -250) // offset per non nasconderlo sotto l'header
                    delay(100) // slight pause to let the user see it before it disappears
                }
            }

            deletingCouponCodes.add(deletedCouponCode)
            delay(400) // Duration of exit animation
            ListOfCoupon.removeAll { it.code == deletedCouponCode }
            deletingCouponCodes.remove(deletedCouponCode)
            navController.currentBackStackEntry?.savedStateHandle?.set("deleted_coupon_code", "")
        }
    }

    LaunchedEffect(selectedTab) {
        listState.scrollToItem(0)
    }

    val currentSelectedOffer = selectedOffer?.let { offer ->
        ListOfCoupon.find { it.code == offer.code }
    } ?: selectedOffer

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(top = paddingValues.calculateTopPadding())
    ) {
        if (currentSelectedOffer == null) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding() + 100.dp)
            ) {
                // 1. TITOLO
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 70.dp, start = 20.dp, end = 20.dp, bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Offerte e Coupon",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A1A1A)
                        )
                        Text(
                            text = "Risparmia sulla tua spesa quotidiana",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        androidx.compose.material3.Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            shadowElevation = 6.dp,
                            color = Color.White
                        ) {
                            androidx.compose.material3.TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Cerca prodotto o offerta...", color = Color.Gray) },
                                modifier = Modifier.fillMaxSize(),
                                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Cancella", tint = Color.Gray)
                                        }
                                    }
                                },
                                singleLine = true,
                                colors = androidx.compose.material3.TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    cursorColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }

                // 2. STICKY TABS
                stickyHeader {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(com.example.superspan.ui.theme.Neutral50)
                            .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 12.dp)
                    ) {
                        AnimatedSegmentedControl(
                            options = listOf("Coupon", "Offerte"),
                            selectedIndex = selectedTab
                        ) { selectedTab = it }
                    }
                }

                // 3. LISTA FILTRATA
                val filteredList = (if (selectedTab == 0) {
                    ListOfCoupon.filter { it.products.size == 3 }
                } else {
                    ListOfCoupon.filter { it.products.size == 1 }
                }).filter { coupon ->
                    searchQuery.isEmpty() ||
                    coupon.description.contains(searchQuery, ignoreCase = true) ||
                    coupon.products.any { it.nome.contains(searchQuery, ignoreCase = true) }
                }

                items(filteredList, key = { it.code }) { item ->
                    val isDeleting = deletingCouponCodes.contains(item.code)
                    val isHighlighted = highlightedCouponCode == item.code

                    AnimatedVisibility(
                        visible = !isDeleting,
                        enter = fadeIn(),
                        exit = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
                    ) {
                        Box(modifier = Modifier.padding(vertical = 6.dp)) {
                            if (selectedTab == 0) {
                                CouponTicketCard(item, isHighlighted, navController, onDeleteClick = { couponToDelete = item }) { selectedOffer = item }
                            } else {
                                OfferPromoCard(item, isHighlighted, navController, onDeleteClick = { couponToDelete = item })
                            }
                        }
                    }
                }
            }
        } else {
            OfferDetailPage(currentSelectedOffer!!, navController) { selectedOffer = null }
        }

        if (actualUser.admin && currentSelectedOffer == null) {
            FloatingActionButton(
                onClick = {
                    navController?.currentBackStackEntry?.savedStateHandle?.set("add_type", selectedTab)
                    navController?.navigate(Destination.ADD_COUPON.route)
                },
                containerColor = com.example.superspan.ui.theme.LogoLeft,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 24.dp, bottom = paddingValues.calculateBottomPadding() + 24.dp)
            ) {
                Icon(Icons.Default.Add, "Aggiungi")
            }
        }

        if (couponToDelete != null) {
            ModernAlertDialog(
                onDismissRequest = { couponToDelete = null },
                title = "Conferma Eliminazione",
                text = "Sei sicuro di voler eliminare questo elemento?",
                icon = Icons.Default.Delete,
                isDestructive = true,
                confirmText = "Elimina",
                onConfirm = {
                    val code = couponToDelete!!.code
                    couponToDelete = null
                    scope.launch {
                        deletingCouponCodes.add(code)
                        kotlinx.coroutines.delay(400)
                        ListOfCoupon.removeAll { it.code == code }
                        deletingCouponCodes.remove(code)
                        android.widget.Toast.makeText(context, "Eliminato con successo", android.widget.Toast.LENGTH_SHORT).show()
                    }
                },
                dismissText = "Annulla",
                onDismiss = { couponToDelete = null }
            )
        }
    }
}

// --- FUNZIONI DI SUPPORTO ---

fun formatDisplayDate(dateString: String): String {
    return try {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ITALY)
        val date = LocalDate.parse(dateString, inputFormatter)
        date.format(outputFormatter)
    } catch (e: Exception) {
        dateString
    }
}

@Composable
fun CouponTicketCard(coupon: Coupon, isHighlighted: Boolean = false, navController: NavController? = null, onDeleteClick: () -> Unit = {}, onClick: () -> Unit = {}) {
    val status = getExpirationStatus(coupon.dateOfExpiration)
    val isExpired = status.label == "SCADUTO"

    Card(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().alpha(if(isExpired) 0.6f else 1f).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isHighlighted) Color(0xFFE0E0E0) else Color.White),
        elevation = CardDefaults.cardElevation(if(isExpired) 0.dp else 2.dp)
    ) {
        Column {
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = status.color.copy(0.15f), shape = RoundedCornerShape(12.dp)) {
                            Text(status.label, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = status.color, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(text = "Scade: ${formatDisplayDate(coupon.dateOfExpiration)}", fontSize = 11.sp, color = status.color, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(coupon.description, fontWeight = FontWeight.Bold, fontSize = 16.sp, textDecoration = if(isExpired) TextDecoration.LineThrough else null)
                    Text("Valido su: ${coupon.products.joinToString(", ") { it.nome }}", fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(12.dp))
                    Surface(color = if(isExpired) Color.Transparent else Color(0xFFF1F1F1), shape = RoundedCornerShape(8.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(0.5f))) {
                        Text(coupon.code, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp, color = if(isExpired) Color.Gray else Color.DarkGray)
                    }
                }
                Box(Modifier.fillMaxHeight().width(1.dp).background(Color.LightGray))
                Column(
                    modifier = Modifier.fillMaxHeight().width(90.dp).background(if(isExpired) Color.LightGray.copy(0.2f) else status.color.copy(0.05f)),
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
                ) {
                    Text("SCONTO", fontSize = 10.sp, color = Color.Gray)
                    Text("-${coupon.discount.toInt()}%", fontSize = 26.sp, fontWeight = FontWeight.Black, color = if(isExpired) Color.Gray else status.color)
                }
            }
            if (actualUser.admin) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF9F9F9))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { navController?.navigate("${Destination.EDIT_COUPON.route}/${coupon.code}") }) {
                            Icon(Icons.Default.Edit, contentDescription = "Modifica", modifier = Modifier.size(16.dp), tint = Color.DarkGray)
                            Spacer(Modifier.width(6.dp))
                            Text("Modifica", color = Color.DarkGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = onDeleteClick) {
                            Icon(Icons.Default.Delete, contentDescription = "Elimina", modifier = Modifier.size(16.dp), tint = com.example.superspan.ui.theme.AppError.copy(0.8f))
                            Spacer(Modifier.width(6.dp))
                            Text("Elimina", color = com.example.superspan.ui.theme.AppError.copy(0.8f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OfferPromoCard(coupon: Coupon, isHighlighted: Boolean = false, navController: NavController? = null, onDeleteClick: () -> Unit = {}) {
    val status = getExpirationStatus(coupon.dateOfExpiration)
    val product = coupon.products.firstOrNull() ?: return
    val isExpired = status.label == "SCADUTO"

    Card(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().alpha(if(isExpired) 0.6f else 1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isHighlighted) Color(0xFFE0E0E0) else Color.White),
        elevation = CardDefaults.cardElevation(if(isExpired) 0.dp else 2.dp)
    ) {
        Column {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).background(if(isExpired) Color.LightGray else Color(0xFFE8F5E9), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.LocalOffer, null, tint = if(isExpired) Color.Gray else Color(0xFF388E3C), modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(product.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp, textDecoration = if(isExpired) TextDecoration.LineThrough else null)
                    Text(coupon.description, fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = status.color.copy(0.15f), shape = RoundedCornerShape(12.dp)) {
                            Text(status.label, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = status.color, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(text = "Scade: ${formatDisplayDate(coupon.dateOfExpiration)}", fontSize = 11.sp, color = status.color, fontWeight = FontWeight.SemiBold)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("-${coupon.discount.toInt()}%", fontSize = 24.sp, fontWeight = FontWeight.Black, color = if(isExpired) Color.Gray else status.color)
                }
            }
            if (actualUser.admin) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF9F9F9))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { navController?.navigate("${Destination.EDIT_COUPON.route}/${coupon.code}") }) {
                            Icon(Icons.Default.Edit, contentDescription = "Modifica", modifier = Modifier.size(16.dp), tint = Color.DarkGray)
                            Spacer(Modifier.width(6.dp))
                            Text("Modifica", color = Color.DarkGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = onDeleteClick) {
                            Icon(Icons.Default.Delete, contentDescription = "Elimina", modifier = Modifier.size(16.dp), tint = com.example.superspan.ui.theme.AppError.copy(0.8f))
                            Spacer(Modifier.width(6.dp))
                            Text("Elimina", color = com.example.superspan.ui.theme.AppError.copy(0.8f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun OfferDetailPage(coupon: Coupon, navController: NavController? = null, onBack: () -> Unit) {
    AuraBackground(Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(80.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(coupon.description, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 34.sp, modifier = Modifier.weight(1f))
                if (actualUser.admin) {
                    IconButton(onClick = { navController?.navigate("${Destination.EDIT_COUPON.route}/${coupon.code}") }) {
                        Icon(Icons.Default.Edit, contentDescription = "Modifica", tint = Color.Gray)
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                Icon(Icons.Default.Event, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Offerta valida fino al ${formatDisplayDate(coupon.dateOfExpiration)}", color = Color.Gray, fontSize = 14.sp)
            }
            Spacer(Modifier.height(24.dp))
            coupon.products.forEach { product ->
                ProductDiscountItem(product, coupon)
                Spacer(Modifier.height(12.dp))
            }
            Spacer(Modifier.height(32.dp))
            Surface(modifier = Modifier.fillMaxWidth(), color = com.example.superspan.ui.theme.AppSuccess.copy(0.1f), shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(2.dp, com.example.superspan.ui.theme.AppSuccess)) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("MOSTRA ALLA CASSA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = com.example.superspan.ui.theme.AppSuccess)
                    Spacer(Modifier.height(8.dp))
                    Text(text = coupon.code, fontSize = 36.sp, fontWeight = FontWeight.Black, letterSpacing = 6.sp, color = Color.Black)
                    Spacer(Modifier.height(16.dp))
                    FakeBarcode(coupon.code, modifier = Modifier.fillMaxWidth(0.9f))
                }
            }
            Spacer(Modifier.height(40.dp))
        }

        // Floating Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = androidx.compose.foundation.layout.WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 16.dp, start = 16.dp)
                .background(Color.White.copy(alpha = 0.7f), CircleShape)
                .size(48.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro", tint = com.example.superspan.ui.theme.LogoLeft)
        }
    }
}

@Composable
fun ProductDiscountItem(product: Product, coupon: Coupon) {
    val discountedPrice = coupon.calculateDiscountedPrice(product)
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1))) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(product.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("€%.2f".format(product.prezzo), textDecoration = TextDecoration.LineThrough, color = Color.Gray, fontSize = 14.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("€%.2f".format(discountedPrice), color = Color(0xFF1B5E20), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
            }
            Text("-${coupon.discount.toInt()}%", fontWeight = FontWeight.Black, color = Color(0xFFD32F2F), fontSize = 20.sp)
        }
    }
}

private fun getExpirationStatus(dateString: String): ExpirationStatus {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val expiry = LocalDate.parse(dateString, formatter)
        val today = LocalDate.now()
        val days = ChronoUnit.DAYS.between(today, expiry)
        when {
            days < 0 -> ExpirationStatus("SCADUTO", com.example.superspan.ui.theme.AppExpired)
            days == 0L -> ExpirationStatus("SCADE OGGI", com.example.superspan.ui.theme.AppError)
            days <= 3 -> ExpirationStatus("SCADE TRA $days GG", com.example.superspan.ui.theme.AppWarning)
            else -> ExpirationStatus("ATTIVO", com.example.superspan.ui.theme.AppSuccess)
        }
    } catch (e: Exception) { ExpirationStatus("ATTIVO", com.example.superspan.ui.theme.AppSuccess) }
}

@Composable
@Preview(showBackground = true)
fun CouponPreview() {
    CouponPageComplete(PaddingValues(0.dp), null)
}