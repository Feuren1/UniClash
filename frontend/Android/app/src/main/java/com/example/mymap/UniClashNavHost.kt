package com.example.mymap

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mymap.ui.menu.MenuScreen

@Composable
fun UniClashNavHost(navController: NavHostController,
                    modifier: Modifier = Modifier) {

    NavHost(navController = navController,
        startDestination = Menu.route,
        modifier = modifier) {

        composable(route = Menu.route) {

            MenuScreen()
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) = this.navigate(route) {

    popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) {
        saveState = true
    }

    launchSingleTop = true
    restoreState = true
}