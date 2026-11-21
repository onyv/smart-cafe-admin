package com.example.mycafeadminap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mycafeadminap.data.Feedback
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FeedbackAdapter : RecyclerView.Adapter<FeedbackAdapter.FeedbackHolder>() {

    private var items: List<Feedback> = emptyList()

    fun submit(list: List<Feedback>) {
        items = list
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feedback_row, parent, false)
        return FeedbackHolder(view)
    }

    override fun onBindViewHolder(holder: FeedbackHolder, position: Int) {
        holder.bind(items[position])
    }

    class FeedbackHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.feedbackName)
        private val stars: TextView = view.findViewById(R.id.feedbackStars)
        private val comment: TextView = view.findViewById(R.id.feedbackComment)
        private val date: TextView = view.findViewById(R.id.feedbackDate)

        private val dateFormat = SimpleDateFormat("d MMM yyyy, HH:mm", Locale.UK)

        fun bind(feedback: Feedback) {
            name.text = "${feedback.customerName}  (order #${feedback.orderId})"
            // Filled stars for the rating, empty stars for the rest
            stars.text = "★".repeat(feedback.rating) + "☆".repeat(5 - feedback.rating)
            comment.text = feedback.comment
            date.text = dateFormat.format(Date(feedback.createdAt))
        }
    }
}
