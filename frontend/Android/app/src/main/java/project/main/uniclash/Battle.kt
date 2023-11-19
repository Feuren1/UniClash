package project.main.uniclash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import project.main.uniclash.ui.theme.UniClashTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.viewmodel.compose.viewModel
import project.main.uniclash.datatypes.Attack
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.viewmodels.BattleViewModel

class Battle : ComponentActivity() {
    //TODO Rename into BattleActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initializes a viewmodel for further use. Uses the critterservice in order to talk to the backend
        val battleViewModel by viewModels<BattleViewModel> {
            BattleViewModel.provideFactory(CritterService.getInstance(this))
        }
        setContent {
            UniClashTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //creates the pokemongame composable (name should be critterBattle)
                    val battleViewPlayerUIState by battleViewModel.playerCritter.collectAsState()
                    var playerCritter = battleViewPlayerUIState.playerCritter
                    val battleViewcpuCritterUIState by battleViewModel.cpuCritter.collectAsState()
                    var cpuCritter = battleViewcpuCritterUIState.cpuCritter
                    if(playerCritter!=null&&cpuCritter!=null){
                        CritterBattle(battleViewModel)
                    }
                }

            }
        }
    }
}

@Composable
fun CritterBattle(battleViewModel: BattleViewModel = viewModel()) {
    val battleViewPlayerUIState by battleViewModel.playerCritter.collectAsState()
    var playerCritter = battleViewPlayerUIState.playerCritter
    val battleViewcpuCritterUIState by battleViewModel.cpuCritter.collectAsState()
    var cpuCritter = battleViewcpuCritterUIState.cpuCritter

    val attack1: Attack? = playerCritter?.attacks?.get(0)
    val attack2: Attack? = playerCritter?.attacks?.get(1)
    val attack3: Attack? = playerCritter?.attacks?.get(2)
    val attack4: Attack? = playerCritter?.attacks?.get(3)

    var playerHealth by remember { mutableStateOf(playerCritter!!.hp) }
    var cpuHealth by remember { mutableStateOf(cpuCritter!!.hp) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column() {
                if (playerCritter != null) {
                    HealthBar(
                        currentHealth = playerHealth,
                        maxHealth = playerCritter.hp,
                        barColor = Color.Green
                    )
                }
                if (playerCritter != null) {
                    Text(playerCritter.name)
                }
                val context = LocalContext.current
                val name: String = playerCritter!!.name.lowercase()
                // Get the resource ID using getIdentifier
                val resourceId = context.resources.getIdentifier(name, "drawable", context.packageName)
                // Check if the resource ID is valid
                if (resourceId != 0) {
                    // Load the image using painterResource
                    val picture = painterResource(id = resourceId)

                    // Display the image
                    Image(
                        painter = picture,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                    )
                } else {
                    Text("Image not found for $name")
                }
            }

        }

        Spacer(modifier = Modifier.height(16.dp)) // Add vertical space

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column() {
                if (cpuCritter != null) {
                    HealthBar(
                        currentHealth = cpuHealth,
                        maxHealth = cpuCritter.hp,
                        barColor = Color.Red
                    )
                }
                if (cpuCritter != null) {
                    Text(cpuCritter.name)
                }
                val context = LocalContext.current
                val name: String = cpuCritter!!.name.lowercase()
                // Get the resource ID using getIdentifier
                val resourceId = context.resources.getIdentifier(name, "drawable", context.packageName)
                // Check if the resource ID is valid
                if (resourceId != 0) {
                    // Load the image using painterResource
                    val picture = painterResource(id = resourceId)

                    // Display the image
                    Image(
                        painter = picture,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                    )
                } else {
                    Text("Image not found for $name")
                }

            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Add more vertical space

        // Create a horizontal list of attack options
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(4) { index ->
                val attack = playerCritter?.attacks?.get(index)
                if (attack != null) {
                    ClickableAttack(attack)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            
            onClick = {
                println("CritterUsable:$playerCritter")
                println("CritterUsable:$cpuCritter")
                      },
            modifier = Modifier
                .padding(2.dp)
                .size(100.dp)

        ) {
            Text(text = "Debug")
        }
        
        Column {
            // Display the list of critters
            LazyColumn {
                //items(uniClashUIState.critters.size) {
                       // index ->
                    //Column {
                        /*Text(text = uniClashUIState.critters[index].name)
                        Text(text = uniClashUIState.critters[index].baseHealth.toString())
                        Text(text = uniClashUIState.critters[index].baseAttack.toString())
                        Text(text = uniClashUIState.critters[index].baseDefend.toString())
                        Text(text = uniClashUIState.critters[index].baseSpeed.toString())
                        Text(text = uniClashUIState.critters[index].attack1.toString())
                        Text(text = uniClashUIState.critters[index].attack2.toString())
                        Text(text = uniClashUIState.critters[index].attack3.toString())
                        Text(text = uniClashUIState.critters[index].attack4.toString())
                        Divider() // Add a divider between items*/
                    }
                }
            }
       // }
   // }
    // You can add controls or game logic here to update player1Health and player2Health.
}

@Composable
fun ClickableAttack(attack: Attack) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .height(40.dp)
            .background(Color.Blue, RoundedCornerShape(8.dp))
            .clickable { /* Handle attack selection here */ },
        contentAlignment = Alignment.Center
    ) {
        Text(text = attack.name, color = Color.White)
    }
}


@Composable
fun HealthBar(
    currentHealth: Int,
    maxHealth: Int,
    barColor: Color
) {
    val healthPercentage = (currentHealth / maxHealth.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
            .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(healthPercentage)
                .fillMaxHeight()
                .background(barColor, RoundedCornerShape(10.dp))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PokemonGamePreview() {
    UniClashTheme {
        //PokemonGame()
    }
}