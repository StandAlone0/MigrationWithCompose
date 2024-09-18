package com.standalone.migrationwithcompose.di

import com.standalone.migrationwithcompose.MainScreenViewModel
import com.standalone.migrationwithcompose.db.AppDatabase
import com.standalone.migrationwithcompose.db.getRoomDatabase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val commonModule = module {
    factory { getRoomDatabase(get()) }
    viewModel { MainScreenViewModel(get()) }
}