package project.main.uniclash

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.viewmodels.BattleViewModel

class BattleWildEncounter : ComponentActivity() {
    //TODO Rename into BattleActivity
    private var mediaPlayer: MediaPlayer? = null
    private val battleViewModel by viewModels<BattleViewModel> {
        BattleViewModel.provideFactory(CritterService.getInstance(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initializes a viewmodel for further use. Uses the critterservice in order to talk to the backend
        mediaPlayer = MediaPlayer.create(this, R.raw.battlesoundtrack1)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).run {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        val b = intent.extras
        var wildeCritterId = -1 // or other values
        var playerCritterId = -1
        if (b != null) wildeCritterId = b.getInt("WildeCritterId")
        if (b != null) playerCritterId = b.getInt("PlayerCritterId")
        battleViewModel.loadCpuCritter(wildeCritterId)
        battleViewModel.loadPlayerCritter(playerCritterId)
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
                        CritterBattleWildEncounter(battleViewModel)
                    }
                }

            }
        }

    }
    override fun onDestroy() {
        super.onDestroy()

        // Release the MediaPlayer when the activity is destroyed
        mediaPlayer?.release()
    }
}

@Composable
private fun CritterBattleWildEncounter(battleViewModel: BattleViewModel = viewModel()) {
    val battleViewPlayerUIState by battleViewModel.playerCritter.collectAsState()
    val battleViewcpuCritterUIState by battleViewModel.cpuCritter.collectAsState()
    val battleText by battleViewModel.battleText.collectAsState()
    val playerInputUIState by battleViewModel.playerInput.collectAsState()
    val cpuInputUIState by battleViewModel.cpuInput.collectAsState()
    val playerMaxHealth by remember {
        mutableStateOf(battleViewPlayerUIState.playerCritter?.hp ?: 0)
    }

    val cpuMaxHealth by remember {
        mutableStateOf(battleViewcpuCritterUIState.cpuCritter?.hp ?: 0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column() {
                    HealthBar(
                        currentHealth = battleViewPlayerUIState.playerCritter!!.hp,
                        maxHealth = playerMaxHealth,
                        barColor = Color.Green
                    )
                CritterInfoText(battleViewPlayerUIState.playerCritter!!)

                val context = LocalContext.current
                val name: String = battleViewPlayerUIState.playerCritter!!.name.lowercase()
                val resourceId =
                    context.resources.getIdentifier(name, "drawable", context.packageName)
                if (resourceId != 0) {
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
                    HealthBar(
                        currentHealth = battleViewcpuCritterUIState.cpuCritter!!.hp,
                        maxHealth = cpuMaxHealth,
                        barColor = Color.Red
                    )
                CritterInfoText(battleViewcpuCritterUIState.cpuCritter!!)

                val context = LocalContext.current
                val name: String = battleViewcpuCritterUIState.cpuCritter!!.name.lowercase()
                val resourceId = context.resources.getIdentifier(name, "drawable", context.packageName)
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
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            item {
                // First row with two attacks side by side
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(8.dp),
                ) {
                    items(2) {
                        ClickableAttack(
                            attack = battleViewPlayerUIState.playerCritter!!.attacks[it],
                            onAttackClicked = { selectedAttack ->
                                battleViewModel.selectPlayerAttack(selectedAttack)
                            }
                        )
                    }
                }
            }

            item {
                // Second row with two attacks side by side
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(2) {
                        ClickableAttack(
                            attack = battleViewPlayerUIState.playerCritter!!.attacks[it + 2],
                            onAttackClicked = { selectedAttack ->
                                battleViewModel.selectPlayerAttack(selectedAttack)
                            }
                        )
                    }
                }
            }
        }

        /*Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            // "Start" button
            Button(
                onClick = {  },
                modifier = Modifier
                    .padding(2.dp)
                    .size(80.dp)
            ) {
                Text(text = "Start")
            }

            Spacer(modifier = Modifier.width(16.dp)) // Add space between buttons

            // "Debug" button
            Button(
                onClick = {
                    println("PlayerCritter:${battleViewPlayerUIState.playerCritter}")
                    println("CpuCritter:${battleViewcpuCritterUIState.cpuCritter}")
                },
                modifier = Modifier
                    .padding(2.dp)
                    .size(80.dp)
            ) {
                Text(text = "Debug")
            }
        }*/

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clickable {
                    if (playerInputUIState.isPlayerAttackSelected) {
                        battleViewModel.executePlayerAttack()
                    }
                    if (cpuInputUIState.isCpuAttackSelected) {
                        battleViewModel.executeCpuAttack()
                    }

                } // Handle click to execute attack
        ) {
            Text(
                text = if (playerInputUIState.isPlayerAttackSelected) {
                    "${battleViewPlayerUIState.playerCritter!!.name} attacks with ${playerInputUIState.selectedPlayerAttack!!.name}!"
                } else if (cpuInputUIState.isCpuAttackSelected) {
                    "${battleViewcpuCritterUIState.cpuCritter!!.name} attacks with ${cpuInputUIState.selectedCpuAttack!!.name}!"
                }

                else {
                    battleText
                },
                modifier = Modifier
                    .padding(16.dp)
                    .background(
                        color = Color.Gray,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                fontFamily = FontFamily.Default, // Replace with your custom font
                fontSize = 18.sp,
                color = Color.White
            )
        }

    }
}





