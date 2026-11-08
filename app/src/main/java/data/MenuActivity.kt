package com.example.mycafeadminap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycafeadminap.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import android.app.AlertDialog
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import android.text.InputType
import com.example.mycafeadminap.data.MenuItem
import android.widget.Button

class MenuActivity : AppCompatActivity() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Only a logged-in admin may see this screen
        if (!SessionManager(this).isAdminLoggedIn()) {
            finish()
            return
        }
        setContentView(R.layout.activity_menu)

        val adapter = MenuAdapter()
        val list = findViewById<RecyclerView>(R.id.menuList)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        // Use your own DAO names here if they differ from these
        val dao = AppDatabase.get(applicationContext).MenuItemDb()
        findViewById<Button>(R.id.addMenuButton).setOnClickListener {
            showAddMenuDialog(dao)
        }

        // The list refreshes by itself whenever the menu_items table changes
        scope.launch {
            dao.getALL().collect { items -> adapter.submit(items) }
        }
    }

    private fun showAddMenuDialog(dao: com.example.mycafeadminap.data.MenuItemDb) {

        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 16, 48, 0)
        }

        val nameInput = EditText(this).apply {
            hint = "Item name"
        }

        val descriptionInput = EditText(this).apply {
            hint = "Description"
        }

        val categoryInput = EditText(this).apply {
            hint = "Category"
        }

        val priceInput = EditText(this).apply {
            hint = "Price (£)"
            inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        form.addView(nameInput)
        form.addView(descriptionInput)
        form.addView(categoryInput)
        form.addView(priceInput)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Menu Item")
            .setView(form)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {

                    val name = nameInput.text.toString().trim()
                    val description = descriptionInput.text.toString().trim()
                    val category = categoryInput.text.toString().trim()
                    val price = priceInput.text.toString().toDoubleOrNull()

                    if (name.isEmpty() ||
                        description.isEmpty() ||
                        category.isEmpty() ||
                        price == null ||
                        price <= 0
                    ) {
                        Toast.makeText(
                            this,
                            "Please enter valid information in every field",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    scope.launch {
                        dao.Insert(
                            MenuItem(
                                name = name,
                                description = description,
                                category = category,
                                price = price
                            )
                        )

                        Toast.makeText(
                            this@MenuActivity,
                            "Menu item added successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    dialog.dismiss()
                }
        }

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}