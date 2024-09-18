package com.standalone.migrationwithcompose.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.standalone.migrationwithcompose.LoginScreen
import com.standalone.migrationwithcompose.MainScreen
import com.standalone.migrationwithcompose.MainScreenViewModel
import org.koin.compose.viewmodel.koinViewModel

private const val LOGIN_SCREEN_ROUTE = "login"
private const val MAIN_SCREEN_ROUTE = "main_screen"

@Composable
fun AppNavHost() {

    val navHostController = rememberNavController()

    NavHost(navController = navHostController, startDestination = LOGIN_SCREEN_ROUTE) {

        composable(route = LOGIN_SCREEN_ROUTE) {
            LoginScreen(
                onNavigateToMainMenu = { navHostController.navigate(MAIN_SCREEN_ROUTE) }
            )
        }

        composable(route = MAIN_SCREEN_ROUTE) {
            val viewModel = koinViewModel<MainScreenViewModel>()
            MainScreen(
                list = viewModel.items.value,
                onNavigateBack = { navHostController.navigateUp() }
                )
        }

    }
}