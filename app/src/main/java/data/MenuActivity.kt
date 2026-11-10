package com.example.mycafeadminap

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycafeadminap.data.AppDatabase
import com.example.mycafeadminap.data.MenuItem
import com.example.mycafeadminap.data.MenuItemDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MenuActivity : AppCompatActivity() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // Fixed choices so "drinks" and "Drinks" can't create two headings
    private val categories = listOf("Drinks", "Pastries", "Food")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Only a logged-in admin may see this screen
        if (!SessionManager(this).isAdminLoggedIn()) {
            finish()
            return
        }
        setContentView(R.layout.activity_menu)

        val dao = AppDatabase.get(applicationContext).MenuItemDb()

        // Tap a row = edit, long press = delete
        val adapter = MenuAdapter(
            onClick = { item -> showItemDialog(dao, item) },
            onLongClick = { item -> confirmDelete(dao, item) }
        )
        val list = findViewById<RecyclerView>(R.id.menuList)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        findViewById<Button>(R.id.addMenuButton).setOnClickListener {
            showItemDialog(dao)
        }

        // The list refreshes by itself whenever the menu_items table changes
        scope.launch {
            dao.getALL().collect { items -> adapter.submit(items) }
        }
    }

    // One dialog for both adding (existing = null) and editing (existing = the tapped item)
    private fun showItemDialog(dao: MenuItemDb, existing: MenuItem? = null) {

        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 16, 48, 0)
        }

        val nameInput = EditText(this).apply {
            hint = "Item name"
            filters = arrayOf(InputFilter.LengthFilter(40))
        }

        val descriptionInput = EditText(this).apply {
            hint = "Description"
        }

        val categorySpinner = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@MenuActivity,
                android.R.layout.simple_spinner_dropdown_item,
                categories
            )
        }

        val priceInput = EditText(this).apply {
            hint = "Price (£)"
            inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        val availableSwitch = SwitchCompat(this).apply {
            text = "Available"
            isChecked = true
        }

        // When editing, fill the form with the current values
        if (existing != null) {
            nameInput.setText(existing.name)
            descriptionInput.setText(existing.description)
            priceInput.setText(existing.price.toString())
            availableSwitch.isChecked = existing.availability
            val index = categories.indexOf(existing.category)
            if (index >= 0) categorySpinner.setSelection(index)
        }

        form.addView(nameInput)
        form.addView(descriptionInput)
        form.addView(categorySpinner)
        form.addView(priceInput)
        form.addView(availableSwitch)

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (existing == null) "Add Menu Item" else "Edit Menu Item")
            .setView(form)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save", null)
            .create()

        // Setting the Save click here stops the dialog closing when validation fails
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {

                    val name = nameInput.text.toString().trim()
                    val description = descriptionInput.text.toString().trim()
                    val price = priceInput.text.toString().toDoubleOrNull()

                    nameInput.error = null
                    descriptionInput.error = null
                    priceInput.error = null

                    var valid = true
                    if (name.isEmpty()) {
                        nameInput.error = "Enter a name"
                        valid = false
                    }
                    if (description.isEmpty()) {
                        descriptionInput.error = "Enter a description"
                        valid = false
                    }
                    if (price == null || price <= 0) {
                        priceInput.error = "Enter a price above 0"
                        valid = false
                    } else if (price > 100) {
                        priceInput.error = "Price can't be more than £100"
                        valid = false
                    }
                    if (!valid) return@setOnClickListener

                    val category = categorySpinner.selectedItem as String
                    val finalPrice = Math.round(requireNotNull(price) * 100) / 100.0
                    val available = availableSwitch.isChecked

                    scope.launch {
                        if (existing == null) {
                            dao.Insert(
                                MenuItem(
                                    name = name,
                                    description = description,
                                    category = category,
                                    price = finalPrice,
                                    availability = available
                                )
                            )
                            Toast.makeText(
                                this@MenuActivity,
                                "Menu item added",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // copy keeps the same id, so Room updates the right row
                            dao.Update(
                                existing.copy(
                                    name = name,
                                    description = description,
                                    category = category,
                                    price = finalPrice,
                                    availability = available
                                )
                            )
                            Toast.makeText(
                                this@MenuActivity,
                                "Menu item updated",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    dialog.dismiss()
                }
        }

        dialog.show()
    }

    // Only deletes if the admin presses Delete (FR-07)
    private fun confirmDelete(dao: MenuItemDb, item: MenuItem) {
        AlertDialog.Builder(this)
            .setTitle("Delete item")
            .setMessage("Delete ${item.name}? This can't be undone.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                scope.launch {
                    dao.Delete(item)
                    Toast.makeText(
                        this@MenuActivity,
                        "Menu item deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}