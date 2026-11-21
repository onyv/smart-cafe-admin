package com.example.mycafeadminap

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycafeadminap.data.AppDatabase
import com.example.mycafeadminap.data.Feedback
import com.example.mycafeadminap.data.FeedbackStats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class FeedbackActivity : AppCompatActivity() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private lateinit var adapter: FeedbackAdapter
    private lateinit var emptyText: TextView
    private lateinit var averageText: TextView

    // All feedback from the database, and which star rating is being shown (null = all)
    private var allFeedback: List<Feedback> = emptyList()
    private var ratingFilter: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Only a logged-in admin may see this screen
        if (!SessionManager(this).isAdminLoggedIn()) {
            finish()
            return
        }
        setContentView(R.layout.activity_feedback)

        val db = AppDatabase.get(applicationContext)

        emptyText = findViewById(R.id.emptyText)
        averageText = findViewById(R.id.averageText)
        adapter = FeedbackAdapter()
        val list = findViewById<RecyclerView>(R.id.feedbackList)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        findViewById<Button>(R.id.backButton).setOnClickListener { finish() }

        // Star filter buttons (FR-16)
        val all = findViewById<Button>(R.id.filterAll)
        val star1 = findViewById<Button>(R.id.filter1)
        val star2 = findViewById<Button>(R.id.filter2)
        val star3 = findViewById<Button>(R.id.filter3)
        val star4 = findViewById<Button>(R.id.filter4)
        val star5 = findViewById<Button>(R.id.filter5)
        val filterButtons = listOf(all, star1, star2, star3, star4, star5)

        fun choose(rating: Int?, selected: Button) {
            ratingFilter = rating
            // The chosen button is fully visible, the others are faded
            filterButtons.forEach { it.alpha = if (it == selected) 1f else 0.6f }
            showFeedback()
        }
        all.setOnClickListener { choose(null, all) }
        star1.setOnClickListener { choose(1, star1) }
        star2.setOnClickListener { choose(2, star2) }
        star3.setOnClickListener { choose(3, star3) }
        star4.setOnClickListener { choose(4, star4) }
        star5.setOnClickListener { choose(5, star5) }
        choose(null, all)

        // The list refreshes by itself whenever the feedback table changes
        scope.launch {
            db.feedbackDb().getAll().collect { feedback ->
                allFeedback = feedback
                showFeedback()
            }
        }
    }

    // Applies the star filter, updates the list and shows the overall average (FR-15)
    private fun showFeedback() {
        val shown = allFeedback.filter { ratingFilter == null || it.rating == ratingFilter }
        adapter.submit(shown)
        emptyText.visibility = if (shown.isEmpty()) View.VISIBLE else View.GONE

        averageText.text = if (allFeedback.isEmpty()) {
            "No feedback yet"
        } else {
            "Average rating: " + "%.1f".format(FeedbackStats.average(allFeedback)) +
                    " / 5  (" + allFeedback.size + " reviews)"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
