package com.example.mycafeadminap.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuItemDb {
    @Query("SELECT * FROM Menu_Items ORDER BY category")
    fun getALL(): Flow<List<MenuItem //Get all function returns each menu item sorted by category

    @Insert
    suspend fun insert(item: MenuItem):> Long //Adds new item to database and returns its id number
    @Delete
    suspend fun Delete(item: MenuItem) //Deletes row with id number specified
    @Update
    suspend fun Update(item: MenuItem) // Overwrites item with specified id number
    @Query ("SELECT COUNT (*) FROM menu_items")
    suspend fun count(): Int //Row are counted to see if there are any empty

}

