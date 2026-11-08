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

        // The list refreshes by itself whenever the menu_items table changes
        scope.launch {
            dao.getALL().collect { items -> adapter.submit(items) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}