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
import android.content.Intent
import project.main.uniclash.battle.BattleResult
import project.main.uniclash.datatypes.Attack
import project.main.uniclash.datatypes.AttackType
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.type.TypeCalculation
import project.main.uniclash.viewmodels.BattleTutorialAdvancedViewModel

class BattleTutorialAdvancedActivity : ComponentActivity() {

    private var repeatRequest by mutableStateOf(false)
    private var progressRequest by mutableStateOf(false)
    //TODO Rename into BattleActivity
    private val battleTutorialAdvancedViewModel by viewModels<BattleTutorialAdvancedViewModel> {
        BattleTutorialAdvancedViewModel.provideFactory(CritterService.getInstance(this))
    }

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
                        var battleResult = battleTutorialAdvancedViewModel.checkResult()
                        if (battleResult.equals(BattleResult.PLAYER_WINS)){
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
                        if (battleResult.equals(BattleResult.CPU_WINS)){
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
                            CritterBattleAdvancedTutorialIntro(battleTutorialAdvancedViewModel)
                        }
                    }

                }

            }
            if(repeatRequest){
                val intent = Intent(this, this::class.java)
                this.startActivity(intent)
            }
            if(progressRequest){
                val intent = Intent(this, FinalBattleActivity::class.java)
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
fun CritterBattleAdvancedTutorialIntro(battleTutorialAdvancedViewModel: BattleTutorialAdvancedViewModel = viewModel()) {
    val battleViewPlayerUIState by battleTutorialAdvancedViewModel.playerCritter.collectAsState()
    val battleViewcpuCritterUIState by battleTutorialAdvancedViewModel.cpuCritter.collectAsState()
    val battleText by battleTutorialAdvancedViewModel.battleText.collectAsState()
    val playerInputUIState by battleTutorialAdvancedViewModel.playerInput.collectAsState()
    val cpuInputUIState by battleTutorialAdvancedViewModel.cpuInput.collectAsState()
    val currentTutorialStep by battleTutorialAdvancedViewModel.tutorialDialogStep.collectAsState()
    val tutorialDialogMessage = battleTutorialAdvancedViewModel.getTutorialMessage(currentTutorialStep)
    val isPlayerTurn by battleTutorialAdvancedViewModel.isPlayerTurn.collectAsState()
    val playerWon by battleTutorialAdvancedViewModel.playerWon.collectAsState()

    val playerMaxHealth by remember {
        mutableStateOf(battleViewPlayerUIState.playerCritter?.hp ?: 0)
    }

    val cpuMaxHealth by remember {
        mutableStateOf(battleViewcpuCritterUIState.cpuCritter?.hp ?: 0)
    }
    if (playerWon == null) {
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
                            .size(100.dp)
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
                    CpuCritterAdvancedTutorialInfoText(battleViewcpuCritterUIState.cpuCritter!!)
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
                            .size(100.dp)
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
                    PlayerCritterAdvancedTutorialInfoText(battleViewPlayerUIState.playerCritter!!)
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
                if (isPlayerTurn) {
                    item {
                        // First row with two attacks side by side
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(2.dp),
                        ) {
                            items(2) {
                                ClickableAttackAdvancedTutorial(
                                    attack = battleViewPlayerUIState.playerCritter!!.attacks[it],
                                    enemyCritterType = battleViewcpuCritterUIState.cpuCritter!!.type,
                                    playerCritterType = battleViewPlayerUIState!!.playerCritter!!.type,
                                    onAttackClicked = { selectedAttack ->
                                        battleTutorialAdvancedViewModel.selectPlayerAttack(
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
                                    enemyCritterType = battleViewcpuCritterUIState.cpuCritter!!.type,
                                    playerCritterType = battleViewPlayerUIState!!.playerCritter!!.type,
                                    onAttackClicked = { selectedAttack ->
                                        battleTutorialAdvancedViewModel.selectPlayerAttack(
                                            selectedAttack
                                        )
                                    },
                                    isClickable = true // Make buttons not clickable during the introduction
                                )
                            }
                        }
                    }
                }else {
                    item {
                        // First row with two attacks side by side
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(2.dp),
                        ) {
                            items(2) {
                                ClickableAttackAdvancedTutorial(
                                    attack = battleViewPlayerUIState.playerCritter!!.attacks[it],
                                    enemyCritterType = battleViewcpuCritterUIState.cpuCritter!!.type,
                                    playerCritterType = battleViewPlayerUIState!!.playerCritter!!.type,
                                    onAttackClicked = { selectedAttack ->
                                        battleTutorialAdvancedViewModel.selectPlayerAttack(
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
                                    enemyCritterType = battleViewcpuCritterUIState.cpuCritter!!.type,
                                    playerCritterType = battleViewPlayerUIState!!.playerCritter!!.type,
                                    onAttackClicked = { selectedAttack ->
                                        battleTutorialAdvancedViewModel.selectPlayerAttack(
                                            selectedAttack
                                        )
                                    },
                                    isClickable = false // Make buttons not clickable during the introduction
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
                            battleTutorialAdvancedViewModel.executePlayerAttack()
                        }
                        if (!playerInputUIState.isPlayerAttackSelected && !cpuInputUIState.isCpuAttackSelected && !isPlayerTurn) {
                            battleTutorialAdvancedViewModel.selectCpuAttack()
                        }
                        if (cpuInputUIState.isCpuAttackSelected && !isPlayerTurn) {
                            battleTutorialAdvancedViewModel.executeCpuAttack()
                        }
                    }
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
                        battleTutorialAdvancedViewModel.nextTutorialStep()
                    } // Handle click to execute attack
            ) {
                Text(
                    text = battleTutorialAdvancedViewModel.getTutorialMessage(currentTutorialStep),
                    modifier = Modifier
                        .padding(8.dp)
                        .background(
                            color = Color(0xFFFFEBCD),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(8.dp),
                    fontFamily = FontFamily.Default, // Replace with your custom font
                    fontSize = 13.sp,
                    color = Color.Black
                )
            }

        }

    }else if(playerWon==true){
        Text("YOU WON!")
    }else if(playerWon==false){
        Text("YOU LOST!")
    }
}



@Composable
fun PlayerCritterAdvancedTutorialInfoText(critter: CritterUsable) {
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
                    fontSize = 14.sp,
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
fun CpuCritterAdvancedTutorialInfoText(critter: CritterUsable) {
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
                fontSize = 14.sp,
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
    enemyCritterType: String,
    playerCritterType:String,
    onAttackClicked: (Attack) -> Unit,
    isClickable: Boolean = true // Added a parameter to control clickability
) {
    val swordDrawable: Int? = R.drawable.attackbuffsymbol
    val shieldDrawable: Int? =  R.drawable.defencebuffsymbol
    val redSwordDrawable: Int? = R.drawable.attackdebuffsymbol
    val redShieldDrawable: Int? = R.drawable.defencedebuffsymbol
    val attackDrawable: Int? = R.drawable.pow

    val drawable = when (attack.attackType) {
        AttackType.ATK_BUFF -> swordDrawable
        AttackType.DEF_BUFF -> shieldDrawable
        AttackType.ATK_DEBUFF -> redSwordDrawable
        AttackType.DEF_DEBUFF -> redShieldDrawable
        AttackType.DAMAGE_DEALER -> attackDrawable
        else -> attackDrawable // Handle other cases or leave it null
    }

    var typeResourceId = when (attack.typeId) {
        "DRAGON" -> {R.drawable.dragon}
        "WATER" -> {R.drawable.water}
        "ELECTRIC" -> {R.drawable.electric}
        "FIRE" -> {R.drawable.fire}
        "STONE" -> {R.drawable.stone}
        "ICE" -> {R.drawable.ice}
        else -> {R.drawable.normal}
    }
    var typeCalculation = TypeCalculation()
    Box(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth(0.5f)
            .height(50.dp)
            .background(
                color = if (isClickable) Color.Blue else Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                if (isClickable) {
                    onAttackClicked(attack)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (drawable != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Image(
                    painter = painterResource(id = drawable),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).background(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(2.dp))

                Image(
                    painter = painterResource(id = typeResourceId),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).background(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(text = "${attack.name}: ${attack.strength}\n${typeCalculation.howEffective(attack.typeId,enemyCritterType, playerCritterType == attack.typeId )}", color = Color.White)
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}







