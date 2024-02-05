package project.main.uniclash

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collect
import org.osmdroid.util.GeoPoint
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.CustomColor
import project.main.uniclash.datatypes.Locations
import project.main.uniclash.datatypes.MapSaver
import project.main.uniclash.datatypes.MarkerWildEncounter
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.InventoryService
import project.main.uniclash.viewmodels.WildEncounterViewModel


class WildEncounterActivity : ComponentActivity() {

    private var runaway by mutableStateOf(false)
    private var catchCritter by mutableStateOf(false)

    private lateinit var wildEncounterViewModel: WildEncounterViewModel
    private var reCalculate by mutableStateOf(false)

    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wildEncounterViewModel = viewModels<WildEncounterViewModel> {
            WildEncounterViewModel.provideFactory(CritterService.getInstance(this), InventoryService.getInstance(this),Application())
        }.value

        var wildEncounter = wildEncounterViewModel.getWildEncounterMarker()
        wildEncounterViewModel.loadCritterUsable(wildEncounter!!.critterUsable!!.critterId)
        setContent {
            val usedItem = wildEncounterViewModel.usedItem.collectAsState()
            if(!usedItem.value.itemAvail){
                Toast.makeText(baseContext, "You have no chocolatewaffles :(", Toast.LENGTH_SHORT).show()
            }

            wildEncounterViewModel.calculateCatchChance()
            Box(){
                Image(
                    painter = painterResource(id = R.drawable.background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .fillMaxWidth() // making box from left to right site
                        .background(
                            Color.LightGray.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                        Image(
                            painter = painterResource(id = R.drawable.exit),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    runaway = true
                                }
                                .align(Alignment.TopEnd)
                        )
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        MenuHeader(wildEncounter!!.critterUsable!!.name)
                    }
                }
                Box(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ) {
                    Column {
                        var drawableImage = painterResource(id = R.drawable.icon)
                        val context = LocalContext.current
                        val name: String = wildEncounter!!.critterUsable!!.name!!.lowercase()
                        val resourceId =
                            context.resources.getIdentifier(name, "drawable", context.packageName)

                        if (resourceId > 0) {
                            drawableImage = painterResource(id = resourceId)
                        }
                        Spacer(modifier = Modifier.height(80.dp))
                        Image(
                            painter = drawableImage,
                            contentDescription = null,
                            modifier = Modifier
                                .size(240.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        val critterUsableUIState by wildEncounterViewModel.critterUsable.collectAsState()
                        if(critterUsableUIState.critterUsable != null) {
                            WildEncounterStats(wildEncounter)
                            WildEncounterCatchChange(wildEncounter)
                        }

                        // Add a button to request location permissions and start the map
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = CustomColor.DarkPurple.getColor()),
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                reCalculate = wildEncounterViewModel.useChocolatewaffle()
                            }) {
                            Text(text = "feed me!" , color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = CustomColor.DarkPurple.getColor()),
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                            val wildencounterList = MapSaver.WILDENCOUNTER.getMarker()
                            wildencounterList!!.remove(wildEncounter)
                            MapSaver.WILDENCOUNTER.setMarker(wildencounterList)
                            catchCritter = true
                        }) {
                            Text(text = "catch me!", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            }
            if(runaway){
                val intent = Intent(this, MapActivity::class.java)
                this.startActivity(intent)
                finish()
            }
            if(catchCritter){
                val preferences = this.getSharedPreferences("Ids", Context.MODE_PRIVATE)
                val message = wildEncounterViewModel.addWildEncounterToUser()

                Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MapActivity::class.java)
                this.startActivity(intent)
                finish()
            }
        }
    }

    @Composable
    fun MenuHeader(wildEncounterName : String) {
        Text(
            text = wildEncounterName,
            fontSize = 50.sp, // Adjust the font size as needed
            fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(vertical = 16.dp) // Add vertical padding
        )
    }

    @Composable
    fun WildEncounterStats(wildEncounter : MarkerWildEncounter) {
        val critterUsableUIState by wildEncounterViewModel.critterUsable.collectAsState()
        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth() // making box from left to right site
                .background(
                    Color.LightGray.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(8.dp)
                ) // Hintergrundfarbe und abgeflachte Ecken

        ) {
            Row(modifier = Modifier.padding(all = 8.dp)) {

                var drawableImage = painterResource(id = R.drawable.icon)
                val context = LocalContext.current
                val name: String = wildEncounter!!.critterUsable!!.name!!.lowercase()
                val resourceId = context.resources.getIdentifier(name, "drawable", context.packageName)

                if(resourceId > 0) {
                    drawableImage = painterResource(id = resourceId)
                }

                Image(
                    painter = drawableImage,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Critter Stats:",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "\nName: ${wildEncounter.critterUsable!!.name}\nLevel: ${wildEncounter.critterUsable!!.level}\nHealthpoints: ${critterUsableUIState.critterUsable!!.hp}\nAttack: ${critterUsableUIState.critterUsable!!.atk}\nDefence: ${critterUsableUIState.critterUsable!!.def}\nSpeed: ${critterUsableUIState.critterUsable!!.spd}\n",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    @Composable
    fun WildEncounterCatchChange(wildEncounter : MarkerWildEncounter) {
        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth() // making box from left to right site
                .background(
                    Color.LightGray.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(8.dp)
                ) // Hintergrundfarbe und abgeflachte Ecken

        ) {
            Row(modifier = Modifier.padding(all = 8.dp)) {

                var drawableImage = painterResource(id = R.drawable.chocolatewaffle)
                val context = LocalContext.current
                val name: String = wildEncounter!!.critterUsable!!.name!!.lowercase()
                val resourceId = context.resources.getIdentifier(name, "drawable", context.packageName)

                /*if(resourceId > 0) {
                    drawableImage = painterResource(id = resourceId)
                }*/

                Image(
                    painter = drawableImage,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.height(18.dp))
                    val catchChance = wildEncounterViewModel.catchChance;
                    val textColor = when {
                        catchChance in 0.0..30.0 -> Color.Red
                        catchChance in 31.0..49.0 -> Color(0xFFE65100) // Orange
                        catchChance in 50.0..69.0 -> Color.Yellow
                        catchChance in 70.0..89.0 -> Color.Green
                        else -> Color(0xFF006400) //dark Green
                    }
                    Text(
                        text = "Critter catch change: $catchChance %",
                        fontSize = 18.sp,
                        color = textColor,
                        style = MaterialTheme.typography.titleSmall
                    )
                    /*Text(
                        text = "\nName: ${wildEncounter.critterUsable!!.name}\nLevel: ${wildEncounter.critterUsable!!.level}\nHealthpoints: ${wildEncounter.critterUsable!!.hp}\nAttak: ${wildEncounter.critterUsable!!.atk}\nDefence: ${wildEncounter.critterUsable!!.def}\nSpeed: ${wildEncounter.critterUsable!!.spd}\n",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )*/
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}