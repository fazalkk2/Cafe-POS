package com.example.dineflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.dineflow.data.model.TableEntity
import com.example.dineflow.ui.theme.DeepTealContainer
import com.example.dineflow.ui.theme.DeepTealPrimary
import com.example.dineflow.ui.theme.StatusAvailableGreen
import com.example.dineflow.ui.theme.StatusBilledBlue
import com.example.dineflow.ui.theme.StatusOccupiedAmber
import com.example.dineflow.ui.theme.WarmGoldAccent

@Composable
fun TableSelectionScreen(
    tables: List<TableEntity>,
    onSelectAvailableTable: (tableNumber: Int, guestCount: Int) -> Unit,
    onSelectOccupiedTable: (tableNumber: Int, orderId: String) -> Unit,
    onSwitchToTakeaway: () -> Unit
) {
    var selectedSection by remember { mutableStateOf("All") }
    val sections = listOf("All", "Main Dining", "Patio", "VIP Lounge")

    var tableForGuestDialog by remember { mutableStateOf<TableEntity?>(null) }
    var guestCountInput by remember { mutableIntStateOf(2) }

    val filteredTables = remember(selectedSection, tables) {
        if (selectedSection == "All") tables
        else tables.filter { it.section == selectedSection }
    }

    val availableCount = tables.count { it.status == "Available" }
    val occupiedCount = tables.count { it.status == "Occupied" }
    val billedCount = tables.count { it.status == "Billed" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Floor Plan Stats Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Floor Plan & Tables",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DeepTealPrimary
                )
                Text(
                    text = "Tap an available table to seat guests",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onSwitchToTakeaway,
                colors = ButtonDefaults.buttonColors(containerColor = WarmGoldAccent),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Takeaway / Counter", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Status Legend Badges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatusLegendBadge(
                label = "Available ($availableCount)",
                color = StatusAvailableGreen,
                modifier = Modifier.weight(1f)
            )
            StatusLegendBadge(
                label = "Occupied ($occupiedCount)",
                color = StatusOccupiedAmber,
                modifier = Modifier.weight(1f)
            )
            StatusLegendBadge(
                label = "Billed ($billedCount)",
                color = StatusBilledBlue,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Section Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sections.forEach { section ->
                FilterChip(
                    selected = selectedSection == section,
                    onClick = { selectedSection = section },
                    label = { Text(section, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DeepTealContainer,
                        selectedLabelColor = DeepTealPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Table Grid
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 130.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredTables) { table ->
                TableCardItem(
                    table = table,
                    onClick = {
                        if (table.status == "Available") {
                            tableForGuestDialog = table
                            guestCountInput = 2
                        } else if (table.currentOrderId != null) {
                            onSelectOccupiedTable(table.tableNumber, table.currentOrderId)
                        } else {
                            tableForGuestDialog = table
                        }
                    }
                )
            }
        }
    }

    // Guest Count Selector Dialog
    if (tableForGuestDialog != null) {
        val selectedTable = tableForGuestDialog!!
        AlertDialog(
            onDismissRequest = { tableForGuestDialog = null },
            title = {
                Text("Seat Table #${selectedTable.tableNumber}")
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "${selectedTable.section} • Max Capacity: ${selectedTable.capacity} guests",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Number of Guests:", style = MaterialTheme.typography.labelMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { if (guestCountInput > 1) guestCountInput-- },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                        }

                        Text(
                            text = "$guestCountInput",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = DeepTealPrimary,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        IconButton(
                            onClick = { if (guestCountInput < selectedTable.capacity + 2) guestCountInput++ },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = selectedTable.tableNumber
                        tableForGuestDialog = null
                        onSelectAvailableTable(num, guestCountInput)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTealPrimary),
                    modifier = Modifier.testTag("confirm_seat_button")
                ) {
                    Text("Open Order")
                }
            },
            dismissButton = {
                Button(
                    onClick = { tableForGuestDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("Cancel", color = Color.Black)
                }
            }
        )
    }
}

@Composable
fun StatusLegendBadge(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

@Composable
fun TableCardItem(
    table: TableEntity,
    onClick: () -> Unit
) {
    val statusColor = when (table.status) {
        "Available" -> StatusAvailableGreen
        "Occupied" -> StatusOccupiedAmber
        "Billed" -> StatusBilledBlue
        else -> Color.Gray
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .height(136.dp)
            .border(1.5.dp, statusColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = table.status.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Group,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${table.capacity}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "T-${table.tableNumber}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = DeepTealPrimary
                )
                Text(
                    text = table.section,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (table.status == "Occupied") {
                Text(
                    text = "Active • #${table.currentOrderId?.takeLast(4) ?: "..."}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = WarmGoldAccent
                )
            } else if (table.status == "Billed") {
                Text(
                    text = "Bill Generated",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = StatusBilledBlue
                )
            } else {
                Text(
                    text = "Tap to Seat",
                    style = MaterialTheme.typography.labelSmall,
                    color = StatusAvailableGreen,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
