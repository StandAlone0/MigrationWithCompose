package com.standalone.migrationwithcompose.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ItemsDao {

    @Query("SELECT * FROM items")
    fun getItems(): List<Item>

    @Upsert
    fun upsertItems(list: List<Item>)
}