package com.example.dineflow.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.dineflow.data.model.DiscountCouponEntity
import com.example.dineflow.data.model.MenuItemEntity
import com.example.dineflow.ui.theme.DeepTealContainer
import com.example.dineflow.ui.theme.DeepTealPrimary
import com.example.dineflow.ui.theme.StatusAvailableGreen
import com.example.dineflow.ui.theme.StatusDangerRed
import com.example.dineflow.ui.theme.WarmGoldAccent
import com.example.dineflow.ui.theme.WarmGoldContainer
import com.example.dineflow.ui.viewmodel.CartItem
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCreationScreen(
    menuItems: List<MenuItemEntity>,
    coupons: List<DiscountCouponEntity>,
    orderType: String,
    tableNumber: Int?,
    guestCount: Int,
    customerName: String,
    cartItems: List<CartItem>,
    appliedCoupon: DiscountCouponEntity?,
    currencySymbol: String,
    taxRatePercent: Double,
    deliveryFee: Double,
    onAddToCart: (MenuItemEntity, String) -> Unit,
    onUpdateQuantity: (menuItemId: Long, delta: Int) -> Unit,
    onUpdateNotes: (menuItemId: Long, notes: String) -> Unit,
    onClearCart: () -> Unit,
    onApplyCoupon: (DiscountCouponEntity?) -> Unit,
    onSendToKitchen: () -> Unit,
    onProceedToCheckout: () -> Unit,
    onBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var itemForModifierDialog by remember { mutableStateOf<MenuItemEntity?>(null) }
    var modifierNoteText by remember { mutableStateOf("") }
    var showMobileCartSheet by remember { mutableStateOf(false) }
    var showCouponSelectorDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Burgers", "Pizzas", "Appetizers", "Mains", "Desserts", "Beverages", "Combos")

    val filteredItems = remember(selectedCategory, searchQuery, menuItems) {
        menuItems.filter { item ->
            val matchCategory = selectedCategory == "All" || item.category == selectedCategory
            val matchSearch = searchQuery.isBlank() || item.name.contains(searchQuery, ignoreCase = true) || item.description.contains(searchQuery, ignoreCase = true)
            matchCategory && matchSearch
        }
    }

    val subtotal = cartItems.sumOf { it.menuItem.price * it.quantity }
    val discount = appliedCoupon?.let { (subtotal * it.discountPercent) / 100.0 } ?: 0.0
    val taxable = (subtotal - discount).coerceAtLeast(0.0)
    val tax = taxable * (taxRatePercent / 100.0)
    val total = taxable + tax + deliveryFee

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 840.dp

        if (isWideScreen) {
            // Dual-Panel for Tablet / Wide Display
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Panel: Menu Items (62% width)
                Column(
                    modifier = Modifier
                        .weight(0.62f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    MenuTopHeader(
                        orderType = orderType,
                        tableNumber = tableNumber,
                        customerName = customerName,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        onBack = onBack
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CategoryChipsRow(
                        categories = categories,
                        selected = selectedCategory,
                        onSelect = { selectedCategory = it }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    MenuGrid(
                        items = filteredItems,
                        currencySymbol = currencySymbol,
                        onItemClick = { item -> onAddToCart(item, "") },
                        onCustomizeClick = { item ->
                            itemForModifierDialog = item
                            modifierNoteText = ""
                        }
                    )
                }

                // Right Panel: Order Cart (38% width)
                Column(
                    modifier = Modifier
                        .weight(0.38f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        .padding(16.dp)
                ) {
                    CartContent(
                        cartItems = cartItems,
                        orderType = orderType,
                        tableNumber = tableNumber,
                        customerName = customerName,
                        guestCount = guestCount,
                        currencySymbol = currencySymbol,
                        subtotal = subtotal,
                        discount = discount,
                        appliedCoupon = appliedCoupon,
                        tax = tax,
                        deliveryFee = deliveryFee,
                        total = total,
                        taxRatePercent = taxRatePercent,
                        onUpdateQuantity = onUpdateQuantity,
                        onOpenModifier = { item, currentNotes ->
                            itemForModifierDialog = item
                            modifierNoteText = currentNotes
                        },
                        onOpenCoupons = { showCouponSelectorDialog = true },
                        onClearCart = onClearCart,
                        onSendToKitchen = onSendToKitchen,
                        onCheckout = onProceedToCheckout
                    )
                }
            }
        } else {
            // Mobile Portrait: Menu List with Floating Bottom Cart Bar
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(12.dp)
            ) {
                MenuTopHeader(
                    orderType = orderType,
                    tableNumber = tableNumber,
                    customerName = customerName,
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    onBack = onBack
                )
                Spacer(modifier = Modifier.height(8.dp))
                CategoryChipsRow(
                    categories = categories,
                    selected = selectedCategory,
                    onSelect = { selectedCategory = it }
                )
                Spacer(modifier = Modifier.height(10.dp))

                Box(modifier = Modifier.weight(1f)) {
                    MenuGrid(
                        items = filteredItems,
                        currencySymbol = currencySymbol,
                        onItemClick = { item -> onAddToCart(item, "") },
                        onCustomizeClick = { item ->
                            itemForModifierDialog = item
                            modifierNoteText = ""
                        }
                    )
                }

                // Mobile Bottom Floating Cart Summary Bar
                if (cartItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        onClick = { showMobileCartSheet = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DeepTealPrimary),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .testTag("mobile_cart_bar")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(WarmGoldAccent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${cartItems.sumOf { it.quantity }}",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Current Order",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        text = "$currencySymbol${String.format(Locale.US, "%.2f", total)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }

                            Button(
                                onClick = { showMobileCartSheet = true },
                                colors = ButtonDefaults.buttonColors(containerColor = WarmGoldAccent),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("View Cart", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Mobile Bottom Sheet for Cart
            if (showMobileCartSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showMobileCartSheet = false },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    dragHandle = { BottomSheetDefaults.DragHandle() }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        CartContent(
                            cartItems = cartItems,
                            orderType = orderType,
                            tableNumber = tableNumber,
                            customerName = customerName,
                            guestCount = guestCount,
                            currencySymbol = currencySymbol,
                            subtotal = subtotal,
                            discount = discount,
                            appliedCoupon = appliedCoupon,
                            tax = tax,
                            deliveryFee = deliveryFee,
                            total = total,
                            taxRatePercent = taxRatePercent,
                            onUpdateQuantity = onUpdateQuantity,
                            onOpenModifier = { item, currentNotes ->
                                itemForModifierDialog = item
                                modifierNoteText = currentNotes
                            },
                            onOpenCoupons = { showCouponSelectorDialog = true },
                            onClearCart = onClearCart,
                            onSendToKitchen = {
                                showMobileCartSheet = false
                                onSendToKitchen()
                            },
                            onCheckout = {
                                showMobileCartSheet = false
                                onProceedToCheckout()
                            }
                        )
                    }
                }
            }
        }
    }

    // Item Modifier / Special Notes Dialog
    if (itemForModifierDialog != null) {
        val activeItem = itemForModifierDialog!!
        AlertDialog(
            onDismissRequest = { itemForModifierDialog = null },
            title = { Text("Custom Notes: ${activeItem.name}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Add preparation notes for kitchen:", style = MaterialTheme.typography.bodySmall)

                    OutlinedTextField(
                        value = modifierNoteText,
                        onValueChange = { modifierNoteText = it },
                        placeholder = { Text("e.g. Extra cheese, No onions, Less spicy") },
                        singleLine = false,
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Quick suggestion pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Extra Cheese", "Spicy", "No Onion", "Well Done").forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable {
                                    modifierNoteText = if (modifierNoteText.isBlank()) tag else "$modifierNoteText, $tag"
                                }
                            ) {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val currentCartIndex = cartItems.indexOfFirst { it.menuItem.id == activeItem.id }
                        if (currentCartIndex >= 0) {
                            onUpdateNotes(activeItem.id, modifierNoteText)
                        } else {
                            onAddToCart(activeItem, modifierNoteText)
                        }
                        itemForModifierDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTealPrimary)
                ) {
                    Text("Save Notes")
                }
            },
            dismissButton = {
                Button(
                    onClick = { itemForModifierDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("Cancel", color = Color.Black)
                }
            }
        )
    }

    // Coupon Code Selector Dialog
    if (showCouponSelectorDialog) {
        AlertDialog(
            onDismissRequest = { showCouponSelectorDialog = false },
            title = { Text("Apply Discount Coupon") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (appliedCoupon != null) {
                        OutlinedButton(
                            onClick = {
                                onApplyCoupon(null)
                                showCouponSelectorDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Remove Coupon (${appliedCoupon.code})", color = StatusDangerRed)
                        }
                    }

                    coupons.forEach { coupon ->
                        val isApplied = appliedCoupon?.code == coupon.code
                        Card(
                            onClick = {
                                onApplyCoupon(coupon)
                                showCouponSelectorDialog = false
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isApplied) DeepTealContainer else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(coupon.code, fontWeight = FontWeight.Black, color = DeepTealPrimary)
                                    Text(coupon.description, style = MaterialTheme.typography.bodySmall)
                                }
                                Text("${coupon.discountPercent.toInt()}% OFF", fontWeight = FontWeight.Bold, color = WarmGoldAccent)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCouponSelectorDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTealPrimary)
                ) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun MenuTopHeader(
    orderType: String,
    tableNumber: Int?,
    customerName: String,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onBack: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DeepTealContainer
                        ) {
                            Text(
                                text = orderType.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = DeepTealPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        if (tableNumber != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Table $tableNumber",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        } else if (customerName.isNotBlank()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = customerName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search menu items...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("menu_search_input")
        )
    }
}

@Composable
fun CategoryChipsRow(
    categories: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { category ->
            FilterChip(
                selected = selected == category,
                onClick = { onSelect(category) },
                label = { Text(category, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = DeepTealPrimary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
fun MenuGrid(
    items: List<MenuItemEntity>,
    currencySymbol: String,
    onItemClick: (MenuItemEntity) -> Unit,
    onCustomizeClick: (MenuItemEntity) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 145.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { item ->
            MenuItemCard(
                item = item,
                currencySymbol = currencySymbol,
                onAdd = { onItemClick(item) },
                onCustomize = { onCustomizeClick(item) }
            )
        }
    }
}

@Composable
fun MenuItemCard(
    item: MenuItemEntity,
    currencySymbol: String,
    onAdd: () -> Unit,
    onCustomize: () -> Unit
) {
    // Pick photo asset based on drawableName
    val imageRes = when (item.drawableName) {
        "food_burger" -> R.drawable.food_burger
        "food_pizza" -> R.drawable.food_pizza
        "food_dessert" -> R.drawable.food_dessert
        "food_beverage" -> R.drawable.food_beverage
        else -> R.drawable.food_burger
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAdd() }
    ) {
        Column {
            // Food Photography
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Veg / Non-Veg Indicator
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.TopStart)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(3.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (item.isVeg) StatusAvailableGreen else Color(0xFFDC2626))
                    )
                }

                // Customize / Modifier Note Icon
                IconButton(
                    onClick = onCustomize,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                ) {
                    Icon(
                        Icons.Default.EditNote,
                        contentDescription = "Special Request",
                        tint = DeepTealPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Details
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$currencySymbol${String.format(Locale.US, "%.2f", item.price)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = DeepTealPrimary
                    )

                    Button(
                        onClick = onAdd,
                        colors = ButtonDefaults.buttonColors(containerColor = DeepTealPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("add_item_${item.id}")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CartContent(
    cartItems: List<CartItem>,
    orderType: String,
    tableNumber: Int?,
    customerName: String,
    guestCount: Int,
    currencySymbol: String,
    subtotal: Double,
    discount: Double,
    appliedCoupon: DiscountCouponEntity?,
    tax: Double,
    deliveryFee: Double,
    total: Double,
    taxRatePercent: Double,
    onUpdateQuantity: (menuItemId: Long, delta: Int) -> Unit,
    onOpenModifier: (MenuItemEntity, currentNotes: String) -> Unit,
    onOpenCoupons: () -> Unit,
    onClearCart: () -> Unit,
    onSendToKitchen: () -> Unit,
    onCheckout: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Order Info Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Current Order",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepTealPrimary
                )
                Text(
                    text = if (tableNumber != null) "Table $tableNumber • $guestCount Guests" else "$orderType • $customerName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (cartItems.isNotEmpty()) {
                IconButton(onClick = onClearCart) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Clear", tint = StatusDangerRed)
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Order is empty",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Select delicious items from menu",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Cart Items List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cartItems) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.menuItem.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                            Text(
                                text = "$currencySymbol${String.format(Locale.US, "%.2f", item.menuItem.price * item.quantity)}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = DeepTealPrimary
                            )
                            if (item.notes.isNotBlank()) {
                                Text(
                                    text = ">> ${item.notes}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 11.sp,
                                    color = Color(0xFFDC2626)
                                )
                            }
                        }

                        // Quantity Stepper
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onOpenModifier(item.menuItem, item.notes) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.EditNote, contentDescription = "Edit Note", modifier = Modifier.size(16.dp))
                            }

                            IconButton(
                                onClick = { onUpdateQuantity(item.menuItem.id, -1) },
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Less", modifier = Modifier.size(14.dp))
                            }

                            Text(
                                text = "${item.quantity}",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            IconButton(
                                onClick = { onUpdateQuantity(item.menuItem.id, 1) },
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(DeepTealPrimary)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "More", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Voucher / Coupon Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenCoupons() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Discount, contentDescription = null, tint = WarmGoldAccent, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = appliedCoupon?.let { "Applied: ${it.code} (${it.discountPercent.toInt()}%)" } ?: "Apply Promo Voucher",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (appliedCoupon != null) Color(0xFF16A34A) else WarmGoldAccent
                    )
                }
                Text(
                    text = if (appliedCoupon != null) "Change" else "Select",
                    style = MaterialTheme.typography.labelSmall,
                    color = DeepTealPrimary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Subtotals
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal", style = MaterialTheme.typography.bodySmall)
                Text("$currencySymbol${String.format(Locale.US, "%.2f", subtotal)}", style = MaterialTheme.typography.bodySmall)
            }

            if (discount > 0) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Discount", style = MaterialTheme.typography.bodySmall, color = Color(0xFF16A34A))
                    Text("-$currencySymbol${String.format(Locale.US, "%.2f", discount)}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tax (${taxRatePercent.toInt()}%)", style = MaterialTheme.typography.bodySmall)
                Text("$currencySymbol${String.format(Locale.US, "%.2f", tax)}", style = MaterialTheme.typography.bodySmall)
            }

            if (deliveryFee > 0) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Delivery Fee", style = MaterialTheme.typography.bodySmall)
                    Text("$currencySymbol${String.format(Locale.US, "%.2f", deliveryFee)}", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                Text(
                    text = "$currencySymbol${String.format(Locale.US, "%.2f", total)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = DeepTealPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Send to Kitchen (KOT) and Checkout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSendToKitchen,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("send_to_kitchen_button")
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Send KOT", fontSize = 13.sp)
                }

                Button(
                    onClick = onCheckout,
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTealPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("checkout_button")
                ) {
                    Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Checkout", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
