package project.main.uniclash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import project.main.uniclash.datatypes.CritterTemplate
import project.main.uniclash.datatypes.CustomColor
import project.main.uniclash.retrofit.CritterService
import project.main.uniclash.viewmodels.CritterDexViewModel


class CritterDexActivity : ComponentActivity() {
    private var exitRequest by mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        val critterDexViewModel: CritterDexViewModel by viewModels(factoryProducer = {
            CritterDexViewModel.provideFactory(CritterService.getInstance(this))
        })

        super.onCreate(savedInstanceState)

        setContent {
            critterDexViewModel.loadCritterTemplates()
            val critterDexUiStateCritterTemplates by critterDexViewModel.critterTemplatesOrdered.collectAsState()
            val critterTemplates = critterDexUiStateCritterTemplates.critterTemplates


            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(16.dp)
                ) {
                    MenuHeader()
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

                // Scroll-Box für Critters mit weißem Hintergrund
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black) // Hier wird der Hintergrund weiß gemacht
                ) {
                    Box(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column {
                            for (critters in critterTemplates) {
                                CritterDetail(critters)
                            }
                        }
                    }
                }
            }

            if (exitRequest) {
                val intent = Intent(this, MenuActivity::class.java)
                this.startActivity(intent)
                finish()
                exitRequest = false
            }
        }
    }

    @Composable
    fun MenuHeader() {
        Text(
            text = "CritterDex",
            fontSize = 50.sp, // Adjust the font size as needed
            fontWeight = FontWeight.Bold, // Use FontWeight.Bold for bold text
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(vertical = 16.dp), // Add vertical padding
            color = Color.White
        )
    }

    @Composable
    fun CritterDetail(critters: List<CritterTemplate?>) {
        Box(
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth() // making box from left to right site
                .background(
                    CustomColor.DarkPurple.getColor(),
                    shape = RoundedCornerShape(8.dp)
                ) // Hintergrundfarbe und abgeflachte Ecken
                .border(
                    3.dp,
                    CustomColor.Purple.getColor(),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { }
        ) {
            Column {
                for (critter in critters) {
                    Box(modifier = Modifier
                        .clickable {}
                        .fillMaxWidth()) {
                        Row(modifier = Modifier.padding(all = 8.dp)) {
                            val context = LocalContext.current
                            val name: String = critter?.name!!.lowercase()
                            val resourceId = context.resources.getIdentifier(name, "drawable", context.packageName)
                            Image(
                                painter = painterResource(if(resourceId > 0){resourceId}else{R.drawable.icon}),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(60.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = critter!!.name,
                                    fontSize = 18.sp,
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                var text = ""
                                if (critter?.evolesAt == 0) {
                                    text =
                                        "Health: ${critter?.baseHealth} Attack: ${critter?.baseAttack}\nDefence: ${critter?.baseDefence} Speed: ${critter?.baseSpeed}"
                                } else {
                                    text =
                                        "Health: ${critter?.baseHealth} Attack: ${critter?.baseAttack}\nDefence: ${critter?.baseDefence} Speed: ${critter?.baseSpeed}\nEvolves at: ${critter?.evolesAt}"
                                }
                                Text(
                                    text = text,
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                                Box(modifier = Modifier.weight(1f)) {
                                    Image(
                                        painter = painterResource(
                                            id = when (critter.type) {
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
                                            .size(35.dp)
                                            .offset(y = 15.dp)
                                            .align(Alignment.CenterEnd)
                                    )
                            }
                        }
                    }
                }
            }
        }
    }
}