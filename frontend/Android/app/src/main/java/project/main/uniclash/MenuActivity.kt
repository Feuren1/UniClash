package project.main.uniclash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import project.main.uniclash.datatypes.MapSettings
import project.main.uniclash.ui.theme.UniClashTheme


class MenuActivity : ComponentActivity() {

    private var buttonRequest by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    MenuHeader()
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clickable {
                                buttonRequest = 0 //TODO to Profile
                            }
                            .align(Alignment.TopEnd)
                    )
                }
                Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White) // Hier wird der Hintergrund weiß gemacht
                        ) {
                    Box(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column {
                            MenuCard(listOf(
                                Category(
                                    "Critters List",
                                    painterResource(R.drawable.prc2duck),
                                    1
                                ),Category("Critterdex", painterResource(R.drawable.critterdex), 3)
                            ))
                            MenuCard(listOf(
                                Category(
                                    "Inventory",
                                    painterResource(R.drawable.bag),
                                    2
                                )
                            )
                            )
                            MenuCard(listOf(Category("Back to map", painterResource(R.drawable.map), 7),
                                Category(
                                    if (MapSettings.MOVINGCAMERA.getMapSetting()) {
                                        "Following location arrow off"
                                    } else {
                                        "Following location arrow on"
                                    }, painterResource(R.drawable.location), 4
                                ), Category(
                                    if (MapSettings.CRITTERBINOCULARS.getMapSetting()) {
                                        "Deactivate Binoculars"
                                    } else {
                                        "Activate Binoculars"
                                    }, painterResource(R.drawable.binoculars), 10
                                )
                            )
                            )
                            MenuCard(listOf(Category("New Building", painterResource(R.drawable.store), 5)))
                            MenuCard(listOf(
                                Category(
                                    "Battle Activity",
                                    painterResource(R.drawable.arena),
                                    6
                                )
                            )
                            )
                            MenuCard(listOf(Category("Student Hub", painterResource(R.drawable.store), 8)))
                            MenuCard(listOf(Category("Camera", painterResource(R.drawable.swords), 9)))
                        }
                    }
                }
            }
            if(buttonRequest == 1) {
                val intent = Intent(this, CritterListActivity::class.java)
                this.startActivity(intent)
                buttonRequest == 0
            }
            if(buttonRequest == 3) {
                val intent = Intent(this, CritterDexActivity::class.java)
                this.startActivity(intent)
                buttonRequest == 0
            }
            if(buttonRequest == 10) {
                MapSettings.CRITTERBINOCULARS.setMapSetting(!MapSettings.CRITTERBINOCULARS.getMapSetting())
                val intent = Intent(this, MenuActivity::class.java)
                this.startActivity(intent)
            }
            if(buttonRequest == 6) {
                val intent = Intent(this, Battle::class.java)
                this.startActivity(intent)
                buttonRequest = 0
            }
            if(buttonRequest == 9) {
                val intent = Intent(this, CameraActivity::class.java)
                this.startActivity(intent)
                buttonRequest = 0
            }
            if(buttonRequest == 7) {
                val intent = Intent(this, MapActivity::class.java)
                this.startActivity(intent)
                buttonRequest = 0
            }
            if(buttonRequest == 8) {
                val intent = Intent(this, StudentHubActivity::class.java)
                this.startActivity(intent)
                buttonRequest = 0
            }
            if(buttonRequest == 4){
                MapSettings.MOVINGCAMERA.setMapSetting(!MapSettings.MOVINGCAMERA.getMapSetting())
                val intent = Intent(this, MenuActivity::class.java)
                this.startActivity(intent)
                buttonRequest = 0
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
        /*Text(
            text = "Coins",
            fontSize = 18.sp, // Adjust the font size as needed
            fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
            textAlign = TextAlign.End,
            modifier = Modifier.padding(vertical = 16.dp) // Add vertical padding
        )*/
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

    data class Category(val title: String, val picture: Painter, val id: Int)

    @Composable
    fun MenuCard(categories: List<Category>) {
        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth() // making box from left to right site
                .background(
                    Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                ) // Hintergrundfarbe und abgeflachte Ecken
                //.clickable {buttonRequest = category.id }
        ) {
            Column {
            for (category in categories) {
                Box(modifier = Modifier.clickable {buttonRequest = category.id }.fillMaxWidth()){
                Row(modifier = Modifier.padding(all = 8.dp)) {
                    Image(
                        painter = category.picture,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp) //40
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = category.title,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.titleSmall

                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                }
                }
            }
        }
    }



        @Preview(showBackground = true)
    @Composable
    fun MenuActivityPreview() {
        UniClashTheme {
            MenuCategories()
        }
    }
}