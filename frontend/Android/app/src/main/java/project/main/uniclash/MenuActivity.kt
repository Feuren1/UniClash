package project.main.uniclash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import project.main.uniclash.datatypes.MapSettings


class MenuActivity : ComponentActivity() {

    private var buttonRequest: Class<out Activity> by mutableStateOf(MainActivity::class.java)

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
                                buttonRequest = ProfileActivity::class.java
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
                                    CritterListActivity::class.java
                                ),Category("Critterdex", painterResource(R.drawable.critterdex), CritterDexActivity::class.java)
                            ))
                            MenuCard(listOf(
                                Category(
                                    "Inventory",
                                    painterResource(R.drawable.bag),
                                    InventoryActivity::class.java
                                )
                            )
                            )
                            MenuCard(listOf(Category("Back to map", painterResource(R.drawable.map), MapActivity::class.java),
                                Category(
                                    if (MapSettings.MOVINGCAMERA.getMapSetting()) {
                                        "Following location arrow off"
                                    } else {
                                        "Following location arrow on"
                                    }, painterResource(R.drawable.location), MenuActivity::class.java
                                ), Category(
                                    if (MapSettings.CRITTERBINOCULARS.getMapSetting()) {
                                        "Deactivate Binoculars"
                                    } else {
                                        "Activate Binoculars"
                                    }, painterResource(R.drawable.binoculars), MenuActivity::class.java
                                )
                            )
                            )
                            MenuCard(listOf(Category("New Building", painterResource(R.drawable.buildings), NewBuildingActivity::class.java)))
                            MenuCard(listOf(
                                Category(
                                    "Battle Activity",
                                    painterResource(R.drawable.arena),
                                    Battle::class.java
                                )
                            )
                            )
                            MenuCard(listOf(Category("Student Hub", painterResource(R.drawable.store), StudentHubActivity::class.java)))
                            MenuCard(listOf(Category("Camera", painterResource(R.drawable.swords), CameraActivityTest::class.java)))
                            MenuCard(listOf(Category("Log into other acc", painterResource(R.drawable.profile), LoginActivity::class.java),Category("Register new acc", painterResource(R.drawable.profile), RegisterActivity::class.java)))
                            MenuCard(listOf(Category("Arena", painterResource(R.drawable.arena), ArenaActivity::class.java)))
                            MenuCard(listOf(Category("Poké", painterResource(R.drawable.studentassistance), PokéActivity::class.java)))
                        }
                    }
                }
            }
            if(buttonRequest != MainActivity::class.java) {
                val intent = Intent(this, buttonRequest)
                this.startActivity(intent)
                buttonRequest = MainActivity::class.java
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
    }

    data class Category(val title: String, val picture: Painter, val activity: Class<out Activity>)

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
                Box(modifier = Modifier.clickable {buttonRequest = category.activity }.fillMaxWidth()){
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
}
