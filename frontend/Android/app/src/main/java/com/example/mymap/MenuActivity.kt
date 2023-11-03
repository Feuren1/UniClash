package com.example.mymap

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mymap.ui.theme.MyMapTheme

class MenuActivity : ComponentActivity() {

    private var buttonRequest by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MenuCategories()
            }
        }
    }

    @Composable
    fun MenuCategories() {
        val menuFontSize = 20.sp // Define a single font size

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            MenuHeader()
            Text(text = "Critters List", fontSize = menuFontSize)
            Text(text = "Inventar", fontSize = menuFontSize)
            Text(text = "Pokedex", fontSize = menuFontSize)
            Text(text = "Fix Location Camera on/off", fontSize = menuFontSize)
            Text(text = "New Building", fontSize = menuFontSize)
            Text(text = "Battle Activity", fontSize = menuFontSize, modifier = Modifier.clickable { buttonRequest = 6 })
            OpenBattleActivityButton()
        }
        if(buttonRequest == 6) {
            val intent = Intent(this, Battle::class.java)
            this.startActivity(intent)
            buttonRequest = 0
        }
    }


    @Composable
    fun MenuHeader() {
        Text(
            text = "Menu",
            fontSize = 50.sp, // Adjust the font size as needed
            fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(vertical = 16.dp) // Add vertical padding
        )
    }

   /* @Composable
    fun OpenBattleActivity() {
        if(buttonRequest == 6) {
            val intent = Intent(this, Battle::class.java)
            this.startActivity(intent)
            buttonRequest = 0
        }
    }*/

    @Composable
    fun OpenBattleActivityButton() {
        Button(
            onClick = {
                // Handle the button click to open the new activity here
                val intent = Intent(this, Battle::class.java)
                this.startActivity(intent)
            },
            modifier = Modifier
                .padding(2.dp)
                .width(200.dp)
                .height(50.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(), // Füllt den gesamten Button-Bereich aus
                contentAlignment = Alignment.CenterStart // Linksbündige Ausrichtung
            ) {
                Text(
                    text = "Open Another Activity",
                    color = Color.Black, // Schwarzer Text
                    style = TextStyle(textAlign = TextAlign.Start)
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MenuActivityPreview() {
        MyMapTheme {
            MenuCategories()
        }
    }
}