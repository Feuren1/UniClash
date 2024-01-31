package project.main.uniclash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import project.main.uniclash.datatypes.CritterInFightInformation
import project.main.uniclash.datatypes.OnlineFightState
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.viewmodels.StateUIState
import java.util.concurrent.TimeUnit

class OnlineFightActivity : ComponentActivity() {
    private val onlineFightViewModel by viewModels<OnlineFightViewModel> {
        OnlineFightViewModel.provideFactory(OnlineFightService.getInstance(this))
    }

    val sampleCritter = CritterInFightInformation(
        critterId = 0,
        health = 0,
        attack = 0,
        defence = 0,
        name = "---"
    )

    private var state by mutableStateOf(OnlineFightState.WAITING)
    private var myCritter by mutableStateOf(sampleCritter)
    private var myCritterLoaded by mutableStateOf(false)
    private var enemyCritter by mutableStateOf(sampleCritter)
    private var enemyCritterLoaded by mutableStateOf(false)
    private var fightConnectionId by mutableIntStateOf(0)
    private var clickedAttack by mutableStateOf("")
    private var timerValue by mutableIntStateOf(10)
    var exitRequest by mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val b = intent.extras
        fightConnectionId = b?.getInt("fightConnectionId")!!
        onlineFightViewModel.fightConnectionID.value.fightConnectionId = fightConnectionId

        setContent {
            val stateUIState by onlineFightViewModel.state.collectAsState()
            state = stateUIState.state
            val critterInFightUIState by onlineFightViewModel.critterInFight.collectAsState()
            if(critterInFightUIState.critterInFightInformation != null){
                myCritter = critterInFightUIState.critterInFightInformation!!
            }
            val enemyCritterInFightUIState by onlineFightViewModel.enemyCritterInFight.collectAsState()
            if(enemyCritterInFightUIState.critterInFightInformation != null){
                enemyCritter = critterInFightUIState.critterInFightInformation!!
            }

            ScreenRefresher(stateUIState)
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
                        // Hier wird der "Blaken" mit Text, Exit-Button und Timer hinzugefügt
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
                            Spacer(modifier = Modifier.height(64.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset(y = 75.dp)
                                    .horizontalScroll(rememberScrollState()),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                ) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    AttackBox(
                                        attackName = "Ember",
                                        value = 50,
                                        type = BattleAction.DAMAGE_DEALER,
                                        selected = clickedAttack == "Ember"
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    AttackBox(
                                        attackName = "Growl",
                                        value = 20,
                                        type = BattleAction.DAMAGE_DEALER,
                                        selected = clickedAttack == "Growl"
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    AttackBox(
                                        attackName = "Volt Tackle",
                                        value = 20,
                                        type = BattleAction.DEF_BUFF,
                                        selected = clickedAttack == "Volt Tackle"
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    AttackBox(
                                        attackName = "Double Shock",
                                        value = 20,
                                        type = BattleAction.ATK_BUFF,
                                        selected = clickedAttack == "Double Shock"
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            }
                        Box(
                            modifier = Modifier
                            .offset(y = 150.dp)
                        ) {
                            Button(
                                onClick = { /* TODO: Aktion beim Klicken */ },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
                            ) {
                                Text(
                                    text = "Execute attack!",
                                    color = Color.White, // Ändern Sie die Textfarbe nach Bedarf
                                )
                            }
                        }
                        }
                    }
                }
            if (exitRequest) {
                val intent = Intent(this, OnlineFightListActivity::class.java)
                this.startActivity(intent)
                finish()
                exitRequest = false
            }
            }
        }

    @Composable
    fun ScreenRefresher(stateUIState: StateUIState.HasEntries){
        LaunchedEffect(Unit) {
            while (true) {
                delay(3000)
                if(state==OnlineFightState.WAITING) onlineFightViewModel.checkIfFightCanStart()
                //if(!myCritterLoaded)
                //if(!enemyCritterLoaded)
                onlineFightViewModel.checkMyState()
                onlineFightViewModel.getCritterInformation()
                onlineFightViewModel.getEnemyCritterInformation()
            }
        }
    }


    @Composable
    fun BattleInfoOverlay() {
        LaunchedEffect(Unit) {
            // Startet den Timer und aktualisiert den Wert jede Sekunde
            while (true) {
                delay(1000)
                timerValue--
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
                    text = "Battle Information\n${state.getState()}",
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
            CritterBox(enemyCritter.name,0,500,enemyCritter.health,myCritter.defence,myCritter.attack,Color.Red, true)
            Spacer(modifier = Modifier.height(16.dp))
            CritterBox(myCritter.name,0,500,myCritter.health,myCritter.defence,myCritter.attack,Color.Green, false)
        }
    }

    @Composable
    fun CritterBox(critterName : String,lvl : Int, hp : Int, currentHp: Int, def : Int, ack : Int, color : Color, myTurn : Boolean){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                //.background(MaterialTheme.colorScheme.primary)
                .background(
                    MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    if (myTurn) {
                        3.dp // Dicke des Rahmens für myTurn
                    } else {
                        0.dp // Kein Rahmen, wenn myTurn falsch ist
                    },
                    if (myTurn) {
                        MaterialTheme.colorScheme.primary // Farbe des Rahmens für myTurn
                    } else {
                        Color.Transparent // Transparenter Rahmen, wenn myTurn falsch ist
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            // Foto auf der linken Seite
            val context = LocalContext.current
            val resourceId = context.resources.getIdentifier(critterName.lowercase(), "drawable", context.packageName)
            Image(
                painter = painterResource(if(resourceId > 0){resourceId}else{R.drawable.icon}),
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
    fun AttackBox(attackName : String, value : Int, type : BattleAction, selected : Boolean){
        Box(
            modifier = Modifier
                .clickable {
                    clickedAttack = attackName
                }
                //.fillMaxWidth()
                .height(90.dp)
                //.background(MaterialTheme.colorScheme.primary)
                .background(
                    MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    if (selected) {
                        3.dp
                    } else {
                        0.dp
                    },
                    if (selected) {
                        MaterialTheme.colorScheme.primary
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
            if(type == BattleAction.DEF_BUFF)resourceId = R.drawable.defencebuffsymbol
            if(type == BattleAction.ATK_BUFF)resourceId = R.drawable.attackbuffsymbol
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
                }
            }
        }
    }
}

enum class BattleAction {
    DAMAGE_DEALER,
    DEF_BUFF,
    ATK_BUFF
}

