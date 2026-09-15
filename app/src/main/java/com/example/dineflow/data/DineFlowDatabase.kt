package com.example.dineflow.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.dineflow.data.dao.DineFlowDao
import com.example.dineflow.data.model.CustomerEntity
import com.example.dineflow.data.model.DiscountCouponEntity
import com.example.dineflow.data.model.ExpenseEntity
import com.example.dineflow.data.model.InventoryItemEntity
import com.example.dineflow.data.model.MenuItemEntity
import com.example.dineflow.data.model.OrderEntity
import com.example.dineflow.data.model.OrderItemEntity
import com.example.dineflow.data.model.TableEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        MenuItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        TableEntity::class,
        CustomerEntity::class,
        InventoryItemEntity::class,
        ExpenseEntity::class,
        DiscountCouponEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DineFlowDatabase : RoomDatabase() {
    abstract fun dao(): DineFlowDao

    companion object {
        @Volatile
        private var INSTANCE: DineFlowDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): DineFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DineFlowDatabase::class.java,
                    "dineflow_pos.db"
                ).addCallback(DineFlowDatabaseCallback(scope)).build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DineFlowDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.dao())
                }
            }
        }

        suspend fun populateInitialData(dao: DineFlowDao) {
            // Seed Menu Items
            val menuItems = listOf(
                MenuItemEntity(
                    id = 1,
                    name = "Truffle Angus Burger",
                    category = "Burgers",
                    price = 14.50,
                    costPrice = 5.20,
                    description = "Brioche bun, prime angus beef, truffle aioli, aged cheddar & caramelized onions.",
                    isVeg = false,
                    drawableName = "food_burger",
                    stockQuantity = 45
                ),
                MenuItemEntity(
                    id = 2,
                    name = "Artisan Margherita Pizza",
                    category = "Pizzas",
                    price = 16.00,
                    costPrice = 4.80,
                    description = "Wood-fired crust, San Marzano tomato sauce, fresh buffalo mozzarella, fragrant basil.",
                    isVeg = true,
                    drawableName = "food_pizza",
                    stockQuantity = 38
                ),
                MenuItemEntity(
                    id = 3,
                    name = "Crispy Chicken Supreme Burger",
                    category = "Burgers",
                    price = 12.50,
                    costPrice = 4.10,
                    description = "Buttermilk fried chicken, spicy cabbage slaw, house pickles & chipotle mayo.",
                    isVeg = false,
                    drawableName = "food_burger",
                    stockQuantity = 50
                ),
                MenuItemEntity(
                    id = 4,
                    name = "Pepperoni Rustica Pizza",
                    category = "Pizzas",
                    price = 18.00,
                    costPrice = 5.90,
                    description = "Double artisan pepperoni slices, crushed red pepper, hot honey drizzle & mozzarella.",
                    isVeg = false,
                    drawableName = "food_pizza",
                    stockQuantity = 40
                ),
                MenuItemEntity(
                    id = 5,
                    name = "Molten Chocolate Lava Cake",
                    category = "Desserts",
                    price = 9.00,
                    costPrice = 2.80,
                    description = "Decadent dark chocolate center, Madagascar vanilla gelato & strawberry compote.",
                    isVeg = true,
                    drawableName = "food_dessert",
                    stockQuantity = 25
                ),
                MenuItemEntity(
                    id = 6,
                    name = "New York Berry Cheesecake",
                    category = "Desserts",
                    price = 8.50,
                    costPrice = 2.50,
                    description = "Silky graham crust, rich cream cheese, and fresh forest berry drizzle.",
                    isVeg = true,
                    drawableName = "food_dessert",
                    stockQuantity = 20
                ),
                MenuItemEntity(
                    id = 7,
                    name = "Fresh Mint Lime Mojito",
                    category = "Beverages",
                    price = 6.50,
                    costPrice = 1.20,
                    description = "Muddled mint leaves, fresh key lime juice, sparkling soda and organic cane sugar.",
                    isVeg = true,
                    drawableName = "food_beverage",
                    stockQuantity = 60
                ),
                MenuItemEntity(
                    id = 8,
                    name = "Cold Brew Iced Coffee",
                    category = "Beverages",
                    price = 5.50,
                    costPrice = 1.00,
                    description = "18-hour steeped single-origin Ethiopian cold brew served over crystal clear ice.",
                    isVeg = true,
                    drawableName = "food_beverage",
                    stockQuantity = 55
                ),
                MenuItemEntity(
                    id = 9,
                    name = "Truffle Parmesan Fries",
                    category = "Appetizers",
                    price = 7.50,
                    costPrice = 2.00,
                    description = "Crispy golden shoestring potatoes tossed with white truffle oil and Grana Padano.",
                    isVeg = true,
                    drawableName = "food_burger",
                    stockQuantity = 70
                ),
                MenuItemEntity(
                    id = 10,
                    name = "Honey Glazed Buffalo Wings",
                    category = "Appetizers",
                    price = 11.00,
                    costPrice = 4.20,
                    description = "Eight jumbo crispy wings glazed in spicy wildflower honey glaze with ranch dip.",
                    isVeg = false,
                    drawableName = "food_burger",
                    stockQuantity = 35
                ),
                MenuItemEntity(
                    id = 11,
                    name = "Herb Grilled Salmon Fillet",
                    category = "Mains",
                    price = 22.00,
                    costPrice = 8.50,
                    description = "Atlantic salmon fillet, lemon dill butter glaze, roasted asparagus and puree.",
                    isVeg = false,
                    drawableName = "food_pizza",
                    stockQuantity = 22
                ),
                MenuItemEntity(
                    id = 12,
                    name = "Creamy Chicken Alfredo Penne",
                    category = "Mains",
                    price = 17.50,
                    costPrice = 5.00,
                    description = "Handmade penne pasta in garlic parmesan cream sauce with grilled chicken breast.",
                    isVeg = false,
                    drawableName = "food_pizza",
                    stockQuantity = 30
                ),
                MenuItemEntity(
                    id = 13,
                    name = "Family Feast Combo Special",
                    category = "Combos",
                    price = 45.00,
                    costPrice = 16.00,
                    description = "1 Truffle Burger + 1 Margherita Pizza + 1 Buffalo Wings + 2 Beverages + Fries.",
                    isVeg = false,
                    drawableName = "food_pizza",
                    stockQuantity = 30
                ),
                MenuItemEntity(
                    id = 14,
                    name = "Burger & Brew Express Deal",
                    category = "Combos",
                    price = 18.00,
                    costPrice = 6.00,
                    description = "Any signature burger + Large fries + Choice of cold brew or refreshing mojito.",
                    isVeg = false,
                    drawableName = "food_burger",
                    stockQuantity = 40
                )
            )
            dao.insertMenuItems(menuItems)

            // Seed Tables
            val tables = listOf(
                TableEntity(tableNumber = 1, capacity = 2, status = "Available", section = "Main Dining"),
                TableEntity(tableNumber = 2, capacity = 4, status = "Available", section = "Main Dining"),
                TableEntity(tableNumber = 3, capacity = 4, status = "Occupied", currentOrderId = "ORD-1021", guestCount = 2, section = "Main Dining"),
                TableEntity(tableNumber = 4, capacity = 6, status = "Available", section = "Main Dining"),
                TableEntity(tableNumber = 5, capacity = 2, status = "Billed", currentOrderId = "ORD-1019", guestCount = 2, section = "Patio"),
                TableEntity(tableNumber = 6, capacity = 4, status = "Available", section = "Patio"),
                TableEntity(tableNumber = 7, capacity = 8, status = "Occupied", currentOrderId = "ORD-1020", guestCount = 6, section = "Patio"),
                TableEntity(tableNumber = 8, capacity = 4, status = "Available", section = "VIP Lounge"),
                TableEntity(tableNumber = 9, capacity = 10, status = "Available", section = "VIP Lounge"),
                TableEntity(tableNumber = 10, capacity = 4, status = "Available", section = "VIP Lounge")
            )
            dao.insertTables(tables)

            // Seed Customers
            val customers = listOf(
                CustomerEntity(
                    name = "Sarah Jenkins",
                    phone = "+1 (555) 234-8901",
                    email = "sarah.j@example.com",
                    address = "442 Pinecrest Blvd, Apt 4B",
                    totalVisits = 14,
                    totalSpent = 642.50,
                    loyaltyPoints = 640,
                    notes = "Prefers booth seating, allergy to peanuts"
                ),
                CustomerEntity(
                    name = "Michael Vance",
                    phone = "+1 (555) 345-6712",
                    email = "m.vance@example.com",
                    address = "782 Elm Avenue, Suite 10",
                    totalVisits = 9,
                    totalSpent = 385.00,
                    loyaltyPoints = 380,
                    notes = "Loyal lunch takeaway customer"
                ),
                CustomerEntity(
                    name = "Emma Watson",
                    phone = "+1 (555) 789-0123",
                    email = "emma.w@example.com",
                    address = "1200 Sunset Drive",
                    totalVisits = 26,
                    totalSpent = 1240.00,
                    loyaltyPoints = 1240,
                    notes = "VIP Patron, loves Truffle items"
                ),
                CustomerEntity(
                    name = "David Miller",
                    phone = "+1 (555) 456-7890",
                    email = "david.m@example.com",
                    address = "315 Oak Street",
                    totalVisits = 4,
                    totalSpent = 115.00,
                    loyaltyPoints = 110,
                    notes = "Regular delivery orders"
                )
            )
            customers.forEach { dao.insertCustomer(it) }

            // Seed Inventory Items
            val inventoryItems = listOf(
                InventoryItemEntity(
                    name = "Buffalo Mozzarella Cheese",
                    category = "Raw Ingredients",
                    currentStock = 3.5,
                    unit = "kg",
                    minStockThreshold = 10.0,
                    unitCost = 14.00,
                    supplier = "Artisan Dairy Supply",
                    location = "Cold Storage"
                ),
                InventoryItemEntity(
                    name = "Fresh Organic Mint Sprigs",
                    category = "Raw Ingredients",
                    currentStock = 0.4,
                    unit = "kg",
                    minStockThreshold = 1.5,
                    unitCost = 8.00,
                    supplier = "GreenValley Herbs",
                    location = "Bar / Kitchen"
                ),
                InventoryItemEntity(
                    name = "Prime Angus Beef Patties",
                    category = "Raw Ingredients",
                    currentStock = 8.0,
                    unit = "kg",
                    minStockThreshold = 15.0,
                    unitCost = 18.50,
                    supplier = "Heritage Butchery",
                    location = "Walk-in Freezer"
                ),
                InventoryItemEntity(
                    name = "Golden Brioche Buns",
                    category = "Raw Ingredients",
                    currentStock = 42.0,
                    unit = "pcs",
                    minStockThreshold = 50.0,
                    unitCost = 0.85,
                    supplier = "SunRise Artisan Bakery",
                    location = "Pantry"
                ),
                InventoryItemEntity(
                    name = "San Marzano Pizza Sauce",
                    category = "Raw Ingredients",
                    currentStock = 18.0,
                    unit = "liters",
                    minStockThreshold = 12.0,
                    unitCost = 4.20,
                    supplier = "Napoli Imports",
                    location = "Pantry"
                ),
                InventoryItemEntity(
                    name = "Single Origin Coffee Beans",
                    category = "Raw Ingredients",
                    currentStock = 14.0,
                    unit = "kg",
                    minStockThreshold = 6.0,
                    unitCost = 22.00,
                    supplier = "Peak Roasters",
                    location = "Bar"
                ),
                InventoryItemEntity(
                    name = "Compostable Takeaway Boxes",
                    category = "Finished Goods",
                    currentStock = 240.0,
                    unit = "pcs",
                    minStockThreshold = 100.0,
                    unitCost = 0.35,
                    supplier = "EcoPack Solutions",
                    location = "Dry Storage"
                )
            )
            dao.insertInventoryItems(inventoryItems)

            // Seed Expenses
            val now = System.currentTimeMillis()
            val dayMs = 86400000L
            val expenses = listOf(
                ExpenseEntity(
                    date = now - dayMs * 1,
                    description = "Fresh Farm Vegetable & Produce Delivery",
                    category = "Ingredients",
                    amount = 450.00,
                    paymentMethod = "Bank Transfer",
                    recordedBy = "Alex (Manager)"
                ),
                ExpenseEntity(
                    date = now - dayMs * 3,
                    description = "Commercial Gas & Electric Utility Bill",
                    category = "Utilities",
                    amount = 680.00,
                    paymentMethod = "Bank Transfer",
                    recordedBy = "Admin"
                ),
                ExpenseEntity(
                    date = now - dayMs * 5,
                    description = "Thermal Printer Paper Rolls & POS Supplies",
                    category = "Maintenance",
                    amount = 95.00,
                    paymentMethod = "Card",
                    recordedBy = "Alex (Manager)"
                ),
                ExpenseEntity(
                    date = now - dayMs * 12,
                    description = "Monthly Restaurant Premises Lease",
                    category = "Rent",
                    amount = 3200.00,
                    paymentMethod = "Bank Transfer",
                    recordedBy = "Admin"
                ),
                ExpenseEntity(
                    date = now - dayMs * 15,
                    description = "Kitchen Staff Bi-Weekly Wages",
                    category = "Salaries",
                    amount = 2600.00,
                    paymentMethod = "Bank Transfer",
                    recordedBy = "Admin"
                )
            )
            dao.insertExpenses(expenses)

            // Seed Coupons
            val coupons = listOf(
                DiscountCouponEntity(
                    code = "WELCOME10",
                    discountPercent = 10.0,
                    minOrderAmount = 25.0,
                    description = "10% off on your order above $25"
                ),
                DiscountCouponEntity(
                    code = "HAPPYHOUR",
                    discountPercent = 15.0,
                    minOrderAmount = 35.0,
                    description = "15% off food and beverage combo"
                ),
                DiscountCouponEntity(
                    code = "VIP20",
                    discountPercent = 20.0,
                    minOrderAmount = 50.0,
                    description = "20% off for VIP loyalty members"
                )
            )
            dao.insertCoupons(coupons)

            // Seed Orders & Items
            val order1 = OrderEntity(
                id = "ORD-1021",
                orderType = "Dine-In",
                tableNumber = 3,
                guestCount = 2,
                customerName = "Walk-in Guest",
                subtotal = 38.00,
                taxRate = 0.16,
                taxAmount = 6.08,
                discountAmount = 0.0,
                totalAmount = 44.08,
                status = "Preparing",
                paymentMethod = "Pending",
                createdAt = now - 1800000L,
                serverName = "Alex",
                isKotPrinted = true
            )
            val order1Items = listOf(
                OrderItemEntity(orderId = "ORD-1021", menuItemId = 1, itemName = "Truffle Angus Burger", itemPrice = 14.50, quantity = 1, notes = "Medium rare"),
                OrderItemEntity(orderId = "ORD-1021", menuItemId = 2, itemName = "Artisan Margherita Pizza", itemPrice = 16.00, quantity = 1, notes = "Extra crispy crust"),
                OrderItemEntity(orderId = "ORD-1021", menuItemId = 9, itemName = "Truffle Parmesan Fries", itemPrice = 7.50, quantity = 1)
            )

            val order2 = OrderEntity(
                id = "ORD-1022",
                orderType = "Takeaway",
                customerName = "Michael Vance",
                customerPhone = "+1 (555) 345-6712",
                subtotal = 27.00,
                taxRate = 0.16,
                taxAmount = 4.32,
                discountAmount = 0.0,
                totalAmount = 31.32,
                status = "Ready",
                paymentMethod = "Card",
                createdAt = now - 2400000L,
                serverName = "Sarah",
                isKotPrinted = true
            )
            val order2Items = listOf(
                OrderItemEntity(orderId = "ORD-1022", menuItemId = 1, itemName = "Truffle Angus Burger", itemPrice = 14.50, quantity = 1),
                OrderItemEntity(orderId = "ORD-1022", menuItemId = 3, itemName = "Crispy Chicken Supreme Burger", itemPrice = 12.50, quantity = 1, notes = "No onions")
            )

            val order3 = OrderEntity(
                id = "ORD-1023",
                orderType = "Delivery",
                customerName = "Sarah Jenkins",
                customerPhone = "+1 (555) 234-8901",
                deliveryAddress = "442 Pinecrest Blvd, Apt 4B",
                deliveryFee = 4.50,
                subtotal = 40.50,
                taxRate = 0.16,
                taxAmount = 6.48,
                discountAmount = 4.05,
                discountCode = "WELCOME10",
                totalAmount = 47.43,
                status = "Preparing",
                paymentMethod = "Online Transfer",
                createdAt = now - 900000L,
                serverName = "Online Platform",
                notes = "Leave at front desk with code 4421",
                isKotPrinted = true
            )
            val order3Items = listOf(
                OrderItemEntity(orderId = "ORD-1023", menuItemId = 4, itemName = "Pepperoni Rustica Pizza", itemPrice = 18.00, quantity = 1),
                OrderItemEntity(orderId = "ORD-1023", menuItemId = 2, itemName = "Artisan Margherita Pizza", itemPrice = 16.00, quantity = 1),
                OrderItemEntity(orderId = "ORD-1023", menuItemId = 7, itemName = "Fresh Mint Lime Mojito", itemPrice = 6.50, quantity = 1)
            )

            // Completed orders today for sales statistics
            val order4 = OrderEntity(
                id = "ORD-1018",
                orderType = "Dine-In",
                tableNumber = 1,
                guestCount = 2,
                customerName = "Emma Watson",
                subtotal = 65.50,
                taxRate = 0.16,
                taxAmount = 10.48,
                totalAmount = 75.98,
                status = "Paid",
                paymentMethod = "Card",
                tenderedAmount = 75.98,
                createdAt = now - 14400000L,
                completedAt = now - 10800000L,
                isKotPrinted = true
            )
            val order5 = OrderEntity(
                id = "ORD-1019",
                orderType = "Dine-In",
                tableNumber = 5,
                guestCount = 2,
                customerName = "David Miller",
                subtotal = 54.00,
                taxRate = 0.16,
                taxAmount = 8.64,
                totalAmount = 62.64,
                status = "Billed",
                paymentMethod = "Pending",
                createdAt = now - 5400000L,
                isKotPrinted = true
            )

            dao.insertOrder(order1)
            dao.insertOrderItems(order1Items)

            dao.insertOrder(order2)
            dao.insertOrderItems(order2Items)

            dao.insertOrder(order3)
            dao.insertOrderItems(order3Items)

            dao.insertOrder(order4)
            dao.insertOrder(order5)
        }
    }
}
