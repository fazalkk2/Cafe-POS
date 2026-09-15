package com.example.dineflow.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.dineflow.data.model.CustomerEntity
import com.example.dineflow.data.model.MenuItemEntity
import com.example.dineflow.data.model.OrderEntity
import com.example.dineflow.ui.theme.DeepTealContainer
import com.example.dineflow.ui.theme.DeepTealDark
import com.example.dineflow.ui.theme.DeepTealPrimary
import com.example.dineflow.ui.theme.OnDeepTealContainer
import com.example.dineflow.ui.theme.WarmGoldAccent
import com.example.dineflow.ui.theme.WarmGoldContainer
import com.example.dineflow.ui.viewmodel.AppScreen
import com.example.dineflow.ui.viewmodel.UserSession

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DineFlowTopBar(
    restaurantName: String,
    currentUser: UserSession,
    activeOrderCount: Int,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    canNavigateBack: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // App Logo
                Image(
                    painter = painterResource(id = R.drawable.ic_dineflow_logo),
                    contentDescription = "DineFlow Logo",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                )
                Column {
                    Text(
                        text = restaurantName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF34D399))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${currentUser.name} • ${currentUser.role}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Global Search",
                    tint = Color.White
                )
            }

            BadgedBox(
                badge = {
                    if (activeOrderCount > 0) {
                        Badge(containerColor = WarmGoldAccent) {
                            Text("$activeOrderCount", color = Color.White)
                        }
                    }
                }
            ) {
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White
                    )
                }
            }

            IconButton(onClick = onProfileClick) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(WarmGoldAccent)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "User Profile",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DeepTealPrimary,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Composable
fun DineFlowBottomNavigation(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen == AppScreen.DASHBOARD,
            onClick = { onNavigate(AppScreen.DASHBOARD) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text("Dashboard", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DeepTealPrimary,
                selectedTextColor = DeepTealPrimary,
                indicatorColor = DeepTealContainer
            )
        )
        NavigationBarItem(
            selected = currentScreen == AppScreen.TABLE_SELECTION,
            onClick = { onNavigate(AppScreen.TABLE_SELECTION) },
            icon = { Icon(Icons.Default.TableBar, contentDescription = "Tables") },
            label = { Text("Tables", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DeepTealPrimary,
                selectedTextColor = DeepTealPrimary,
                indicatorColor = DeepTealContainer
            )
        )
        NavigationBarItem(
            selected = currentScreen == AppScreen.ORDER_CREATION,
            onClick = { onNavigate(AppScreen.ORDER_CREATION) },
            icon = { Icon(Icons.Default.PointOfSale, contentDescription = "POS") },
            label = { Text("POS Order", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DeepTealPrimary,
                selectedTextColor = DeepTealPrimary,
                indicatorColor = DeepTealContainer
            )
        )
        NavigationBarItem(
            selected = currentScreen == AppScreen.ORDERS,
            onClick = { onNavigate(AppScreen.ORDERS) },
            icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Orders") },
            label = { Text("Orders", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DeepTealPrimary,
                selectedTextColor = DeepTealPrimary,
                indicatorColor = DeepTealContainer
            )
        )
        NavigationBarItem(
            selected = currentScreen in listOf(AppScreen.INVENTORY, AppScreen.REPORTS, AppScreen.EXPENSES, AppScreen.CRM, AppScreen.SETTINGS),
            onClick = { onNavigate(AppScreen.INVENTORY) },
            icon = { Icon(Icons.Default.Inventory2, contentDescription = "Manage") },
            label = { Text("Manage", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DeepTealPrimary,
                selectedTextColor = DeepTealPrimary,
                indicatorColor = DeepTealContainer
            )
        )
    }
}

@Composable
fun GlobalSearchModal(
    menuItems: List<MenuItemEntity>,
    orders: List<OrderEntity>,
    customers: List<CustomerEntity>,
    onSelectMenuItem: (MenuItemEntity) -> Unit,
    onSelectOrder: (OrderEntity) -> Unit,
    onSelectCustomer: (CustomerEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    val filteredItems = remember(query, menuItems) {
        if (query.isBlank()) emptyList()
        else menuItems.filter { it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true) }
    }
    val filteredOrders = remember(query, orders) {
        if (query.isBlank()) emptyList()
        else orders.filter { it.id.contains(query, ignoreCase = true) || it.customerName.contains(query, ignoreCase = true) }
    }
    val filteredCustomers = remember(query, customers) {
        if (query.isBlank()) emptyList()
        else customers.filter { it.name.contains(query, ignoreCase = true) || it.phone.contains(query, ignoreCase = true) }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Global Search",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepTealPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search items, orders, customers...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (query.isNotBlank()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    if (query.isBlank()) {
                        item {
                            Text(
                                "Type to quickly search across menu items, active orders, and customer records.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else if (filteredItems.isEmpty() && filteredOrders.isEmpty() && filteredCustomers.isEmpty()) {
                        item {
                            Text(
                                "No matches found for \"$query\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    if (filteredItems.isNotEmpty()) {
                        item {
                            Text(
                                "MENU ITEMS (${filteredItems.size})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = DeepTealPrimary,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(filteredItems) { item ->
                            Card(
                                onClick = {
                                    onSelectMenuItem(item)
                                    onDismiss()
                                },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(item.name, fontWeight = FontWeight.SemiBold)
                                        Text(item.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text("$${String.format("%.2f", item.price)}", fontWeight = FontWeight.Bold, color = DeepTealPrimary)
                                }
                            }
                        }
                    }

                    if (filteredOrders.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "ORDERS (${filteredOrders.size})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = WarmGoldAccent,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(filteredOrders) { ord ->
                            Card(
                                onClick = {
                                    onSelectOrder(ord)
                                    onDismiss()
                                },
                                colors = CardDefaults.cardColors(containerColor = WarmGoldContainer.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("#${ord.id} • ${ord.orderType}", fontWeight = FontWeight.SemiBold)
                                        Text(ord.customerName.ifEmpty { "Table ${ord.tableNumber}" }, style = MaterialTheme.typography.labelSmall)
                                    }
                                    Text("$${String.format("%.2f", ord.totalAmount)}", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (filteredCustomers.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "CUSTOMERS (${filteredCustomers.size})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = DeepTealPrimary,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(filteredCustomers) { cust ->
                            Card(
                                onClick = {
                                    onSelectCustomer(cust)
                                    onDismiss()
                                },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(cust.name, fontWeight = FontWeight.SemiBold)
                                        Text(cust.phone, style = MaterialTheme.typography.labelSmall)
                                    }
                                    Text("${cust.totalVisits} visits", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
