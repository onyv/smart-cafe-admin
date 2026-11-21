package com.example.mycafeadminap.data

import org.junit.Assert.assertEquals
import org.junit.Test

class FeedbackStatsTest {

    private fun feedback(rating: Int) =
        Feedback(orderId = 1, customerName = "Test", rating = rating, comment = "", createdAt = 0L)

    @Test
    fun average_ofThreeRatings() {
        val list = listOf(feedback(5), feedback(4), feedback(3))
        assertEquals(4.0, FeedbackStats.average(list), 0.001)
    }

    @Test
    fun average_roundsToOneDecimalPlace() {
        val list = listOf(feedback(5), feedback(5), feedback(4))   // 14 / 3 = 4.666...
        assertEquals(4.7, FeedbackStats.average(list), 0.001)
    }

    @Test
    fun average_halfStars() {
        val list = listOf(feedback(5), feedback(4))
        assertEquals(4.5, FeedbackStats.average(list), 0.001)
    }

    @Test
    fun average_noFeedback_isZero() {
        assertEquals(0.0, FeedbackStats.average(emptyList()), 0.001)
    }
}
