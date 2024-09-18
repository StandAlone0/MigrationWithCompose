package com.standalone.migrationwithcompose.di

import com.standalone.migrationwithcompose.db.getDatabaseBuilder
import com.standalone.migrationwithcompose.db.getRoomDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module {
    return module {
        factory { getDatabaseBuilder(androidContext()) }
    }
}