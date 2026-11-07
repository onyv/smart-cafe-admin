package com.example.mycafeadminap

import android.content.Context
import com.example.mycafeadminap.data.User

class SessionManager(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("session", Context.MODE_PRIVATE)

    fun save(user: User) {
        prefs.edit()
            .putLong("user_id", user.id)
            .putString("role", user.role)
            .putString("name", user.displayName)
            .apply()
    }

    fun isAdminLoggedIn(): Boolean =
        prefs.getLong("user_id", -1L) != -1L && prefs.getString("role", null) == "admin"

    fun displayName(): String = prefs.getString("name", "") ?: ""

    fun clear() {
        prefs.edit().clear().apply()
    }
}