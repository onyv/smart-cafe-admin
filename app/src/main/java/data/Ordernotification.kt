package com.example.mycafeadminap.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// A record created whenever an order's status changes (FR-14)
@Entity(tableName = "notifications")
data class OrderNotification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val message: String,
    val createdAt: Long
)
