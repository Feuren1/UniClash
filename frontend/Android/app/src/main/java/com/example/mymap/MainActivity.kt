package com.example.mymap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mymap.ui.components.UniClashTabRow

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                UniClashApp()
            }
        }
    }

    @Composable
    fun UniClashApp() {

        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
        val currentScreen = UniClashTabRowScreens.find { it.route == currentDestination?.route } ?: Menu

        Scaffold(
            topBar = {
                UniClashTabRow(
                    allScreens = UniClashTabRowScreens,
                    onTabSelected = { newScreen ->
                        navController.navigateSingleTopTo(newScreen.route)
                    },
                    currentScreen = currentScreen
                )
            }
        ) { innerPadding ->
            UniClashNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }

//        val context = LocalContext.current
//        Button(
//            onClick = {
//                // Handle the button click to open the new activity here
//                val intent = Intent(context, MapActivity::class.java)
//                this.startActivity(intent)
//            },
//            modifier = Modifier
//                .padding(2.dp)
//                .width(200.dp)
//                .height(50.dp)
//
//        ) {
//            Text("Menu")
//        }
    }
}