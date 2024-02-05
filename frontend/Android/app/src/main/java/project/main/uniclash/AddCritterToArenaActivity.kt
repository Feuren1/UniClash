package project.main.uniclash

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.MapSaver
import project.main.uniclash.datatypes.MarkerArena
import project.main.uniclash.datatypes.MarkerData
import project.main.uniclash.retrofit.ArenaCritterService
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.viewmodels.AddCritterToArenaViewModel

class AddCritterToArenaActivity : ComponentActivity() {
    private var exitRequest by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        val addCritterToArenaViewModel: AddCritterToArenaViewModel by viewModels(factoryProducer = {
            AddCritterToArenaViewModel.provideFactory(ArenaCritterService.getInstance(this))
        })
        MapSaver.ARENA.setMarker(ArrayList<MarkerData?>())

        super.onCreate(savedInstanceState)
        setContent {
            val textMessage by addCritterToArenaViewModel.text.collectAsState()
            val selectedCritter by addCritterToArenaViewModel.selectedCritter.collectAsState()
            val bundle = intent.extras
            val arenaId = bundle?.getInt("ArenaID")
            UniClashTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Set the background image
                        Image(
                            painter = painterResource(id = R.drawable.arenabackground),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            if (selectedCritter != null){
                Column {
                    Row {
                        Text(text = "You have selected this critter")

                    }
                    Row {
                        CritterDetail(critter = selectedCritter)
                    }
                    Row {
                        Button(onClick = {
                            Toast.makeText(baseContext, "Critters inserted!!\n350EP for you and your critter\n7 Credits", Toast.LENGTH_LONG).show()
                            addCritterToArenaViewModel.patchArenaCritter(arenaId!!)
                            exitRequest = true
                            finish()
                        }) {
                            Text("Insert Critter")
                        }
                    }
                    Row {
                        Text(text = textMessage)
                    }
                    Row{
                        GifImage(gifName = "pokemonwalking", modifier = Modifier.size(100.dp) )
                    }
                }


            } else

            {
                CritterList(addCritterToArenaViewModel)
            }
            if (exitRequest) {
                val intent = Intent(this, MapActivity::class.java)
                this.startActivity(intent)
                finish()
                exitRequest = false
            }


        }
}
    @Composable
    fun CritterList(addCritterToArenaViewModel: AddCritterToArenaViewModel) {
        val critterListUIState by addCritterToArenaViewModel.critterUsables.collectAsState()
        val critterList = critterListUIState.critterUsables

        LazyColumn {
            items(critterList) { critter ->
                if(critter!!.critterTemplateId != 21)CritterDetail(critter = critter,addCritterToArenaViewModel)
                //Legendarys are not allowed :(
            }
        }
    }

    @Composable
    fun CritterDetail(critter: CritterUsable?, addCritterToArenaViewModel: AddCritterToArenaViewModel = viewModel()) {


        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth() // making box from left to right site
                .background(
                    Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                ) // Hintergrundfarbe und abgeflachte Ecken
                .clickable {
                    addCritterToArenaViewModel.selectedCritter.value = critter!!
                }

        ) {
            Row(modifier = Modifier.padding(all = 8.dp)) {
                val context = LocalContext.current
                val name: String = critter!!.name.lowercase()
                val resourceId = context.resources.getIdentifier(name, "drawable", context.packageName)
                if (resourceId != 0) {
                    val picture = painterResource(id = resourceId)
                    Image(
                        painter = picture,
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                    )
                } else {
                    Text("Image not found for $name")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = critter!!.name,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Level: ${critter?.level}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


