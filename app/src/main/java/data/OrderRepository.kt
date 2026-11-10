package com.example.mycafeadminap.data

import androidx.room.withTransaction

class OrderRepository(private val db: AppDatabase) {

    // Moves an order to its next status (FR-13) and records a notification (FR-14).
    // Returns the new status, or null if the order was already completed.
    suspend fun advanceStatus(order: Order): String? {
        val next = OrderStatus.next(order.status) ?: return null
        db.withTransaction {
            db.orderDb().updateStatus(order.id, next)
            db.orderDb().insertNotification(
                OrderNotification(
                    orderId = order.id,
                    message = "Order #${order.id} is now $next",
                    createdAt = System.currentTimeMillis()
                )
            )
        }
        return next
    }
}
