package project.main.uniclash

import android.annotation.SuppressLint
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
import androidx.compose.ui.unit.dp
import project.main.uniclash.ui.theme.UniClashTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import project.main.uniclash.battle.BattleResult
import project.main.uniclash.datatypes.Attack
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.viewmodels.AdvancedForcedTutorialStep
import project.main.uniclash.viewmodels.AdvancedTutorialStep
import project.main.uniclash.viewmodels.BattleForcedAdvancedTutorialViewModel
import project.main.uniclash.viewmodels.BattleForcedTutorialViewModel
import project.main.uniclash.viewmodels.BattleTutorialViewModel
import project.main.uniclash.viewmodels.TutorialStep

class BattleForcedTutorialAdvancedActivity : ComponentActivity() {

    private var exitRequest by mutableStateOf(false)
    //TODO Rename into BattleActivity
    private val battleForcedAdvancedTutorialViewModel by viewModels<BattleForcedAdvancedTutorialViewModel> {
        BattleForcedAdvancedTutorialViewModel.provideFactory(CritterService.getInstance(this))
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
                    val battleViewPlayerUIState by battleForcedAdvancedTutorialViewModel.playerCritter.collectAsState()
                    var playerCritter = battleViewPlayerUIState.playerCritter
                    val battleViewcpuCritterUIState by battleForcedAdvancedTutorialViewModel.cpuCritter.collectAsState()
                    var cpuCritter = battleViewcpuCritterUIState.cpuCritter
                    Column {
                        if (battleForcedAdvancedTutorialViewModel.checkResult() == BattleResult.PLAYER_WINS || battleForcedAdvancedTutorialViewModel.checkResult() == BattleResult.CPU_WINS){
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
                            CritterBattleForcedTutorialAdvancedIntro(battleForcedAdvancedTutorialViewModel)
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
fun CritterBattleForcedTutorialAdvancedIntro(battleForcedAdvancedTutorialViewModel: BattleForcedAdvancedTutorialViewModel = viewModel()) {
    val battleViewPlayerUIState by battleForcedAdvancedTutorialViewModel.playerCritter.collectAsState()
    val battleViewcpuCritterUIState by battleForcedAdvancedTutorialViewModel.cpuCritter.collectAsState()
    val battleText by battleForcedAdvancedTutorialViewModel.battleText.collectAsState()
    val playerInputUIState by battleForcedAdvancedTutorialViewModel.playerInput.collectAsState()
    val cpuInputUIState by battleForcedAdvancedTutorialViewModel.cpuInput.collectAsState()
    val currentTutorialStep by battleForcedAdvancedTutorialViewModel.forcedTutorialDialogStep.collectAsState()
    val tutorialDialogMessage = battleForcedAdvancedTutorialViewModel.getTutorialMessage(currentTutorialStep)
    val isPlayerTurn by battleForcedAdvancedTutorialViewModel.isPlayerTurn.collectAsState()
    val playerWon by battleForcedAdvancedTutorialViewModel.playerWon.collectAsState()



    val playerMaxHealth by remember {
        mutableStateOf(battleViewPlayerUIState.playerCritter?.hp ?: 0)
    }

    val cpuMaxHealth by remember {
        mutableStateOf(battleViewcpuCritterUIState.cpuCritter?.hp ?: 0)
    }
    if(playerWon==null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.Gray.copy(alpha = 0.2f), // Adjust the alpha value for transparency
                        shape = RoundedCornerShape(8.dp) // Optional: Add rounded corners
                    )
            ) {
                // Image for CPU Critter
                val context = LocalContext.current
                val name: String = battleViewcpuCritterUIState.cpuCritter!!.name.lowercase()
                val resourceId =
                    context.resources.getIdentifier(name, "drawable", context.packageName)
                if (resourceId != 0) {
                    val picture = painterResource(id = resourceId)
                    Image(
                        painter = picture,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
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
                    CpuCritterForcedTutorialAdvancedInfoText(
                        battleViewcpuCritterUIState.cpuCritter!!,
                        battleForcedAdvancedTutorialViewModel = viewModel()
                    )
                }
            }


            Spacer(modifier = Modifier.height(5.dp)) // Add vertical space

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.Gray.copy(alpha = 0.2f), // Adjust the alpha value for transparency
                        shape = RoundedCornerShape(8.dp) // Optional: Add rounded corners
                    )
            ) {
                // Image for Player Critter
                val context = LocalContext.current
                val playerName: String = battleViewPlayerUIState.playerCritter!!.name.lowercase()
                val playerResourceId =
                    context.resources.getIdentifier(playerName, "drawable", context.packageName)
                if (playerResourceId != 0) {
                    val playerPicture = painterResource(id = playerResourceId)
                    Image(
                        painter = playerPicture,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
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
                    PlayerCritterForcedTutorialAdvancedInfoText(
                        battleViewPlayerUIState.playerCritter!!,
                        battleForcedAdvancedTutorialViewModel = viewModel()
                    )
                }
            }


            Spacer(modifier = Modifier.height(6.dp)) // Add more vertical space
            AttackSelectionAdvancedTutorial(battleForcedAdvancedTutorialViewModel = viewModel())
            AttackBoxAdvancedTutorial(battleForcedAdvancedTutorialViewModel = viewModel())
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp)
                    .clickable {
                        battleForcedAdvancedTutorialViewModel.nextTutorialStep()
                    } // Handle click to execute attack
            ) {
                Text(
                    text = battleForcedAdvancedTutorialViewModel.getTutorialMessage(currentTutorialStep),
                    modifier = Modifier
                        .padding(10.dp)
                        .background(
                            color = Color(0xFF800080),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(10.dp),
                    fontFamily = FontFamily.Default, // Replace with your custom font
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    }
    else if(playerWon==true){
        Text("YOU WON!")
    }else if(playerWon==false){
        Text("YOU LOST!")
    }
}

@Composable
fun CpuCritterForcedTutorialAdvancedInfoText(critter: CritterUsable, battleForcedAdvancedTutorialViewModel: BattleForcedAdvancedTutorialViewModel) {

    Box(
        modifier = Modifier
            .background(Color.LightGray, RoundedCornerShape(4.dp))
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = critter.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color.Black)) {
                                    append("LVL: ${critter.level} HP: ${critter.hp}")
                                }
                            },
                            fontSize = 18.sp,
                            color = Color.White
                        )
            }
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.Black)) {
                        append("ATK: ${critter.atk} DEF: ${critter.def} SPD: ${critter.spd}")
                    }
                },
                fontSize = 10.sp,
                color = Color.White
            )

        }

    }
}


@Composable
fun PlayerCritterForcedTutorialAdvancedInfoText(critter: CritterUsable, battleForcedAdvancedTutorialViewModel: BattleForcedAdvancedTutorialViewModel) {
    Box(
        modifier = Modifier
            .background(Color.LightGray, RoundedCornerShape(4.dp))
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = critter.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color.Black)) {
                                    append("LVL: ${critter.level} HP: ${critter.hp}")
                                }
                            },
                            fontSize = 18.sp,
                            color = Color.White
                        )
            }
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Black)) {
                                append("ATK: ${critter.atk} DEF: ${critter.def} SPD: ${critter.spd}")
                            }
                        },
                        fontSize = 10.sp,
                        color = Color.White
                    )


        }
    }
}

@Composable
fun AttackSelectionAdvancedTutorial(
    battleForcedAdvancedTutorialViewModel: BattleForcedAdvancedTutorialViewModel
) {
    val battleViewPlayerUIState by battleForcedAdvancedTutorialViewModel.playerCritter.collectAsState()
    val isPlayerTurn by battleForcedAdvancedTutorialViewModel.isPlayerTurn.collectAsState()
    val playerInputUIState by battleForcedAdvancedTutorialViewModel.playerInput.collectAsState()
    when (battleForcedAdvancedTutorialViewModel.advancedForcedTutorialStep) {
        AdvancedForcedTutorialStep.SelectAttack, AdvancedForcedTutorialStep.SelectAttackBuff, AdvancedForcedTutorialStep.SelectDefenseDebuff,AdvancedForcedTutorialStep.SelectDefenseDebuff2, AdvancedForcedTutorialStep.LetplayerPlay ->
            if (isPlayerTurn && !playerInputUIState.isPlayerAttackSelected) {
                AttackRowAdvancedTutorial(battleForcedAdvancedTutorialViewModel, true)
                AttackRowAdvancedTutorial(battleForcedAdvancedTutorialViewModel, true, offset = 2)
            } else {
                AttackRowAdvancedTutorial(battleForcedAdvancedTutorialViewModel, false)
                AttackRowAdvancedTutorial(battleForcedAdvancedTutorialViewModel, false, offset = 2)
            }

        else -> {
            AttackRowAdvancedTutorial(battleForcedAdvancedTutorialViewModel, false)
            AttackRowAdvancedTutorial(battleForcedAdvancedTutorialViewModel, false, offset = 2)
        }
    }
}


@Composable
fun AttackRowAdvancedTutorial(
    battleForcedAdvancedTutorialViewModel: BattleForcedAdvancedTutorialViewModel,
    isClickable: Boolean,
    offset: Int = 0
) {
    val battleViewPlayerUIState by battleForcedAdvancedTutorialViewModel.playerCritter.collectAsState()
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(2.dp),
    ) {
        items(2) {
            val attackIndex = it + offset
            val attack = battleViewPlayerUIState.playerCritter?.attacks?.get(attackIndex)
            if (attack != null) {
                when (battleForcedAdvancedTutorialViewModel.advancedForcedTutorialStep) {
                    AdvancedForcedTutorialStep.SelectAttackBuff -> {
                        if(attack.name!="Beak Sharpener"){
                            ClickableAttackAdvancedTutorial(
                                attack = attack,
                                onAttackClicked = { selectedAttack ->
                                    battleForcedAdvancedTutorialViewModel.selectPlayerAttack(selectedAttack)
                                },
                                isClickable = false
                            )
                        } else {
                            ClickableAttackAdvancedTutorial(
                                attack = attack,
                                onAttackClicked = { selectedAttack ->
                                    battleForcedAdvancedTutorialViewModel.selectPlayerAttack(selectedAttack)
                                },
                                isClickable = isClickable
                            )
                        }
                    }
                    AdvancedForcedTutorialStep.SelectDefenseDebuff, AdvancedForcedTutorialStep.SelectDefenseDebuff2 -> {
                        if(attack.name!="Defence Break"){
                            ClickableAttackAdvancedTutorial(
                                attack = attack,
                                onAttackClicked = { selectedAttack ->
                                    battleForcedAdvancedTutorialViewModel.selectPlayerAttack(selectedAttack)
                                },
                                isClickable = false
                            )
                        } else {
                            ClickableAttackAdvancedTutorial(
                                attack = attack,
                                onAttackClicked = { selectedAttack ->
                                    battleForcedAdvancedTutorialViewModel.selectPlayerAttack(selectedAttack)
                                },
                                isClickable = isClickable
                            )
                        }
                    }
                    AdvancedForcedTutorialStep.SelectAttack-> {
                        if(attack.name!="HyperBeam"){
                            ClickableAttackAdvancedTutorial(
                                attack = attack,
                                onAttackClicked = { selectedAttack ->
                                    battleForcedAdvancedTutorialViewModel.selectPlayerAttack(selectedAttack)
                                },
                                isClickable = false
                            )
                        } else {
                            ClickableAttackAdvancedTutorial(
                                attack = attack,
                                onAttackClicked = { selectedAttack ->
                                    battleForcedAdvancedTutorialViewModel.selectPlayerAttack(selectedAttack)
                                },
                                isClickable = isClickable
                            )
                        }
                    }
                    else -> {
                        ClickableAttackAdvancedTutorial(
                            attack = attack,
                            onAttackClicked = { selectedAttack ->
                                battleForcedAdvancedTutorialViewModel.selectPlayerAttack(selectedAttack)
                            },
                            isClickable = isClickable
                        )
                    }
                }

            }
        }
    }
}


@Composable
fun AttackBoxAdvancedTutorial(
    battleForcedAdvancedTutorialViewModel: BattleForcedAdvancedTutorialViewModel,
) {
    val playerInputUIState by battleForcedAdvancedTutorialViewModel.playerInput.collectAsState()
    val isPlayerTurn by battleForcedAdvancedTutorialViewModel.isPlayerTurn.collectAsState()
    val cpuInputUIState by battleForcedAdvancedTutorialViewModel.cpuInput.collectAsState()
    val battleText by battleForcedAdvancedTutorialViewModel.battleText.collectAsState()

    val handleAttackClick: () -> Unit = {
        when (battleForcedAdvancedTutorialViewModel.advancedForcedTutorialStep) {
            AdvancedForcedTutorialStep.ExecuteAttackBuff,AdvancedForcedTutorialStep.ExecuteDefenseDebuff,AdvancedForcedTutorialStep.ExecuteDefenseDebuff2 -> {
                if (playerInputUIState.isPlayerAttackSelected && isPlayerTurn) {
                    battleForcedAdvancedTutorialViewModel.executePlayerAttack()
                }

            }
            AdvancedForcedTutorialStep.DefenseDebuffResult,AdvancedForcedTutorialStep.DefenseDebuffResult2,AdvancedForcedTutorialStep.AttackBuffResult ->{
                if (!playerInputUIState.isPlayerAttackSelected && !cpuInputUIState.isCpuAttackSelected && !isPlayerTurn) {
                    battleForcedAdvancedTutorialViewModel.selectCpuAttack()
                }
                if (cpuInputUIState.isCpuAttackSelected && !isPlayerTurn) {
                    battleForcedAdvancedTutorialViewModel.executeCpuAttack()
                }
            }
            AdvancedForcedTutorialStep.SelectAttackBuff,AdvancedForcedTutorialStep.SelectAttack,AdvancedForcedTutorialStep.SelectDefenseDebuff,AdvancedForcedTutorialStep.SelectDefenseDebuff2 ->{
                if (!playerInputUIState.isPlayerAttackSelected && !cpuInputUIState.isCpuAttackSelected && !isPlayerTurn) {
                    battleForcedAdvancedTutorialViewModel.selectCpuAttack()
                }
                if (cpuInputUIState.isCpuAttackSelected && !isPlayerTurn) {
                    battleForcedAdvancedTutorialViewModel.executeCpuAttack()
                }

            }
            AdvancedForcedTutorialStep.LetplayerPlay -> {
                if (playerInputUIState.isPlayerAttackSelected && isPlayerTurn) {
                    battleForcedAdvancedTutorialViewModel.executePlayerAttack()
                }
                if (!playerInputUIState.isPlayerAttackSelected && !cpuInputUIState.isCpuAttackSelected && !isPlayerTurn) {
                    battleForcedAdvancedTutorialViewModel.selectCpuAttack()
                }
                if (cpuInputUIState.isCpuAttackSelected && !isPlayerTurn) {
                    battleForcedAdvancedTutorialViewModel.executeCpuAttack()
                }
            }

            else -> {

            }

        }

    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .clickable {
                handleAttackClick()
            }
        // Handle click to execute attack
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(8.dp)
                .background(
                    color = Color.Gray,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = battleText,
                fontFamily = FontFamily.Default, // Replace with your custom font
                fontSize = 16.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                painter = painterResource(id = R.drawable.arrow), // Replace with your arrow icon
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier
                    .graphicsLayer(rotationX = 180f) // Flip the icon upside down
                    .size(8.dp)
            )
        }
    }
}








