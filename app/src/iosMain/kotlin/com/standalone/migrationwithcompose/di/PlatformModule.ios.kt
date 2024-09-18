package com.standalone.migrationwithcompose.di

import com.standalone.migrationwithcompose.db.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module {
    return module {
        single { getDatabaseBuilder() }
    }
}