package com.example.mycafeadminap.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.first
import kotlin.collections.random
import kotlin.random.Random

// Simulated customer side (SIM-02): creates realistic orders from the current menu
object OrderGenerator {

    private const val DAY_MS = 24L * 60 * 60 * 1000
    private val customerNames = listOf(
        "Alex", "Sam", "Jordan", "Taylor", "Morgan", "Casey", "Riley", "Jamie", "Hassan", "Ali", "Bobby", "Cole", "Tyrese", "Edward"
    )

    // spreadDays = 30 makes past orders spread over the last 30 days (for analytics).
    // spreadDays = 0 makes brand new "ordered" orders placed right now (for demoing status changes).
    suspend fun generate(db: AppDatabase, count: Int, spreadDays: Int) {
        val menu = db.MenuItemDb().getALL().first().filter { it.availability }
        if (menu.isEmpty()) return

        val dao = db.orderDb()
        val now = System.currentTimeMillis()

        repeat(count) {
            // 1 to 4 different items, each with a quantity of 1 to 3
            val lines = menu.shuffled()
                .take(Random.nextInt(1, 5))
                .map { item -> Pair(item, Random.nextInt(1, 4)) }
            val total = Math.round(lines.sumOf { (item, qty) -> item.price * qty } * 100) / 100.0

            val offset = if (spreadDays > 0) Random.nextLong(0, spreadDays * DAY_MS) else 0L
            val createdAt = now - offset

            // Orders more than a day old are finished. Newer ones can be at any stage.
            val status = when {
                spreadDays == 0 -> OrderStatus.Ordered
                offset > DAY_MS -> OrderStatus.Completed
                else -> listOf(
                    OrderStatus.Ordered, OrderStatus.Preparing, OrderStatus.Completed
                ).random()
            }

            // The order and its items are saved together or not at all
            db.withTransaction {
                val orderId = dao.insertOrder(
                    Order(
                        customerName = customerNames.random(),
                        total = total,
                        status = status,
                        createdAt = createdAt
                    )
                )
                dao.insertItems(
                    lines.map { (item, qty) ->
                        OrderItem(
                            orderId = orderId,
                            menuItemName = item.name,
                            quantity = qty,
                            unitPrice = item.price
                        )
                    }
                )
            }
        }
    }
}