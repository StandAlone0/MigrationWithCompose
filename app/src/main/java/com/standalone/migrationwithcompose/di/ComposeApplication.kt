package com.standalone.migrationwithcompose.di

import android.app.Application

class ComposeApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(applicationContext)
    }

    companion object {
        var container: AppContainer? = null
            private set
    }
}