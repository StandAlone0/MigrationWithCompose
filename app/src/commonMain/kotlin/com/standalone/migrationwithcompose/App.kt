package com.standalone.migrationwithcompose

import androidx.compose.runtime.Composable
import com.standalone.migrationwithcompose.di.commonModule
import com.standalone.migrationwithcompose.navigation.AppNavHost
import org.koin.compose.KoinApplication
import org.koin.dsl.koinApplication

@Composable
fun App() {
        AppNavHost()
}