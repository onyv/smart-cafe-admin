package com.example.mycafeadminap.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="Orders")
data class Order(
@PrimaryKey(autoGenerate = true) val id: Long = 0,
val customerName: String,
val total: Double,
val status: String,   // Ordered,Preparing or Completed
val createdAt: Long   // time placed in m/s
)

//The phase of customers food being prepared

object OrderStatus {
    const val Ordered = "Ordered"
    const val Preparing = "Preparing"
    const val Completed = "Completed"

    fun next(current: String): String? =
        when (current) { /*Next phase of order is returned or null is returned if order state is completed*/
            Ordered -> Preparing
            Preparing -> Completed
            else -> null
        }
}
