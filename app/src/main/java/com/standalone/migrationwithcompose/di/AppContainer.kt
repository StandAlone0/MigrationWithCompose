package com.standalone.migrationwithcompose.di

import android.content.Context
import androidx.room.Room
import com.standalone.migrationwithcompose.db.AppDatabase

class AppContainer(appContext: Context) {

    val database by lazy {
        Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app.db"
        ).build()
    }

}