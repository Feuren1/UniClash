package project.main.uniclash

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import project.main.uniclash.retrofit.OnlineFightService
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.viewmodels.OnlineFightViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter.State.Empty.painter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import project.main.uniclash.datatypes.CritterInFightInformation
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.CustomColor
import project.main.uniclash.datatypes.OnlineFightState
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.type.Effectiveness
import project.main.uniclash.type.Type
import project.main.uniclash.type.TypeCalculation
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.viewmodels.StateUIState
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

class OnlineFightActivity : ComponentActivity() {
    private val onlineFightViewModel by viewModels<OnlineFightViewModel> {
        OnlineFightViewModel.provideFactory(
            OnlineFightService.getInstance(this),
            CritterService.getInstance(this)
        )
    }

    private val sampleCritter = CritterInFightInformation(
        critterId = 0,
        health = 0,
        attack = 0,
        defence = 0,
        name = "---"
    )

    private val sampleCritterUsable = CritterUsable(
        name = "---",
        critterId = 0,
        atk = 0,
        critterTemplateId = 0,
        attacks = emptyList(),
        def = 0,
        hp = 100,
        level = 0,
        spd = 0,
        expToNextLevel = 0,
        type = "NORMAL"
    )

    private val messages: List<String> = listOf(
        "Good game!",
        "Bad game!",
        "I hope your Critter has health\ninsurance because it's going to need it!",
        "Your Critter looks like it\nlost to a mirror in a battle yesterday!",
        "My Critter are like math geniuses,\nthey add up your defeats!",
        "Get ready to be defeated\nby my coolness!"
    )

    private var state by mutableStateOf(OnlineFightState.WAITING)
    private var myCritter by mutableStateOf(sampleCritter)
    private var critterUsable by mutableStateOf(sampleCritterUsable)
    private var myCritterLoaded by mutableStateOf(false)
    private var enemyCritter by mutableStateOf(sampleCritter)
    private var enemyCritterUsable by mutableStateOf(sampleCritterUsable)
    private var enemyCritterLoaded by mutableStateOf(false)
    private var fightConnectionId by mutableIntStateOf(0)
    private var clickedAttack by mutableStateOf("")
    private var timerValue by mutableIntStateOf(10)
    private var selectedAttack by mutableIntStateOf(0)
    private var selectedTypOfAttack by mutableStateOf(BattleAction.DAMAGE_DEALER)
    private var winner by mutableStateOf(false)
    private var loser by mutableStateOf(false)
    var exitRequest by mutableStateOf(false)
    private var mediaPlayer: MediaPlayer? = null
    private var mediaPlayerStop = false
    private var sendInAppNotification by mutableStateOf(false)

    private var enemyType by mutableStateOf("WATER")
    private var myType by mutableStateOf("WATER")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val b = intent.extras
        fightConnectionId = b?.getInt("fightConnectionId")!!
        onlineFightViewModel.fightConnectionID.value.fightConnectionId = fightConnectionId

        mediaPlayer = MediaPlayer.create(this, R.raw.battlesoundtrack1)
        mediaPlayer?.isLooping = true

        setContent {
            val stateUIState by onlineFightViewModel.state.collectAsState()
            state = stateUIState.state
            val critterInFightUIState by onlineFightViewModel.critterInFight.collectAsState()
            if (critterInFightUIState.critterInFightInformation != null) {
                myCritter = critterInFightUIState.critterInFightInformation!!
            }
            val enemyCritterInFightUIState by onlineFightViewModel.enemyCritterInFight.collectAsState()
            if (enemyCritterInFightUIState.critterInFightInformation != null) {
                enemyCritter = enemyCritterInFightUIState.critterInFightInformation!!
            }

            val critterUsableUIState by onlineFightViewModel.critterUsable.collectAsState()
            if (critterUsableUIState.critterUsable != null) {
                critterUsable = critterUsableUIState.critterUsable!!
                myCritterLoaded = true
            }
            val enemyCritterUsableUIState by onlineFightViewModel.enemyCritterUsable.collectAsState()
            if (enemyCritterUsableUIState.critterUsable != null) {
                enemyCritterUsable = enemyCritterUsableUIState.critterUsable!!
                enemyCritterLoaded = true
            }

            val timerUIState by onlineFightViewModel.timer.collectAsState()
            timerValue = timerUIState.timer
            if (state != OnlineFightState.WAITING && !mediaPlayerStop) mediaPlayer?.start()

            if(sendInAppNotification)inAppNotification()

            ScreenRefresher()
            UniClashTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.battlebackround),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Hier wird der "Balken" mit Text, Exit-Button und Timer hinzugefügt
                        Column(
                        ) {
                            BattleInfoOverlay()
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(), // Füllen Sie den gesamten Bildschirm aus
                        contentAlignment = Alignment.Center // Zentrieren Sie den Inhalt in der Box
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center // Zentrieren Sie den Inhalt der inneren Box
                        ) {
                            Text(
                                text = "Attacks",
                                style = MaterialTheme.typography.bodyLarge
                                    .copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    ),
                                color = Color.White,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = 75.dp)
                                .horizontalScroll(rememberScrollState()),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                            ) {
                                if (critterUsable.attacks.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    for (attack in critterUsable.attacks) {
                                        var attackType = BattleAction.DAMAGE_DEALER
                                        if (attack.attackType.toString()
                                                .uppercase() == "DEF_BUFF"
                                        ) attackType = BattleAction.DEF_BUFF
                                        if (attack.attackType.toString()
                                                .uppercase() == "ATK_BUFF"
                                        ) attackType = BattleAction.ATK_BUFF
                                        if (attack.attackType.toString()
                                                .uppercase() == "ATK_DEBUFF"
                                        ) attackType = BattleAction.ATK_DEBUFF
                                        if (attack.attackType.toString()
                                                .uppercase() == "DEF_DEBUFF"
                                        ) attackType = BattleAction.DEF_DEBUFF
                                        AttackBox(
                                            attackName = attack.name,
                                            value = attack.strength,
                                            attackType = attackType,
                                            type = attack.typeId,
                                            selected = clickedAttack == attack.name
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .offset(y = 150.dp)
                        ) {
                            Button(
                                onClick = {
                                    onlineFightViewModel.makingDamage(
                                        selectedAttack,
                                        selectedTypOfAttack
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CustomColor.DarkPurple.getColor()),
                            ) {
                                Text(
                                    text = "Execute attack!",
                                    color = Color.White, // Ändern Sie die Textfarbe nach Bedarf
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(), // Füllen Sie den gesamten Bildschirm aus
                        contentAlignment = Alignment.BottomCenter // Zentrieren Sie den Inhalt in der Box
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-150).dp),
                            contentAlignment = Alignment.BottomCenter // Zentrieren Sie den Inhalt der inneren Box
                        ) {
                            Text(
                                text = "Messages",
                                style = MaterialTheme.typography.bodyLarge
                                    .copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    ),
                                color = Color.White,
                            )
                        }
                        Spacer(modifier = Modifier.height(64.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-45).dp)
                                .horizontalScroll(rememberScrollState()),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                            ) {
                                Spacer(modifier = Modifier.width(8.dp))
                                for (message in messages) {
                                    MessageBox(message)
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            }
                        }
                    }
                }
            }
            if (exitRequest) {
                onDestroy()
                val intent = Intent(this, OnlineFightListActivity::class.java)
                this.startActivity(intent)
                finish()
                exitRequest = false
            }

            if (winner) EndBox(true)
            if (loser) EndBox(false)
            if (state == OnlineFightState.NOTFOUND) TimeOutBox()
        }
    }

    override fun onDestroy() {
        mediaPlayerStop = true
        mediaPlayer?.release()
        super.onDestroy()
    }

    @Composable
    fun ScreenRefresher() {
        LaunchedEffect(Unit) {
            while (true) {
                delay(3000)
                if (state == OnlineFightState.WAITING) onlineFightViewModel.checkIfFightCanStart()
                if (!myCritterLoaded) onlineFightViewModel.getCritterUsable()
                if (!enemyCritterLoaded) onlineFightViewModel.getEnemyCritterUsable()

                if (state != OnlineFightState.WINNER && state != OnlineFightState.LOSER) onlineFightViewModel.checkMyState()
                if (state != OnlineFightState.WINNER && state != OnlineFightState.LOSER) onlineFightViewModel.getCritterInformation()
                if (state != OnlineFightState.WINNER && state != OnlineFightState.LOSER) onlineFightViewModel.getEnemyCritterInformation()

                if (state == OnlineFightState.WINNER) winner = true
                if (state == OnlineFightState.LOSER) loser = true
            }
        }
    }


    @Composable
    fun BattleInfoOverlay() {
        LaunchedEffect(Unit) {
            // Startet den Timer und aktualisiert den Wert jede Sekunde
            while (true) {
                delay(1000)
                if (timerValue > 0) timerValue--
                if(Notification.Message.containsMessage())sendInAppNotification = true
            }
        }

        // Blaken mit Text, Exit-Button und Timer
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            //horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
            ) {
                // Timer-Anzeige ganz links
                Text(
                    text = "$timerValue sec.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.align(alignment = TopStart)
                )

                // Battle Information in der Mitte
                Text(
                    text = "Current state:\n${state}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.align(alignment = TopCenter)
                )

                // Exit-Button ganz rechts
                Image(
                    painter = painterResource(id = R.drawable.exit),
                    contentDescription = null,
                    modifier = Modifier
                        .align(alignment = TopEnd)
                        .size(30.dp)
                        .clickable {
                            exitRequest = true
                        }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if(enemyCritter.name == "MOCKITO") enemyCritterUsable.hp = critterUsable.hp
            enemyType = enemyCritterUsable.type
            CritterBox(
                enemyCritter.name,
                enemyCritterUsable.level,
                enemyCritterUsable.hp,
                enemyCritter.health,
                enemyCritter.defence,
                enemyCritter.attack,
                Color.Red,
                state == OnlineFightState.ENEMYTURN
            )
            Spacer(modifier = Modifier.height(16.dp))
            if(myCritter.name == "MOCKITO") critterUsable.hp = enemyCritterUsable.hp
            CritterBox(
                myCritter.name,
                critterUsable.level,
                critterUsable.hp,
                myCritter.health,
                myCritter.defence,
                myCritter.attack,
                Color.Green,
                state == OnlineFightState.YOURTURN
            )
        }
    }

    @Composable
    fun CritterBox(
        critterName: String,
        lvl: Int,
        hp: Int,
        currentHp: Int,
        def: Int,
        ack: Int,
        color: Color,
        myTurn: Boolean
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                //.background(MaterialTheme.colorScheme.primary)
                .background(
                    CustomColor.DarkPurple.getColor(),
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    if (myTurn) {
                        3.dp // Dicke des Rahmens für myTurn
                    } else {
                        0.dp // Kein Rahmen, wenn myTurn falsch ist
                    },
                    if (myTurn) {
                        CustomColor.Purple.getColor() // Farbe des Rahmens für myTurn
                    } else {
                        Color.Transparent // Transparenter Rahmen, wenn myTurn falsch ist
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            // Foto auf der linken Seite
            val context = LocalContext.current
            val resourceId = context.resources.getIdentifier(
                critterName.lowercase(),
                "drawable",
                context.packageName
            )
            Image(
                painter = painterResource(
                    if (resourceId > 0) {
                        resourceId
                    } else {
                        R.drawable.icon
                    }
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp)
            )

            // Text auf der rechten Seite
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 100.dp)
                    .align(Alignment.CenterStart)
            ) {
                Text(
                    text = "$critterName",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = "Level: $lvl ATK: $ack DEF: $def HP: $hp",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Balken zur Anzeige von Leben
                HealthBar(health = hp, currentHealth = currentHp, barColor = color)
            }
        }
    }

    @Composable
    fun HealthBar(
        health: Int,
        currentHealth: Int,
        barColor: Color
    ) {
        val percentage = (currentHealth / health.toFloat()).coerceIn(0f, 1f)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage)
                    .fillMaxHeight()
                    .background(barColor, RoundedCornerShape(10.dp))
            )
        }
    }

    @SuppressLint("UnrememberedMutableState")
    @Composable
    fun AttackBox(attackName: String, value: Int, attackType: BattleAction, type : String, selected: Boolean) {
        val typeCalculation = TypeCalculation()

        var effectiveness : Effectiveness = typeCalculation.howEffective(type.uppercase(),enemyType)
        Box(
            modifier = Modifier
                .clickable {
                    clickedAttack = attackName
                    selectedAttack = value
                    selectedTypOfAttack = attackType
                }
                //.fillMaxWidth()
                .height(100.dp)
                //.background(CustomColor.Purple.getColor())
                .background(
                    CustomColor.DarkPurple.getColor(),
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    if (selected) {
                        3.dp
                    } else {
                        0.dp
                    },
                    if (selected) {
                        CustomColor.Purple.getColor()
                    } else {
                        Color.Transparent
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            // Foto auf der linken Seite
            val context = LocalContext.current
            var resourceId = R.drawable.pow
            if (attackType == BattleAction.DEF_BUFF) resourceId = R.drawable.defencebuffsymbol
            if (attackType == BattleAction.ATK_BUFF) resourceId = R.drawable.attackbuffsymbol
            if (attackType == BattleAction.DEF_DEBUFF) resourceId = R.drawable.defencedebuffsymbol
            if (attackType == BattleAction.ATK_DEBUFF) resourceId = R.drawable.attackdebuffsymbol

            var resourceIdType = R.drawable.normal
            resourceIdType = when (type) {
                "DRAGON" -> {R.drawable.dragon}
                "WATER" -> {R.drawable.water}
                "ELECTRIC" -> {R.drawable.electric}
                "FIRE" -> {R.drawable.fire}
                "STONE" -> {R.drawable.stone}
                "ICE" -> {R.drawable.ice}
                else -> {R.drawable.normal}
            }

            Row(
            ) {
                Image(
                    painter = painterResource(
                        if (resourceId > 0) {
                            resourceId
                        } else {
                            R.drawable.icon
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
                )
                Image(
                    painter = painterResource(
                        if (resourceIdType > 0) {
                            resourceIdType
                        } else {
                            R.drawable.icon
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
                )

                // Text auf der rechten Seite
                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                ) {
                    Text(
                        text = "$attackName",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )

                    Text(
                        text = "Value: $value",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Text(
                        text = "$effectiveness",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }
    }

    @Composable
    fun EndBox(winner: Boolean) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .offset(y = 275.dp)
                    .height(150.dp)
                    .fillMaxWidth()
                    .background(
                        CustomColor.DarkPurple.getColor(),
                        shape = RoundedCornerShape(8.dp),
                    )
                    .border(
                        3.dp,
                        CustomColor.Purple.getColor(),
                        shape = RoundedCornerShape(8.dp),
                    )
                    .wrapContentSize(Alignment.Center),
                contentAlignment = Alignment.Center,
            ) {
                // Foto auf der linken Seite
                Row(
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    if (winner) GifImage(gifName = "vibe", modifier = Modifier.size(80.dp))
                    if (!winner) Image(
                        painter = painterResource(R.drawable.swords),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                    ) {
                        if (winner) {
                            Text(
                                text = "Congratulations",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            Text(
                                text = "you won!\nYou received 5 credits and\n200 ep for you and your critter.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "You lose !",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            Text(
                                text = "...bad luck",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    @Composable
    fun TimeOutBox() {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .offset(y = 275.dp)
                    .height(150.dp)
                    .fillMaxWidth()
                    .background(
                        CustomColor.DarkPurple.getColor(),
                        shape = RoundedCornerShape(8.dp),
                    )
                    .border(
                        3.dp,
                        CustomColor.Purple.getColor(),
                        shape = RoundedCornerShape(8.dp),
                    )
                    .wrapContentSize(Alignment.Center),
                contentAlignment = Alignment.Center,
            ) {
                // Foto auf der linken Seite
                Row(
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(R.drawable.warning),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                    ) {
                        Text(
                            text = "Timeout",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Text(
                            text = "Your fight end because\nof time limitation.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    @SuppressLint("UnrememberedMutableState")
    @Composable
    fun MessageBox(message: String) {
        Box(
            modifier = Modifier
                .clickable {
                    onlineFightViewModel.sendMessageViaPushNotification(message)
                }
                .height(75.dp)
                .background(
                    CustomColor.DarkPurple.getColor(),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            // Foto auf der linken Seite
            Row(
            ) {
                // Text auf der rechten Seite
                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                ) {
                    Text(
                        text = "$message",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }
    }

    @Composable
    fun inAppNotification() {
        if (sendInAppNotification) {
            Toast.makeText(baseContext, Notification.Message.takeMessage(), Toast.LENGTH_LONG)
                .show()
            sendInAppNotification = false
        }
    }
}



enum class BattleAction {
    DAMAGE_DEALER,
    DEF_BUFF,
    DEF_DEBUFF,
    ATK_BUFF,
    ATK_DEBUFF
}

enum class Notification(private var title : String, private var text : String){
    Message("","");

    fun containsMessage():Boolean{
        return title.isNotEmpty()
    }
    fun takeMessage():String{
        var message = "$title:\n$text"
        this.text = ""
        this.title = ""
        return message
    }

    fun setMessage(title : String, text :String){
        this.title = title
        this.text = text
    }
}

