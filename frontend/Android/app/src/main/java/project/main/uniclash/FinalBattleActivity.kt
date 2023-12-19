package project.main.uniclash

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build.VERSION.SDK_INT
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import project.main.uniclash.battle.BattleResult
import project.main.uniclash.datatypes.Attack
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.viewmodels.BattleForcedTutorialViewModel
import project.main.uniclash.viewmodels.BattleTutorialViewModel
import project.main.uniclash.viewmodels.FinalBattleViewModel
import project.main.uniclash.viewmodels.ForcedTutorialStep
import project.main.uniclash.viewmodels.TutorialStep

class FinalBattleActivity : ComponentActivity() {
    private var repeatRequest by mutableStateOf(false)
    private var progressRequest by mutableStateOf(false)
    private var exitRequest by mutableStateOf(false)
    private var mediaPlayer: MediaPlayer? = null
    //TODO Rename into BattleActivity
    private val finalBattleViewModel by viewModels<FinalBattleViewModel> {
        FinalBattleViewModel.provideFactory(CritterService.getInstance(this))
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    val battleViewPlayerUIState by finalBattleViewModel.playerCritter.collectAsState()
                    var playerCritter = battleViewPlayerUIState.playerCritter
                    val battleViewcpuCritterUIState by finalBattleViewModel.cpuCritter.collectAsState()
                    var cpuCritter = battleViewcpuCritterUIState.cpuCritter
                    Column {
                        var battleResult = finalBattleViewModel.checkResult()
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
                                            mediaPlayer?.release()
                                            progressRequest = true
                                        }
                                        .align(Alignment.TopEnd)
                                )
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
                            FinalBattle(finalBattleViewModel)
                        }
                    }

                }

            }
            if(repeatRequest){
                val intent = Intent(this, this::class.java)
                this.startActivity(intent)
            }
            if(progressRequest){
                val intent = Intent(this, MenuActivity::class.java)
                this.startActivity(intent)
            }
            if (exitRequest) {
                mediaPlayer?.release()
                val intent = Intent(this, MenuActivity::class.java)
                this.startActivity(intent)
                exitRequest = false
            }
        }

    }
    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FinalBattle(finalBattleViewModel:FinalBattleViewModel = viewModel()) {
    val battleViewPlayerUIState by finalBattleViewModel.playerCritter.collectAsState()
    val battleViewcpuCritterUIState by finalBattleViewModel.cpuCritter.collectAsState()
    val battleText by finalBattleViewModel.battleText.collectAsState()
    val playerInputUIState by finalBattleViewModel.playerInput.collectAsState()
    val cpuInputUIState by finalBattleViewModel.cpuInput.collectAsState()
    val isPlayerTurn by finalBattleViewModel.isPlayerTurn.collectAsState()
    val playerWon by finalBattleViewModel.playerWon.collectAsState()

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
                            .size(80.dp)
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
                    CpuCritterFinalBattleInfoText(battleViewcpuCritterUIState.cpuCritter!!,finalBattleViewModel)
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
                            .size(80.dp)
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
                    PlayerCritterFinalBattleInfoText(battleViewPlayerUIState.playerCritter!!, finalBattleViewModel)
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
                                    onAttackClicked = { selectedAttack ->
                                        finalBattleViewModel.selectPlayerAttack(
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
                                    onAttackClicked = { selectedAttack ->
                                        finalBattleViewModel.selectPlayerAttack(
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
                                    onAttackClicked = { selectedAttack ->
                                        finalBattleViewModel.selectPlayerAttack(
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
                                    onAttackClicked = { selectedAttack ->
                                        finalBattleViewModel.selectPlayerAttack(
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
                            finalBattleViewModel.executePlayerAttack()
                        }
                        if (!playerInputUIState.isPlayerAttackSelected && !cpuInputUIState.isCpuAttackSelected && !isPlayerTurn) {
                            finalBattleViewModel.selectCpuAttack()
                        }
                        if (cpuInputUIState.isCpuAttackSelected && !isPlayerTurn) {
                            finalBattleViewModel.executeCpuAttack()
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

            GifImage("vibe", Modifier.padding(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp)
                    .clickable {
                    } // Handle click to execute attack
            ) {
                Text(
                    text = "Remember what you learned about Stats, Buffs and Debuffs and beat the final Challenge! Try to keep your attack and defence high, while lowering the Linuxpenguin´s defence.",
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
    }
    else if (playerWon == true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "YOU WON!",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.Green
                )
            )
            GifImage(modifier = Modifier
                .fillMaxSize(0.8f)
                .padding(0.dp), gifName = "vibe")
            Text(
                text = "Please fill in your Post Experiment Questions and please be brutally honest :)\n" +
                        "Thank you very much for participating!",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            )
        }
    } else if (playerWon == false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "You Lost! Please try again :)",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.Red
                )
            )
            GifImage(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), gifName = "finalbattle")
        }
    }

}

@Composable
fun PlayerCritterFinalBattleInfoText(critter: CritterUsable, finalBattleViewModel: FinalBattleViewModel) {

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
fun CpuCritterFinalBattleInfoText(critter: CritterUsable, finalBattleViewModel: FinalBattleViewModel) {
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
                    fontSize = 15.sp,
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
fun GifImage(
    gifName: String,
    modifier: Modifier
) {
    val context = LocalContext.current
    val resourceId = context.resources.getIdentifier(gifName, "drawable", context.packageName)

    if (resourceId == 0) {
        // Handle the case where the resource is not found
        // You may want to display a placeholder or provide some default behavior
        Text(text = "Image not found: $gifName")
    } else {
        val imageLoader = ImageLoader.Builder(context)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()

        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context).data(resourceId).apply {
                    size(Size.ORIGINAL)
                }.build(),
                imageLoader = imageLoader
            ),
            contentDescription = null,
            modifier = modifier.fillMaxWidth(),
        )
    }
}








