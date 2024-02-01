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
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.retrofit.InventoryService
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.viewmodels.CritterProfileViewModel
import project.main.uniclash.viewmodels.CritterUsableUIState
import project.main.uniclash.viewmodels.UniClashViewModel

class CritterProfileActivity : ComponentActivity() {
    private var exitRequest by mutableStateOf(false)
    val critterProfileViewModel: CritterProfileViewModel by viewModels(factoryProducer = {
        CritterProfileViewModel.provideFactory(CritterService.getInstance(this), InventoryService.getInstance(this))
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = intent.extras
        var critterId = -1 // or other values

        if (b != null) critterId = b.getInt("critterId")
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
                    val critterUsable = critterUsableUIState.critterUsable
                    val critter = critterUIState.critter
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.battlebackround),
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
                                CritterProfile(critterProfileViewModel, LocalContext.current)
                            }
                        }
                    }
                }

                }
                if (exitRequest) {
                    val intent = Intent(this, CritterListActivity::class.java)
                    this.startActivity(intent)
                    exitRequest = false
                    finish()
                }
            }
        }
    }
}

    @SuppressLint("SuspiciousIndentation")
    @Composable
    fun CritterProfile(critterProfileViewModel: CritterProfileViewModel = viewModel(), context : Context) {
        val critterUsableUIState by critterProfileViewModel.critterUsable.collectAsState()
        val critterUIState by critterProfileViewModel.critter.collectAsState()
        if (critterUsableUIState.critterUsable != null && critterUIState.critter != null) {
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
                            ExpBar(25, critterUsableUIState.critterUsable!!.level)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.onPrimaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    3.dp,
                                    MaterialTheme.colorScheme.primary,
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
                                    MaterialTheme.colorScheme.onPrimaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    3.dp,
                                    MaterialTheme.colorScheme.primary,
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
                                                painter = if(attack.attackType == AttackType.ATK_BUFF){painterResource(R.drawable.attackbuffsymbol)}else if(attack.attackType==AttackType.DEF_BUFF){painterResource(R.drawable.defencebuffsymbol)}else{painterResource(R.drawable.pow)},
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
                                        }
                                    }
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.onPrimaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    3.dp,
                                    MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = "?:", color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge
                                        .copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        ),
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                //TODO

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
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
                        ) {
                            Text(text = "Select this critter to fight!")
                        }
                        Button(
                            onClick = {
                                critterProfileViewModel.loadCritterUsable(critterUIState.critter!!.id)
                                critterProfileViewModel.evolve(critterUsableUIState.critterUsable!!.critterId)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
                        ) {
                            Text(text = "Evolve")
                        }
                        Button(
                            onClick = {
                                critterProfileViewModel.loadCritterUsable(critterUIState.critter!!.id)
                                critterProfileViewModel.useRedBull(critterUsableUIState.critterUsable!!.critterId)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
                        ) {
                            Text(text = "Use Rebull")
                        }
                        Button(
                            onClick = {
                                critterProfileViewModel.delCritter(critterUsableUIState.critterUsable!!.critterId)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
                        ) {
                            Text(text = "Delete Critter :(")
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
            .width(240.dp)
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

@Composable
fun ExpBar(
    currentValue: Int,
    level: Int
) {
    val percentage = (currentValue /  Math.floor(50 * Math.pow((1.025), (level + 1).toDouble())).toFloat()).coerceIn(0f, 1f)
    Box(
        modifier = Modifier
            .width(150.dp)
            .offset(x = 30.dp)
            .height(20.dp)
            .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(percentage)
                .fillMaxHeight()
                .background(Color.Magenta, RoundedCornerShape(10.dp))
        )
    }
}
