package com.example.mycafeadminap

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mycafeadminap.data.AppDatabase
import com.example.mycafeadminap.data.Analytics
import com.example.mycafeadminap.data.Order
import com.example.mycafeadminap.data.OrderItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Only a logged-in admin may see this screen
        if (!SessionManager(this).isAdminLoggedIn()) {
            finish()
            return
        }
        setContentView(R.layout.activity_dashboard)

        db = AppDatabase.get(applicationContext)

        findViewById<Button>(R.id.backButton).setOnClickListener { finish() }
        findViewById<Button>(R.id.retryButton).setOnClickListener { loadDashboard() }

        loadDashboard()
    }

    // Reads the orders and items, then fills in the screen. A failure shows an error and a Retry button.
    private fun loadDashboard() {
        val errorText = findViewById<TextView>(R.id.errorText)
        val retryButton = findViewById<Button>(R.id.retryButton)
        errorText.visibility = View.GONE
        retryButton.visibility = View.GONE

        scope.launch {
            try {
                val dao = db.orderDb()
                val orders = dao.getAllOrders().first()
                val items = dao.getAllItems()
                showDashboard(orders, items)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                errorText.visibility = View.VISIBLE
                retryButton.visibility = View.VISIBLE
            }
        }
    }

    private fun showDashboard(orders: List<Order>, items: List<OrderItem>) {
        val stats = Analytics.summary(orders)

        findViewById<TextView>(R.id.emptyText).visibility =
            if (orders.isEmpty()) View.VISIBLE else View.GONE

        findViewById<TextView>(R.id.totalOrdersText).text = "Total orders: ${stats.totalOrders}"
        findViewById<TextView>(R.id.revenueText).text =
            "Revenue (completed orders): £" + "%.2f".format(stats.totalRevenue)
        findViewById<TextView>(R.id.statusText).text =
            "Ordered ${stats.ordered}  |  Preparing ${stats.preparing}  |  Completed ${stats.completed}"

        // Most ordered items (FR-18)
        val popularContainer = findViewById<LinearLayout>(R.id.popularContainer)
        popularContainer.removeAllViews()
        val popular = Analytics.popularItems(items, 5)
        val maxQuantity = (popular.maxOfOrNull { it.quantity } ?: 0).toDouble()
        popular.forEach {
            addBarRow(popularContainer, it.name, it.quantity.toDouble(), maxQuantity, "${it.quantity}")
        }

        // Revenue chart for the last 7 days (FR-19)
        val revenueContainer = findViewById<LinearLayout>(R.id.revenueContainer)
        revenueContainer.removeAllViews()
        val days = Analytics.revenueByDay(orders, 7)
        val maxRevenue = days.maxOfOrNull { it.revenue } ?: 0.0
        val dayFormat = SimpleDateFormat("EEE d MMM", Locale.UK)
        days.forEach {
            addBarRow(
                revenueContainer,
                dayFormat.format(Date(it.dayStart)),
                it.revenue,
                maxRevenue,
                "£" + "%.2f".format(it.revenue)
            )
        }
    }

    // One line of a bar chart: a label, a bar sized against the largest value, and the value
    private fun addBarRow(
        container: LinearLayout,
        label: String,
        value: Double,
        maxValue: Double,
        valueText: String
    ) {
        val density = resources.displayMetrics.density

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, (4 * density).toInt(), 0, (4 * density).toInt())
        }

        val labelView = TextView(this).apply {
            text = label
            setTextColor(Color.WHITE)
            textSize = 13f
            layoutParams = LinearLayout.LayoutParams((100 * density).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        val bar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100
            progress = if (maxValue > 0) (value / maxValue * 100).toInt() else 0
            layoutParams = LinearLayout.LayoutParams(0, (12 * density).toInt(), 1f)
        }

        val valueView = TextView(this).apply {
            text = valueText
            setTextColor(Color.WHITE)
            textSize = 13f
            gravity = Gravity.END
            layoutParams = LinearLayout.LayoutParams((70 * density).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        row.addView(labelView)
        row.addView(bar)
        row.addView(valueView)
        container.addView(row)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
