package com.example.mycafeadminap.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.first
import kotlin.random.Random

// Simulated customer side (SIM-02 and SIM-04): creates realistic orders from the current menu,
// and sample feedback for some of the completed ones
object OrderGenerator {

    private const val DAY_MS = 24L * 60 * 60 * 1000
    private val customerNames = listOf(
        "Alex", "Sam", "Jordan", "Taylor", "Morgan", "Casey", "Riley", "Jamie", "Hassan", "Tyrese" , "Edward", "Thomas"
    )

    private val goodComments = listOf(
        "Lovely coffee and quick service",
        "Great atmosphere, will be back",
        "Best latte in town",
        "Fresh pastries and friendly staff"
    )
    private val okComments = listOf(
        "Good, but a bit slow",
        "Decent drink, nothing special"
    )
    private val badComments = listOf(
        "Order took far too long",
        "My drink was lukewarm",
        "Wrong item in my order"
    )

    // spreadDays = 30 makes past orders spread over the last 30 days (for analytics).
    // spreadDays = 0 makes brand new "ordered" orders placed right now (for demoing status changes).
    suspend fun generate(db: AppDatabase, count: Int, spreadDays: Int) {
        val menu = db.MenuItemDb().getALL().first().filter { it.availability }
        if (menu.isEmpty()) return

        val dao = db.orderDb()
        val feedbackDao = db.feedbackDb()
        val now = System.currentTimeMillis()

        repeat(count) {
            // 1 to 4 different items, each with a quantity of 1 to 3
            val lines = menu.shuffled()
                .take(Random.nextInt(1, 5))
                .map { item -> Pair(item, Random.nextInt(1, 4)) }
            val total = Math.round(lines.sumOf { (item, qty) -> item.price * qty } * 100) / 100.0

            val offset = if (spreadDays > 0) Random.nextLong(0, spreadDays * DAY_MS) else 0L
            val createdAt = now - offset
            val customer = customerNames.random()

            // Orders more than a day old are finished. Newer ones can be at any stage.
            val status = when {
                spreadDays == 0 -> OrderStatus.Ordered
                offset > DAY_MS -> OrderStatus.Completed
                else -> listOf(
                    OrderStatus.Ordered, OrderStatus.Preparing, OrderStatus.Completed
                ).random()
            }

            // The order, its items and any feedback are saved together or not at all
            db.withTransaction {
                val orderId = dao.insertOrder(
                    Order(
                        customerName = customer,
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

                // About 6 in 10 completed orders get a review, written 10 to 120 minutes later
                if (status == OrderStatus.Completed && Random.nextInt(100) < 60) {
                    val rating = pickRating()
                    feedbackDao.insert(
                        Feedback(
                            orderId = orderId,
                            customerName = customer,
                            rating = rating,
                            comment = commentFor(rating),
                            createdAt = minOf(now, createdAt + Random.nextLong(10, 120) * 60_000L)
                        )
                    )
                }
            }
        }
    }

    // Weighted towards 4 and 5 stars, as real reviews usually are
    private fun pickRating(): Int = when (Random.nextInt(100)) {
        in 0..4 -> 1
        in 5..14 -> 2
        in 15..29 -> 3
        in 30..59 -> 4
        else -> 5
    }

    private fun commentFor(rating: Int): String = when {
        rating >= 4 -> goodComments.random()
        rating == 3 -> okComments.random()
        else -> badComments.random()
    }
}
