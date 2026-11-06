package com.example.mycafeadminap.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Users",indices = [Index(value=["email"],unique = true)])
data class `User.kt`(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email: String,
    val displayName: String,
    val role: String,
    val passwordHash: String,
    val salt: String,
)




