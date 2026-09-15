package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dineflow.ui.components.DineFlowBottomNav
import com.example.dineflow.ui.components.DineFlowTopBar
import com.example.dineflow.ui.components.GlobalSearchDialog
import com.example.dineflow.ui.components.ThermalKotDialog
import com.example.dineflow.ui.components.ThermalReceiptDialog
import com.example.dineflow.ui.screens.AuthScreen
import com.example.dineflow.ui.screens.CheckoutScreen
import com.example.dineflow.ui.screens.CrmScreen
import com.example.dineflow.ui.screens.DashboardScreen
import com.example.dineflow.ui.screens.ExpensesScreen
import com.example.dineflow.ui.screens.InventoryScreen
import com.example.dineflow.ui.screens.OrderCreationScreen
import com.example.dineflow.ui.screens.OrdersScreen
import com.example.dineflow.ui.screens.ReportsScreen
import com.example.dineflow.ui.screens.SettingsScreen
import com.example.dineflow.ui.screens.TableSelectionScreen
import com.example.dineflow.ui.theme.DineFlowTheme
import com.example.dineflow.ui.viewmodel.AppScreen
import com.example.dineflow.ui.viewmodel.DineFlowViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DineFlowTheme {
                val viewModel: DineFlowViewModel = viewModel()
                DineFlowApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DineFlowApp(viewModel: DineFlowViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val tables by viewModel.tables.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val menuItems by viewModel.menuItems.collectAsState()
    val inventory by viewModel.inventory.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val coupons by viewModel.coupons.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val settings by viewModel.settings.collectAsState()

    val activeOrderType by viewModel.activeOrderType.collectAsState()
    val activeTableNumber by viewModel.activeTableNumber.collectAsState()
    val activeGuestCount by viewModel.activeGuestCount.collectAsState()
    val activeCustomerName by viewModel.activeCustomerName.collectAsState()

    val activeKotOrder by viewModel.activeKotOrder.collectAsState()
    val activeReceiptOrder by viewModel.activeReceiptOrder.collectAsState()
    val isSearchOpen by viewModel.isSearchOpen.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Android Hardware Back button handling
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD && currentUser != null) {
        when (currentScreen) {
            AppScreen.ORDER_CREATION -> viewModel.navigateTo(AppScreen.TABLE_SELECTION)
            AppScreen.CHECKOUT -> viewModel.navigateTo(AppScreen.ORDER_CREATION)
            AppScreen.TABLE_SELECTION,
            AppScreen.ORDERS_LIST,
            AppScreen.INVENTORY,
            AppScreen.CRM,
            AppScreen.REPORTS,
            AppScreen.EXPENSES,
            AppScreen.SETTINGS -> viewModel.navigateTo(AppScreen.DASHBOARD)
            else -> viewModel.navigateTo(AppScreen.DASHBOARD)
        }
    }

    if (currentUser == null) {
        AuthScreen(
            onLoginSuccess = { user ->
                viewModel.loginUser(user)
                scope.launch {
                    snackbarHostState.showSnackbar("Welcome back, ${user.name} (${user.role})")
                }
            }
        )
    } else {
        Scaffold(
            topBar = {
                DineFlowTopBar(
                    restaurantName = settings.restaurantName,
                    currentUser = currentUser!!,
                    activeTableNumber = if (currentScreen == AppScreen.ORDER_CREATION || currentScreen == AppScreen.CHECKOUT) activeTableNumber else null,
                    onOpenSearch = { viewModel.openSearch(true) },
                    onOpenSettings = { viewModel.navigateTo(AppScreen.SETTINGS) },
                    onLogout = { viewModel.logout() }
                )
            },
            bottomBar = {
                // Bottom bar visible on primary hub screens
                val showBottomBar = currentScreen in listOf(
                    AppScreen.DASHBOARD,
                    AppScreen.TABLE_SELECTION,
                    AppScreen.ORDERS_LIST,
                    AppScreen.INVENTORY,
                    AppScreen.REPORTS
                )
                if (showBottomBar) {
                    DineFlowBottomNav(
                        currentScreen = currentScreen,
                        onNavigate = { screen -> viewModel.navigateTo(screen) }
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    AppScreen.DASHBOARD -> {
                        DashboardScreen(
                            orders = orders,
                            inventory = inventory,
                            currencySymbol = settings.currencySymbol,
                            onNavigate = { screen -> viewModel.navigateTo(screen) },
                            onStartTakeaway = { name ->
                                viewModel.startTakeawayOrder(name)
                            },
                            onStartDelivery = { name, phone, address ->
                                viewModel.startDeliveryOrder(name, phone, address)
                            },
                            onQuickRestock = { item ->
                                viewModel.adjustStock(item, 10.0)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Restocked 10 ${item.unit} for ${item.name}")
                                }
                            }
                        )
                    }

                    AppScreen.TABLE_SELECTION -> {
                        TableSelectionScreen(
                            tables = tables,
                            onSelectAvailableTable = { tableNum, guests ->
                                viewModel.startDineInOrder(tableNum, guests)
                            },
                            onSelectOccupiedTable = { tableNum, orderId ->
                                viewModel.loadExistingOrder(orderId, tableNum)
                            },
                            onSwitchToTakeaway = {
                                viewModel.startTakeawayOrder("Counter Takeaway")
                            }
                        )
                    }

                    AppScreen.ORDER_CREATION -> {
                        OrderCreationScreen(
                            menuItems = menuItems,
                            coupons = coupons,
                            orderType = activeOrderType,
                            tableNumber = activeTableNumber,
                            guestCount = activeGuestCount,
                            customerName = activeCustomerName,
                            cartItems = cartItems,
                            appliedCoupon = appliedCoupon,
                            currencySymbol = settings.currencySymbol,
                            taxRatePercent = settings.taxRatePercent,
                            deliveryFee = if (activeOrderType == "Delivery") 4.50 else 0.0,
                            onAddToCart = { item, notes ->
                                viewModel.addToCart(item, notes)
                            },
                            onUpdateQuantity = { id, delta ->
                                viewModel.updateCartItemQuantity(id, delta)
                            },
                            onUpdateNotes = { id, notes ->
                                viewModel.updateCartItemNotes(id, notes)
                            },
                            onClearCart = { viewModel.clearCart() },
                            onApplyCoupon = { coupon -> viewModel.applyCoupon(coupon) },
                            onSendToKitchen = {
                                viewModel.sendToKitchen()
                                scope.launch {
                                    snackbarHostState.showSnackbar("Sent to Kitchen • KOT Generated")
                                }
                            },
                            onProceedToCheckout = {
                                viewModel.proceedToCheckout()
                            },
                            onBack = {
                                viewModel.navigateTo(AppScreen.TABLE_SELECTION)
                            }
                        )
                    }

                    AppScreen.CHECKOUT -> {
                        val subtotal = cartItems.sumOf { it.menuItem.price * it.quantity }
                        val discount = appliedCoupon?.let { (subtotal * it.discountPercent) / 100.0 } ?: 0.0
                        val taxable = (subtotal - discount).coerceAtLeast(0.0)
                        val tax = taxable * (settings.taxRatePercent / 100.0)
                        val delivery = if (activeOrderType == "Delivery") 4.50 else 0.0

                        CheckoutScreen(
                            cartItems = cartItems,
                            orderType = activeOrderType,
                            tableNumber = activeTableNumber,
                            customerName = activeCustomerName,
                            currencySymbol = settings.currencySymbol,
                            subtotal = subtotal,
                            taxAmount = tax,
                            taxRatePercent = settings.taxRatePercent,
                            deliveryFee = delivery,
                            initialDiscount = discount,
                            onConfirmPayment = { method, tendered, change ->
                                viewModel.confirmPayment(method, tendered, change)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Payment Received ($method) • Bill Settled")
                                }
                            },
                            onBack = {
                                viewModel.navigateTo(AppScreen.ORDER_CREATION)
                            }
                        )
                    }

                    AppScreen.ORDERS_LIST -> {
                        OrdersScreen(
                            orders = orders,
                            currencySymbol = settings.currencySymbol,
                            onViewKot = { order -> viewModel.showKotTicket(order) },
                            onViewReceipt = { order -> viewModel.showReceiptTicket(order) },
                            onUpdateStatus = { orderId, newStatus ->
                                viewModel.updateOrderStatus(orderId, newStatus)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Order #$orderId updated to $newStatus")
                                }
                            }
                        )
                    }

                    AppScreen.INVENTORY -> {
                        InventoryScreen(
                            inventory = inventory,
                            currencySymbol = settings.currencySymbol,
                            onAdjustStock = { item, delta ->
                                viewModel.adjustStock(item, delta)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Adjusted stock for ${item.name}")
                                }
                            },
                            onAddNewItem = { name, category, stock, unit, minStock, cost, supplier ->
                                viewModel.addInventoryItem(name, category, stock, unit, minStock, cost, supplier)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Added $name to inventory")
                                }
                            },
                            onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                        )
                    }

                    AppScreen.CRM -> {
                        CrmScreen(
                            customers = customers,
                            currencySymbol = settings.currencySymbol,
                            onAddNewCustomer = { name, phone, email, address, notes ->
                                viewModel.addNewCustomer(name, phone, email, address, notes)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Registered patron $name")
                                }
                            },
                            onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                        )
                    }

                    AppScreen.REPORTS -> {
                        ReportsScreen(
                            orders = orders,
                            expenses = expenses,
                            currencySymbol = settings.currencySymbol,
                            onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                        )
                    }

                    AppScreen.EXPENSES -> {
                        ExpensesScreen(
                            expenses = expenses,
                            currencySymbol = settings.currencySymbol,
                            onAddExpense = { desc, cat, amount, method ->
                                viewModel.addExpense(desc, cat, amount, method)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Logged expense of $amount")
                                }
                            },
                            onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                        )
                    }

                    AppScreen.SETTINGS -> {
                        SettingsScreen(
                            restaurantName = settings.restaurantName,
                            restaurantAddress = settings.restaurantAddress,
                            taxRatePercent = settings.taxRatePercent,
                            currencySymbol = settings.currencySymbol,
                            kitchenPrinter = settings.kitchenPrinter,
                            cashierPrinter = settings.cashierPrinter,
                            isOfflineSync = settings.isOfflineSync,
                            currentUser = currentUser!!,
                            onSaveSettings = { name, addr, tax, cur ->
                                viewModel.updateSettings(name, addr, tax, cur)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Settings updated successfully")
                                }
                            },
                            onTestPrint = { printer ->
                                scope.launch {
                                    snackbarHostState.showSnackbar("Printed test document to $printer")
                                }
                            },
                            onResetDemoData = {
                                viewModel.reloadFreshDemoData()
                                scope.launch {
                                    snackbarHostState.showSnackbar("Database reset to demo state")
                                }
                            },
                            onLogout = { viewModel.logout() },
                            onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                        )
                    }

                    else -> {}
                }
            }
        }
    }

    // Thermal KOT Dialog overlay
    if (activeKotOrder != null) {
        val (kotOrder, kotItems) = activeKotOrder!!
        ThermalKotDialog(
            order = kotOrder,
            items = kotItems,
            onDismiss = { viewModel.dismissKotTicket() }
        )
    }

    // Thermal Receipt Dialog overlay
    if (activeReceiptOrder != null) {
        val (receiptOrder, receiptItems) = activeReceiptOrder!!
        ThermalReceiptDialog(
            order = receiptOrder,
            items = receiptItems,
            restaurantName = settings.restaurantName,
            restaurantAddress = settings.restaurantAddress,
            currencySymbol = settings.currencySymbol,
            onDismiss = { viewModel.dismissReceiptTicket() }
        )
    }

    // Global Search Dialog overlay
    if (isSearchOpen) {
        GlobalSearchDialog(
            menuItems = menuItems,
            tables = tables,
            orders = orders,
            customers = customers,
            currencySymbol = settings.currencySymbol,
            onDismiss = { viewModel.openSearch(false) },
            onSelectMenuItem = { item ->
                viewModel.openSearch(false)
                viewModel.startTakeawayOrder("Counter")
                viewModel.addToCart(item, "")
            },
            onSelectTable = { table ->
                viewModel.openSearch(false)
                if (table.status == "Available") {
                    viewModel.startDineInOrder(table.tableNumber, 2)
                } else if (table.currentOrderId != null) {
                    viewModel.loadExistingOrder(table.currentOrderId, table.tableNumber)
                } else {
                    viewModel.navigateTo(AppScreen.TABLE_SELECTION)
                }
            },
            onSelectOrder = { order ->
                viewModel.openSearch(false)
                viewModel.showReceiptTicket(order)
            },
            onSelectCustomer = { customer ->
                viewModel.openSearch(false)
                viewModel.navigateTo(AppScreen.CRM)
            }
        )
    }
}
