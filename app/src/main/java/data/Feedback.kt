package com.example.mycafeadminap.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// A customer's rating and comment for a completed order (simulated customer side)
@Entity(
    tableName = "feedback",
    foreignKeys = [
        ForeignKey(
            entity = Order::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("orderId")]
)
data class Feedback(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val customerName: String,   // copied from the order so the list can show it without a lookup
    val rating: Int,            // 1 to 5 stars
    val comment: String,
    val createdAt: Long
)
