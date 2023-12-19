package project.main.uniclash

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import project.main.uniclash.datatypes.Attack
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.viewmodels.BattleViewModel

class Battle : ComponentActivity() {

    private var exitRequest by mutableStateOf(false)
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
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(16.dp)
                        ) {
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
                        Box {
                            if(playerCritter!=null&&cpuCritter!=null){
                                CritterBattle(battleViewModel)
                            }
                        }
                    }
                    if (exitRequest) {
                        mediaPlayer?.release()
                        val intent = Intent(this, MenuActivity::class.java)
                        this.startActivity(intent)
                        exitRequest = false
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
fun CritterBattle(battleViewModel: BattleViewModel = viewModel()) {
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
                PlayerCritterInfoText(battleViewPlayerUIState.playerCritter!!)

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
                PlayerCritterInfoText(battleViewcpuCritterUIState.cpuCritter!!)

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

@Composable
fun ClickableAttack(
    attack: Attack,
    onAttackClicked: (Attack) -> Unit
) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .height(40.dp)
            .background(Color.Blue, RoundedCornerShape(8.dp))
            .clickable {
                // Handle attack selection here
                onAttackClicked(attack) // Assuming 10 damage for now
            },
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
    // Calculate health percentage
    val healthPercentage = (currentHealth / maxHealth.toFloat()).coerceIn(0f, 1f)

    // Create an Animatable for the shake animation
    val shakeOffset = remember { androidx.compose.animation.core.Animatable(0f) }

    // Start the shake animation when the health changes
    LaunchedEffect(currentHealth) {
        if (currentHealth < maxHealth) {
            shakeOffset.animateTo(
                targetValue = 20f,
                animationSpec = keyframes {
                    durationMillis = 200
                    0.0f at 0
                    5.0f at 50
                    -5.0f at 100
                    0.0f at 150
                    5.0f at 200
                    -5.0f at 250
                    0.0f at 300
                }
            )

            // Reset the shake offset after the animation
            shakeOffset.animateTo(0f, animationSpec = tween(1))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
            .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
    ) {
        // Apply the shake animation to the health bar
        Box(
            modifier = Modifier
                .fillMaxWidth(healthPercentage)
                .fillMaxHeight()
                .offset(y = shakeOffset.value.dp)
                .background(barColor, RoundedCornerShape(10.dp))
        )
    }
}

@Composable
fun BattleDialogText(
    isPlayerAttack: Boolean,
    critterName: String,
    selectedAttack: Attack?,
    battleText: String,
    onAttackClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clickable { onAttackClicked() } // Handle click to execute attack
    ) {
        Text(
            text = if (isPlayerAttack) {
                battleText.ifBlank {
                    "$critterName attacks with ${selectedAttack?.name}!"
                }
            } else {
                battleText.ifBlank {
                    "$critterName is attacking with ${selectedAttack?.name}! Click to continue."
                }
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

@Composable
fun PlayerCritterInfoText(critter: CritterUsable) {
    Box(
        modifier = Modifier
            .background(Color.Gray, RoundedCornerShape(4.dp))
            .padding(2.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(6.dp)
                .background(
                    Color.Green,
                    RoundedCornerShape(4.dp)
                )  // Optional: Add a background to the text itself
                .padding(6.dp),  // Optional: Add padding to the text itself
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Black)) {
                    append(critter.name + " LVL: ${critter.level} HP: ${critter.hp}")
                }
            },
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun CpuCritterInfoText(critter: CritterUsable) {
    Box(
        modifier = Modifier
            .background(Color.Gray, RoundedCornerShape(4.dp))
            .padding(2.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(6.dp)
                .background(
                    Color.Red,
                    RoundedCornerShape(4.dp)
                )  // Optional: Add a background to the text itself
                .padding(6.dp),  // Optional: Add padding to the text itself
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Black)) {
                    append(critter.name + " LVL: ${critter.level} HP: ${critter.hp}")
                }
            },
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
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