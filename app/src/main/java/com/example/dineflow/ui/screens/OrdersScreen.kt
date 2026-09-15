package com.example.dineflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material.icons.filled.TakeoutDining
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dineflow.data.model.OrderEntity
import com.example.dineflow.ui.theme.DeepTealContainer
import com.example.dineflow.ui.theme.DeepTealPrimary
import com.example.dineflow.ui.theme.StatusAvailableGreen
import com.example.dineflow.ui.theme.StatusBilledBlue
import com.example.dineflow.ui.theme.StatusDangerRed
import com.example.dineflow.ui.theme.StatusOccupiedAmber
import com.example.dineflow.ui.theme.WarmGoldAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrdersScreen(
    orders: List<OrderEntity>,
    currencySymbol: String,
    onViewKot: (OrderEntity) -> Unit,
    onViewReceipt: (OrderEntity) -> Unit,
    onUpdateStatus: (orderId: String, newStatus: String) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var orderToVoid by remember { mutableStateOf<OrderEntity?>(null) }

    val tabs = listOf("All Orders", "Active / Kitchen", "Served & Ready", "Paid & Completed", "Voided")

    val filteredOrders = remember(selectedTabIndex, searchQuery, orders) {
        orders.filter { order ->
            val matchesTab = when (selectedTabIndex) {
                1 -> order.status == "Preparing"
                2 -> order.status == "Ready" || order.status == "Served" || order.status == "Billed"
                3 -> order.status == "Paid"
                4 -> order.status == "Voided"
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                    order.id.contains(searchQuery, ignoreCase = true) ||
                    order.customerName.contains(searchQuery, ignoreCase = true) ||
                    order.orderType.contains(searchQuery, ignoreCase = true)
            matchesTab && matchesSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header & Search
        Text(
            text = "Order Management",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = DeepTealPrimary
        )
        Text(
            text = "Real-time kitchen status and order history",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by order #, table, customer...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 0.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) DeepTealPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No orders found in this section",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredOrders) { order ->
                    OrderManageCard(
                        order = order,
                        currencySymbol = currencySymbol,
                        onViewKot = { onViewKot(order) },
                        onViewReceipt = { onViewReceipt(order) },
                        onMarkServed = { onUpdateStatus(order.id, "Served") },
                        onMarkReady = { onUpdateStatus(order.id, "Ready") },
                        onMarkPaid = { onUpdateStatus(order.id, "Paid") },
                        onVoidClick = { orderToVoid = order }
                    )
                }
            }
        }
    }

    // Void Order Confirmation
    if (orderToVoid != null) {
        val voidTarget = orderToVoid!!
        AlertDialog(
            onDismissRequest = { orderToVoid = null },
            title = { Text("Void Order #${voidTarget.id}") },
            text = { Text("Are you sure you want to cancel and void this order? This action cannot be reversed.") },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateStatus(voidTarget.id, "Voided")
                        orderToVoid = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDangerRed)
                ) {
                    Text("Void Order")
                }
            },
            dismissButton = {
                Button(
                    onClick = { orderToVoid = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("Keep Order", color = Color.Black)
                }
            }
        )
    }
}

@Composable
fun OrderManageCard(
    order: OrderEntity,
    currencySymbol: String,
    onViewKot: () -> Unit,
    onViewReceipt: () -> Unit,
    onMarkServed: () -> Unit,
    onMarkReady: () -> Unit,
    onMarkPaid: () -> Unit,
    onVoidClick: () -> Unit
) {
    val statusColor = when (order.status) {
        "Preparing" -> StatusOccupiedAmber
        "Ready" -> Color(0xFF0284C7)
        "Served" -> DeepTealPrimary
        "Paid" -> StatusAvailableGreen
        "Billed" -> StatusBilledBlue
        "Voided" -> StatusDangerRed
        else -> Color.Gray
    }

    val typeIcon = when (order.orderType) {
        "Dine-In" -> Icons.Default.TableRestaurant
        "Takeaway" -> Icons.Default.TakeoutDining
        "Delivery" -> Icons.Default.DeliveryDining
        else -> Icons.Default.Receipt
    }

    val timeFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(order.createdAt))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(typeIcon, contentDescription = null, tint = DeepTealPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "#${order.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (order.tableNumber != null) "Table ${order.tableNumber}" else order.customerName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = order.status.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$formattedTime • ${order.serverName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (order.notes.isNotBlank()) {
                        Text(
                            text = "Note: ${order.notes}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFDC2626)
                        )
                    }
                }

                Text(
                    text = "$currencySymbol${String.format(Locale.US, "%.2f", order.totalAmount)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = DeepTealPrimary
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onViewKot,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Kitchen, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("KOT", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onViewReceipt,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bill", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.weight(1f))

                if (order.status == "Preparing") {
                    Button(
                        onClick = onMarkReady,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Ready", fontSize = 12.sp)
                    }
                } else if (order.status == "Ready") {
                    Button(
                        onClick = onMarkServed,
                        colors = ButtonDefaults.buttonColors(containerColor = DeepTealPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Served", fontSize = 12.sp)
                    }
                } else if (order.status == "Served" || order.status == "Billed") {
                    Button(
                        onClick = onMarkPaid,
                        colors = ButtonDefaults.buttonColors(containerColor = StatusAvailableGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pay", fontSize = 12.sp)
                    }
                }

                if (order.status != "Voided" && order.status != "Paid") {
                    IconButton(onClick = onVoidClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Void", tint = StatusDangerRed, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
