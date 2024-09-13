package com.standalone.migrationwithcompose.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("items")
data class Item(
    @PrimaryKey
    val id: Int,
    val text: String
)
