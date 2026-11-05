package com.example.mycafeadminap.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Menu_Items")
data class MenuItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val description: String,
    val price: Double,
    val availability: Boolean = true
)