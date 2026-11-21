package com.example.mycafeadminap.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedbackDb {
    @Insert
    suspend fun insert(feedback: Feedback)

    // Newest first. Flow = the screen updates itself when feedback changes
    @Query("SELECT * FROM feedback ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Feedback>>

    @Query("SELECT COUNT(*) FROM feedback")
    suspend fun count(): Int
}
