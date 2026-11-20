package com.example.mycafeadminap

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val session = SessionManager(this)
        if (!session.isAdminLoggedIn()) {
            goToLogin()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<TextView>(R.id.welcomeText).text = "Welcome, ${session.displayName()}"

        findViewById<Button>(R.id.menuButton).setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }

        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            session.clear()
            goToLogin()
        }
        findViewById<Button>(R.id.orderButton).setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }

        findViewById<Button>(R.id.dashboardButton).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}