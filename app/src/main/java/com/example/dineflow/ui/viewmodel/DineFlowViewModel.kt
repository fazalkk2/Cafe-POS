package com.example.dineflow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dineflow.data.DineFlowDatabase
import com.example.dineflow.data.model.CustomerEntity
import com.example.dineflow.data.model.DiscountCouponEntity
import com.example.dineflow.data.model.ExpenseEntity
import com.example.dineflow.data.model.InventoryItemEntity
import com.example.dineflow.data.model.MenuItemEntity
import com.example.dineflow.data.model.OrderEntity
import com.example.dineflow.data.model.OrderItemEntity
import com.example.dineflow.data.model.TableEntity
import com.example.dineflow.data.repository.DineFlowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class CartItem(
    val menuItem: MenuItemEntity,
    val quantity: Int,
    val notes: String = ""
)

enum class AppScreen {
    AUTH,
    DASHBOARD,
    TABLE_SELECTION,
    ORDER_CREATION,
    CHECKOUT,
    ORDERS,
    CRM,
    INVENTORY,
    REPORTS,
    EXPENSES,
    SETTINGS
}

data class UserSession(
    val name: String = "Alex Rivera",
    val role: String = "Manager", // Admin, Manager, Waiter, Cashier
    val pin: String = "1234"
)

class DineFlowViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DineFlowRepository

    init {
        val database = DineFlowDatabase.getDatabase(application, viewModelScope)
        repository = DineFlowRepository(database.dao())
    }

    val menuItems: StateFlow<List<MenuItemEntity>> = repository.allMenuItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tables: StateFlow<List<TableEntity>> = repository.allTables
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customers: StateFlow<List<CustomerEntity>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inventory: StateFlow<List<InventoryItemEntity>> = repository.allInventory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val coupons: StateFlow<List<DiscountCouponEntity>> = repository.allCoupons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation & Session
    private val _currentScreen = MutableStateFlow(AppScreen.AUTH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentUser = MutableStateFlow(UserSession())
    val currentUser: StateFlow<UserSession> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Active Order / Cart State
    private val _activeOrderType = MutableStateFlow("Dine-In") // "Dine-In", "Takeaway", "Delivery"
    val activeOrderType: StateFlow<String> = _activeOrderType.asStateFlow()

    private val _activeTableNumber = MutableStateFlow<Int?>(null)
    val activeTableNumber: StateFlow<Int?> = _activeTableNumber.asStateFlow()

    private val _activeGuestCount = MutableStateFlow(2)
    val activeGuestCount: StateFlow<Int> = _activeGuestCount.asStateFlow()

    private val _activeCustomerName = MutableStateFlow("")
    val activeCustomerName: StateFlow<String> = _activeCustomerName.asStateFlow()

    private val _activeCustomerPhone = MutableStateFlow("")
    val activeCustomerPhone: StateFlow<String> = _activeCustomerPhone.asStateFlow()

    private val _activeDeliveryAddress = MutableStateFlow("")
    val activeDeliveryAddress: StateFlow<String> = _activeDeliveryAddress.asStateFlow()

    private val _activeDeliveryFee = MutableStateFlow(0.0)
    val activeDeliveryFee: StateFlow<Double> = _activeDeliveryFee.asStateFlow()

    private val _activeOrderId = MutableStateFlow<String?>(null)
    val activeOrderId: StateFlow<String?> = _activeOrderId.asStateFlow()

    private val _activeCart = MutableStateFlow<List<CartItem>>(emptyList())
    val activeCart: StateFlow<List<CartItem>> = _activeCart.asStateFlow()

    private val _appliedCoupon = MutableStateFlow<DiscountCouponEntity?>(null)
    val appliedCoupon: StateFlow<DiscountCouponEntity?> = _appliedCoupon.asStateFlow()

    private val _customDiscountPercent = MutableStateFlow(0.0)
    val customDiscountPercent: StateFlow<Double> = _customDiscountPercent.asStateFlow()

    private val _customDiscountFixed = MutableStateFlow(0.0)
    val customDiscountFixed: StateFlow<Double> = _customDiscountFixed.asStateFlow()

    // UI Dialog States
    private val _kotTicketDialog = MutableStateFlow<Pair<OrderEntity, List<OrderItemEntity>>?>(null)
    val kotTicketDialog: StateFlow<Pair<OrderEntity, List<OrderItemEntity>>?> = _kotTicketDialog.asStateFlow()

    private val _receiptTicketDialog = MutableStateFlow<Pair<OrderEntity, List<OrderItemEntity>>?>(null)
    val receiptTicketDialog: StateFlow<Pair<OrderEntity, List<OrderItemEntity>>?> = _receiptTicketDialog.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _globalSearchOpen = MutableStateFlow(false)
    val globalSearchOpen: StateFlow<Boolean> = _globalSearchOpen.asStateFlow()

    // Restaurant Settings Info
    private val _restaurantName = MutableStateFlow("DineFlow Bistro & Cafe")
    val restaurantName: StateFlow<String> = _restaurantName.asStateFlow()

    private val _restaurantAddress = MutableStateFlow("742 Evergreen Terrace, Downtown")
    val restaurantAddress: StateFlow<String> = _restaurantAddress.asStateFlow()

    private val _taxRatePercent = MutableStateFlow(16.0)
    val taxRatePercent: StateFlow<Double> = _taxRatePercent.asStateFlow()

    private val _currencySymbol = MutableStateFlow("$")
    val currencySymbol: StateFlow<String> = _currencySymbol.asStateFlow()

    private val _connectedKitchenPrinter = MutableStateFlow("Epson TM-T88VI (Kitchen)")
    val connectedKitchenPrinter: StateFlow<String> = _connectedKitchenPrinter.asStateFlow()

    private val _connectedCashierPrinter = MutableStateFlow("Goojprt POS-80 (Cashier)")
    val connectedCashierPrinter: StateFlow<String> = _connectedCashierPrinter.asStateFlow()

    private val _isOfflineSync = MutableStateFlow(true)
    val isOfflineSync: StateFlow<Boolean> = _isOfflineSync.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun login(role: String, name: String) {
        _currentUser.value = UserSession(name = name, role = role)
        _isLoggedIn.value = true
        _currentScreen.value = AppScreen.DASHBOARD
        showToast("Welcome back, $name ($role)")
    }

    fun biometricLogin() {
        _currentUser.value = UserSession(name = "Alex Rivera", role = "Manager")
        _isLoggedIn.value = true
        _currentScreen.value = AppScreen.DASHBOARD
        showToast("Biometric verification successful")
    }

    fun logout() {
        _isLoggedIn.value = false
        _currentScreen.value = AppScreen.AUTH
    }

    fun toggleGlobalSearch(open: Boolean) {
        _globalSearchOpen.value = open
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    // Order Initiation
    fun startNewDineIn(tableNumber: Int, guestCount: Int) {
        _activeOrderType.value = "Dine-In"
        _activeTableNumber.value = tableNumber
        _activeGuestCount.value = guestCount
        _activeCustomerName.value = "Table $tableNumber Guest"
        _activeCustomerPhone.value = ""
        _activeDeliveryAddress.value = ""
        _activeDeliveryFee.value = 0.0
        _activeOrderId.value = "ORD-" + Random.nextInt(1000, 9999)
        _activeCart.value = emptyList()
        _appliedCoupon.value = null
        _customDiscountPercent.value = 0.0
        _customDiscountFixed.value = 0.0
        _currentScreen.value = AppScreen.ORDER_CREATION
    }

    fun loadExistingTableOrder(tableNumber: Int, orderId: String) {
        viewModelScope.launch {
            val order = repository.getOrderByIdDirect(orderId)
            val items = repository.getOrderItemsDirect(orderId)
            if (order != null) {
                _activeOrderType.value = "Dine-In"
                _activeTableNumber.value = tableNumber
                _activeGuestCount.value = order.guestCount ?: 2
                _activeCustomerName.value = order.customerName
                _activeCustomerPhone.value = order.customerPhone
                _activeDeliveryAddress.value = ""
                _activeDeliveryFee.value = 0.0
                _activeOrderId.value = order.id

                val menuList = menuItems.value
                val cartList = items.map { ordItem ->
                    val matchedItem = menuList.find { it.id == ordItem.menuItemId }
                        ?: MenuItemEntity(
                            id = ordItem.menuItemId,
                            name = ordItem.itemName,
                            category = "Mains",
                            price = ordItem.itemPrice,
                            costPrice = ordItem.itemPrice * 0.4,
                            description = "",
                            isVeg = false
                        )
                    CartItem(matchedItem, ordItem.quantity, ordItem.notes)
                }
                _activeCart.value = cartList
                _appliedCoupon.value = null
                _currentScreen.value = AppScreen.ORDER_CREATION
            }
        }
    }

    fun startNewTakeaway(customerName: String = "") {
        _activeOrderType.value = "Takeaway"
        _activeTableNumber.value = null
        _activeGuestCount.value = 1
        _activeCustomerName.value = customerName.ifEmpty { "Takeaway #${Random.nextInt(10, 99)}" }
        _activeCustomerPhone.value = ""
        _activeDeliveryAddress.value = ""
        _activeDeliveryFee.value = 0.0
        _activeOrderId.value = "ORD-" + Random.nextInt(1000, 9999)
        _activeCart.value = emptyList()
        _appliedCoupon.value = null
        _customDiscountPercent.value = 0.0
        _customDiscountFixed.value = 0.0
        _currentScreen.value = AppScreen.ORDER_CREATION
    }

    fun startNewDelivery(name: String, phone: String, address: String, fee: Double = 4.50) {
        _activeOrderType.value = "Delivery"
        _activeTableNumber.value = null
        _activeGuestCount.value = 1
        _activeCustomerName.value = name.ifEmpty { "Delivery Guest" }
        _activeCustomerPhone.value = phone
        _activeDeliveryAddress.value = address
        _activeDeliveryFee.value = fee
        _activeOrderId.value = "ORD-" + Random.nextInt(1000, 9999)
        _activeCart.value = emptyList()
        _appliedCoupon.value = null
        _customDiscountPercent.value = 0.0
        _customDiscountFixed.value = 0.0
        _currentScreen.value = AppScreen.ORDER_CREATION
    }

    // Cart Management
    fun addToCart(item: MenuItemEntity, notes: String = "") {
        val current = _activeCart.value.toMutableList()
        val index = current.indexOfFirst { it.menuItem.id == item.id && it.notes == notes }
        if (index >= 0) {
            current[index] = current[index].copy(quantity = current[index].quantity + 1)
        } else {
            current.add(CartItem(menuItem = item, quantity = 1, notes = notes))
        }
        _activeCart.value = current
    }

    fun updateCartQuantity(menuItemId: Long, delta: Int) {
        val current = _activeCart.value.toMutableList()
        val index = current.indexOfFirst { it.menuItem.id == menuItemId }
        if (index >= 0) {
            val newQty = current[index].quantity + delta
            if (newQty > 0) {
                current[index] = current[index].copy(quantity = newQty)
            } else {
                current.removeAt(index)
            }
            _activeCart.value = current
        }
    }

    fun updateCartItemNotes(menuItemId: Long, newNotes: String) {
        val current = _activeCart.value.toMutableList()
        val index = current.indexOfFirst { it.menuItem.id == menuItemId }
        if (index >= 0) {
            current[index] = current[index].copy(notes = newNotes)
            _activeCart.value = current
        }
    }

    fun clearCart() {
        _activeCart.value = emptyList()
    }

    fun applyCoupon(coupon: DiscountCouponEntity?) {
        _appliedCoupon.value = coupon
        if (coupon != null) {
            showToast("Coupon ${coupon.code} applied: ${coupon.discountPercent.toInt()}% OFF")
        }
    }

    fun setCustomDiscount(percent: Double, fixed: Double) {
        _customDiscountPercent.value = percent
        _customDiscountFixed.value = fixed
    }

    fun updateCustomerDetails(name: String, phone: String, address: String) {
        _activeCustomerName.value = name
        _activeCustomerPhone.value = phone
        _activeDeliveryAddress.value = address
    }

    // Calculation Helpers
    fun calculateSubtotal(): Double {
        return _activeCart.value.sumOf { it.menuItem.price * it.quantity }
    }

    fun calculateDiscount(subtotal: Double): Double {
        val couponDiscount = _appliedCoupon.value?.let { (subtotal * it.discountPercent) / 100.0 } ?: 0.0
        val customPercentDiscount = (subtotal * _customDiscountPercent.value) / 100.0
        val totalDiscount = couponDiscount + customPercentDiscount + _customDiscountFixed.value
        return totalDiscount.coerceAtMost(subtotal)
    }

    fun calculateTax(taxableAmount: Double): Double {
        return (taxableAmount * (_taxRatePercent.value / 100.0))
    }

    fun calculateTotal(): Double {
        val subtotal = calculateSubtotal()
        val discount = calculateDiscount(subtotal)
        val discounted = (subtotal - discount).coerceAtLeast(0.0)
        val tax = calculateTax(discounted)
        return discounted + tax + _activeDeliveryFee.value
    }

    // Send to Kitchen (KOT)
    fun sendToKitchen() {
        if (_activeCart.value.isEmpty()) {
            showToast("Please add items to order first")
            return
        }
        val orderId = _activeOrderId.value ?: ("ORD-" + Random.nextInt(1000, 9999))
        val subtotal = calculateSubtotal()
        val discount = calculateDiscount(subtotal)
        val discounted = (subtotal - discount).coerceAtLeast(0.0)
        val tax = calculateTax(discounted)
        val total = discounted + tax + _activeDeliveryFee.value

        val order = OrderEntity(
            id = orderId,
            orderType = _activeOrderType.value,
            tableNumber = _activeTableNumber.value,
            guestCount = _activeGuestCount.value,
            customerName = _activeCustomerName.value,
            customerPhone = _activeCustomerPhone.value,
            deliveryAddress = _activeDeliveryAddress.value,
            deliveryFee = _activeDeliveryFee.value,
            subtotal = subtotal,
            taxRate = _taxRatePercent.value / 100.0,
            taxAmount = tax,
            discountAmount = discount,
            discountCode = _appliedCoupon.value?.code ?: "",
            totalAmount = total,
            status = "Preparing",
            paymentMethod = "Pending",
            serverName = _currentUser.value.name,
            isKotPrinted = true,
            createdAt = System.currentTimeMillis()
        )

        val items = _activeCart.value.map {
            OrderItemEntity(
                orderId = orderId,
                menuItemId = it.menuItem.id,
                itemName = it.menuItem.name,
                itemPrice = it.menuItem.price,
                quantity = it.quantity,
                notes = it.notes
            )
        }

        viewModelScope.launch {
            repository.saveOrderWithItems(order, items)
            _kotTicketDialog.value = Pair(order, items)
            showToast("Order $orderId sent to kitchen (KOT Printed)")
        }
    }

    fun closeKotDialog() {
        _kotTicketDialog.value = null
    }

    // Checkout & Payment
    fun finalizePayment(
        paymentMethod: String,
        tendered: Double,
        change: Double
    ) {
        val orderId = _activeOrderId.value ?: ("ORD-" + Random.nextInt(1000, 9999))
        val subtotal = calculateSubtotal()
        val discount = calculateDiscount(subtotal)
        val discounted = (subtotal - discount).coerceAtLeast(0.0)
        val tax = calculateTax(discounted)
        val total = discounted + tax + _activeDeliveryFee.value

        val order = OrderEntity(
            id = orderId,
            orderType = _activeOrderType.value,
            tableNumber = _activeTableNumber.value,
            guestCount = _activeGuestCount.value,
            customerName = _activeCustomerName.value,
            customerPhone = _activeCustomerPhone.value,
            deliveryAddress = _activeDeliveryAddress.value,
            deliveryFee = _activeDeliveryFee.value,
            subtotal = subtotal,
            taxRate = _taxRatePercent.value / 100.0,
            taxAmount = tax,
            discountAmount = discount,
            discountCode = _appliedCoupon.value?.code ?: "",
            totalAmount = total,
            status = "Paid",
            paymentMethod = paymentMethod,
            tenderedAmount = if (paymentMethod == "Cash") tendered else total,
            changeAmount = if (paymentMethod == "Cash") change else 0.0,
            serverName = _currentUser.value.name,
            completedAt = System.currentTimeMillis(),
            isKotPrinted = true
        )

        val items = _activeCart.value.map {
            OrderItemEntity(
                orderId = orderId,
                menuItemId = it.menuItem.id,
                itemName = it.menuItem.name,
                itemPrice = it.menuItem.price,
                quantity = it.quantity,
                notes = it.notes
            )
        }

        viewModelScope.launch {
            repository.saveOrderWithItems(order, items)
            _receiptTicketDialog.value = Pair(order, items)
            showToast("Payment confirmed! Receipt generated.")
            clearCart()
        }
    }

    fun closeReceiptDialog() {
        _receiptTicketDialog.value = null
        _currentScreen.value = AppScreen.DASHBOARD
    }

    fun openOrderReceipt(order: OrderEntity) {
        viewModelScope.launch {
            val items = repository.getOrderItemsDirect(order.id)
            _receiptTicketDialog.value = Pair(order, items)
        }
    }

    fun openOrderKot(order: OrderEntity) {
        viewModelScope.launch {
            val items = repository.getOrderItemsDirect(order.id)
            _kotTicketDialog.value = Pair(order, items)
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
            showToast("Order $orderId marked as $newStatus")
        }
    }

    // Inventory operations
    fun addInventoryItem(name: String, category: String, stock: Double, unit: String, minStock: Double, cost: Double, supplier: String) {
        viewModelScope.launch {
            repository.addInventoryItem(
                InventoryItemEntity(
                    name = name,
                    category = category,
                    currentStock = stock,
                    unit = unit,
                    minStockThreshold = minStock,
                    unitCost = cost,
                    supplier = supplier.ifEmpty { "General Supplier" }
                )
            )
            showToast("Added $name to inventory")
        }
    }

    fun adjustStock(item: InventoryItemEntity, delta: Double) {
        viewModelScope.launch {
            val newStock = (item.currentStock + delta).coerceAtLeast(0.0)
            repository.updateInventoryItem(item.copy(currentStock = newStock))
            showToast("Stock updated for ${item.name}: $newStock ${item.unit}")
        }
    }

    // Expense operations
    fun addExpense(desc: String, cat: String, amount: Double, method: String) {
        viewModelScope.launch {
            repository.addExpense(
                ExpenseEntity(
                    description = desc,
                    category = cat,
                    amount = amount,
                    paymentMethod = method,
                    recordedBy = _currentUser.value.name,
                    date = System.currentTimeMillis()
                )
            )
            showToast("Recorded expense: $$amount ($cat)")
        }
    }

    // Customer operations
    fun addCustomer(name: String, phone: String, email: String, address: String, notes: String) {
        viewModelScope.launch {
            repository.addCustomer(
                CustomerEntity(
                    name = name,
                    phone = phone,
                    email = email,
                    address = address,
                    notes = notes,
                    totalVisits = 1,
                    totalSpent = 0.0,
                    loyaltyPoints = 50
                )
            )
            showToast("Customer $name registered successfully")
        }
    }

    // Settings Updates
    fun updateGeneralSettings(name: String, address: String, taxPercent: Double, currency: String) {
        _restaurantName.value = name
        _restaurantAddress.value = address
        _taxRatePercent.value = taxPercent
        _currencySymbol.value = currency
        showToast("Settings updated successfully")
    }

    fun testPrinter(printerName: String) {
        showToast("Test print sent to: $printerName [Paper 80mm - OK]")
    }

    fun resetDemoData() {
        viewModelScope.launch {
            val database = DineFlowDatabase.getDatabase(getApplication(), viewModelScope)
            database.clearAllTables()
            DineFlowDatabase.DineFlowDatabaseCallback(viewModelScope)
                .populateInitialData(database.dao())
            showToast("Demo restaurant data reloaded fresh")
        }
    }
}
