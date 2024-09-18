package com.standalone.migrationwithcompose

import androidx.compose.runtime.Composable
import com.standalone.migrationwithcompose.di.commonModule
import com.standalone.migrationwithcompose.di.platformModule
import org.koin.compose.KoinApplication
import org.koin.dsl.koinApplication

fun koinConfiguration() = koinApplication {
    modules(platformModule(), commonModule)
}

@Composable
fun IosApp() {
    KoinApplication(::koinConfiguration) {
        App()
    }
}