package com.example.dineflow.data.repository

import com.example.dineflow.data.dao.DineFlowDao
import com.example.dineflow.data.model.CustomerEntity
import com.example.dineflow.data.model.DiscountCouponEntity
import com.example.dineflow.data.model.ExpenseEntity
import com.example.dineflow.data.model.InventoryItemEntity
import com.example.dineflow.data.model.MenuItemEntity
import com.example.dineflow.data.model.OrderEntity
import com.example.dineflow.data.model.OrderItemEntity
import com.example.dineflow.data.model.TableEntity
import kotlinx.coroutines.flow.Flow

class DineFlowRepository(private val dao: DineFlowDao) {
    val allMenuItems: Flow<List<MenuItemEntity>> = dao.getAllMenuItems()
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()
    val allTables: Flow<List<TableEntity>> = dao.getAllTables()
    val allCustomers: Flow<List<CustomerEntity>> = dao.getAllCustomers()
    val allInventory: Flow<List<InventoryItemEntity>> = dao.getAllInventory()
    val allExpenses: Flow<List<ExpenseEntity>> = dao.getAllExpenses()
    val allCoupons: Flow<List<DiscountCouponEntity>> = dao.getAllCoupons()

    fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>> = dao.getOrderItems(orderId)

    suspend fun getOrderItemsDirect(orderId: String): List<OrderItemEntity> = dao.getOrderItemsDirect(orderId)

    suspend fun getOrderByIdDirect(orderId: String): OrderEntity? = dao.getOrderByIdDirect(orderId)

    suspend fun saveOrderWithItems(
        order: OrderEntity,
        items: List<OrderItemEntity>
    ) {
        dao.insertOrder(order)
        dao.deleteOrderItems(order.id)
        dao.insertOrderItems(items)

        // If dine in, update table
        order.tableNumber?.let { tableNum ->
            val status = if (order.status == "Paid" || order.status == "Voided") "Available" else "Occupied"
            val currentOrder = if (status == "Available") null else order.id
            dao.updateTable(
                TableEntity(
                    tableNumber = tableNum,
                    capacity = 4,
                    status = status,
                    currentOrderId = currentOrder,
                    guestCount = order.guestCount ?: 2
                )
            )
        }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        val existing = dao.getOrderByIdDirect(orderId) ?: return
        val updated = existing.copy(
            status = newStatus,
            completedAt = if (newStatus == "Paid") System.currentTimeMillis() else existing.completedAt
        )
        dao.updateOrder(updated)

        if ((newStatus == "Paid" || newStatus == "Voided") && existing.tableNumber != null) {
            dao.updateTable(
                TableEntity(
                    tableNumber = existing.tableNumber,
                    capacity = 4,
                    status = "Available",
                    currentOrderId = null,
                    guestCount = 0
                )
            )
        }
    }

    suspend fun markKotPrinted(orderId: String) {
        val existing = dao.getOrderByIdDirect(orderId) ?: return
        dao.updateOrder(existing.copy(isKotPrinted = true))
    }

    suspend fun completeCheckout(
        orderId: String,
        paymentMethod: String,
        tenderedAmount: Double,
        changeAmount: Double,
        discountAmount: Double,
        discountCode: String,
        finalTotal: Double
    ) {
        val existing = dao.getOrderByIdDirect(orderId) ?: return
        val updated = existing.copy(
            status = "Paid",
            paymentMethod = paymentMethod,
            tenderedAmount = tenderedAmount,
            changeAmount = changeAmount,
            discountAmount = discountAmount,
            discountCode = discountCode,
            totalAmount = finalTotal,
            completedAt = System.currentTimeMillis()
        )
        dao.updateOrder(updated)

        // Free up table if dine in
        existing.tableNumber?.let { tbl ->
            dao.updateTable(
                TableEntity(
                    tableNumber = tbl,
                    capacity = 4,
                    status = "Available",
                    currentOrderId = null,
                    guestCount = 0
                )
            )
        }
    }

    suspend fun addCustomer(customer: CustomerEntity): Long = dao.insertCustomer(customer)

    suspend fun updateCustomer(customer: CustomerEntity) = dao.updateCustomer(customer)

    suspend fun addInventoryItem(item: InventoryItemEntity) = dao.insertInventoryItem(item)

    suspend fun updateInventoryItem(item: InventoryItemEntity) = dao.updateInventoryItem(item)

    suspend fun adjustInventoryStock(itemId: Long, adjustmentDelta: Double) {
        // Will be called by UI
    }

    suspend fun addExpense(expense: ExpenseEntity) = dao.insertExpense(expense)
}
