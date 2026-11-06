package com.example.mycafeadminap.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface `UserDb.kt` {
    @Insert
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): User?
}