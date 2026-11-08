package com.example.mycafeadminap

import com.example.mycafeadminap.data.MenuItem

// One line in the list: Is either a heading for the category or a menu item
sealed class MenuRow {
    data class Header(val title: String) : MenuRow()
    data class Entry(val item: MenuItem) : MenuRow()
}