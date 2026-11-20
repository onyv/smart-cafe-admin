package com.example.mycafeadminap

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycafeadminap.data.AppDatabase
import com.example.mycafeadminap.data.Order
import com.example.mycafeadminap.data.OrderDb
import com.example.mycafeadminap.data.OrderGenerator
import com.example.mycafeadminap.data.OrderRepository
import com.example.mycafeadminap.data.OrderStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class OrderActivity : AppCompatActivity() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private lateinit var adapter: OrderAdapter
    private lateinit var emptyText: TextView

    // Every order from the database, and which status is being shown (null = all)
    private var allOrders: List<Order> = emptyList()
    private var statusFilter: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Only a logged-in admin may see this screen
        if (!SessionManager(this).isAdminLoggedIn()) {
            finish()
            return
        }
        setContentView(R.layout.activity_order)

        val db = AppDatabase.get(applicationContext)
        val dao = db.orderDb()
        val repo = OrderRepository(db)

        emptyText = findViewById(R.id.emptyText)
        adapter = OrderAdapter { order -> showOrderDialog(dao, repo, order) }
        val list = findViewById<RecyclerView>(R.id.ordersList)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        findViewById<Button>(R.id.backButton).setOnClickListener { finish() }

        // Filter buttons
        val all = findViewById<Button>(R.id.filterAll)
        val ordered = findViewById<Button>(R.id.filterOrdered)
        val preparing = findViewById<Button>(R.id.filterPreparing)
        val completed = findViewById<Button>(R.id.filterCompleted)
        val filterButtons = listOf(all, ordered, preparing, completed)

        fun choose(filter: String?, selected: Button) {
            statusFilter = filter
            // The chosen button is fully visible, the others are faded
            filterButtons.forEach { it.alpha = if (it == selected) 1f else 0.6f }
            showOrders()
        }
        all.setOnClickListener { choose(null, all) }
        ordered.setOnClickListener { choose(OrderStatus.Ordered, ordered) }
        preparing.setOnClickListener { choose(OrderStatus.Preparing, preparing) }
        completed.setOnClickListener { choose(OrderStatus.Completed, completed) }
        choose(null, all)

        // Adds one brand new "Ordered" order, to demonstrate the flow (SIM-02)
        findViewById<Button>(R.id.newOrderButton).setOnClickListener {
            scope.launch {
                OrderGenerator.generate(db, 1, 0)
                Toast.makeText(this@OrderActivity, "New demo order added", Toast.LENGTH_SHORT).show()
            }
        }

        // The list refreshes by itself whenever the orders table changes
        scope.launch {
            dao.getAllOrders().collect { orders ->
                allOrders = orders
                showOrders()
            }
        }
    }

    // Applies the status filter and updates the list
    private fun showOrders() {
        val shown = allOrders.filter { statusFilter == null || it.status == statusFilter }
        adapter.submit(shown)
        emptyText.visibility = if (shown.isEmpty()) View.VISIBLE else View.GONE
    }

    // Shows the items in an order and lets the admin move it to the next status (FR-12, FR-13)
    private fun showOrderDialog(dao: OrderDb, repo: OrderRepository, order: Order) {
        scope.launch {
            val items = dao.getItemsForOrder(order.id)
            val lines = items.joinToString("\n") {
                "${it.quantity} x ${it.menuItemName}   £" + "%.2f".format(it.unitPrice * it.quantity)
            }
            val message = lines +
                    "\n\nTotal: £" + "%.2f".format(order.total) +
                    "\nStatus: " + order.status

            val builder = AlertDialog.Builder(this@OrderActivity)
                .setTitle("Order #${order.id} - ${order.customerName}")
                .setMessage(message)
                .setNegativeButton("Close", null)

            // A completed order has no next status, so it gets no button
            val next = OrderStatus.next(order.status)
            if (next != null) {
                builder.setPositiveButton("Mark as $next") { _, _ ->
                    scope.launch {
                        repo.advanceStatus(order)
                        Toast.makeText(
                            this@OrderActivity,
                            "Order #${order.id} is now $next",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            builder.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
