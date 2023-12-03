package project.main.uniclash

import android.annotation.SuppressLint
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import project.main.uniclash.battle.BattleResult
import project.main.uniclash.datatypes.Attack
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.viewmodels.AdvancedTutorialStep
import project.main.uniclash.viewmodels.BattleTutorialAdvancedViewModel

class BattleTutorialAdvancedActivity : ComponentActivity() {

    private var exitRequest by mutableStateOf(false)
    //TODO Rename into BattleActivity
    private val battleTutorialAdvancedViewModel by viewModels<BattleTutorialAdvancedViewModel> {
        BattleTutorialAdvancedViewModel.provideFactory(CritterService.getInstance(this))
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initializes a viewmodel for further use. Uses the critterservice in order to talk to the backend
        setContent {
            UniClashTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //creates the pokemongame composable (name should be critterBattle)
                    val battleViewPlayerUIState by battleTutorialAdvancedViewModel.playerCritter.collectAsState()
                    var playerCritter = battleViewPlayerUIState.playerCritter
                    val battleViewcpuCritterUIState by battleTutorialAdvancedViewModel.cpuCritter.collectAsState()
                    var cpuCritter = battleViewcpuCritterUIState.cpuCritter
                    Column {
                        if (battleTutorialAdvancedViewModel.checkResult() == BattleResult.PLAYER_WINS || battleTutorialAdvancedViewModel.checkResult() == BattleResult.CPU_WINS){
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
                        }
                        Box {
                            CritterBattleAdvancedTutorialIntro(battleTutorialAdvancedViewModel)
                        }
                    }

                }

            }

        }

    }
    override fun onDestroy() {
        super.onDestroy()
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun CritterBattleAdvancedTutorialIntro(battleTutorialAdvancedViewModel: BattleTutorialAdvancedViewModel = viewModel()) {
    val battleViewPlayerUIState by battleTutorialAdvancedViewModel.playerCritter.collectAsState()
    val battleViewcpuCritterUIState by battleTutorialAdvancedViewModel.cpuCritter.collectAsState()
    val battleText by battleTutorialAdvancedViewModel.battleText.collectAsState()
    val playerInputUIState by battleTutorialAdvancedViewModel.playerInput.collectAsState()
    val cpuInputUIState by battleTutorialAdvancedViewModel.cpuInput.collectAsState()
    val currentTutorialStep by battleTutorialAdvancedViewModel.tutorialDialogStep.collectAsState()
    val tutorialDialogMessage = battleTutorialAdvancedViewModel.getTutorialMessage(currentTutorialStep)

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
            // Image for CPU Critter
            val context = LocalContext.current
            val name: String = battleViewcpuCritterUIState.cpuCritter!!.name.lowercase()
            val resourceId = context.resources.getIdentifier(name, "drawable", context.packageName)
            if (resourceId != 0) {
                val picture = painterResource(id = resourceId)
                Image(
                    painter = picture,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                )
            } else {
                Text("Image not found for $name")
            }

            // Column with HealthBar and InfoText for CPU Critter
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            ) {
                HealthBar(
                    currentHealth = battleViewcpuCritterUIState.cpuCritter!!.hp,
                    maxHealth = cpuMaxHealth,
                    barColor = Color.Red
                )
                CpuCritterInfoText(battleViewcpuCritterUIState.cpuCritter!!)
            }
        }

        Spacer(modifier = Modifier.height(5.dp)) // Add vertical space

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Image for Player Critter
            val context = LocalContext.current
            val playerName: String = battleViewPlayerUIState.playerCritter!!.name.lowercase()
            val playerResourceId = context.resources.getIdentifier(playerName, "drawable", context.packageName)
            if (playerResourceId != 0) {
                val playerPicture = painterResource(id = playerResourceId)
                Image(
                    painter = playerPicture,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                )
            } else {
                Text("Image not found for $playerName")
            }

            // Column with HealthBar and InfoText for Player Critter
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            ) {
                HealthBar(
                    currentHealth = battleViewPlayerUIState.playerCritter!!.hp,
                    maxHealth = playerMaxHealth,
                    barColor = Color.Green
                )
                PlayerCritterInfoText(battleViewPlayerUIState.playerCritter!!)
            }
        }

        Spacer(modifier = Modifier.height(6.dp)) // Add more vertical space

        // ATTACKS:
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            when (battleTutorialAdvancedViewModel.advancedTutorialStep) {
                AdvancedTutorialStep.SelectAttack -> {
                    // WHEN TUTORIAL IS AT SELECT ATTACK:
                    item {
                        // First row with two attacks side by side
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(8.dp),
                        ) {
                            items(2) {
                                ClickableAttackAdvancedTutorial(
                                    attack = battleViewPlayerUIState.playerCritter!!.attacks[it],
                                    onAttackClicked = { selectedAttack ->
                                        battleTutorialAdvancedViewModel.selectPlayerAttack(selectedAttack)
                                    },
                                    isClickable = true // Make buttons not clickable during the introduction
                                )
                            }
                        }
                    }

                    item {
                        // Second row with two attacks side by side
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            items(2) {
                                ClickableAttackAdvancedTutorial(
                                    attack = battleViewPlayerUIState.playerCritter!!.attacks[it + 2],
                                    onAttackClicked = { selectedAttack ->
                                        battleTutorialAdvancedViewModel.selectPlayerAttack(selectedAttack)
                                    },
                                    isClickable = true // Make buttons not clickable during the introduction
                                )
                            }
                        }
                    }
                }
                // WHEN TUTORIAL LETS THE PLAYER PLAY
                AdvancedTutorialStep.LetPlayerPlay -> {
                    item {
                        // First row with two attacks side by side
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(8.dp),
                        ) {
                            items(2) {
                                ClickableAttackAdvancedTutorial(
                                    attack = battleViewPlayerUIState.playerCritter!!.attacks[it],
                                    onAttackClicked = { selectedAttack ->
                                        battleTutorialAdvancedViewModel.selectPlayerAttack(selectedAttack)
                                    },
                                    isClickable = true // Make buttons not clickable during the introduction
                                )
                            }
                        }
                    }
                    item {
                        // Second row with two attacks side by side
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            items(2) {
                                ClickableAttackAdvancedTutorial(
                                    attack = battleViewPlayerUIState.playerCritter!!.attacks[it + 2],
                                    onAttackClicked = { selectedAttack ->
                                        battleTutorialAdvancedViewModel.selectPlayerAttack(selectedAttack)
                                    },
                                    isClickable = true // Make buttons not clickable during the introduction
                                )
                            }
                        }
                    }
                }
                // Add other cases for different tutorial steps
                //TutorialStep.SelectAttack -> {

                //}
                // WHEN TUTORIAL IS AT ANY OTHER PART DISABLE ALL ATTACK BUTTONS:
                else -> {
                    item {
                        // First row with two attacks side by side
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(8.dp),
                        ) {
                            items(2) {
                                ClickableAttackAdvancedTutorial(
                                    attack = battleViewPlayerUIState.playerCritter!!.attacks[it],
                                    onAttackClicked = { selectedAttack ->
                                        battleTutorialAdvancedViewModel.selectPlayerAttack(selectedAttack)
                                    },
                                    isClickable = false // Make buttons not clickable during the introduction
                                )
                            }
                        }
                    }

                    item {
                        // Second row with two attacks side by side
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            items(2) {
                                ClickableAttackAdvancedTutorial(
                                    attack = battleViewPlayerUIState.playerCritter!!.attacks[it + 2],
                                    onAttackClicked = { selectedAttack ->
                                        battleTutorialAdvancedViewModel.selectPlayerAttack(selectedAttack)
                                    },
                                    isClickable = false // Make buttons not clickable during the introduction
                                )
                            }
                        }
                    }
                }
            }
        }
        when(battleTutorialAdvancedViewModel.advancedTutorialStep){
            AdvancedTutorialStep.LetPlayerPlay ->{
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                        .clickable {
                            if (playerInputUIState.isPlayerAttackSelected) {
                                battleTutorialAdvancedViewModel.executePlayerAttack()
                            }
                            if (cpuInputUIState.isCpuAttackSelected) {
                                battleTutorialAdvancedViewModel.executeCpuAttack()
                            }
                        }
                    // Handle click to execute attack
                ) {
                    Text(
                        text = if (playerInputUIState.isPlayerAttackSelected) {
                            "${battleViewPlayerUIState.playerCritter!!.name} attacks with ${playerInputUIState.selectedPlayerAttack!!.name}!"
                        } else if (cpuInputUIState.isCpuAttackSelected) {
                            "${battleViewcpuCritterUIState.cpuCritter!!.name} attacks with ${cpuInputUIState.selectedCpuAttack!!.name}!"
                        }
                        else {
                            battleTutorialAdvancedViewModel.battleText.value
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
            else -> {
                //Battle Dialog:
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                        .clickable {
                            when (battleTutorialAdvancedViewModel.advancedTutorialStep) {
                                AdvancedTutorialStep.ExecuteAttack -> {
                                    if (playerInputUIState.isPlayerAttackSelected) {
                                        battleTutorialAdvancedViewModel.executePlayerAttack()
                                    }
                                }

                                AdvancedTutorialStep.ExecuteCpuAttack -> {
                                    if (cpuInputUIState.isCpuAttackSelected) {
                                        battleTutorialAdvancedViewModel.executeCpuAttack()
                                    }
                                }

                                else -> {}
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
                            battleTutorialAdvancedViewModel.battleText.value
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


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)
                .clickable {
                    battleTutorialAdvancedViewModel.nextTutorialStep()
                } // Handle click to execute attack
        ) {
            Text(
                text = battleTutorialAdvancedViewModel.getTutorialMessage(currentTutorialStep),
                modifier = Modifier
                    .padding(16.dp)
                    .background(
                        color = Color(0xFF800080),
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
fun HealthBarAdvancedTutorial(
    currentHealth: Double,
    maxHealth: Int,
    barColor: Color
) {
    // Calculate health percentage
    val healthPercentage = (currentHealth / maxHealth.toFloat()).coerceIn(0.0, 1.0)

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
                .fillMaxWidth(healthPercentage.toFloat())
                .fillMaxHeight()
                .offset(y = shakeOffset.value.dp)
                .background(barColor, RoundedCornerShape(10.dp))
        )
    }
}


@Composable
fun ClickableAttackAdvancedTutorial(
    attack: Attack,
    onAttackClicked: (Attack) -> Unit,
    isClickable: Boolean = true // Added a parameter to control clickability
) {
    Box(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(0.5f)
            .height(40.dp)
            .background(
                color = if (isClickable) Color.Blue else Color.Gray, // Adjust background color based on clickability
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                if (isClickable) {
                    onAttackClicked(attack)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = attack.name + ": "+ attack.strength, color = Color.White)
    }
}





