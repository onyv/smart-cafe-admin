package com.example.mycafeadminap.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.util.TimeZone

class AnalyticsTest {

    private val utc = TimeZone.getTimeZone("UTC")
    private val now = Instant.parse("2026-11-18T12:00:00Z").toEpochMilli()
    private val day = 24L * 60 * 60 * 1000

    private fun order(id: Long, total: Double, status: String, createdAt: Long) =
        Order(id = id, customerName = "Test", total = total, status = status, createdAt = createdAt)

    private fun line(orderId: Long, name: String, qty: Int) =
        OrderItem(orderId = orderId, menuItemName = name, quantity = qty, unitPrice = 1.0)

    // Small data set small enough to check by hand
    private val orders = listOf(
        order(1, 10.00, OrderStatus.Completed, now),            // today
        order(2, 5.50, OrderStatus.Completed, now - day),       // yesterday
        order(3, 20.00, OrderStatus.Ordered, now),              // today, not finished
        order(4, 7.25, OrderStatus.Completed, now - 10 * day),  // outside a 3 day window
        order(5, 4.50, OrderStatus.Preparing, now - day)        // yesterday, not finished
    )

    @Test
    fun summary_countsOrdersAndCompletedRevenue() {
        val stats = Analytics.summary(orders)
        assertEquals(5, stats.totalOrders)
        assertEquals(22.75, stats.totalRevenue, 0.001)   // 10.00 + 5.50 + 7.25
        assertEquals(1, stats.ordered)
        assertEquals(1, stats.preparing)
        assertEquals(3, stats.completed)
    }

    @Test
    fun summary_emptyList_isAllZero() {
        val stats = Analytics.summary(emptyList())
        assertEquals(0, stats.totalOrders)
        assertEquals(0.0, stats.totalRevenue, 0.001)
        assertEquals(0, stats.completed)
    }

    @Test
    fun popularItems_sortedByQuantityThenName() {
        val items = listOf(
            line(1, "Latte", 2),
            line(1, "Muffin", 1),
            line(2, "Latte", 3),
            line(3, "Espresso", 1),
            line(3, "Croissant", 1)
        )
        val result = Analytics.popularItems(items)
        assertEquals("Latte", result[0].name)
        assertEquals(5, result[0].quantity)
        // Muffin, Espresso and Croissant all sold 1, so they are listed alphabetically
        assertEquals(listOf("Latte", "Croissant", "Espresso", "Muffin"), result.map { it.name })
    }

    @Test
    fun popularItems_respectsLimit() {
        val items = listOf(line(1, "Latte", 3), line(1, "Muffin", 2), line(1, "Espresso", 1))
        assertEquals(2, Analytics.popularItems(items, limit = 2).size)
    }

    @Test
    fun popularItems_noItems_returnsEmptyList() {
        assertTrue(Analytics.popularItems(emptyList()).isEmpty())
    }

    @Test
    fun revenueByDay_groupsCompletedOrdersByDay() {
        val result = Analytics.revenueByDay(orders, days = 3, now = now, zone = utc)
        assertEquals(3, result.size)
        // oldest first: 16 Nov, 17 Nov, 18 Nov
        assertEquals(listOf(0.0, 5.5, 10.0), result.map { it.revenue })
        assertEquals(Instant.parse("2026-11-18T00:00:00Z").toEpochMilli(), result.last().dayStart)
    }

    @Test
    fun revenueByDay_noOrders_returnsZeroForEveryDay() {
        val result = Analytics.revenueByDay(emptyList(), days = 7, now = now, zone = utc)
        assertEquals(7, result.size)
        assertTrue(result.all { it.revenue == 0.0 })
    }
}
