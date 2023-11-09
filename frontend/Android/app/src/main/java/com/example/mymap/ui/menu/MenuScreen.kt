package com.example.mymap.ui.menu

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mymap.R
import com.example.mymap.ui.battle.Battle
import com.example.mymap.ui.map.MapActivity

@Composable
fun MenuScreen() {

//    val menuFontSize = 20.sp // Define a single font size
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Top,
//        horizontalAlignment = Alignment.Start
//    ) {
//        MenuHeader()
//        Text(text = "Critters List", fontSize = menuFontSize)
//        Text(text = "Inventar", fontSize = menuFontSize)
//        Text(text = "Pokedex", fontSize = menuFontSize)
//        Text(text = "Fix Location Camera on/off", fontSize = menuFontSize)
//        Text(text = "New Building", fontSize = menuFontSize)
//    }
//}

    val context: Context = LocalContext.current

    var buttonRequest by rememberSaveable { mutableStateOf(0) }
    Column {
        MenuHeader()
        MenuCard(Category("Critters List", painterResource(R.drawable.prc2duck)), onButtonRequestChange = { buttonRequest = 1 })
        MenuCard(Category("Inventar", painterResource(R.drawable.energy_drink)), onButtonRequestChange = { buttonRequest = 2 })
        MenuCard(Category("Pokedex", painterResource(R.drawable.prc2duck)), onButtonRequestChange = { buttonRequest = 3 })
        MenuCard(Category("Fix Location Camera on/off", painterResource(R.drawable.location)), onButtonRequestChange = { buttonRequest = 4 })
        MenuCard(Category("New Building", painterResource(R.drawable.store)), onButtonRequestChange = { buttonRequest = 5 })
        MenuCard(Category("Battle Activity", painterResource(R.drawable.arena)), onButtonRequestChange = { buttonRequest = 6 })
        MenuCard(Category("Back to map", painterResource(R.drawable.map)), onButtonRequestChange = { buttonRequest = 7 })
    }
    if (buttonRequest == 6) {
        val intent = Intent(context, Battle::class.java)
        context.startActivity(intent)
        buttonRequest = 0
    }
    if (buttonRequest == 7) {
        val intent = Intent(context, MapActivity::class.java)
        context.startActivity(intent)
        buttonRequest = 0
    }
}

data class Category(val title: String, val picture: Painter)

@Composable
fun MenuCard(category: Category, onButtonRequestChange: () -> Unit) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Image(
            painter = category.picture,
            contentDescription = null,
            modifier = Modifier
                .clickable { onButtonRequestChange }
                .size(60.dp) //40
            //.clip(CircleShape)
            //.border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = category.title,
                fontSize = 18.sp,
                modifier = Modifier.clickable { onButtonRequestChange },
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall

            )
            Spacer(modifier = Modifier.height(8.dp))
            /*Surface(shape = MaterialTheme.shapes.medium, shadowElevation = 1.dp) {
                Text(
                    text = category.body,
                    modifier = Modifier.padding(all = 4.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }*/
        }
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
    Text(
        text = "Coins",
        fontSize = 18.sp, // Adjust the font size as needed
        fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
        textAlign = TextAlign.End,
        modifier = Modifier.padding(vertical = 16.dp) // Add vertical padding
    )
}

//@Composable
//fun MenuCategories() {
//    val menuFontSize = 20.sp // Define a single font size
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Top,
//        horizontalAlignment = Alignment.Start
//    ) {
//        MenuHeader()
//        Text(text = "Critters List", fontSize = menuFontSize)
//        Text(text = "Inventar", fontSize = menuFontSize)
//        Text(text = "Pokedex", fontSize = menuFontSize)
//        Text(text = "Fix Location Camera on/off", fontSize = menuFontSize)
//        Text(text = "New Building", fontSize = menuFontSize)
//        Text(
//            text = "Battle Activity",
//            fontSize = menuFontSize,
//            modifier = Modifier.clickable { buttonRequest = 6 })
//        OpenBattleActivityButton()
//    }
//    if (buttonRequest == 6) {
//        val intent = Intent(this, Battle::class.java)
//        this.startActivity(intent)
//        buttonRequest = 0
//    }

//@Composable
//fun OpenBattleActivityButton() {
//    Button(
//        onClick = {
//            // Handle the button click to open the new activity here
//            val intent = Intent(this, Battle::class.java)
//            this.startActivity(intent)
//        },
//        modifier = Modifier
//            .padding(2.dp)
//            .width(200.dp)
//            .height(50.dp)
//    ) {
//        Box(
//            modifier = Modifier.fillMaxSize(), // Füllt den gesamten Button-Bereich aus
//            contentAlignment = Alignment.CenterStart // Linksbündige Ausrichtung
//        ) {
//            Text(
//                text = "Open Another Activity",
//                color = Color.Black, // Schwarzer Text
//                style = TextStyle(textAlign = TextAlign.Start)
//            )
//        }
//    }

/* @Composable
 fun OpenBattleActivity() {
     if(buttonRequest == 6) {
         val intent = Intent(this, Battle::class.java)
         this.startActivity(intent)
         buttonRequest = 0
     }
 }*/

//}
