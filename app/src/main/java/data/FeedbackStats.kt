package com.example.mycafeadminap.data

object FeedbackStats {

    // Average star rating to one decimal place, or 0.0 if there is no feedback
    fun average(feedback: List<Feedback>): Double {
        if (feedback.isEmpty()) return 0.0
        val total = feedback.sumOf { it.rating }
        return Math.round(total * 10.0 / feedback.size) / 10.0
    }
}
