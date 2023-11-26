package project.main.uniclash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import project.main.uniclash.datatypes.CritterPic
import project.main.uniclash.datatypes.MapSaver
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.viewmodels.WildEncounterViewModel


class WildEncounterActivity : ComponentActivity() {

    private var runaway by mutableStateOf(false)
    private var catchCritter by mutableStateOf(false)

    private lateinit var wildEncounterActivity: WildEncounterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wildEncounterActivity = viewModels<WildEncounterViewModel> {
            WildEncounterViewModel.provideFactory(CritterService.getInstance(this))
        }.value

        var wildEncounter = wildEncounterActivity.getWildEncounterMarker()

        setContent {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MenuHeader()
                var drawableImage = painterResource(id = R.drawable.icon)

                if(CritterPic.MUSK.searchDrawable(wildEncounter!!.critterUsable!!.name) != null) {
                     drawableImage = painterResource(id = CritterPic.MUSK.searchDrawable(wildEncounter!!.critterUsable!!.name))
                }

                Image(
                    painter = drawableImage,
                    contentDescription = null,
                    modifier = Modifier.size(240.dp)
                )
                if (wildEncounter != null) {
                    Text(
                        text = "\nName:${wildEncounter.critterUsable!!.name}\nLevel:${wildEncounter.critterUsable!!.level}\nHealthpoints:${wildEncounter.critterUsable!!.hp}\nAttak:${wildEncounter.critterUsable!!.atk}\nDefence:${wildEncounter.critterUsable!!.def}\nSpeed:${wildEncounter.critterUsable!!.spd}\n",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Add a button to request location permissions and start the map
                Button(onClick = {
                    val wildencounterList = MapSaver.WILDENCOUNTER.getMarker()
                    wildencounterList!!.remove(wildEncounter)
                    MapSaver.WILDENCOUNTER.setMarker(wildencounterList)
                    catchCritter = true
                }) {
                    Text(text = "catch me!")
                }
                Button(onClick = {
                    runaway = true
                }) {
                    Text(text = "run away!")
                }
            }
            if(runaway){
                val intent = Intent(this, MapActivity::class.java)
                this.startActivity(intent)
            }
            if(catchCritter){
                val preferences = this.getSharedPreferences("Ids", Context.MODE_PRIVATE)
                val studentId = preferences.getInt("StudentId", 1) ?: 1

                wildEncounterActivity.addWildEncounterToUser(studentId)
                val intent = Intent(this, MapActivity::class.java)
                this.startActivity(intent)
            }
        }
    }

    @Composable
    fun MenuHeader() {
        Text(
            text = "Wild Encounter Activity",
            fontSize = 50.sp, // Adjust the font size as needed
            fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(vertical = 16.dp) // Add vertical padding
        )
    }
}