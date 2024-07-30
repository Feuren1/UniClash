package project.main.uniclash

import android.annotation.SuppressLint
import android.content.Intent
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
import project.main.uniclash.viewmodels.BattleForcedTutorialViewModel
import project.main.uniclash.viewmodels.BattleTutorialViewModel
import project.main.uniclash.viewmodels.ForcedTutorialStep
import project.main.uniclash.viewmodels.TutorialStep

class BattleTutorialActivity : ComponentActivity() {

    private var repeatRequest by mutableStateOf(false)
    private var progressRequest by mutableStateOf(false)
    //TODO Rename into BattleActivity
    private val battleTutorialViewModel by viewModels<BattleTutorialViewModel> {
        BattleTutorialViewModel.provideFactory(CritterService.getInstance(this))
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
                    val battleViewPlayerUIState by battleTutorialViewModel.playerCritter.collectAsState()
                    var playerCritter = battleViewPlayerUIState.playerCritter
                    val battleViewcpuCritterUIState by battleTutorialViewModel.cpuCritter.collectAsState()
                    var cpuCritter = battleViewcpuCritterUIState.cpuCritter
                    Column {
                        var battleResult = battleTutorialViewModel.checkResult()
                        if (battleResult == BattleResult.PLAYER_WINS){
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
                                            progressRequest = true
                                        }
                                        .align(Alignment.TopEnd)
                                )
                            }
                        }
                        if (battleResult == BattleResult.CPU_WINS){
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(16.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.repeat),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable {
                                            repeatRequest = true
                                        }
                                        .align(Alignment.TopEnd)
                                )
                            }
                        }
                        Box {
                                    CritterBattleTutorialIntro(battleTutorialViewModel)
                        }
                    }

                }

            }
            if(repeatRequest){
                val intent = Intent(this, this::class.java)
                this.startActivity(intent)
            }
            if(progressRequest){
                val intent = Intent(this, BattleTutorialAdvancedActivity::class.java)
                this.startActivity(intent)
            }
        }

    }
    override fun onDestroy() {
        super.onDestroy()
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun CritterBattleTutorialIntro(battleTutorialViewModel: BattleTutorialViewModel = viewModel()) {
    val battleViewPlayerUIState by battleTutorialViewModel.playerCritter.collectAsState()
    val battleViewcpuCritterUIState by battleTutorialViewModel.cpuCritter.collectAsState()
    val battleText by battleTutorialViewModel.battleText.collectAsState()
    val playerInputUIState by battleTutorialViewModel.playerInput.collectAsState()
    val cpuInputUIState by battleTutorialViewModel.cpuInput.collectAsState()
    val currentTutorialStep by battleTutorialViewModel.tutorialDialogStep.collectAsState()
    val tutorialDialogMessage = battleTutorialViewModel.getTutorialMessage(currentTutorialStep)
    val isPlayerTurn by battleTutorialViewModel.isPlayerTurn.collectAsState()
    val playerWon by battleTutorialViewModel.playerWon.collectAsState()

    val playerMaxHealth by remember {
        mutableStateOf(battleViewPlayerUIState.playerCritter?.hp ?: 0)
    }

    val cpuMaxHealth by remember {
        mutableStateOf(battleViewcpuCritterUIState.cpuCritter?.hp ?: 0)
    }
    if(playerWon==null){
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
            val resourceId = context.resources.getIdentifier(name, "drawable", context.packageName)
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
                CpuCritterTutorialInfoText(battleViewcpuCritterUIState.cpuCritter!!,battleTutorialViewModel)
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
                PlayerCritterTutorialInfoText(battleViewPlayerUIState.playerCritter!!, battleTutorialViewModel)
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

            if (!isPlayerTurn) {
                item {
                    // First row with two attacks side by side
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(2.dp),
                    ) {
                        items(2) {
                            ClickableAttackAdvancedTutorial(
                                attack = battleViewPlayerUIState.playerCritter!!.attacks[it],
                                enemyCritterType = "NORMAL",
                                playerCritterType = battleViewPlayerUIState!!.playerCritter!!.type,
                                onAttackClicked = { selectedAttack ->
                                    battleTutorialViewModel.selectPlayerAttack(
                                        selectedAttack
                                    )
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
                        contentPadding = PaddingValues(2.dp)
                    ) {
                        items(2) {
                            ClickableAttackAdvancedTutorial(
                                attack = battleViewPlayerUIState.playerCritter!!.attacks[it + 2],
                                enemyCritterType = "NORMAL",
                                playerCritterType = battleViewPlayerUIState!!.playerCritter!!.type,
                                onAttackClicked = { selectedAttack ->
                                    battleTutorialViewModel.selectPlayerAttack(
                                        selectedAttack
                                    )
                                },
                                isClickable = false // Make buttons not clickable during the introduction
                            )
                        }
                    }
                }
            } else {
                item {
                    // First row with two attacks side by side
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(2.dp),
                    ) {
                        items(2) {
                            ClickableAttackAdvancedTutorial(
                                attack = battleViewPlayerUIState.playerCritter!!.attacks[it],
                                enemyCritterType = "NORMAL",
                                playerCritterType = battleViewPlayerUIState!!.playerCritter!!.type,
                                onAttackClicked = { selectedAttack ->
                                    battleTutorialViewModel.selectPlayerAttack(
                                        selectedAttack
                                    )
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
                        contentPadding = PaddingValues(2.dp)
                    ) {
                        items(2) {
                            ClickableAttackAdvancedTutorial(
                                attack = battleViewPlayerUIState.playerCritter!!.attacks[it + 2],
                                enemyCritterType = "NORMAL",
                                playerCritterType = battleViewPlayerUIState!!.playerCritter!!.type,
                                onAttackClicked = { selectedAttack ->
                                    battleTutorialViewModel.selectPlayerAttack(
                                        selectedAttack
                                    )
                                },
                                isClickable = true // Make buttons not clickable during the introduction
                            )
                        }
                    }
                }
            }
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
                .clickable {
                    if (playerInputUIState.isPlayerAttackSelected && isPlayerTurn) {
                        battleTutorialViewModel.executePlayerAttack()
                    }
                    if (!playerInputUIState.isPlayerAttackSelected && !cpuInputUIState.isCpuAttackSelected && !isPlayerTurn) {
                        battleTutorialViewModel.selectCpuAttack()
                    }
                    if (cpuInputUIState.isCpuAttackSelected && !isPlayerTurn) {
                        battleTutorialViewModel.executeCpuAttack()
                    }
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


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)
                .clickable {
                    battleTutorialViewModel.nextTutorialStep()
                } // Handle click to execute attack
        ) {
            Text(
                text = battleTutorialViewModel.getTutorialMessage(currentTutorialStep),
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
fun CpuCritterTutorialInfoText(critter: CritterUsable, battleTutorialViewModel: BattleTutorialViewModel) {

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
                when (battleTutorialViewModel.tutorialStep) {
                    TutorialStep.CpuHP ->
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color.Black)) {
                                    append("LVL: ")
                                }
                                withStyle(style = SpanStyle(color = Color.Black)) {
                                    append("${critter.level}")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp,
                                        color = Color.Black
                                    )
                                ) {
                                    append(" HP: ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp,
                                        color = Color.Black
                                    )
                                ) {
                                    append("${critter.hp}")
                                }
                            },
                            fontSize = 18.sp,
                            color = Color.White
                        )

                    else -> {
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
                }

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
fun PlayerCritterTutorialInfoText(critter: CritterUsable, battleTutorialViewModel: BattleTutorialViewModel) {
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
                when (battleTutorialViewModel.tutorialStep) {
                    TutorialStep.PlayerHP ->
                        Text( text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Black)) {
                                append("LVL: ")
                            }
                            withStyle(style = SpanStyle(color = Color.Black)) {
                                append("${critter.level}")
                            }
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.Black)) {
                                append(" HP: ")
                            }
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.Black)) {
                                append("${critter.hp}")
                            }
                        },
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    TutorialStep.Level ->
                        Text( text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.Black)) {
                                append("LVL: ")
                            }
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.Black)) {
                                append("${critter.level}")
                            }
                            withStyle(style = SpanStyle(color = Color.Black)) {
                                append(" HP: ")
                            }
                            withStyle(style = SpanStyle(color = Color.Black)) {
                                append("${critter.hp}")
                            }
                        },
                            fontSize = 18.sp,
                            color = Color.White
                        )

                    else -> {
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
                }

            }
            when (battleTutorialViewModel.tutorialStep){
                TutorialStep.Stats ->
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Black)) {
                                append("ATK: ${critter.atk} DEF: ${critter.def} SPD: ${critter.spd}")
                            }
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                else -> {
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
    }
}


@Composable
fun ClickableAttackTutorial(
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





