package com.example.mycafeadminap.data

import android.R
import androidx.room.Entity
import android.room.Index
import android.room.PrimaryKey

@Entity(tableName = "Users",indices = [Index(value=["email"],unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email = String;
    val displayName = String;
    val role = String;
    val passwordHash = String;
    val salt: String;





