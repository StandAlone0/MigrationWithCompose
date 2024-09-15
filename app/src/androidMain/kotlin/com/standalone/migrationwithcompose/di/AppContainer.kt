package com.standalone.migrationwithcompose.di

import android.content.Context
import androidx.room.Room
import com.standalone.migrationwithcompose.db.AppDatabase
import com.standalone.migrationwithcompose.db.getDatabaseBuilder
import com.standalone.migrationwithcompose.db.getRoomDatabase

class AppContainer(appContext: Context) {

    val database by lazy {
        val dbBuilder = getDatabaseBuilder(appContext)
        getRoomDatabase(dbBuilder)
    }

}