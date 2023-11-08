package com.example.mymap

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.vector.ImageVector

interface UniClashDestination {
    val icon: ImageVector
    val route: String
}

object Menu : UniClashDestination {
    override val icon = Icons.Filled.Menu
    override val route = "menu"
}

val UniClashTabRowScreens = listOf(Menu)