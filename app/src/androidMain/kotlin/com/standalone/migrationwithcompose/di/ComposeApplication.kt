package com.standalone.migrationwithcompose.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ComposeApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ComposeApplication)
            modules(platformModule(), commonModule)
        }
    }

}