package com.example.mycafeadminap

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mycafeadminap.data.Order
import com.example.mycafeadminap.data.OrderStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// onClick = tap an order to see its details and change its status
class OrderAdapter(
    private val onClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderHolder>() {

    private var orders: List<Order> = emptyList()

    fun submit(list: List<Order>) {
        orders = list
        notifyDataSetChanged()
    }

    override fun getItemCount() = orders.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_row, parent, false)
        return OrderHolder(view)
    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        holder.bind(orders[position], onClick)
    }

    class OrderHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.orderTitle)
        private val time: TextView = view.findViewById(R.id.orderTime)
        private val status: TextView = view.findViewById(R.id.orderStatus)
        private val total: TextView = view.findViewById(R.id.orderTotal)

        private val timeFormat = SimpleDateFormat("d MMM yyyy, HH:mm", Locale.UK)

        fun bind(order: Order, onClick: (Order) -> Unit) {
            title.text = "#${order.id}  ${order.customerName}"
            time.text = timeFormat.format(Date(order.createdAt))
            status.text = order.status
            total.text = "£" + "%.2f".format(order.total)

            // A colour for each stage so the list is easy to scan
            status.setTextColor(
                when (order.status) {
                    OrderStatus.Ordered -> Color.parseColor("#FFB74D")
                    OrderStatus.Preparing -> Color.parseColor("#64B5F6")
                    else -> Color.parseColor("#81C784")
                }
            )

            itemView.setOnClickListener { onClick(order) }
        }
    }
}
