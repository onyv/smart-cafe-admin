package com.example.mycafeadminap.data

import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

data class DashboardStats(
    val totalOrders: Int,
    val totalRevenue: Double,   // completed orders only
    val ordered: Int,
    val preparing: Int,
    val completed: Int
)

data class PopularItem(val name: String, val quantity: Int)

data class DayRevenue(val dayStart: Long, val revenue: Double)

// The calculations take plain lists and return plain results,
// so they can be unit tested with small hand-made data (no database or phone needed).
object Analytics {

    // Totals for the top of the dashboard
    fun summary(orders: List<Order>): DashboardStats {
        val completed = orders.filter { it.status == OrderStatus.Completed }
        return DashboardStats(
            totalOrders = orders.size,
            totalRevenue = round2(completed.sumOf { it.total }),
            ordered = orders.count { it.status == OrderStatus.Ordered },
            preparing = orders.count { it.status == OrderStatus.Preparing },
            completed = completed.size
        )
    }

    // Most ordered items by total quantity. Ties are sorted by name.
    fun popularItems(items: List<OrderItem>, limit: Int = 5): List<PopularItem> =
        items.groupBy { it.menuItemName }
            .map { (name, lines) -> PopularItem(name, lines.sumOf { it.quantity }) }
            .sortedWith(compareByDescending<PopularItem> { it.quantity }.thenBy { it.name })
            .take(limit)

    // Revenue for each of the last days, oldest first, today being last
    // Only completed orders count, and days with no sales show 0.
    fun revenueByDay(
        orders: List<Order>,
        days: Int,
        now: Long = System.currentTimeMillis(),
        zone: TimeZone = TimeZone.getDefault()
    ): List<DayRevenue> {
        val todayStart = startOfDay(now, zone)

        val dayStarts = (days - 1 downTo 0).map { daysAgo ->
            val cal = Calendar.getInstance(zone, Locale.UK)
            cal.timeInMillis = todayStart
            cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
            cal.timeInMillis
        }

        val totals = HashMap<Long, Double>()
        for (order in orders) {
            if (order.status != OrderStatus.Completed) continue
            val key = startOfDay(order.createdAt, zone)
            totals[key] = (totals[key] ?: 0.0) + order.total
        }

        return dayStarts.map { DayRevenue(it, round2(totals[it] ?: 0.0)) }
    }

    // Midnight at the start of the day that contains `time`
    private fun startOfDay(time: Long, zone: TimeZone): Long {
        val cal = Calendar.getInstance(zone, Locale.UK)
        cal.timeInMillis = time
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun round2(value: Double) = Math.round(value * 100) / 100.0
}
