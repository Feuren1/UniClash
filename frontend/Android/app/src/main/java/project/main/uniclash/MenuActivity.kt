package project.main.uniclash

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import project.main.uniclash.dataManagers.PermissionManager
import project.main.uniclash.dataManagers.UserDataManager
import project.main.uniclash.datatypes.MapSettings
import project.main.uniclash.retrofit.ArenaService
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.StudentHubService
import project.main.uniclash.retrofit.StudentService
import project.main.uniclash.viewmodels.MapMarkerViewModel
import project.main.uniclash.viewmodels.MenuViewModel


class MenuActivity : ComponentActivity() {

    private var buttonRequest: Class<out Activity> by mutableStateOf(MainActivity::class.java)

    private val menuViewModel: MenuViewModel by viewModels(factoryProducer = {
        MenuViewModel.provideFactory()
    })

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
                    if(buttonRequest == MainActivity::class.java) {
                        MenuBars()
                    }
                }
            }
            if(buttonRequest == MenuActivity::class.java) {
                buttonRequest = MainActivity::class.java
            } else if(buttonRequest != MenuActivity::class.java && buttonRequest != MainActivity::class.java) {
                val intent = Intent(this, buttonRequest)
                this.startActivity(intent)
                buttonRequest = MainActivity::class.java
            }
        }
    }

    @Composable
    fun MenuBars(){
        Box(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                MenuCard(listOf(
                    Category(
                        "Critters List",
                        painterResource(R.drawable.prc2duck),
                        CritterListActivity::class.java,1
                    ),Category("Critterdex", painterResource(R.drawable.critterdex), CritterDexActivity::class.java,1)
                ))
                MenuCard(listOf(
                    Category(
                        "Inventory",
                        painterResource(R.drawable.bag),
                        InventoryActivity::class.java,1
                    )
                )
                )
                MenuCard(listOf(Category("Back to map", painterResource(R.drawable.map), MapActivity::class.java,1),
                    Category(
                        if (MapSettings.MOVINGCAMERA.getMapSetting()) {
                            "Following location arrow off"
                        } else {
                            "Following location arrow on"
                        }, painterResource(R.drawable.location), MenuActivity::class.java,2
                    ), Category(
                        if (MapSettings.CRITTERBINOCULARS.getMapSetting()) {
                            "Deactivate Binoculars"
                        } else {
                            "Activate Binoculars"
                        }, painterResource(R.drawable.binoculars), MenuActivity::class.java,3
                    ),
                    Category(
                        if (menuViewModel.returnPermission()) {
                            "Deactivate location sharing"
                        } else {
                            "Activate location sharing"
                        }, painterResource(R.drawable.shareloaction), MenuActivity::class.java,4
                    )
                )
                )
                MenuCard(listOf(Category("Online Fights", painterResource(R.drawable.swords), OnlineFightListActivity::class.java,1)))
                MenuCard(listOf(Category("New Building", painterResource(R.drawable.buildings), NewBuildingActivity::class.java,1)))
                MenuCard(listOf(Category("Log into other acc", painterResource(R.drawable.profile), LoginActivity::class.java,1),Category("Register new acc", painterResource(R.drawable.profile), RegisterActivity::class.java,1)))
                MenuCard(listOf(Category("Student Hub", painterResource(R.drawable.store), StudentHubActivity::class.java,1)))
                //MenuCard(listOf(Category("Camera", painterResource(R.drawable.swords), CameraActivity::class.java,1)))
                MenuCard(listOf(Category("Poké", painterResource(R.drawable.studentassistant), PokéActivity::class.java,1)))
                MenuCard(listOf(Category("Battle Activity", painterResource(R.drawable.arena), Battle::class.java,1)))
                MenuCard(listOf(Category("BattleForcedTutorial", painterResource(R.drawable.prc2duck), BattleForcedTutorialActivity::class.java,1),Category("BattleForcedTutorialAdvanced", painterResource(R.drawable.prc2duck), BattleForcedTutorialAdvancedActivity::class.java,1),Category("BattleTutorial", painterResource(R.drawable.prc2duck), BattleTutorialActivity::class.java,1),Category("BattleTutorialAdvanced", painterResource(R.drawable.prc2duck), BattleTutorialAdvancedActivity::class.java,1),Category("Final Battle Challenge", painterResource(R.drawable.prc2duck), FinalBattleActivity::class.java,1)))
            }
        }
    }

    fun checkBySpecificID(id : Int){
        if(id == 2){
            MapSettings.MOVINGCAMERA.setMapSetting(!MapSettings.MOVINGCAMERA.getMapSetting())
        }
        if(id == 3){
            MapSettings.CRITTERBINOCULARS.setMapSetting(!MapSettings.CRITTERBINOCULARS.getMapSetting())
        }
        if(id == 4){
            menuViewModel.setPermission()
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

    data class Category(val title: String, val picture: Painter, val activity: Class<out Activity>, val id: Int)

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
                    Box(modifier = Modifier
                        .clickable {
                            buttonRequest = category.activity
                            checkBySpecificID(category.id)
                        }
                        .fillMaxWidth()){
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
