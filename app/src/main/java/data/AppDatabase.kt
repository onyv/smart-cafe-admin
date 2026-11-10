package com.example.mycafeadminap.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// version raised from 1 to 2 because three tables were added.
// Use your own accessor names for the first two functions if they differ.
@Database(
    entities = [User::class, MenuItem::class, Order::class, OrderItem::class, OrderNotification::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDb(): UserDb
    abstract fun MenuItemDb(): MenuItemDb
    abstract fun orderDb(): OrderDb

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_cafe.db"
                )
                    // During development a version change wipes and rebuilds the database
                    .fallbackToDestructiveMigration(true)
                    .build().also { INSTANCE = it }
            }
    }
}