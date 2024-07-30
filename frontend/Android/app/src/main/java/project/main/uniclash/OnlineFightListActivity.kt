package project.main.uniclash

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import org.osmdroid.util.GeoPoint
import project.main.uniclash.datatypes.CustomColor
import project.main.uniclash.datatypes.MapSettings
import project.main.uniclash.datatypes.OnlineFightInformation
import project.main.uniclash.retrofit.OnlineFightService
import project.main.uniclash.viewmodels.OnlineFightListViewModel


class OnlineFightListActivity : ComponentActivity() {
    private var exitRequest by mutableStateOf(false)
    private var newRequest by mutableStateOf(false)

    val onlineFightListViewModel: OnlineFightListViewModel by viewModels(factoryProducer = {
        OnlineFightListViewModel.provideFactory(OnlineFightService.getInstance(this), Application())
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val onlineFightCollection by onlineFightListViewModel.onlineFights.collectAsState()
            var isLoading by rememberSaveable { mutableStateOf(true) }
            isLoading = onlineFightCollection.isLoading
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(16.dp)
                ) {
                    MenuHeader()
                    Image(
                        painter = painterResource(id = R.drawable.exit),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                exitRequest = true
                            }
                            .align(Alignment.TopEnd)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                            UsableList(onlineFightListViewModel,isLoading)
                    }
                }

            if (exitRequest) {
                val intent = Intent(this, MenuActivity::class.java)
                this.startActivity(intent)
                finish()
                exitRequest = false
            }

            LaunchedEffect(Unit) {
                // Startet den Timer und aktualisiert den Wert jede Sekunde
                while (true) {
                    delay(5000)
                    newRequest = true
                }
            }
            if(newRequest){
                onlineFightListViewModel.loadOnlineFights()
                newRequest = false
            }
        }
    }

    @Composable
    fun UsableList(onlineFightListViewModel: OnlineFightListViewModel, isLoading: Boolean) {
        val onlineFightCollection by onlineFightListViewModel.onlineFights.collectAsState()
        if (isLoading) {
            LoadingCircle(Modifier)
        }else {
            LazyColumn(modifier = Modifier) {
                items(items = onlineFightCollection.onlineFightsInformation, key= { onlineFight -> onlineFight!!.fightConnectionId }) { onlineFight ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        FightDetail(onlineFight)
                    }
                }
            }
        }
    }


    @Composable
    fun MenuHeader() {
        Column {
            Text(
                text = "Online Fights",
                fontSize = 50.sp, // Adjust the font size as needed
                fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
                textAlign = TextAlign.Start,
                color = Color.White,
                modifier = Modifier.padding(vertical = 16.dp) // Add vertical padding
            )
            Text(
                text = "Show fight invitations from other players.\nGo to map to invite a student.",
                fontSize = 12.sp, // Adjust the font size as needed
                fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
                textAlign = TextAlign.Start,
                color = Color.White
            )
        }
    }

    @Composable
    fun FightDetail(onlineFight: OnlineFightInformation?) {
        val isSelected by onlineFightListViewModel.selectedCritter.collectAsState()
        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth() // making box from left to right site
                .background(
                    CustomColor.DarkPurple.getColor(),
                    shape = RoundedCornerShape(8.dp)
                ) // Hintergrundfarbe und abgeflachte Ecken
                .border(
                    3.dp,
                    CustomColor.Purple.getColor(),
                    shape = RoundedCornerShape(8.dp)
                ) // Hintergrundfarbe und abgeflachte Ecken
                .clickable {
                    if(isSelected.isSelected) {
                        onlineFightListViewModel.insertCritter(onlineFight!!.fightConnectionId)
                        val intent = Intent(this, OnlineFightActivity::class.java)
                        val b = Bundle()
                        b.putInt("fightConnectionId", onlineFight!!.fightConnectionId)
                        intent.putExtras(b)
                        startActivity(intent)
                        finish()
                    }
                }
        ) {
            Row(modifier = Modifier.padding(all = 8.dp)) {
                val context = LocalContext.current
                val name: String = onlineFight?.userName.toString()
                Image(
                    painter = painterResource(R.drawable.swords),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Fight: ${onlineFight!!.fightConnectionId}",
                        fontSize = 18.sp,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall
                    )
                    if(isSelected.isSelected) {
                        Text(
                            text = "Against Trainer: ${onlineFight?.userName}",
                            fontSize = 12.sp,
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall
                        )
                    } else {
                        Text(
                            text = "Against Trainer: ${onlineFight?.userName}\nYou have to select a critter, out of CritterList!",
                            fontSize = 12.sp,
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}