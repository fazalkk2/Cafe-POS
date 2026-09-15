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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode2
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dineflow.ui.theme.DeepTealContainer
import com.example.dineflow.ui.theme.DeepTealPrimary
import com.example.dineflow.ui.theme.StatusAvailableGreen
import com.example.dineflow.ui.theme.WarmGoldAccent
import com.example.dineflow.ui.theme.WarmGoldContainer
import com.example.dineflow.ui.viewmodel.CartItem
import java.util.Locale

@Composable
fun CheckoutScreen(
    cartItems: List<CartItem>,
    orderType: String,
    tableNumber: Int?,
    customerName: String,
    currencySymbol: String,
    subtotal: Double,
    taxAmount: Double,
    taxRatePercent: Double,
    deliveryFee: Double,
    initialDiscount: Double,
    onConfirmPayment: (paymentMethod: String, tendered: Double, change: Double) -> Unit,
    onBack: () -> Unit
) {
    var selectedPaymentMethod by remember { mutableStateOf("Cash") }
    var selectedDiscountPercent by remember { mutableDoubleStateOf(0.0) }

    val discountAmount = if (selectedDiscountPercent > 0) (subtotal * (selectedDiscountPercent / 100.0)) else initialDiscount
    val totalAmount = ((subtotal - discountAmount).coerceAtLeast(0.0)) + taxAmount + deliveryFee

    var tenderedInput by remember { mutableStateOf(String.format(Locale.US, "%.2f", totalAmount)) }
    val tenderedDouble = tenderedInput.toDoubleOrNull() ?: totalAmount
    val changeDue = (tenderedDouble - totalAmount).coerceAtLeast(0.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Checkout & Payment",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DeepTealPrimary
                )
                Text(
                    text = if (tableNumber != null) "Table $tableNumber" else "$orderType • $customerName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Bill Summary Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Bill Breakdown",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Item Lines
                cartItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${item.quantity}x ${item.menuItem.name}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "$currencySymbol${String.format(Locale.US, "%.2f", item.menuItem.price * item.quantity)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Discount Preset Chips
                Text(
                    text = "Quick Discount:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0.0, 5.0, 10.0, 15.0, 20.0).forEach { pct ->
                        val isSelected = selectedDiscountPercent == pct
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) WarmGoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedDiscountPercent = pct }
                        ) {
                            Text(
                                text = if (pct == 0.0) "None" else "${pct.toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Subtotals
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal", style = MaterialTheme.typography.bodyMedium)
                    Text("$currencySymbol${String.format(Locale.US, "%.2f", subtotal)}", style = MaterialTheme.typography.bodyMedium)
                }

                if (discountAmount > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Discount", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF16A34A))
                        Text("-$currencySymbol${String.format(Locale.US, "%.2f", discountAmount)}", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tax (${taxRatePercent.toInt()}%)", style = MaterialTheme.typography.bodyMedium)
                    Text("$currencySymbol${String.format(Locale.US, "%.2f", taxAmount)}", style = MaterialTheme.typography.bodyMedium)
                }

                if (deliveryFee > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Delivery Fee", style = MaterialTheme.typography.bodyMedium)
                        Text("$currencySymbol${String.format(Locale.US, "%.2f", deliveryFee)}", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("TOTAL PAYABLE", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                    Text(
                        text = "$currencySymbol${String.format(Locale.US, "%.2f", totalAmount)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = DeepTealPrimary
                    )
                }
            }
        }

        // Payment Method Selection
        Text(
            text = "Payment Method",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PaymentMethodButton(
                title = "Cash",
                icon = Icons.Default.LocalAtm,
                isSelected = selectedPaymentMethod == "Cash",
                modifier = Modifier.weight(1f),
                onClick = { selectedPaymentMethod = "Cash" }
            )
            PaymentMethodButton(
                title = "Card",
                icon = Icons.Default.CreditCard,
                isSelected = selectedPaymentMethod == "Card",
                modifier = Modifier.weight(1f),
                onClick = {
                    selectedPaymentMethod = "Card"
                    tenderedInput = String.format(Locale.US, "%.2f", totalAmount)
                }
            )
            PaymentMethodButton(
                title = "QR / Online",
                icon = Icons.Default.QrCode2,
                isSelected = selectedPaymentMethod == "Online Transfer",
                modifier = Modifier.weight(1f),
                onClick = {
                    selectedPaymentMethod = "Online Transfer"
                    tenderedInput = String.format(Locale.US, "%.2f", totalAmount)
                }
            )
            PaymentMethodButton(
                title = "Split",
                icon = Icons.Default.Payments,
                isSelected = selectedPaymentMethod == "Split Payment",
                modifier = Modifier.weight(1f),
                onClick = { selectedPaymentMethod = "Split Payment" }
            )
        }

        // Cash Tendered & Change Calculator
        if (selectedPaymentMethod == "Cash" || selectedPaymentMethod == "Split Payment") {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Cash Tendered & Change",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = tenderedInput,
                        onValueChange = { tenderedInput = it },
                        label = { Text("Amount Tendered") },
                        prefix = { Text(currencySymbol) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tendered_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Tendered Suggestion Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Pair("Exact", totalAmount),
                            Pair("$20", 20.0),
                            Pair("$50", 50.0),
                            Pair("$100", 100.0)
                        ).forEach { (label, value) ->
                            OutlinedButton(
                                onClick = { tenderedInput = String.format(Locale.US, "%.2f", value) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(label, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Change Due Display Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = WarmGoldContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Change Due:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$currencySymbol${String.format(Locale.US, "%.2f", changeDue)}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = WarmGoldAccent
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Confirm Button
        Button(
            onClick = {
                onConfirmPayment(selectedPaymentMethod, tenderedDouble, changeDue)
            },
            colors = ButtonDefaults.buttonColors(containerColor = DeepTealPrimary),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("confirm_payment_button")
        ) {
            Icon(Icons.Default.Print, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Confirm Payment & Print Receipt",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun PaymentMethodButton(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) DeepTealPrimary else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) DeepTealPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = if (isSelected) Color.White else DeepTealPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
