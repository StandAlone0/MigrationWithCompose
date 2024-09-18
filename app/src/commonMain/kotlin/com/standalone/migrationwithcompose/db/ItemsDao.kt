package com.standalone.migrationwithcompose.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ItemsDao {

    @Query("SELECT * FROM items")
    suspend fun getItems(): List<Item>

    @Upsert
    suspend fun upsertItems(list: List<Item>)
}