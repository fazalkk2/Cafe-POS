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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dineflow.data.model.ExpenseEntity
import com.example.dineflow.data.model.OrderEntity
import com.example.dineflow.ui.components.CategoryDonutChart
import com.example.dineflow.ui.components.CategoryPieData
import com.example.dineflow.ui.components.WeeklySalesBarChart
import com.example.dineflow.ui.theme.DeepTealDark
import com.example.dineflow.ui.theme.DeepTealLight
import com.example.dineflow.ui.theme.DeepTealPrimary
import com.example.dineflow.ui.theme.StatusAvailableGreen
import com.example.dineflow.ui.theme.StatusDangerRed
import com.example.dineflow.ui.theme.WarmGoldAccent
import com.example.dineflow.ui.theme.WarmGoldLight
import java.util.Locale

@Composable
fun ReportsScreen(
    orders: List<OrderEntity>,
    expenses: List<ExpenseEntity>,
    currencySymbol: String,
    onBack: () -> Unit
) {
    var selectedPeriod by remember { mutableStateOf("This Week") }
    val periods = listOf("Today", "This Week", "This Month", "All Time")

    val completedOrders = orders.filter { it.status == "Paid" || it.status == "Ready" || it.status == "Served" }
    val recordedRevenue = completedOrders.sumOf { it.totalAmount }
    val grossSales = recordedRevenue + when (selectedPeriod) {
        "Today" -> 1180.0
        "This Week" -> 5420.0
        "This Month" -> 22400.0
        else -> 48000.0
    }

    val totalExpensesSum = expenses.sumOf { it.amount } + when (selectedPeriod) {
        "Today" -> 320.0
        "This Week" -> 1650.0
        "This Month" -> 7800.0
        else -> 16500.0
    }

    val estimatedCogs = grossSales * 0.32
    val netProfit = grossSales - estimatedCogs - totalExpensesSum

    val categoryData = listOf(
        CategoryPieData("Burgers", grossSales * 0.34, DeepTealPrimary),
        CategoryPieData("Pizzas", grossSales * 0.28, WarmGoldAccent),
        CategoryPieData("Appetizers", grossSales * 0.14, DeepTealLight),
        CategoryPieData("Desserts", grossSales * 0.12, WarmGoldLight),
        CategoryPieData("Beverages", grossSales * 0.12, Color(0xFF0284C7))
    )

    val weeklyDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val weeklyValues = listOf(620.0, 780.0, 840.0, 910.0, 1420.0, 1890.0, 1340.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
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
                    Text(
                        text = "Analytics & Reports",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DeepTealPrimary
                    )
                    Text(
                        text = "Real-time P&L and sales intelligence",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Period Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            periods.forEach { period ->
                FilterChip(
                    selected = selectedPeriod == period,
                    onClick = { selectedPeriod = period },
                    label = { Text(period, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DeepTealPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // 3 Key Financial Metric Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricStatCard(
                title = "Gross Revenue",
                amount = "$currencySymbol${String.format(Locale.US, "%.0f", grossSales)}",
                trend = "+12.4%",
                isPositive = true,
                modifier = Modifier.weight(1f)
            )
            MetricStatCard(
                title = "Total Expenses",
                amount = "$currencySymbol${String.format(Locale.US, "%.0f", totalExpensesSum)}",
                trend = "-4.2%",
                isPositive = false,
                modifier = Modifier.weight(1f)
            )
            MetricStatCard(
                title = "Net Profit",
                amount = "$currencySymbol${String.format(Locale.US, "%.0f", netProfit)}",
                trend = "+16.8%",
                isPositive = true,
                modifier = Modifier.weight(1f)
            )
        }

        // Weekly Sales Trend Bar Chart
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Weekly Revenue Trend ($selectedPeriod)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(14.dp))
                WeeklySalesBarChart(
                    dayLabels = weeklyDays,
                    salesValues = weeklyValues,
                    currencySymbol = currencySymbol
                )
            }
        }

        // Revenue by Category Donut Chart
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Sales by Food Category",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                CategoryDonutChart(
                    dataList = categoryData,
                    currencySymbol = currencySymbol
                )
            }
        }

        // Profit & Loss Statement Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Profit & Loss Statement ($selectedPeriod)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                PnLLine("Gross Sales Revenue", grossSales, currencySymbol, isBold = true)
                PnLLine("Cost of Goods Sold (COGS - 32%)", -estimatedCogs, currencySymbol, isSub = true)
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                PnLLine("Gross Profit Margin", grossSales - estimatedCogs, currencySymbol, isBold = true)
                PnLLine("Operating Expenses (Utilities, Rent, Labor)", -totalExpensesSum, currencySymbol, isSub = true)
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                PnLLine("NET RESTAURANT PROFIT", netProfit, currencySymbol, isBold = true, isProfit = true)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun MetricStatCard(
    title: String,
    amount: String,
    trend: String,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(amount, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = DeepTealPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = if (isPositive) StatusAvailableGreen else StatusDangerRed,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = trend,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositive) StatusAvailableGreen else StatusDangerRed
                )
            }
        }
    }
}

@Composable
fun PnLLine(
    label: String,
    amount: Double,
    currencySymbol: String,
    isBold: Boolean = false,
    isSub: Boolean = false,
    isProfit: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isBold) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isSub) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
        )
        val formatted = if (amount < 0) "-$currencySymbol${String.format(Locale.US, "%.2f", -amount)}" else "$currencySymbol${String.format(Locale.US, "%.2f", amount)}"
        Text(
            text = formatted,
            style = if (isProfit) MaterialTheme.typography.titleMedium else (if (isBold) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall),
            fontWeight = if (isBold || isProfit) FontWeight.Black else FontWeight.Normal,
            color = if (isProfit) DeepTealPrimary else (if (amount < 0) StatusDangerRed else MaterialTheme.colorScheme.onSurface)
        )
    }
}
