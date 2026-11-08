package com.example.mycafeadminap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mycafeadminap.data.MenuItem

class MenuAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var rows: List<MenuRow> = emptyList()

    // Takes the list sorted by category and adds a heading whenever the category changes
    fun submit(items: List<MenuItem>) {
        val result = mutableListOf<MenuRow>()
        var lastCategory: String? = null
        for (item in items) {
            if (item.category != lastCategory) {
                result.add(MenuRow.Header(item.category))
                lastCategory = item.category
            }
            result.add(MenuRow.Entry(item))
        }
        rows = result
        notifyDataSetChanged()
    }

    override fun getItemCount() = rows.size

    override fun getItemViewType(position: Int) = when (rows[position]) {
        is MenuRow.Header -> TYPE_HEADER
        is MenuRow.Entry -> TYPE_ENTRY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            HeaderHolder(inflater.inflate(R.layout.item_header, parent, false))
        } else {
            EntryHolder(inflater.inflate(R.layout.menu_rows, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = rows[position]) {
            is MenuRow.Header -> (holder as HeaderHolder).title.text = row.title
            is MenuRow.Entry -> (holder as EntryHolder).bind(row.item)
        }
    }

    class HeaderHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.itemHeader)
    }

    class EntryHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val root: View = view.findViewById(R.id.rowRoot)
        private val name: TextView = view.findViewById(R.id.itemName)
        private val description: TextView = view.findViewById(R.id.itemDescription)
        private val status: TextView = view.findViewById(R.id.itemStatus)
        private val price: TextView = view.findViewById(R.id.itemPrice)

        fun bind(item: MenuItem) {
            name.text = item.name
            description.text = item.description
            price.text = "£" + "%.2f".format(item.price)
            status.text = if (item.availability) "Available" else "Unavailable"
            root.alpha = if (item.availability) 1f else 0.5f
        }
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ENTRY = 1
    }
}