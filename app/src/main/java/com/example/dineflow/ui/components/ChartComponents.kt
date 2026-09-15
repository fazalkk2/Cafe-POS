package com.example.dineflow.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dineflow.ui.theme.DeepTealLight
import com.example.dineflow.ui.theme.DeepTealPrimary
import com.example.dineflow.ui.theme.WarmGoldAccent
import com.example.dineflow.ui.theme.WarmGoldLight
import java.util.Locale

data class BarItemData(
    val label: String,
    val value: Double,
    val formattedValue: String,
    val color: Color = DeepTealPrimary
)

@Composable
fun TopSellingItemsBarChart(
    items: List<BarItemData>,
    currencySymbol: String = "$",
    modifier: Modifier = Modifier
) {
    val maxValue = items.maxOfOrNull { it.value } ?: 1.0

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items.forEachIndexed { index, item ->
            val targetFraction = (item.value / maxValue.coerceAtLeast(1.0)).toFloat().coerceIn(0f, 1f)
            val animatedFraction by animateFloatAsState(
                targetValue = targetFraction,
                animationSpec = tween(600 + index * 100),
                label = "bar_anim"
            )

            val barColor = when (index % 4) {
                0 -> DeepTealPrimary
                1 -> WarmGoldAccent
                2 -> DeepTealLight
                else -> WarmGoldLight
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = item.formattedValue,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = barColor
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = animatedFraction)
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(barColor)
                    )
                }
            }
        }
    }
}

data class CategoryPieData(
    val category: String,
    val amount: Double,
    val color: Color
)

@Composable
fun CategoryDonutChart(
    dataList: List<CategoryPieData>,
    currencySymbol: String = "$",
    modifier: Modifier = Modifier
) {
    val total = dataList.sumOf { it.amount }.coerceAtLeast(1.0)
    var selectedCategory by remember { mutableStateOf<CategoryPieData?>(null) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Donut Canvas
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(150.dp)
        ) {
            Canvas(modifier = Modifier.size(130.dp)) {
                var startAngle = -90f
                val strokeWidth = 32f

                dataList.forEach { slice ->
                    val sweepAngle = ((slice.amount / total) * 360f).toFloat()
                    drawArc(
                        color = slice.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle - 2f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        size = Size(size.width, size.height)
                    )
                    startAngle += sweepAngle
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = selectedCategory?.category ?: "Total",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$currencySymbol${String.format(Locale.US, "%.0f", selectedCategory?.amount ?: total)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepTealPrimary
                )
            }
        }

        // Legend List
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(start = 12.dp)
        ) {
            dataList.take(5).forEach { slice ->
                val percent = ((slice.amount / total) * 100).toInt()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { selectedCategory = slice }
                        .padding(vertical = 2.dp, horizontal = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(slice.color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = slice.category,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$percent%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklySalesBarChart(
    dayLabels: List<String>,
    salesValues: List<Double>,
    currencySymbol: String = "$",
    modifier: Modifier = Modifier
) {
    val maxValue = (salesValues.maxOrNull() ?: 100.0).coerceAtLeast(1.0)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            dayLabels.zip(salesValues).forEachIndexed { index, (day, value) ->
                val fraction = (value / maxValue).toFloat().coerceIn(0.05f, 1f)
                val animatedFraction by animateFloatAsState(
                    targetValue = fraction,
                    animationSpec = tween(500 + index * 80),
                    label = "bar_anim_vert"
                )

                val barColor = if (index == dayLabels.size - 1) WarmGoldAccent else DeepTealPrimary

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "$currencySymbol${value.toInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = barColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height((90 * animatedFraction).dp)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(barColor)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
