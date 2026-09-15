package com.example.dineflow.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dineflow.data.model.InventoryItemEntity
import com.example.dineflow.ui.theme.DeepTealContainer
import com.example.dineflow.ui.theme.DeepTealPrimary
import com.example.dineflow.ui.theme.StatusAvailableGreen
import com.example.dineflow.ui.theme.StatusDangerRed
import com.example.dineflow.ui.theme.StatusOccupiedAmber
import com.example.dineflow.ui.theme.WarmGoldAccent
import java.util.Locale

@Composable
fun InventoryScreen(
    inventory: List<InventoryItemEntity>,
    currencySymbol: String,
    onAdjustStock: (InventoryItemEntity, Double) -> Unit,
    onAddNewItem: (name: String, category: String, stock: Double, unit: String, minStock: Double, cost: Double, supplier: String) -> Unit,
    onBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var itemToAdjust by remember { mutableStateOf<InventoryItemEntity?>(null) }
    var adjustmentDelta by remember { mutableDoubleStateOf(5.0) }
    var showAddItemDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Raw Ingredients", "Finished Goods", "Low Stock")

    val filteredItems = remember(selectedCategory, searchQuery, inventory) {
        inventory.filter { item ->
            val matchCategory = when (selectedCategory) {
                "Low Stock" -> item.currentStock <= item.minStockThreshold
                "All" -> true
                else -> item.category == selectedCategory
            }
            val matchSearch = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.supplier.contains(searchQuery, ignoreCase = true)
            matchCategory && matchSearch
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Column {
                        Text(
                            text = "Inventory & Stock",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = DeepTealPrimary
                        )
                        Text(
                            text = "Track ingredient levels and wastage",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = { showAddItemDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTealPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_inventory_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Item", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search ingredient or supplier...") },
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

            // Category Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (cat == "Low Stock") Color(0xFFFEE2E2) else DeepTealContainer,
                            selectedLabelColor = if (cat == "Low Stock") StatusDangerRed else DeepTealPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Inventory List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredItems) { item ->
                    InventoryItemCard(
                        item = item,
                        currencySymbol = currencySymbol,
                        onAdjust = {
                            itemToAdjust = item
                            adjustmentDelta = 5.0
                        }
                    )
                }
            }
        }
    }

    // Adjust Stock Dialog
    if (itemToAdjust != null) {
        val target = itemToAdjust!!
        var deltaInput by remember { mutableStateOf("5.0") }
        var isDeduction by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { itemToAdjust = null },
            title = { Text("Adjust Stock: ${target.name}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Current stock: ${target.currentStock} ${target.unit}")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (!isDeduction) StatusAvailableGreen else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isDeduction = false }
                        ) {
                            Text(
                                text = "Restock (+)",
                                fontWeight = FontWeight.Bold,
                                color = if (!isDeduction) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(10.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDeduction) StatusDangerRed else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isDeduction = true }
                        ) {
                            Text(
                                text = "Wastage / Use (-)",
                                fontWeight = FontWeight.Bold,
                                color = if (isDeduction) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(10.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    OutlinedTextField(
                        value = deltaInput,
                        onValueChange = { deltaInput = it },
                        label = { Text("Quantity in ${target.unit}") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = deltaInput.toDoubleOrNull() ?: 0.0
                        val finalDelta = if (isDeduction) -amount else amount
                        onAdjustStock(target, finalDelta)
                        itemToAdjust = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTealPrimary)
                ) {
                    Text("Save Adjustment")
                }
            },
            dismissButton = {
                Button(
                    onClick = { itemToAdjust = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("Cancel", color = Color.Black)
                }
            }
        )
    }

    // Add New Inventory Item Dialog
    if (showAddItemDialog) {
        var newName by remember { mutableStateOf("") }
        var newCategory by remember { mutableStateOf("Raw Ingredients") }
        var newStock by remember { mutableStateOf("10.0") }
        var newUnit by remember { mutableStateOf("kg") }
        var newMinStock by remember { mutableStateOf("5.0") }
        var newCost by remember { mutableStateOf("12.00") }
        var newSupplier by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddItemDialog = false },
            title = { Text("Add Inventory Item") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Item Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newStock,
                            onValueChange = { newStock = it },
                            label = { Text("Stock") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newUnit,
                            onValueChange = { newUnit = it },
                            label = { Text("Unit (kg, pcs)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newMinStock,
                            onValueChange = { newMinStock = it },
                            label = { Text("Min Alert Threshold") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newCost,
                            onValueChange = { newCost = it },
                            label = { Text("Unit Cost ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = newSupplier,
                        onValueChange = { newSupplier = it },
                        label = { Text("Supplier Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            onAddNewItem(
                                newName,
                                newCategory,
                                newStock.toDoubleOrNull() ?: 0.0,
                                newUnit,
                                newMinStock.toDoubleOrNull() ?: 1.0,
                                newCost.toDoubleOrNull() ?: 0.0,
                                newSupplier
                            )
                            showAddItemDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTealPrimary)
                ) {
                    Text("Add Item")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showAddItemDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("Cancel", color = Color.Black)
                }
            }
        )
    }
}

@Composable
fun InventoryItemCard(
    item: InventoryItemEntity,
    currencySymbol: String,
    onAdjust: () -> Unit
) {
    val isLowStock = item.currentStock <= item.minStockThreshold
    val ratio = (item.currentStock / (item.minStockThreshold * 2.5)).toFloat().coerceIn(0f, 1f)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (isLowStock) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.Warning, contentDescription = null, tint = StatusDangerRed, modifier = Modifier.size(16.dp))
                        }
                    }
                    Text(
                        text = "${item.category} • Supplier: ${item.supplier}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onAdjust,
                    colors = ButtonDefaults.buttonColors(containerColor = if (isLowStock) WarmGoldAccent else DeepTealPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Adjust", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stock level progress bar
            LinearProgressIndicator(
                progress = { ratio },
                color = if (isLowStock) StatusDangerRed else StatusAvailableGreen,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Stock: ${item.currentStock} ${item.unit} (Min: ${item.minStockThreshold})",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isLowStock) StatusDangerRed else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isLowStock) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "Unit Cost: $currencySymbol${String.format(Locale.US, "%.2f", item.unitCost)}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
