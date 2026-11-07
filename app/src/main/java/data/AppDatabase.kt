package com.example.mycafeadminap.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, MenuItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDb(): UserDb
    abstract fun MenuItemDb(): MenuItemDb

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                  INSTANCE ?: Room.databaseBuilder  (
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_cafe.db"
                ).build().also { INSTANCE = it }
            }
    }
}