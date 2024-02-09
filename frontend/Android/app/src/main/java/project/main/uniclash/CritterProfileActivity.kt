package project.main.uniclash

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import project.main.uniclash.datatypes.AttackType
import project.main.uniclash.datatypes.CritterUsable
import project.main.uniclash.datatypes.CustomColor
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.InventoryService
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.viewmodels.CritterProfileViewModel
import project.main.uniclash.viewmodels.CritterUsableUIState
import project.main.uniclash.viewmodels.UniClashViewModel

class CritterProfileActivity : ComponentActivity() {
    private var exitRequest by mutableStateOf(false)
    private var lvlWindow by mutableStateOf(false)
    private var delWindow by mutableStateOf(false)
    private var evoWindow by mutableStateOf(false)
    val critterProfileViewModel: CritterProfileViewModel by viewModels(factoryProducer = {
        CritterProfileViewModel.provideFactory(CritterService.getInstance(this), InventoryService.getInstance(this))
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = intent.extras
        var critterId = -1 // or other values

        if (b != null) critterId = b.getInt("critterId")
        var type = "NORMAL"
        if(b !=null) type = b.getString("type").toString()
        critterProfileViewModel.loadCritter(critterId)
        critterProfileViewModel.loadCritterUsable(critterId)
        setContent {
            UniClashTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val critterUsableUIState by critterProfileViewModel.critterUsable.collectAsState()
                    val critterUIState by critterProfileViewModel.critter.collectAsState()
                    val critterTemplateUIState by critterProfileViewModel.critterTemplate.collectAsState()
                    val critterEvoTemplateUIState by critterProfileViewModel.critterTemplateEvo.collectAsState()
                    val critterUsable = critterUsableUIState.critterUsable
                    val critter = critterUIState.critter
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = when (type) {
                                "DRAGON" -> {R.drawable.dragonbackground}
                                "WATER" -> {R.drawable.waterbackground}
                                "ELECTRIC" -> {R.drawable.elecbackground}
                                "FIRE" -> {R.drawable.firebackground}
                                "STONE" -> {R.drawable.stonebackground}
                                "ICE" -> {R.drawable.icebackground}
                                else -> {R.drawable.battlebackround}
                            }
                            ),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
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
                            if (critterUsable != null) {
                                critterProfileViewModel.loadCritterTemplate(critterUsable.critterTemplateId)
                                if(critterTemplateUIState.critterTemplate!!.name.isNotEmpty() && critterTemplateUIState.critterTemplate!!.evolesAt > 0) critterProfileViewModel.loadCritterTemplateEvo(critterTemplateUIState.critterTemplate!!.evolvesIntoTemplateId)
                                if((critterTemplateUIState.critterTemplate!!.name.isNotEmpty() && critterTemplateUIState.critterTemplate!!.evolesAt == 0)||critterTemplateUIState.critterTemplate!!.name.isNotEmpty()  && critterTemplateUIState.critterTemplate!!.evolesAt > 0 && critterEvoTemplateUIState.critterTemplate != null) CritterProfile(critterProfileViewModel, LocalContext.current)
                            }
                        }
                    }
                }

                }
                if(lvlWindow) EpWindow(critterProfileViewModel)
                if(delWindow) DeletionWindow(critterProfileViewModel)
                if(evoWindow) EvolutionWindow(critterProfileViewModel, LocalContext.current)
                if (exitRequest) {
                    val intent = Intent(this, CritterListActivity::class.java)
                    this.startActivity(intent)
                    exitRequest = false
                    finish()
                }
            }
        }
    }

    @Composable
    fun EpWindow(critterProfileViewModel: CritterProfileViewModel = viewModel()) {
        val critterUsableUIState by critterProfileViewModel.critterUsable.collectAsState()
        val critterUIState by critterProfileViewModel.critter.collectAsState()
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .offset(y = 275.dp)
                    .height(250.dp)
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
                        painter = painterResource(R.drawable.redbull),
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
                            text = "Level - Exp",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Text(
                            text = "Level: ${critterUsableUIState.critterUsable!!.level}\nEP: ${critterUsableUIState.critterUsable!!.expToNextLevel} / ${Math.floor(50 * Math.pow((1.025), (critterUsableUIState.critterUsable!!.level + 1).toDouble()))}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = CustomColor.Purple.getColor()),
                            onClick = {
                                critterProfileViewModel.useRedBull(critterUsableUIState.critterUsable!!.critterId)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                        ) {
                            Text(text = "Use Redbull (+100EP)", color = Color.White)
                        }
                        Button(
                            onClick = { lvlWindow = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        ) {
                            Text(text = "Close", color = Color.White)
                        }
                    }
                }}
        }
    }

    @Composable
    fun DeletionWindow(critterProfileViewModel: CritterProfileViewModel = viewModel()) {
        val critterUsableUIState by critterProfileViewModel.critterUsable.collectAsState()
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .offset(y = 275.dp)
                    .height(200.dp)
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
                        painter = painterResource(R.drawable.cryduck),
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
                            text = "Are you sure...?",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Button(
                            onClick = {
                                critterProfileViewModel.delCritter(critterUsableUIState.critterUsable!!.critterId)
                                exitRequest = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        ) {
                            Text(text = "kill him", color = Color.White)
                        }
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = CustomColor.Purple.getColor()),
                            onClick = { delWindow = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                        ) {
                            Text(text = "Close", color = Color.White)
                        }
                    }
                }}
        }
    }

    @Composable
    fun EvolutionWindow(critterProfileViewModel: CritterProfileViewModel = viewModel(), context : Context) {
        val critterUsableUIState by critterProfileViewModel.critterUsable.collectAsState()
        val critterEvoTemplateUIState by critterProfileViewModel.critterTemplateEvo.collectAsState()
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .offset(y = 275.dp)
                    .height(200.dp)
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
                    val resourceId = context.resources.getIdentifier(
                        critterEvoTemplateUIState.critterTemplate!!.name.lowercase(),
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
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                    ) {
                        Text(
                            text = "Start evolution",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = CustomColor.Purple.getColor()),
                            onClick = {
                                critterProfileViewModel.evolve(critterUsableUIState.critterUsable!!.critterId)
                                evoWindow = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                        ) {
                            Text(text = "Evolve", color = Color.White, )
                        }
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            onClick = { evoWindow = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                        ) {
                            Text(text = "Close")
                        }
                    }
                }}
        }
    }

    @Composable
    fun ExpBar(
        currentValue: Int,
        level: Int
    ) {
        val percentage = (currentValue /  Math.floor(50 * Math.pow((1.025), (level + 1).toDouble())).toFloat()).coerceIn(0f, 1f)
        Box(
            modifier = Modifier
                .width(100.dp)
                .offset(x = 75.dp)
                .offset(y = 5.dp)
                .height(10.dp)
                .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .clickable { lvlWindow = true }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage)
                    .fillMaxHeight()
                    .background(Color.Yellow, RoundedCornerShape(10.dp))
            )
        }
    }

    @SuppressLint("SuspiciousIndentation")
    @Composable
    fun CritterProfile(critterProfileViewModel: CritterProfileViewModel = viewModel(), context : Context) {
        val critterUsableUIState by critterProfileViewModel.critterUsable.collectAsState()
        val critterUIState by critterProfileViewModel.critter.collectAsState()
        val isSelectedUIState by critterProfileViewModel.isSelected.collectAsState()
        val critterTemplateUIState by critterProfileViewModel.critterTemplate.collectAsState()
        val critterEvoTemplateUIState by critterProfileViewModel.critterTemplateEvo.collectAsState()
        if (critterUsableUIState.critterUsable != null && critterUIState.critter != null && critterTemplateUIState.critterTemplate != null) {
            critterProfileViewModel.checkIfCritterIsSelected(critterUsableUIState.critterUsable!!.critterId)
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Box(
                ){
                    val resourceId = context.resources.getIdentifier(
                        critterUsableUIState.critterUsable!!.name.lowercase(),
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
                            .size(200.dp)
                            .padding(8.dp)
                            .align(Alignment.TopCenter)
                    )
                    // Display other critter information as needed
                    Box(
                        modifier = Modifier//.offset(y = 225.dp)
                            .verticalScroll(rememberScrollState()),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.height(225.dp))
                            Text(
                                text = "${critterUsableUIState.critterUsable!!.name}",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                                    .copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 36.sp
                                    ),
                            )
                            Box {
                                Text(
                                    text = "Level: ${critterUsableUIState.critterUsable!!.level}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge
                                        .copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        ),
                                )
                                if(critterUsableUIState.critterUsable!!.level < 100) ExpBar(critterUsableUIState.critterUsable!!.expToNextLevel, critterUsableUIState.critterUsable!!.level)
                            }
                            Text(
                                text = "Type: ${critterUsableUIState.critterUsable!!.type}",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                                    .copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        CustomColor.DarkPurple.getColor(),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        3.dp,
                                        CustomColor.Purple.getColor(),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Stats:",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyLarge
                                            .copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            ),
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box {
                                        Text(
                                            text = "Health: ${critterUsableUIState.critterUsable!!.hp}",
                                            color = Color.White,
                                        )
                                        ProfileBar(
                                            critterUsableUIState.critterUsable!!.level,
                                            Color(255, 165, 0)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box {
                                        Text(
                                            text = "Attack: ${critterUsableUIState.critterUsable!!.atk}",
                                            color = Color.White,
                                        )
                                        ProfileBar(
                                            critterUsableUIState.critterUsable!!.level,
                                            Color.Red
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box {
                                        Text(
                                            text = "Defense: ${critterUsableUIState.critterUsable!!.def}",
                                            color = Color.White,
                                        )
                                        ProfileBar(
                                            critterUsableUIState.critterUsable!!.level,
                                            Color.Green
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box {
                                        Text(
                                            text = "Speed: ${critterUsableUIState.critterUsable!!.spd}",
                                            color = Color.White,
                                        )
                                        ProfileBar(
                                            critterUsableUIState.critterUsable!!.level,
                                            Color.Blue
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            // Display attacks
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        CustomColor.DarkPurple.getColor(),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        3.dp,
                                        CustomColor.Purple.getColor(),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Attacks:", color = Color.White,
                                        style = MaterialTheme.typography.bodyLarge
                                            .copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            ),
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (!critterUsableUIState.critterUsable!!.attacks.isNullOrEmpty()) {
                                        critterUsableUIState.critterUsable!!.attacks.forEach { attack ->
                                            Row {
                                                Image(
                                                    painter = if (attack.attackType == AttackType.ATK_BUFF) {
                                                        painterResource(R.drawable.attackbuffsymbol)
                                                    } else if (attack.attackType == AttackType.DEF_BUFF) {
                                                        painterResource(R.drawable.defencebuffsymbol)
                                                    } else if (attack.attackType == AttackType.DEF_DEBUFF) {
                                                        painterResource(R.drawable.defencedebuffsymbol)
                                                    } else if (attack.attackType == AttackType.ATK_DEBUFF) {
                                                        painterResource(R.drawable.attackdebuffsymbol)
                                                    } else {
                                                        painterResource(R.drawable.pow)
                                                    },
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(50.dp)
                                                        .padding(8.dp)
                                                )

                                                Text(
                                                    text = "${attack.name}: ${attack.strength}",
                                                    color = Color.White,
                                                    modifier = Modifier
                                                        .offset(y = (15).dp),
                                                )
                                                Box(modifier = Modifier.weight(1f)) {
                                                    Image(
                                                        painter = painterResource(
                                                            id = when (attack.typeId) {
                                                                "DRAGON" -> {
                                                                    R.drawable.dragon
                                                                }

                                                                "WATER" -> {
                                                                    R.drawable.water
                                                                }

                                                                "ELECTRIC" -> {
                                                                    R.drawable.electric
                                                                }

                                                                "FIRE" -> {
                                                                    R.drawable.fire
                                                                }

                                                                "STONE" -> {
                                                                    R.drawable.stone
                                                                }

                                                                "ICE" -> {
                                                                    R.drawable.ice
                                                                }

                                                                "METAL" -> {
                                                                    R.drawable.metal
                                                                }

                                                                else -> {
                                                                    R.drawable.normal
                                                                }
                                                            }
                                                        ),
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .size(50.dp)
                                                            .padding(8.dp)
                                                            .align(Alignment.CenterEnd)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            if(critterTemplateUIState.critterTemplate!!.evolesAt>0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            CustomColor.DarkPurple.getColor(),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            3.dp,
                                            CustomColor.Purple.getColor(),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(16.dp)
                                        .clickable { evoWindow = true }
                                ) {
                                    Column {
                                        Text(
                                            text = "Evolution:", color = Color.White,
                                            style = MaterialTheme.typography.bodyLarge
                                                .copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp
                                                ),
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Needed level: ${critterTemplateUIState.critterTemplate!!.evolesAt}",
                                            color = Color.White,
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Evolves in: ${critterEvoTemplateUIState.critterTemplate!!.name}",
                                            color = Color.White,
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    //critterProfileViewModel.evolve(critterUsableUIState.critterUsable!!.critterId)
                                    //critterProfileViewModel.loadCritterUsable(critterUIState.critter!!.id)
                                    critterProfileViewModel.storeFightingCritter()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CustomColor.DarkPurple.getColor()),
                            ) {
                                Text(text = if(!isSelectedUIState.isSelected){"Select this critter to fight!"} else{"This critter is selected for further fights!"},color = Color.White)
                            }
                            Button(
                                onClick = {delWindow = true},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            ) {
                                Text(text = "Delete Critter :(",color = Color.White)
                            }
                        }
                    }
                }
            }
        }else{
            Text("Critter data not available.",color = Color.White)
        }
    }
    @Composable
    fun ProfileBar(
        currentValue: Int,
        barColor: Color
    ) {
        val percentage = (currentValue / 100.toFloat()).coerceIn(0f, 1f)
        Box(
            modifier = Modifier
                .width(210.dp)
                .offset(x = 110.dp)
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
}
