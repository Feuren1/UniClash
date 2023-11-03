package com.example.mymap

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                OpenMenuActivityButton()
            }
        }
    }

    @Composable
    fun OpenMenuActivityButton() {
        val context = LocalContext.current
        Button(
            onClick = {
                // Handle the button click to open the new activity here
                val intent = Intent(context, MapActivity::class.java)
                this.startActivity(intent)
            },
            modifier = Modifier
                .padding(2.dp)
                .width(200.dp)
                .height(50.dp)

        ) {
            Text("Menu")
        }
    }
}