package com.example.dineflow.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu_items")
data class MenuItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // Appetizers, Mains, Pizzas, Burgers, Desserts, Beverages, Combos
    val price: Double,
    val costPrice: Double,
    val description: String,
    val isVeg: Boolean,
    val isAvailable: Boolean = true,
    val drawableName: String = "", // e.g. "food_burger", "food_pizza"
    val stockQuantity: Int = 50
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: String, // e.g. "ORD-1081"
    val orderType: String, // "Dine-In", "Takeaway", "Delivery"
    val tableNumber: Int? = null,
    val guestCount: Int? = null,
    val customerName: String = "",
    val customerPhone: String = "",
    val deliveryAddress: String = "",
    val deliveryFee: Double = 0.0,
    val subtotal: Double = 0.0,
    val taxRate: Double = 0.16,
    val taxAmount: Double = 0.0,
    val discountAmount: Double = 0.0,
    val discountCode: String = "",
    val totalAmount: Double = 0.0,
    val status: String = "Preparing", // "Preparing", "Ready", "Served", "Billed", "Paid", "Voided"
    val paymentMethod: String = "Pending", // "Cash", "Card", "Online Transfer", "Split Payment", "Pending"
    val tenderedAmount: Double = 0.0,
    val changeAmount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val serverName: String = "Alex (Server)",
    val notes: String = "",
    val isKotPrinted: Boolean = false
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: String,
    val menuItemId: Long,
    val itemName: String,
    val itemPrice: Double,
    val quantity: Int,
    val notes: String = ""
)

@Entity(tableName = "tables")
data class TableEntity(
    @PrimaryKey
    val tableNumber: Int,
    val capacity: Int,
    val status: String = "Available", // "Available", "Occupied", "Billed"
    val currentOrderId: String? = null,
    val guestCount: Int = 0,
    val section: String = "Main Dining" // "Main Dining", "Patio", "VIP Lounge"
)

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val email: String = "",
    val address: String = "",
    val totalVisits: Int = 1,
    val totalSpent: Double = 0.0,
    val loyaltyPoints: Int = 0,
    val notes: String = ""
)

@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // "Raw Ingredients", "Finished Goods"
    val currentStock: Double,
    val unit: String, // "kg", "liters", "pcs", "packs"
    val minStockThreshold: Double,
    val unitCost: Double,
    val supplier: String = "Fresh Farms Co.",
    val location: String = "Kitchen"
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long = System.currentTimeMillis(),
    val description: String,
    val category: String, // "Ingredients", "Rent", "Utilities", "Salaries", "Maintenance", "Misc"
    val amount: Double,
    val paymentMethod: String = "Cash",
    val recordedBy: String = "Manager"
)

@Entity(tableName = "discount_coupons")
data class DiscountCouponEntity(
    @PrimaryKey
    val code: String,
    val discountPercent: Double,
    val minOrderAmount: Double,
    val description: String,
    val isActive: Boolean = true
)
